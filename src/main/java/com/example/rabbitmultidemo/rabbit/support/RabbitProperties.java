package com.example.rabbitmultidemo.rabbit.support;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "demo.rabbitmq")
public class RabbitProperties {
    private String host;
    private int port;
    private String username;
    private String password;
    private String virtualHost;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getVirtualHost() {
        return virtualHost;
    }

    public void setVirtualHost(String virtualHost) {
        this.virtualHost = virtualHost;
    }
}
