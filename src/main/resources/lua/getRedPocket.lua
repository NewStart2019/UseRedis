--- -1 已经抢到红包   -2 红包被抢光
--- re 红包金额
--- keys[1] 存储小红包的key
--- keys[2]  红包领取记录key
--- keys[3]  用户id
if redis.call('hexists', KEYS[2], ARGV[1]) ~= 0 then
    return '-1';
else
    local re = redis.call('rpop', KEYS[1]);
    if re then
        redis.call('hset', KEYS[2], ARGV[1], 1);
        return re;
    else
        return '-2';
    end
end
