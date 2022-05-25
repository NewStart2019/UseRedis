package com.example.demo.common.aop;

import com.example.demo.common.annotation.RedisLimit;
import com.example.demo.exception.RedisLimitException;
import com.example.demo.service.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scripting.support.ResourceScriptSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Aspect
@Component
public class RedisLimitAop {

    @Resource
    private RedisService redisService;
    private DefaultRedisScript<Long> redisScript;

    @PostConstruct
    public void init() {
        redisScript = new DefaultRedisScript<>();
        redisScript.setResultType(Long.class);
        redisScript.setScriptSource(new ResourceScriptSource(new ClassPathResource("lua/counterLimiter.lua")));
    }

    // 添加切面注解类
    @Pointcut("@annotation(com.example.demo.common.annotation.RedisLimit)")
    private void check() {
    }

    // 添加切面前置操作
    @Before("check()")
    public void before(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        RedisLimit redisLimit = method.getAnnotation(RedisLimit.class);

        // 存在限流注解 且 过期时间大于0 且 限流次数大于0 则开启限流
        if (redisLimit != null && redisLimit.permitsPerSecond() > 0 && redisLimit.expire() > 0) {
            //获取redis的key
            String key = redisLimit.key();
            String limitKey = key + method.getDeclaringClass().getName() + method.getName();

            log.info(limitKey);
            if (!StringUtils.hasText(key)) {
                throw new RedisLimitException("key cannot be null");
            }

            long limit = redisLimit.permitsPerSecond();

            long expireTime = redisLimit.expire();

            // 脚本里的KEYS参数
            List<String> keys = new ArrayList<>();
            keys.add(limitKey);
            //String luaScript = buildLuaScript();
            //RedisScript<Long> redisScript = new DefaultRedisScript<>(luaScript, Long.class);

            // 脚本里的ARGV参数
            Long count = redisService.execute(this.redisScript, keys,
                    String.valueOf(limit), String.valueOf(expireTime));


            log.info("Access try count is {} for key={}", count, limitKey);

            if (count == null || count == -1) {
                log.debug("令牌桶={}，获取令牌失败", key);
                throw new RedisLimitException(redisLimit.msg());
            }
        }

    }

    /**
     * 构建redis lua脚本
     *
     * @return {@link String}
     */
    private String buildLuaScript() {
        return "local key = KEYS[1]" +
                //获取ARGV内参数Limit
                "\nlocal limit = tonumber(ARGV[1])" +
                //获取key的次数
                "\nlocal curentLimit = tonumber(redis.call('get', key) or \"0\")" +
                "\nif curentLimit + 1 > limit then" +
                "\nreturn 0" +
                "\nelse" +
                //自增长 1
                "\n redis.call(\"INCRBY\", key, 1)" +
                //设置过期时间
                "\nredis.call(\"EXPIRE\", key, ARGV[2])" +
                "\nreturn curentLimit + 1" +
                "\nend";
    }
}
