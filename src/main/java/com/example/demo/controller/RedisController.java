package com.example.demo.controller;

import com.example.demo.common.annotation.RedisLimit;
import com.example.demo.common.web.AjaxResult;
import com.example.demo.service.RedisService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.scripting.support.ResourceScriptSource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@RestController("/redis")
@Api(tags = {"redis测试接口"})
public class RedisController {

    @Resource
    private RedisService redisService;

    @Resource
    private JedisConnectionFactory factory;

    private String redisKey = "test";

    @ApiOperation("测试接口2")
    @GetMapping("/test2")
    public AjaxResult<String> test2(@ApiParam("测试参数2") String test) {
        return AjaxResult.success(test);
    }

    @ApiOperation("测试redis存储数据")
    @GetMapping("/store")
    @RedisLimit(key = "redis-limit:", permitsPerSecond = 2, expire = 10, msg = "当前排队人数较多，请稍后再试！")
    public AjaxResult<String> store(@ApiParam("测试参数2") String param) {
        return redisService.set(redisKey, param, 100L) ?
                AjaxResult.success("操作成功", param) : AjaxResult.error("存储失败");
    }

    @ApiOperation("测试redis取出数据")
    @GetMapping("/get")
    public AjaxResult<Object> get() {
        Object o = redisService.get(redisKey);
        return AjaxResult.success(o);
    }

    /**
     * 这里因为lua脚本返回的是字符串没有序列化过的，所以反序列化会失败只能解析成字符串
     */
    @ApiOperation("测试redis执行lua脚本")
    @GetMapping("/operate/lua")
    public AjaxResult<String> testLua() {
        DefaultRedisScript<String> redisScript = new DefaultRedisScript<>();
        redisScript.setResultType(String.class);
        redisScript.setScriptSource(new ResourceScriptSource(new ClassPathResource("lua/getRedPocket.lua")));
        List<String> keyList = new ArrayList<>();
        //产生的小红包key
        keyList.add("{envelope}:redEnvelopeId:" + "redEnvelopeId");
        //红包领取记录key
        keyList.add("{envelope}:record:" + "redEnvelopeId");
        long userId = 100L;
        keyList.add("{envelope}" + userId);

        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);
        RedisSerializer<? extends String> stringSerializer = new StringRedisSerializer();
        template.setKeySerializer(stringSerializer);
        template.setValueSerializer(stringSerializer);
        template.setHashKeySerializer(stringSerializer);
        template.setHashValueSerializer(stringSerializer);
        template.afterPropertiesSet();

        // -1 已经抢到红包   -2 红包已经完了   ，其余是抢到红包并返回红包余额
        String execute = template.execute(redisScript, keyList, userId + "");
        return AjaxResult.success(execute);
    }
}
