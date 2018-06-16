package com.joham.springbootdemo.stopwatch;

import org.springframework.stereotype.Service;

/**
 * @author qiaoyu
 */
@Service
public class StopWatchService {

    /**
     * 延时的方法
     *
     * @param time 延时时常，单位毫秒
     */
    public void delay(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 假设这个方法在执行本地调用，耗时100毫秒
     */
    public void executeNative() {
        delay(100);
    }

    /**
     * 假设这个方法在执行数据库操作，耗时200毫秒
     */
    public void executeDB() {
        delay(200);
    }

    /**
     * 假设这个方法在执行远程调用，耗时300毫秒
     */
    public void executeRPC() {
        delay(300);
    }
}
