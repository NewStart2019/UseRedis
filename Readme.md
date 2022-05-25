## 使用技术：
    redis-cluster、springfox-swagger2、Aop编程、lua脚本

## 功能：
    * 1、redis+lua实现 限流，可以自定义注解限流
        限流方式支持：计数器限流 和 令牌桶限
    * 2、redis分布式锁应用
        方法一：加锁命令：SETNX key value，当键不存在时，对键进行设置操作并返回成功，否则返回失败。KEY 是锁的唯一标识，一般按业务来决定命名。
        解锁命令：DEL key，通过删除键值对释放锁，以便其他线程可以通过 SETNX 命令来获取锁。
        锁超时：EXPIRE key timeout, 设置 key 的超时时间，以保证即使锁没有被显式释放，锁也可以在一定时间后自动释放，避免资源被永远锁住。
        加锁解锁伪代码：
            if (setnx(key, 1) == 1){
                expire(key, 30)
                try {
                    //TODO 业务逻辑
                } finally {
                    del(key)
                }
            }
            缺点：当执行完，加锁命令后，redis奔溃、重启或网络问题，没有设置过期时间，会导致死锁
        方法二：使用lua脚本同时执行加锁 和 设置过期时间：参考 lua/distributeLockScriptLock.lua 脚本
            缺点：A线程获取到锁之后，B线程获取锁失败，然后把锁释放。导致A线程持有的锁被B线程释放掉了。
            解决办法：可生成一个 UUID 标识当前线程，使用 lua 脚本做验证标识和解锁操作
                解锁脚本参考：lua/distributeLockScriptUnlockVal.lua 
                // 加锁
                String uuid = UUID.randomUUID().toString().replaceAll("-","");
                SET key uuid NX EX 30
                // 解锁
                if (redis.call('get', KEYS[1]) == ARGV[1])
                    then return redis.call('del', KEYS[1])
                else return 0
                end
            缺点：超时时间过短，导致A、B线程同时执行业务
            解决办法：
                 ①将过期时间设置足够长，确保代码逻辑在锁释放之前能够执行完成。
                 ②为获取锁的线程增加守护线程，为将要过期但未释放的锁增加有效时间。
        方法三：不可重入，线程持有锁的情况下不允许再次请求锁。
            Redis 可通过对锁进行重入计数，加锁时加 1，解锁时减 1，当计数归 0 时释放锁。
            如果一个锁支持一个线程多次加锁，那么这个锁就是可重入的。
            脚本参考：lua/distributeLockScriptUnlockVal.lua 
         方法四：解决无法等待获取锁
            使用 Redis 的发布订阅功能，当获取锁失败时，订阅锁释放消息，获取锁成功后释放时，发送锁释放消息。
         还有：主备切换也会导致锁丢失
               集群脑裂会（sentinel无法感知到master节点把从节点提升为主节点）导致两个客户端拥有同一把锁
    * 3、IP限流
    