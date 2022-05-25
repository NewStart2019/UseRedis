--- lua脚本：判断锁住值是否为当前线程持有，是的话解锁，不是的话解锁失败
if redis.call('get', KEYS[1]) == ARGV[1] then
    return redis.call('del', KEYS[1])
else
    return 0
end