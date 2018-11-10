package com.joham.springbootdemo.lock;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.UUID;
import java.util.concurrent.atomic.LongAdder;

/**
 * redis分布式锁测试
 *
 * @author joham
 */
@RequestMapping("/lock")
@RestController
public class LockController {
    private static LongAdder longAdder = new LongAdder();
    private static Long ACQUIRE_TIMEOUT_IN_MILLIS = (long) Integer.MAX_VALUE;
    private static Long stock = 100000L;
    private static DistributedLock lock;

    static {
        longAdder.add(stock);
    }

    private final JedisPool jedisPool;

    @Autowired
    public LockController(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
        lock = new DistributedLock(jedisPool, "seckillV2_" + UUID.randomUUID().toString());
    }

    @GetMapping("/v2/seckill")
    public String seckillV2() throws InterruptedException {

        boolean acquireResult = false;
        try {
            acquireResult = lock.acquire(ACQUIRE_TIMEOUT_IN_MILLIS);

            if (!acquireResult) {
                return "人太多了，换个姿势操作一下!";
            }

            if (longAdder.longValue() == 0L) {
                return "已抢光!";
            }

            doSomeThing(jedisPool);

            longAdder.decrement();

            System.out.println("已抢: " + (stock - longAdder.longValue()) + ", 还剩下: " + longAdder.longValue());

        } finally {
            if (acquireResult) {
                boolean releaseResult = lock.release();
                if (!releaseResult) {
                    System.out.println("释放锁失败！");
                }
            }
        }

        return "OK";
    }

    private void doSomeThing(JedisPool jedisPool) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();

            jedis.incr("already_bought");
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }
}
