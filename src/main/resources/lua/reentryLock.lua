--- 重入锁：当锁不存在时，设置锁的值为集合，然后值为线程标识符，和 重入次数为1
---    再次加锁 则 设置自增 和 重新设置过期时间
---     加锁失败则返回过期时间

---  KEYS[1] string 键值
---  ARGV[1] int 过期时间
---  ARGV[2] String 线程标识

--- 如果 lock_key 不存在
if (redis.call('exists', KEYS[1]) == 0) then
    ---设置 lock_key 线程标识 1 进行加锁
    redis.call('hset', KEYS[1], ARGV[2], 1);
    --- 设置过期时间
    redis.call('pexpire', KEYS[1], ARGV[1]);
    return nil;
end ;
--- 如果 lock_key 存在且线程标识是当前欲加锁的线程标识 自增
if (redis.call('hexists', KEYS[1], ARGV[2]) == 1) then
    redis.call('hincrby', KEYS[1], ARGV[2], 1);
    --- 重置过期时间
    redis.call('pexpire', KEYS[1], ARGV[1]);
    return nil;
end ;
--- 如果加锁失败，返回锁剩余时间
return redis.call('pttl', KEYS[1]);