package com.joham.springbootdemo;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * @author joham
 */
@RestController
public class HelloWorldController {

    @RequestMapping("/hello")
    public String index() {
        return "Hello World";
    }

    /**
     * 内存溢出专用方法
     *
     * @return
     */
    @RequestMapping("/oom")
    @ResponseBody
    public String oom() {
        List<HelloWorldController> list = new ArrayList<>();
        while (true) {
            list.add(new HelloWorldController());
        }
    }
}
