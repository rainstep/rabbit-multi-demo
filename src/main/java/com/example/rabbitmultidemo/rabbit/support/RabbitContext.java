package com.example.rabbitmultidemo.rabbit.support;

import com.example.rabbitmultidemo.util.RabbitUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class RabbitContext implements BeanDefinitionRegistryPostProcessor {
    Logger logger = LoggerFactory.getLogger(getClass());

    // 已注册的rabbitmq连接工厂
    private Map<String, ConnectionFactory> connectionFactoryMap = new HashMap<>();

    // 已注册的rabbitmq监听容器工厂
    private Map<String, SimpleRabbitListenerContainerFactory> listenerContainerFactoryMap = new HashMap<>();

    // 已注册的RabbitTemplate
    private Map<String, RabbitTemplate> rabbitTemplateMap = new HashMap<>();

    private ConfigurableListableBeanFactory beanFactory;

    public void register(RabbitProperties rabbitProperties) {
        String virtualHost = rabbitProperties.getVirtualHost();
        // ConnectionFactory
        ConnectionFactory connectionFactory = registerConnectFactory(rabbitProperties);
        if (connectionFactory != null) {
            connectionFactoryMap.put(virtualHost, connectionFactory);

            // ListenerContainerFactory
            SimpleRabbitListenerContainerFactory listenerContainerFactory = registerListenerContainerFactory(connectionFactory);
            if (listenerContainerFactory != null) {
                listenerContainerFactoryMap.put(virtualHost, listenerContainerFactory);
            }
            // RabbitTemplate
            RabbitTemplate rabbitTemplate = registerRabbitTemplate(connectionFactory);
            if (rabbitTemplate != null) {
                rabbitTemplateMap.put(virtualHost, rabbitTemplate);
            }
        }
    }

    public ConnectionFactory registerConnectFactory(RabbitProperties rabbitProperties) {
        String beanName = "rabbitConnectionFactory" + rabbitProperties.getVirtualHost();
        if (beanFactory.containsBean(beanName)) {
            logger.warn("{} existed", beanName);
            return null;
        }
        ConnectionFactory connectionFactory = RabbitUtils.createConnectionFactory(rabbitProperties.getHost(), rabbitProperties.getPort(),
                rabbitProperties.getUsername(), rabbitProperties.getPassword(), rabbitProperties.getVirtualHost());
        beanFactory.registerSingleton(beanName, connectionFactory);
        return connectionFactory;
    }

    public ConnectionFactory getConnectionFactory(String virtualHost) {
        return connectionFactoryMap.get(virtualHost);
    }

    public SimpleRabbitListenerContainerFactory getListenerContainerFactory(String virtualHost) {
        return listenerContainerFactoryMap.get(virtualHost);
    }

    public RabbitTemplate getRabbitTemplate(String virtualHost) {
        return rabbitTemplateMap.get(virtualHost);
    }


    public SimpleRabbitListenerContainerFactory registerListenerContainerFactory(ConnectionFactory connectionFactory) {
        if (connectionFactory == null) {
            logger.error("[registerListenerContainerFactory] ConnectionFactory is null");
            return null;
        }
        String beanName = "rabbitListenerContainerFactory" + connectionFactory.getVirtualHost();
        if (beanFactory.containsBean(beanName)) {
            logger.warn("{} existed", beanName);
            return null;
        }
        SimpleRabbitListenerContainerFactory listenerContainerFactor = RabbitUtils.createListenerContainerFactor(connectionFactory);
        beanFactory.registerSingleton(beanName, listenerContainerFactor);
        return listenerContainerFactor;
    }

    public RabbitTemplate registerRabbitTemplate(ConnectionFactory connectionFactory) {
        String beanName = "rabbitTemplate" + connectionFactory.getVirtualHost();
        if (beanFactory.containsBean(beanName)) {
            logger.warn("{} existed", beanName);
            return null;
        }
        RabbitTemplate rabbitTemplate = RabbitUtils.createRabbitTemplate(connectionFactory);
        beanFactory.registerSingleton(beanName, rabbitTemplate);
        return rabbitTemplate;
    }


    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry beanDefinitionRegistry) throws BeansException {

    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }
}
