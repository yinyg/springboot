package tech.hiyinyougen.springboot.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConfirmListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.connection.Connection;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tech.hiyinyougen.springboot.config.RabbitMQConfig;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeoutException;

/**
 * @author yinyg
 * @date 2020/7/13
 * @description
 */
@Component
@Slf4j
public class SendMqUtils {
    @Autowired
    private RabbitMQConfig commonRabbit;
    @Autowired
    private ConnectionFactory connectionFactory;

    /**
     * @description 发送数据
     * @param: exchange
     * @param: queue
     * @param: message
     * @throws
     * @author yinyg
     * @date 2020/7/13
     */
    public void publish(String exchange, String queue, Object message) {
        try {
            RabbitTemplate template = commonRabbit.rabbitTemplate();
            template.convertAndSend(exchange, exchange + "." + queue, JSONObject.toJSONString(message, SerializerFeature.WriteMapNullValue));
            log.info("publish mq success, queue={}, message={}", queue, JSON.toJSONString(message));
        } catch (Exception ex) {
            log.error("publish mq error, queue={}, message={}", queue, JSON.toJSONString(message), ex.getMessage());
        }
    }

    /**
     * @description 发送消息，事务
     * @param exchange
     * @param queue
     * @param message
     * @throws
     * @author yinyg
     * @date 2020/9/4
     */
    public void publishWithTransaction(String exchange, String queue, Object message) {
        Connection connection = null;
        Channel channel = null;
        try {
            connection = connectionFactory.createConnection();
            channel = connection.createChannel(true);
            channel.basicPublish(exchange,
                    exchange + "." + queue,
                    new AMQP.BasicProperties.Builder()
                            .contentType(MessageProperties.CONTENT_TYPE_TEXT_PLAIN)
//                            .deliveryMode(MessageDeliveryMode.toInt(MessageDeliveryMode.PERSISTENT))
                            .build(),
                    JSON.toJSONBytes(message, SerializerFeature.WriteMapNullValue));
//            int result = 1 / 0;
            channel.txCommit();
            log.info("publish mq success, queue={}, message={}", queue, JSON.toJSONString(message));
        } catch (Exception ex) {
            log.error("publish mq error, queue={}, message={}", queue, JSON.toJSONString(message), ex.getMessage());
            throw new RuntimeException(ex.getMessage());
        } finally {
            if (channel != null) {
                try {
                    channel.close();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (TimeoutException e) {
                    e.printStackTrace();
                }
            }
            if (connection != null) {
                connection.close();
            }
        }
    }

    /**
     * @description 发送消息，确认机制
     * @param exchange
     * @param queue
     * @param message
     * @throws
     * @author yinyg
     * @date 2020/9/4
     */
    public void publishWithConfirm(String exchange, String queue, Object message) {
        Connection connection = null;
        Channel channel = null;
        try {
            connection = connectionFactory.createConnection();
            channel = connection.createChannel(false);
            channel.confirmSelect();
            channel.basicPublish(exchange,
                    exchange + "." + queue,
                    new AMQP.BasicProperties.Builder().contentType(MessageProperties.CONTENT_TYPE_TEXT_PLAIN).build(),
                    JSON.toJSONBytes(message, SerializerFeature.WriteMapNullValue));
            if (channel.waitForConfirms()) {
                log.info("publish mq success, queue={}, message={}", queue, JSON.toJSONString(message));
            } else {
                log.info("publish mq error, queue={}", queue, JSON.toJSONString(message));
                // do something else...
            }
        } catch (Exception ex) {
            log.error("publish mq error, queue={}, message={}", queue, JSON.toJSONString(message), ex.getMessage());
            throw new RuntimeException(ex.getMessage());
        } finally {
            if (channel != null) {
                try {
                    channel.close();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (TimeoutException e) {
                    e.printStackTrace();
                }
            }
            if (connection != null) {
                connection.close();
            }
        }
    }

    /**
     * @description 发送消息，异步确认
     * @param exchange
     * @param queue
     * @param message
     * @throws
     * @author yinyg
     * @date 2020/9/4
     */
    public void publishWithAsyncConfirm(String exchange, String queue, Object message) {
        Connection connection = null;
        Channel channel = null;
        try {
            connection = connectionFactory.createConnection();
            channel = connection.createChannel(false);
            int batchCount = 100;
            long msgCount = 1;
            SortedSet<Long> confirmSet = new TreeSet<Long>();
            channel.confirmSelect();
            channel.addConfirmListener(new ConfirmListener() {
                @Override
                public void handleAck(long deliveryTag, boolean multiple) throws IOException {
                    log.info("Ack,SeqNo：" + deliveryTag + ",multiple：" + multiple);
                    if (multiple) {
                        confirmSet.headSet(deliveryTag - 1).clear();
                    } else {
                        confirmSet.remove(deliveryTag);
                    }
                }

                @Override
                public void handleNack(long deliveryTag, boolean multiple) throws IOException {
                    log.info("Nack,SeqNo：" + deliveryTag + ",multiple：" + multiple);
                    if (multiple) {
                        confirmSet.headSet(deliveryTag - 1).clear();
                    } else {
                        confirmSet.remove(deliveryTag);
                    }
                    // 注意这里需要添加处理消息重发的场景
                }
            });
            // 演示发送100个消息
            while (msgCount <= batchCount) {
                long nextSeqNo = channel.getNextPublishSeqNo();
                channel.basicPublish(exchange,
                        exchange + "." + queue,
                        new AMQP.BasicProperties.Builder().contentType(MessageProperties.CONTENT_TYPE_TEXT_PLAIN).build(),
                        JSON.toJSONBytes(message, SerializerFeature.WriteMapNullValue));
                log.info("publish mq success, queue={}, message={}", queue, JSON.toJSONString(message));
                confirmSet.add(nextSeqNo);
                ++msgCount;
            }
        } catch (Exception ex) {
            log.error("publish mq error, queue={}, message={}", queue, JSON.toJSONString(message), ex.getMessage());
            throw new RuntimeException(ex.getMessage());
        } finally {
            if (channel != null) {
                try {
                    channel.close();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (TimeoutException e) {
                    e.printStackTrace();
                }
            }
            if (connection != null) {
                connection.close();
            }
        }
    }

    /**
     * @description 发送消息，持久化
     * @param exchange
     * @param queue
     * @param message
     * @throws
     * @author yinyg
     * @date 2020/9/4
     */
    public void publishWithPersistent(String exchange, String queue, Object message) {
        Connection connection = null;
        Channel channel = null;
        try {
            connection = connectionFactory.createConnection();
            channel = connection.createChannel(false);
            channel.basicPublish(exchange,
                    exchange + "." + queue,
                    new AMQP.BasicProperties.Builder()
                            .contentType(MessageProperties.CONTENT_TYPE_TEXT_PLAIN)
                            .deliveryMode(MessageDeliveryMode.toInt(MessageDeliveryMode.PERSISTENT))
                            .build(),
                    JSON.toJSONBytes(message, SerializerFeature.WriteMapNullValue));
            log.info("publish mq success, queue={}, message={}", queue, JSON.toJSONString(message));
        } catch (Exception ex) {
            log.error("publish mq error, queue={}, message={}", queue, JSON.toJSONString(message), ex.getMessage());
            throw new RuntimeException(ex.getMessage());
        } finally {
            if (channel != null) {
                try {
                    channel.close();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (TimeoutException e) {
                    e.printStackTrace();
                }
            }
            if (connection != null) {
                connection.close();
            }
        }
    }

    /**
     * @description 延时队列实现一，死信队列
     * @param exchange
     * @param queue
     * @param message
     * @param times
     * @throws
     * @author yinyg
     * @date 2020/9/4
     */
    public void publishMqTestWithDLX(String exchange, String queue, Object message, long times) {
        try {
            RabbitTemplate template = commonRabbit.rabbitTemplate();
            MessagePostProcessor processor = new MessagePostProcessor(){
                @Override
                public Message postProcessMessage(Message message) throws AmqpException {
                    message.getMessageProperties().setExpiration(times + "");
                    return message;
                }
            };
            template.convertAndSend(exchange, exchange + "." + queue, JSONObject.toJSONString(message, SerializerFeature.WriteMapNullValue), processor);
            log.info("publish mq success, queue={}, message={}, current={}, ttl={}", queue, JSON.toJSONString(message), new Date(), times);
        } catch (Exception ex) {
            log.error("publish mq error, queue={}, message={}", queue, JSON.toJSONString(message), ex.getMessage());
        }
    }

    /**
     * @description 延时队列实现二，rabbitmq插件
     * @param exchange
     * @param queue
     * @param message
     * @param times
     * @throws
     * @author yinyg
     * @date 2020/9/4
     */
    public void publishMqTestWithDelayedMessageQueue(String exchange, String queue, Object message, long times) {
        try {
            RabbitTemplate template = commonRabbit.rabbitTemplate();
            MessagePostProcessor processor = new MessagePostProcessor(){
                @Override
                public Message postProcessMessage(Message message) throws AmqpException {
                    message.getMessageProperties().setHeader(MessageProperties.X_DELAY, times);
                    return message;
                }
            };
            template.convertAndSend(exchange, exchange + "." + queue, JSONObject.toJSONString(message, SerializerFeature.WriteMapNullValue), processor);
            log.info("publish mq success, queue={}, message={}, current={}, ttl={}", queue, JSON.toJSONString(message), new Date(), times);
        } catch (Exception ex) {
            log.error("publish mq error, queue={}, message={}", queue, JSON.toJSONString(message), ex.getMessage());
        }
    }
}
