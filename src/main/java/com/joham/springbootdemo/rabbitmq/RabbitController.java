package com.joham.springbootdemo.rabbitmq;

import com.joham.springbootdemo.User;
import com.joham.springbootdemo.rabbitmq.fanout.FanoutSender;
import com.joham.springbootdemo.rabbitmq.topic.TopicSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * 测试
 *
 * @author joham
 */
@RestController
@RequestMapping("/rabbit")
public class RabbitController {

    @Autowired
    private HelloSender helloSender;

    @Autowired
    private HelloSender1 helloSender1;

    @Autowired
    private TopicSender topicSender;

    @Autowired
    private FanoutSender fanoutSender;

    @RequestMapping(value = "/test", method = RequestMethod.GET)
    public String test() {
        helloSender.send(100);
        return "success";
    }

    @RequestMapping(value = "/many", method = RequestMethod.GET)
    public String many() {
        for (int i = 0; i < 10; i++) {
            helloSender.send(i);
            helloSender1.send(i);
        }
        return "success";
    }

    @RequestMapping(value = "/object", method = RequestMethod.GET)
    public String object() {
        User user = new User();
        user.setId(1L);
        user.setNickName("小明");
        helloSender.send(user);
        return "success";
    }


    @RequestMapping(value = "/topic", method = RequestMethod.GET)
    public String topic() {
        topicSender.send1();
        topicSender.send2();
        return "success";
    }

    @RequestMapping(value = "/fanout", method = RequestMethod.GET)
    public String fanoutSender() {
        fanoutSender.send();
        return "success";
    }
}
