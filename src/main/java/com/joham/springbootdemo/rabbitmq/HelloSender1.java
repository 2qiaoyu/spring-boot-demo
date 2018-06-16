package com.joham.springbootdemo.rabbitmq;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * 发送者
 *
 * @author joham
 */
@Component
public class HelloSender1 {

    @Autowired
    private AmqpTemplate rabbitTemplate;

    public void send(int i) {
        String context = "hello1 " + i;
        System.out.println("Sender1 : " + context);
        this.rabbitTemplate.convertAndSend("hello", context);
    }
}
