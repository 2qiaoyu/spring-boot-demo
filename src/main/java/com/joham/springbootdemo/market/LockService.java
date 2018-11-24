package com.joham.springbootdemo.market;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

/**
 * 分布式锁
 *
 * @author joham
 */
@Service
public class LockService {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 获取锁
     *
     * @param lockName
     * @return
     */
    public String acquireLock(String lockName) {
        long acquireTimeout = 10000;
        //随机128位UUID作为键的值
        String identifier = UUID.randomUUID().toString();
        final byte[] rawKey = stringRedisTemplate.getStringSerializer().serialize("lock:" + lockName);
        final byte[] rawVal = stringRedisTemplate.getStringSerializer().serialize(identifier);
        long end = System.currentTimeMillis() + acquireTimeout;
        //10秒内获取不到锁就返回
        while (System.currentTimeMillis() < end) {
            RedisCallback<Boolean> redisCallback = redisConnection ->
                    //setnx命令的语义是将key的值设为value，当且仅当key不存在若key存在，不做任何动作，返回0(false)
                    redisConnection.setNX(rawKey, rawVal);
            if (stringRedisTemplate.execute(redisCallback)) {
                return identifier;
            }
        }
        return null;
    }

    /**
     * 加锁版购买商品
     *
     * @param buyerId
     * @param itemId
     * @param sellerId
     * @return
     */
    public boolean purchaseItemWithLock(String buyerId, String itemId, String sellerId) {
        String locked = acquireLock("market");
        if (locked == null) {
            return false;
        } else {
            try {
                System.out.println("purchasing....");
                return true;
            } finally {
                //release lock
                System.out.println("release lock");
                releaseLock("market", locked);
            }
        }
    }

    public boolean purchaseItemWithTimeLock(String buyerId, String itemId, String sellerId) {
        String locked = acquireLockWithTimeout("market");
        if (locked == null) {
            return false;
        } else {
            try {
                System.out.println("purchasing....");
                return true;
            } finally {
                //release lock
                System.out.println("release lock");
                releaseLock("market", locked);
            }
        }
    }

    public boolean releaseLock(String lockName, String identifier) {
        String lockKey = "lock:" + lockName;
        SessionCallback<List<Object>> sessionCallback = new SessionCallback<List<Object>>() {
            @Override
            public List<Object> execute(RedisOperations operations) throws DataAccessException {
                operations.watch(lockKey);
                if (identifier.equals(operations.opsForValue().get(lockKey))) {
                    operations.multi();
                    operations.delete(lockKey);
                    return operations.exec();
                }
                //若取出来的不是想要释放的锁，不作任何操作，返回空
                operations.unwatch();
                return null;
            }
        };
        List<Object> results = stringRedisTemplate.execute(sessionCallback);
        return results != null;
    }

    /**
     * 超时锁
     *
     * @param lockName
     * @return
     */
    public String acquireLockWithTimeout(String lockName) {
        //10秒内获取不到锁就返回
        long acquireTimeout = 10000;
        //20秒后锁超时
        long lockTimeout = 20000;
        String identifier = UUID.randomUUID().toString();
        final byte[] rawKey = stringRedisTemplate.getStringSerializer().serialize("lock:" + lockName);
        final byte[] rawVal = stringRedisTemplate.getStringSerializer().serialize(identifier);
        long end = System.currentTimeMillis() + acquireTimeout;
        while (System.currentTimeMillis() < end) {
            RedisCallback<Boolean> redisCallback = redisConnection -> {
                //成功获取锁之后设置锁超时时间
                if (redisConnection.setNX(rawKey, rawVal)) {
                    redisConnection.expire(rawKey, lockTimeout);
                    return true;
                }
                //锁获取失败之后检测锁是否有超时时间，如果没有则设置超时时间这是为了防止程序在setNX和expire之间崩溃
                if (redisConnection.ttl(rawKey) == -1) {
                    redisConnection.expire(rawKey, lockTimeout);
                    return false;
                }
                return false;
            };
            if (stringRedisTemplate.execute(redisCallback)) {
                return identifier;
            }
        }
        return null;
    }
}
