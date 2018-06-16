package com.joham.springbootdemo.rabbitmq.topic;

import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * 接受者
 *
 * @author joham
 */
@Component
@RabbitListener(queues = "topic.messages")
public class TopicReceiver1 {

    @RabbitHandler
    public void process(String message) {
        System.out.println("Topic Receiver1: " + message);
    }
}
