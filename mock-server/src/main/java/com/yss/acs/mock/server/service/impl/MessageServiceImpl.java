package com.yss.acs.mock.server.service.impl;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.yss.acs.mock.server.common.enums.MqType;
import com.yss.acs.mock.server.common.exception.MockException;
import com.yss.acs.mock.server.common.utils.CommonUtil;
import com.yss.acs.mock.server.common.utils.ConnectionUtil;
import com.yss.acs.mock.server.model.vo.request.MessageSendReqVO;
import com.yss.acs.mock.server.service.IMessageService;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.IntegerSerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.exception.RemotingConnectException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.*;

/**
 * 发送消息实现类
 *
 * @author jiayy
 * @date 2020/7/4
 */
@Slf4j
@Service
public class MessageServiceImpl implements IMessageService {

    private static final String ERROR_MSG = "连接失败，请检查连接信息是否正确！";

    @Override
    public void sendMq(MessageSendReqVO param) {
        MqType curType = MqType.valueOf(param.getMqType());
        switch (curType) {
            case RabbitMQ:
                sendRabbitMQ(param);
                return;
            case RocketMQ:
                sendRocketMQ(param);
                return;
            case Kafka:
                sendKafka(param);
                return;
            default:
                throw new MockException("暂不支持此类型");
        }

    }

    /**
     * 发送RabbitMQ
     *
     * @param param
     */
    private void sendRabbitMQ(MessageSendReqVO param) {
        if (StringUtils.isEmpty(param.getQueueName())) {
            throw new MockException("队列名称不能为空");
        }
        param.setQueueName(param.getQueueName().trim());

        Connection connection = null;
        Channel channel = null;
        try {
            //创建连接
            connection = ConnectionUtil.getRabbitMqConnection(param.getIp().trim(), param.getPort(), param.getUserName().trim(), param.getPassword().trim());
            //创建管道
            channel = connection.createChannel();
            //声明队列
            channel.queueDeclare(param.getQueueName(), false, false, false, null);
            //发送消息
            channel.basicPublish("", param.getQueueName(), null, param.getMsg().getBytes());
            log.info("发送RabbitMQ成功, 队列名称:{}, 消息内容:{}", param.getQueueName(), param.getMsg());
        } catch (IOException e) {
            log.error("连接RabbitMQ失败, 服务器IP:{}", param.getIp(), e);
            throw new MockException(ERROR_MSG);
        } catch (TimeoutException e) {
            log.error("连接RabbitMQ失败, 服务器IP:{}", param.getIp(), e);
            throw new MockException(ERROR_MSG);
        } catch (Throwable e) {
            log.error("发送RabbitMQ失败, 服务器IP:{}", param.getIp(), e);
            throw new MockException("发送RabbitMQ失败");
        } finally {
            if (channel != null) {
                try {
                    channel.close();
                } catch (IOException e) {
                    log.error("关闭RabbitMQ通道失败", e);
                } catch (TimeoutException e) {
                    log.error("关闭RabbitMQ通道超时", e);
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (Exception e) {
                    log.error("关闭RabbitMQ连接失败", e);
                }
            }
        }
    }

    /**
     * 发送RocketMQ
     *
     * @param param
     */
    private void sendRocketMQ(MessageSendReqVO param) {
        if (StringUtils.isEmpty(param.getTopicName())) {
            throw new MockException("Topic名称不能为空");
        }
        if (StringUtils.isEmpty(param.getTagName())) {
            throw new MockException("Tag名称不能为空");
        }
        param.setTopicName(param.getTopicName().trim());
        param.setTagName(param.getTagName().trim());

        DefaultMQProducer producer = new DefaultMQProducer("producer" + CommonUtil.getNextId());
        try {
            //设置nameServer的地址
            producer.setNamesrvAddr(String.format("%s:%s", param.getIp().trim(), param.getPort()));
            //启动生产者
            producer.start();
            //发送消息
            Message message = new Message(param.getTopicName(), param.getTagName(), param.getMsg().getBytes("UTF-8"));
            SendResult sendResult = producer.send(message);
            log.info("发送RocketMQ成功, Topic:{}, Tag:{}, Msg：{}, 发送结果:{}", param.getTopicName(), param.getTagName(), param.getMsg(), sendResult.getSendStatus());
        } catch (IllegalStateException e) {
            log.error("连接RocketMQ失败, 服务器IP:{}", param.getIp(), e);
            throw new MockException(ERROR_MSG);
        } catch (RemotingConnectException e) {
            log.error("连接RocketMQ失败, 服务器IP:{}", param.getIp(), e);
            throw new MockException(ERROR_MSG);
        } catch (Exception e) {
            log.error("发送RocketMQ失败, 服务器IP:{}", param.getIp(), e);
            throw new MockException("发送RocketMQ失败");
        } finally {
            if (producer != null) {
                try {
                    producer.shutdown();
                } catch (Exception e1) {
                    log.error("关闭RocketMQ连接失败", e1);
                }
            }
        }
    }

    /**
     * 发送Kafka
     *
     * @param param
     */
    private void sendKafka(MessageSendReqVO param) {
        if (StringUtils.isEmpty(param.getKafkaTopic())) {
            throw new MockException("Topic名称不能为空");
        }
        param.setKafkaTopic(param.getKafkaTopic().trim());

        try {
            Properties properties = new Properties();
            properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, String.format("%s:%s", param.getIp().trim(), param.getPort()));
            properties.put(ProducerConfig.CLIENT_ID_CONFIG, "mockProducer");
            properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, IntegerSerializer.class.getName());
            properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

            final ExecutorService exec = Executors.newSingleThreadExecutor();

            Future future = exec.submit(() -> {
                KafkaProducer producer = null;
                try {
                    producer = new KafkaProducer<Integer, String>(properties);
                    producer.send(new ProducerRecord<>(param.getKafkaTopic(), 3, param.getMsg()));
                } finally {
                    if (producer != null) {
                        try {
                            producer.close();
                        } catch (Exception e1) {
                            log.error("关闭Kafka连接失败", e1);
                        }
                    }
                }
            });
            future.get(1, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error("发送Kafka消息失败, 服务器IP:{}", param.getIp(), e);
            throw new MockException(ERROR_MSG);
        }
    }
}
