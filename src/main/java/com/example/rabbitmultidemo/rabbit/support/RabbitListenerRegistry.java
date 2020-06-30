package com.example.rabbitmultidemo.rabbit.support;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.MessageListener;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerEndpoint;
import org.springframework.amqp.rabbit.listener.RabbitListenerEndpointRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * RabbitMQ监听注册类
 */
@Component
public class RabbitListenerRegistry {
    Logger logger = LoggerFactory.getLogger(getClass());

    @Resource
    RabbitListenerEndpointRegistry rabbitListenerEndpointRegistry;

    @Autowired
    private RabbitContext rabbitContext;

    public void registerListenerContainer(String virtualHost, String queueName, MessageListener messageListener) {
        SimpleRabbitListenerContainerFactory listenerContainerFactory = rabbitContext.getListenerContainerFactory(virtualHost);
        if (listenerContainerFactory == null) {
            // 添加监听前，需要先注册对应的连接
            logger.warn("[registerListenerContainer] listenerContainerFactory(virtualHost = {}) is null", virtualHost);
            return;
        }
        SimpleRabbitListenerEndpoint endpoint = new SimpleRabbitListenerEndpoint();
        endpoint.setId(queueName + "-" + virtualHost);
        endpoint.setQueueNames(queueName);
        endpoint.setMessageListener(messageListener);
        rabbitListenerEndpointRegistry.registerListenerContainer(endpoint, listenerContainerFactory, true);
    }
}
