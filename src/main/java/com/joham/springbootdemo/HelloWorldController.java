package com.joham.springbootdemo;

import com.google.common.collect.Maps;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author joham
 */
@RestController
public class HelloWorldController {

    private ExecutorService executor = Executors.newSingleThreadExecutor();
    private static Map<String, HelloWorldController> map = Maps.newConcurrentMap();

    @RequestMapping("/hello")
    public String index() {
        return "Hello World";
    }

    @RequestMapping("/test")
    public void test() {
        while (true) {
            HelloWorldController ct1 = new HelloWorldController();
            map.put("1", ct1);
            ct1.submitTask();
            map.remove("1");
        }
    }

    public void submitTask() {
        for (int i = 0; i < 10; i++) {
            executor.submit(new Runnable() {
                @Override
                public void run() {
                    System.out.println(Thread.currentThread().getName() + "正处理");
                }
            });
        }
    }
}
