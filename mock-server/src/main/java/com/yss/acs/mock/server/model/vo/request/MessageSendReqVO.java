package com.yss.acs.mock.server.model.vo.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 消息发送参数
 *
 * @author jiayy
 * @date 2020/7/4
 */
@Data
public class MessageSendReqVO {

    /**
     * 服务器ip
     */
    @NotBlank(message = "服务器ip不能为空")
    private String ip;

    /**
     * 服务器端口
     */
    @NotNull(message = "服务器端口不能为空")
    private Integer port;

    /**
     * 用户名
     */
    private String userName;

    /**
     * 密码
     */
    private String password;

    /**
     * mq类型
     */
    @NotBlank(message = "MQ类型不能为空")
    private String mqType;

    /**
     * RabbitMQ-队列名称
     */
    private String queueName;

    /**
     * RocketMQ-Topic名称
     */
    private String topicName;

    /**
     * RocketMQ-Tag名称
     */
    private String tagName;

    /**
     * Kafka-Topic名称
     */
    private String kafkaTopic;

    /**
     * 数据类型
     */
    private String msgType;
    /**
     * 消息内容
     */
    @NotBlank(message = "消息内容不能为空")
    private String msg;
}
