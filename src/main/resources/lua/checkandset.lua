-- checkandset.lua
-- 获取一个Key的值
local current = redis.call('GET', KEYS[1])
-- 如果这个值等于传入的第一个参数,设置这个Key的值为第二个参数
if current == ARGV[1] then
    redis.call('SET', KEYS[1], ARGV[2])
    return true
end
-- 如果这个值不等于传入的第一个参数，直接返回false
return false
