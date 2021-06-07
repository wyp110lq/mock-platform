package com.yss.acs.mock.server.common.utils;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * 链接处理工具类
 *
 * @author jiayy
 * @date 2020/7/4
 */
public class ConnectionUtil {

    /**
     * 创建RabbitMq连接
     *
     * @param ip
     * @param port
     * @param username
     * @param password
     * @return
     */
    public static Connection getRabbitMqConnection(String ip, Integer port, String username, String password) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(ip);
        if (port != null) {
            factory.setPort(port);
        }
        if (!StringUtils.isEmpty(username)) {
            factory.setUsername(username);
        }
        if (!StringUtils.isEmpty(password)) {
            factory.setPassword(password);
        }
        factory.setConnectionTimeout(3000);
        return factory.newConnection();
    }
}
