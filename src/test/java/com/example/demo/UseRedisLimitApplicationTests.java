package com.example.demo;

import jdk.nashorn.internal.ir.annotations.Ignore;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ResolvableType;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scripting.ScriptSource;
import org.springframework.scripting.support.ResourceScriptSource;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;

@SpringBootTest
class UseRedisLimitApplicationTests {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    // 测试checkandset.lua脚本
    @Ignore
    @Test
    void contextLoads() throws IOException {
        //redisTemplate.opsForValue().set("lua:key", "aaa");
        // 根据脚本文件位置创建ScriptSource对象
        ScriptSource scriptSource = new ResourceScriptSource(new ClassPathResource("lua/checkandset.lua"));
        // 根据脚本和返回值类型创建DefaultRedisScript对象，泛型定义为返回值类型
        DefaultRedisScript<Boolean> redisScript = new DefaultRedisScript<>(scriptSource.getScriptAsString(), Boolean.class);
        // 执行脚本
        ArrayList<String> keys = Lists.newArrayList("lua:key");
        Boolean result = redisTemplate.execute(redisScript, keys, "aaa", "bbb");
        System.out.println(result);
    }

    // 测试counterLimiter.lua脚本
    @Ignore
    @Test
    void testCounterLimiter() throws IOException {
        // 根据脚本文件位置创建ScriptSource对象
        ScriptSource scriptSource = new ResourceScriptSource(new ClassPathResource("lua/counterLimiter.lua"));
        // 根据脚本和返回值类型创建DefaultRedisScript对象，泛型定义为返回值类型
        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>(scriptSource.getScriptAsString(), Long.class);
        // 执行脚本
        ArrayList<String> keys = Lists.newArrayList("test");
        Long result = redisTemplate.execute(redisScript, keys, 2L, 10L);
        System.out.println(result);
    }


    // 测试 ResolvableType
    @Test
    @Ignore
    public void testResolvableType() throws NoSuchFieldException {
        ResolvableType t = ResolvableType.forField(getClass().getDeclaredField("redisTemplate"));
        t.getSuperType(); // AbstractMap<Integer, List<String>>
        t.asMap(); // Map<Integer, List<String>>
        t.getGeneric(0).resolve(); // Integer
        t.getGeneric(1).resolve(); // List
        t.getGeneric(1); // List<String>
        t.resolveGeneric(1, 0); // String
    }
}
