package com.example.demo.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.*;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.data.redis.support.atomic.RedisAtomicLong;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class RedisService {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Resource
    private JedisConnectionFactory factory;

    public void setRedisTemplate(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * 模糊查询key
     *
     * @return 值集合
     */
    public Set<String> listKeys(final String key) {
        return redisTemplate.keys(key);
    }

    /**
     * 重命名
     */
    public void rename(final String oldKey, final String newKey) {
        redisTemplate.rename(oldKey, newKey);
    }

    /**
     * 模糊获取
     *
     * @param pattern 正则匹配符
     * @return 列表
     */
    public List<Object> listPattern(final String pattern) {
        List<Object> result = new ArrayList<>();
        Set<String> keys = redisTemplate.keys(pattern);
        assert keys != null;
        for (String str : keys) {
            ValueOperations<String, Object> operations = redisTemplate.opsForValue();
            Object obj = operations.get(str);
            if (!ObjectUtils.isEmpty(obj)) {
                result.add(obj);
            }
        }
        return result;
    }


    /**
     * 写入缓存
     *
     * @param key   键
     * @param value 值
     * @return 写入结果
     */
    public boolean set(final String key, Object value) {
        boolean result = false;
        try {
            ValueOperations<String, Object> operations = redisTemplate.opsForValue();
            operations.set(key, value);
            result = true;
        } catch (Exception e) {
            log.error("set fail ,key is:" + key, e);
        }
        return result;
    }


    /**
     * 批量写入缓存
     *
     * @param map Map集合
     * @return {@link Boolean}
     */
    public boolean multiSet(Map<String, Object> map) {
        boolean result = false;
        try {
            ValueOperations<String, Object> operations = redisTemplate.opsForValue();
            operations.multiSet(map);
            result = true;
        } catch (Exception e) {
            log.error("multiSet fail ", e);
        }
        return result;
    }

    /**
     * 队列出栈
     *
     * @param key 键
     */
    public Object leftPop(String key) {
        ListOperations<String, Object> stringObjectListOperations = redisTemplate.opsForList();
        return stringObjectListOperations.leftPop(key);
    }

    /**
     * 队列大小
     *
     * @param key 键
     * @return 值
     */
    public Object llen(final String key) {
        ListOperations<String, Object> stringObjectListOperations = this.redisTemplate.opsForList();
        return stringObjectListOperations.size(key);
    }

    /**
     * 写入缓存设置时效时间
     *
     * @param key        键
     * @param value      值
     * @param expireTime 过期时间，默认单位认s
     * @return {@link Boolean}
     */
    public boolean set(final String key, Object value, Long expireTime) {
        boolean result = false;
        try {
            ValueOperations<String, Object> operations = redisTemplate.opsForValue();
            operations.set(key, value);
            redisTemplate.expire(key, expireTime, TimeUnit.SECONDS);
            result = true;
        } catch (Exception e) {
            log.error("set fail ", e);
        }
        return result;
    }

    /**
     * 写入缓存设置时效时间
     *
     * @param key        键
     * @param value      值
     * @param expireTime 过期时间 单位秒
     * @return {@link Boolean}
     */
    public boolean setnx(final String key, Object value, Long expireTime) {
        Boolean res = false;
        try {
            ValueOperations<String, Object> operations = redisTemplate.opsForValue();
            res = operations.setIfAbsent(key, value);
            if (Boolean.TRUE.equals(res)) {
                res = redisTemplate.expire(key, expireTime, TimeUnit.SECONDS);
            }
        } catch (Exception e) {
            log.error("setnx fail ", e);
        }
        return Boolean.TRUE.equals(res);
    }

    /**
     * 缓存设置时效时间
     *
     * @param key        键
     * @param expireTime 过期时间，默认单位s
     * @return {@link Void}
     */
    public void expire(final String key, Long expireTime) {
        redisTemplate.expire(key, expireTime, TimeUnit.SECONDS);
    }


    /**
     * 自增操作
     *
     * @param key
     * @return
     */
    public long incr(final String key) {
        RedisAtomicLong entityIdCounter = new RedisAtomicLong(key, redisTemplate.getConnectionFactory());
        return entityIdCounter.getAndIncrement();

    }


    /**
     * 批量删除
     *
     * @param keys
     */
    public void removeKeys(final List<String> keys) {
        if (keys.size() > 0) {
            redisTemplate.delete(keys);
        }
    }

    /**
     * 批量删除key
     *
     * @param pattern 正则表达式
     */
    public void removePattern(final String pattern) {
        Set<String> keys = redisTemplate.keys(pattern);
        if (!CollectionUtils.isEmpty(keys)) {
            redisTemplate.delete(keys);
        }
    }

    /**
     * 删除对应的value
     *
     * @param key
     */
    public void remove(final String key) {
        if (exists(key)) {
            redisTemplate.delete(key);
        }
    }

    /**
     * 判断缓存中是否有对应的value
     *
     * @param key
     * @return
     */
    public boolean exists(final String key) {
        return redisTemplate.hasKey(key);
    }

    /**
     * 判断缓存中是否有对应的value(模糊匹配)
     *
     * @param pattern
     * @return
     */
    public boolean existsPattern(final String pattern) {
        if (redisTemplate.keys(pattern).size() > 0) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 读取缓存
     *
     * @param key
     * @return
     */
    public Object get(final String key) {
        ValueOperations<String, Object> operations = redisTemplate.opsForValue();
        return operations.get(key);
    }

    /**
     * 哈希 添加
     */
    public void hmSet(String key, Object hashKey, Object value) {
        HashOperations<String, Object, Object> hash = redisTemplate.opsForHash();
        hash.put(key, hashKey, value);
    }

    /**
     * 哈希 添加
     */
    public Boolean hmSet(String key, Object hashKey, Object value, Long expireTime, TimeUnit timeUnit) {
        HashOperations<String, Object, Object> hash = redisTemplate.opsForHash();
        hash.put(key, hashKey, value);
        return redisTemplate.expire(key, expireTime, timeUnit);
    }

    /**
     * 哈希获取数据
     *
     * @param key
     * @param hashKey
     * @return
     */
    public Object hmGet(String key, Object hashKey) {
        HashOperations<String, Object, Object> hash = redisTemplate.opsForHash();
        return hash.get(key, hashKey);
    }

    /**
     * 哈希获取所有数据
     *
     * @param key
     * @return
     */
    public Object hmGetValues(String key) {
        HashOperations<String, Object, Object> hash = redisTemplate.opsForHash();
        return hash.values(key);
    }

    /**
     * 哈希获取所有键值
     *
     * @param key
     * @return
     */
    public Object hmGetKeys(String key) {
        HashOperations<String, Object, Object> hash = redisTemplate.opsForHash();
        return hash.keys(key);
    }

    /**
     * 哈希获取所有键值对
     *
     * @param key
     * @return
     */
    public Object hmGetMap(String key) {
        HashOperations<String, Object, Object> hash = redisTemplate.opsForHash();
        return hash.entries(key);
    }

    /**
     * 哈希 删除域
     *
     * @param key
     * @param hashKey
     */
    public Long hdel(String key, Object hashKey) {
        HashOperations<String, Object, Object> hash = redisTemplate.opsForHash();
        return hash.delete(key, hashKey);
    }

    /**
     * 列表添加
     *
     * @param k
     * @param v
     */
    public void rPush(String k, Object v) {
        ListOperations<String, Object> list = redisTemplate.opsForList();
        list.rightPush(k, v);
    }

    /**
     * 列表删除
     *
     * @param k
     * @param v
     */
    public void listRemove(String k, Object v) {
        ListOperations<String, Object> list = redisTemplate.opsForList();
        list.remove(k, 1, v);
    }

    public void rPushAll(String k, Collection var2) {
        ListOperations<String, Object> list = redisTemplate.opsForList();
        list.rightPushAll(k, var2);
    }


    /**
     * 列表获取
     *
     * @param k
     * @param begin
     * @param end
     * @return
     */
    public Object lRange(String k, long begin, long end) {
        ListOperations<String, Object> list = redisTemplate.opsForList();
        return list.range(k, begin, end);
    }

    /**
     * 集合添加
     *
     * @param key   键
     * @param value 值
     */
    public void add(String key, Object value) {
        SetOperations<String, Object> set = redisTemplate.opsForSet();
        set.add(key, value);
    }


    /**
     * 判断元素是否在集合中
     *
     * @param key   键
     * @param value 值
     */
    public Boolean isMember(String key, Object value) {
        SetOperations<String, Object> set = redisTemplate.opsForSet();
        return set.isMember(key, value);
    }


    /**
     * 集合获取
     *
     * @param key 键
     * @return 集合对象
     */
    public Set<Object> setMembers(String key) {
        SetOperations<String, Object> set = redisTemplate.opsForSet();
        return set.members(key);
    }

    /**
     * 有序集合添加
     *
     * @param key    键
     * @param value  值
     * @param scoure 排名的字段
     */
    public void zAdd(String key, Object value, double scoure) {
        ZSetOperations<String, Object> zset = redisTemplate.opsForZSet();
        zset.add(key, value, scoure);
    }

    /**
     * 有序集合获取
     *
     * @param key     键
     * @param scoure  min
     * @param scoure1 max
     * @return 集合对象
     */
    public Set<Object> rangeByScore(String key, double scoure, double scoure1) {
        ZSetOperations<String, Object> zset = redisTemplate.opsForZSet();
        return zset.rangeByScore(key, scoure, scoure1);
    }

    /**
     * 有序集合根据区间删除
     *
     * @param key     键
     * @param scoure  区间开始值 min
     * @param scoure1 区间结束值 max
     * @return 无返回
     */
    public void removeRangeByScore(String key, double scoure, double scoure1) {
        ZSetOperations<String, Object> zset = redisTemplate.opsForZSet();
        zset.removeRangeByScore(key, scoure, scoure1);
    }

    /**
     * 列表添加
     *
     * @param k 键
     * @param v 值
     */
    public void lPush(String k, Object v) {
        ListOperations<String, Object> list = redisTemplate.opsForList();
        list.rightPush(k, v);
    }

    /**
     * 获取当前key的超时时间
     *
     * @param key 键
     * @return {@link Long} 单位s
     */
    public Long getExpireTime(final String key) {
        return redisTemplate.opsForValue().getOperations().getExpire(key, TimeUnit.SECONDS);
    }

    public Long extendExpireTime(final String key, Long extendTime) {
        Long curTime = redisTemplate.opsForValue().getOperations().getExpire(key, TimeUnit.SECONDS);
        long total = curTime.longValue() + extendTime;
        redisTemplate.expire(key, total, TimeUnit.SECONDS);
        return total;
    }

    public Set getKeys(String k) {
        return redisTemplate.keys(k);
    }

    /**
     * 执行脚本 (注意：key 和 value 的序列化方式)
     * 注意：传入参数key 和 value 都要转换成字符串
     *
     * @param redisScript 脚本返回结果类型
     * @param redisScript lua脚本字符串和返回数据格式
     * @param keys        键值列表
     * @param args        参数列表
     * @return lua脚本执行返回的参数
     */
    public <T> T execute(RedisScript<T> redisScript, List<String> keys, Object... args) {
        RedisTemplate<String, String> redisTemplateTemp = new RedisTemplate<>();
        redisTemplateTemp.setConnectionFactory(factory);
        redisTemplateTemp.setKeySerializer(new StringRedisSerializer());
        redisTemplateTemp.setValueSerializer(redisTemplateTemp.getKeySerializer());
        redisTemplateTemp.afterPropertiesSet();
        return redisTemplateTemp.execute(redisScript, keys, args);
    }


}
