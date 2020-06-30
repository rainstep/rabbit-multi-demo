package com.example.rabbitmultidemo.controller;

import com.example.rabbitmultidemo.rabbit.support.RabbitContext;
import com.example.rabbitmultidemo.rabbit.support.RabbitListenerRegistry;
import com.example.rabbitmultidemo.rabbit.support.RabbitProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/rabbit")
public class RabbitController {
    @Autowired
    private RabbitContext rabbitContext;
    @Autowired
    private RabbitListenerRegistry rabbitListenerRegistry;

    @Autowired
    private RabbitProperties rabbitProperties;


    @GetMapping("/register")
    public void register(String virtualHost) {
        rabbitProperties.setVirtualHost(virtualHost);
        rabbitContext.register(rabbitProperties);
    }

    @GetMapping("/send")
    public void send(String virtualHost, String exchange, String routeKey, String message) {
        RabbitTemplate rabbitTemplate = rabbitContext.getRabbitTemplate(virtualHost);
        rabbitTemplate.convertAndSend(exchange, routeKey, message);
    }

    @GetMapping("/receive")
    public void receive(String virtualHost, String queueName) {
        rabbitListenerRegistry.registerListenerContainer(virtualHost, queueName, message -> {
            System.out.println("[" + virtualHost + "]["+queueName+"]: " + message.getBody());
        });

    }

}
