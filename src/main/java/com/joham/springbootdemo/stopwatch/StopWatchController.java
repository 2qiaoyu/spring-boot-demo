package com.joham.springbootdemo.stopwatch;

import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author qiaoyu
 */
@RequestMapping("/StopWatch")
@RestController
public class StopWatchController {

    @Autowired
    private StopWatchService stopWatchService;

    @RequestMapping("/")
    public void StopWatchTest() {
        StopWatch stopWatch = new StopWatch("stopwatch test");


        stopWatch.start("执行本地方法");
        stopWatchService.executeNative();
        stopWatch.stop();


        stopWatch.start("执行数据库操作");
        stopWatchService.executeDB();
        stopWatch.stop();


        stopWatch.start("执行远程调用");
        stopWatchService.executeRPC();
        stopWatch.stop();


        System.out.println(stopWatch.prettyPrint());

        System.out.println("\n");


        System.out.println(stopWatch.shortSummary());

        System.out.println("\n");


        System.out.println(JSON.toJSON(stopWatch.getTaskInfo()));
    }
}
