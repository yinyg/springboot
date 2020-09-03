package tech.hiyinyougen.springboot.config;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.autoconfigure.amqp.SimpleRabbitListenerContainerFactoryConfigurer;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;

/**
 * @Author yinyg
 * @CreateTime 2020/6/29 16:32
 * @Description
 */
@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix="spring.rabbitmq")
@Slf4j
public class RabbitMQConfig {
    private String host;
    private Integer port;
    private String username;
    private String password;
    private String publisherConfirmType;
    private Integer concurrency;
    private Integer maxConcurrency;
    private String acknowledgeMode;

    /**
     * 连接名称
     */
    private static final String CONNECTIONFACTORY = "connectionFactory";

    /**
     * 交换器
     */
    public static final String EXCHANGE = "exchange";

    /**
     * 对列名称
     */
    public static final String QUEUE = "test";

    private static final String LISTENERCONTAINERFACTORY = "listenerContainerFactory";

    @Bean(name = CONNECTIONFACTORY)
    @Primary
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        connectionFactory.setHost(host);
        connectionFactory.setPort(port);
        connectionFactory.setUsername(username);
        connectionFactory.setPassword(password);
//        connectionFactory.setPublisherConfirmType(CachingConnectionFactory.ConfirmType.valueOf(publisherConfirmType));
        return connectionFactory;
    }

    @Bean
    public String queue(@Qualifier(CONNECTIONFACTORY) ConnectionFactory connectionFactory) {
        try {
            Channel channel = connectionFactory.createConnection().createChannel(false);
            channel.exchangeDeclare(EXCHANGE, BuiltinExchangeType.DIRECT,true);
            channel.queueDeclare(QUEUE, true, false, false, null);
            channel.queueBind(QUEUE, EXCHANGE, EXCHANGE + "." + QUEUE);
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            return QUEUE;
        }
    }

    @Bean(name = LISTENERCONTAINERFACTORY)
    public SimpleRabbitListenerContainerFactory javaFactory(
            SimpleRabbitListenerContainerFactoryConfigurer configurer,
            @Qualifier(CONNECTIONFACTORY) ConnectionFactory connectionFactory
    ) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConcurrentConsumers(concurrency);
        factory.setMaxConcurrentConsumers(maxConcurrency);
        factory.setAcknowledgeMode(AcknowledgeMode.valueOf(acknowledgeMode));
        configurer.configure(factory, connectionFactory);
        return factory;
    }

    @RabbitListener(queues = {QUEUE},containerFactory = LISTENERCONTAINERFACTORY)
    public void processMessage0(@Payload Message content,
                                @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag,
                                Channel channel) throws Exception {
        try {
            log.info("base mq process message=============================");
            JSONObject jsonObject = null;//将json字符串转换为json对象
            String contentType = content.getMessageProperties().getContentType();
            if (MessageProperties.CONTENT_TYPE_JSON.equals(contentType)){
                jsonObject = (JSONObject) JSON.parse(content.getBody());
            }else if(MessageProperties.CONTENT_TYPE_TEXT_PLAIN.equals(contentType)){
                jsonObject = JSONObject.parseObject(new String(content.getBody()));
            }else{
                throw new RuntimeException("ContentType不支持！（"+contentType+"）");
            }
            System.out.println(JSON.toJSONString(jsonObject));
        } catch (Exception e){
            log.warn(e.getMessage());
        } finally {
            channel.basicAck(deliveryTag, false);
            log.info("base mq process message==========================end");
        }
    }

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE) // 必须是prototype类型
    public RabbitTemplate rabbitTemplate() {
        RabbitTemplate template = new RabbitTemplate(connectionFactory());
        return template;
    }
}
