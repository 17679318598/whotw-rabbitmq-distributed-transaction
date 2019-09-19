package com.whotw.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import java.util.HashMap;
import java.util.Map;

/**
 * @author administrator
 * @description RabbitMQConfig
 * @date 2019/9/17 16:13
 */
@Configuration
public class RabbitMQConfig {
    public enum Type {
        // 处理成功
        ACCEPT,
        // 重试
        RETRY,
        // 消息被拒
        REJECT,;
    }

    @Value("${spring.rabbitmq.virtual-host}")
    private String visual;
    @Value("${spring.rabbitmq.host}")
    private String host;
    @Value("${spring.rabbitmq.port}")
    private int port;
    @Value("${spring.rabbitmq.username}")
    private String username;
    @Value("${spring.rabbitmq.password}")
    private String password;

    //########### init RabbitTemplate ###########

    @Bean
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory(host, port);
        connectionFactory.setUsername(username);
        connectionFactory.setPassword(password);
        connectionFactory.setVirtualHost(visual);
        connectionFactory.setPublisherConfirms(true);
        connectionFactory.setPublisherReturns(true);
        return connectionFactory;
    }

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public RabbitTemplate rabbitTemplate() {
        return new RabbitTemplate(connectionFactory());
    }

    //########### init RabbitTemplate ###########

    //########### EXCHANGE  QUEUE ROUTINGKEY###########

    public static final String EXCHANGE_ORDER = "EXCHANGE_ORDER";
    public static final String DL_EXCHANGE_ORDER = "DL_EXCHANGE_ORDER";

    public static final String QUEUE_ORDER = "QUEUE_ORDER";
    public static final String DL_QUEUE_ORDER = "DL_QUEUE_ORDER";

    public static final String ROUTINGKEY_ORDER = "ROUTINGKEY_ORDER";
    public static final String DL_ROUTINGKEY_ORDER = "DL_ROUTINGKEY_ORDER";

    //########### EXCHANGE  QUEUE ROUTINGKEY###########

    /**
     * 定义普通交换机
     */
    @Bean
    public DirectExchange exchangeOrder() {
        return new DirectExchange(EXCHANGE_ORDER);
    }

    /**
     * 定义消息处理队列【绑定死信交换机，该队列中的消息成为死信则发送至死信交换机】
     */
    @Bean
    public Queue queueOrder() {
        Map<String, Object> args = new HashMap<>(2);
        args.put("x-dead-letter-exchange", DL_EXCHANGE_ORDER);
        args.put("x-dead-letter-routing-key", DL_ROUTINGKEY_ORDER);
        return new Queue(QUEUE_ORDER, true, false, false, args);
    }

    /**
     * 普通队列绑定到普通交换器上
     */
    @Bean
    public Binding orderBinding(Queue queueOrder, DirectExchange exchangeOrder) {
        return BindingBuilder.bind(queueOrder).to(exchangeOrder).with(ROUTINGKEY_ORDER);

    }

    /**
     * 死信队列
     */
    @Bean
    public Queue dlQueueOrder() {
        return new Queue(DL_QUEUE_ORDER);
    }

    /**
     * 死信交换机
     */
    @Bean
    public DirectExchange dlExchangeOrder() {
        return new DirectExchange(DL_EXCHANGE_ORDER);
    }

    /**
     * 死信队列绑定到死信交换器上【消费死信】
     * 死信:队列溢出、消息过期、消息被拒【nack操作】
     */
    @Bean
    public Binding deadLetterBinding(Queue dlQueueOrder, DirectExchange dlExchangeOrder) {
        return BindingBuilder.bind(dlQueueOrder).to(dlExchangeOrder).with(DL_ROUTINGKEY_ORDER);
    }
}
