--- ip限制访问次数
--- KEYS[1] ip地址
--- ARGV[1] 过期时间
--- ARGV[2] 最大访问次数
--- 返回结果：0 表示超过最大次数，1 表示没有超过访问次数
local visitNum = redis.call('incr', KEYS[1])
if visitNum == 1 then
    redis.call('expire', KEYS[1], ARGV[1])
end
if visitNum > tonumber(ARGV[2]) then
    return 0
end
return 1;
