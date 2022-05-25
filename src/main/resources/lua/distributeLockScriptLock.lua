--- 分布式锁之 加锁用法
--- KEYS[1] string 分布式锁的key
--- ARGV[1] string  分布式锁的value
--- ARGV[2] int  分布式锁的value过期时间，默认单位(秒)

--- 返回：第一次：设置成功，返回1， 表示成功获取到锁
---      后面：过期时间范围内，返回0，表示锁已经被获取
if (redis.call('setnx', KEYS[1], ARGV[1]) < 1) then
    return 0;
end
redis.call('expire', KEYS[1], tonumber(ARGV[2]));
return 1;