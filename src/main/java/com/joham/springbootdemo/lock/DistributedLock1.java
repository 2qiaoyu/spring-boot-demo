package com.joham.springbootdemo.lock;

import com.alibaba.fastjson.JSON;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * <p>
 * 基于redis的分布式锁 v1
 * <p>
 * 需要客户端时间同步
 *
 * @author joham
 */
public class DistributedLock1 {

    /**
     * 请求锁重试屏障，单位毫秒
     */
    private static final long RETRY_BARRIER = 3 * 1000;

    /**
     * redis连接池
     */
    private final JedisPool jedisPool;

    /**
     * lock Key
     */
    private final String lockKey;

    /**
     * 锁的过期时长，单位纳秒
     */
    private final long lockExpiryInNanos;

    private static final ThreadLocal<Lock> lockThreadLocal = new ThreadLocal<Lock>();

    /**
     * 构造方法
     *
     * @param jedisPool          redis连接池
     * @param lockKey            锁的Key
     * @param lockExpiryInMillis 锁的过期时长，单位毫秒
     */
    public DistributedLock1(JedisPool jedisPool, String lockKey, long lockExpiryInMillis) {
        this.jedisPool = jedisPool;
        this.lockKey = lockKey;
        this.lockExpiryInNanos = lockExpiryInMillis * 1000;
    }

    /**
     * 构造方法
     * <p>
     * 使用锁默认的过期时长Integer.MAX_VALUE，即锁永远不会过期
     *
     * @param jedisPool redis连接池
     * @param lockKey   锁的Key
     */
    public DistributedLock1(JedisPool jedisPool, String lockKey) {
        this(jedisPool, lockKey, Integer.MAX_VALUE);
    }

    /**
     * 获取锁在redis中的Key标记
     *
     * @return locks key
     */
    public String getLockKey() {
        return this.lockKey;
    }

    /**
     * 锁的过期时长
     *
     * @return
     */
    public long getLockExpiryInNanos() {
        return lockExpiryInNanos;
    }

    /**
     * 请求分布式锁，不会阻塞，直接返回
     *
     * @param jedis redis 连接
     * @return 成功获取锁返回true, 否则返回false
     */
    private boolean tryAcquire(Jedis jedis) {

        final Lock newLock = new Lock(System.nanoTime() + this.lockExpiryInNanos);

        /**
         * 将新锁(newLock)写入redis中。如果成功写入，redis中不存在锁，获取锁成功；否则，redis中已存在锁，获取锁失败；
         */
        if (jedis.setnx(this.lockKey, newLock.toString()) == 1) {
            lockThreadLocal.set(newLock);
            return true;
        }

        /**
         * 至此，说明redis中已存在锁，获取锁失败，则需要进行如下操作:
         * 1. 判断redis中已存在的锁是否过期，如果过期则直接获取锁；
         * 2. 否则，获取锁失败；
         */

        final String currentLockValue = jedis.get(lockKey);
        // 特别的，当jedis.get()获取已存在的锁currentLockValue为空时，应该重新SETNX
        if (currentLockValue == null || currentLockValue.length() == 0) {
            tryAcquire(jedis);
        }
        // redis中已存在的锁
        final Lock currentLock = Lock.fromJson(currentLockValue);

        // 如果redis中已存在的锁已超时，则重新获取锁
        if (isExpired(currentLock)) {
            String originLockValue = jedis.getSet(lockKey, newLock.toString());

            /**
             * 这里还有个前置条件:
             *      会对已存在的锁进行校验，jedis.get()和jedis.getSet()获取的锁必须是同一锁，重新获取锁才成功
             */

            // 特别的，当jedis.getSet()获取已存在的锁originLockValue为空时，则认定获取锁成功
            if (originLockValue == null || originLockValue.length() == 0) {
                lockThreadLocal.set(newLock);
                return true;
            }

            if (originLockValue.equals(currentLockValue)) {
                lockThreadLocal.set(newLock);
                return true;
            }
        }

        return false;
    }

    /**
     * 请求分布式锁，不会阻塞，直接返回
     *
     * @return 成功获取锁返回true, 否则返回false
     */
    public boolean tryAcquire() {

        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return tryAcquire(jedis);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    /**
     * 超时请求分布式锁，会阻塞
     * <p>
     * 采用"自旋获取锁"的方式，直至获取锁成功或者请求锁超时
     *
     * @param acquireTimeoutInMillis 锁的请求超时时长
     * @return
     */
    public boolean acquire(long acquireTimeoutInMillis) {

        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();

            long acquireTime = System.currentTimeMillis();

            // 锁的请求到期时间
            long expiryTime = System.currentTimeMillis() + acquireTimeoutInMillis;

            while (expiryTime >= System.currentTimeMillis()) {
                boolean result = tryAcquire(jedis);
                // 获取锁成功直接返回，否则循环重试
                if (result) {
                    return true;
                }

                if ((System.currentTimeMillis() - acquireTime) > RETRY_BARRIER) {
                    Thread.yield();
                }
            }

        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return false;
    }

    /**
     * 释放锁
     */
    public void release() {

        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            release(jedis);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    /**
     * 释放锁
     *
     * @param jedis
     */
    private void release(Jedis jedis) {
        Lock currlock = lockThreadLocal.get();
        if (currlock != null) {
            final String currentLockValue = jedis.get(lockKey);
            if (currentLockValue != null && currentLockValue.length() != 0) {
                // redis中已存在的锁
                final Lock currentLock = Lock.fromJson(currentLockValue);
                if (currlock.equals(currentLock)) {
                    lockThreadLocal.remove();
                    jedis.del(lockKey);
                }
            }
        }
    }

    /**
     * 判断当前线程是否持有锁
     * <p>
     * 未持有锁或者锁超时，返回false
     *
     * @return
     */
    public boolean isLocked() {
        Lock currlock = lockThreadLocal.get();
        // 如果当前线程保存的lock不为null，并且未超时，则当前线程必然持有锁，锁未被意外释放
        return currlock != null && !currlock.isExpired();
    }

    /**
     * 判断指定的lock是否是当前线程持有的锁
     *
     * @return
     */
    boolean isMine(final Lock lock) {
        Lock currlock = lockThreadLocal.get();
        return currlock != null && currlock.equals(lock);
    }

    /**
     * 判断锁是否超时
     *
     * @param lock
     * @return
     */
    boolean isExpired(final Lock lock) {
        return lock.isExpired();
    }

    /**
     * 锁
     */
    protected static class Lock {

        /**
         * 锁的过期时间，注意，不是过期时长，单位纳秒
         */
        private long expiryTime;

        Lock(long expiryTime) {
            this.expiryTime = expiryTime;
        }

        /**
         * 解析字符串，根据解析出的过期时间构造Lock
         *
         * @param json
         * @return
         */
        static Lock fromJson(String json) {
            return JSON.parseObject(json, Lock.class);
        }

        @Override
        public String toString() {
            return JSON.toJSONString(this, false);
        }

        public long getExpiryTime() {
            return expiryTime;
        }

        /**
         * 判断锁是否超时，如果锁的过期时间小于当前系统时间，则判定锁超时
         *
         * @return
         */
        boolean isExpired() {
            return this.expiryTime < System.nanoTime();
        }

        @Override
        public boolean equals(Object obj) {
            return obj != null
                    && obj instanceof Lock
                    && this.expiryTime == ((Lock) obj).getExpiryTime();
        }
    }
}
