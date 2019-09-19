package com.whotw.mq;

import com.alibaba.fastjson.JSONObject;
import com.whotw.config.RabbitMQCallback;
import com.whotw.config.RabbitMQConfig;
import com.whotw.dao.OrderRepository;
import com.whotw.entity.Order;
import com.whotw.utils.Retry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.support.CorrelationData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;


/**
 * @author administrator
 * @description OrderProducer
 * @date 2019/9/17 17:05
 */
@Slf4j
@Component
public class OrderProducer extends RabbitMQCallback {
    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void send(String orderId) {
        this.setCallBack(rabbitTemplate);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("order_id", orderId);
        String msg = jsonObject.toJSONString();
        log.info("-----------------发送消息：{}", msg);
        //生成消息id,保证消息重复消费幂等性问题
        CorrelationData correlationData = new CorrelationData(orderId);
        Message message = MessageBuilder.withBody(msg.getBytes()).setContentType(MessageProperties.CONTENT_TYPE_TEXT_PLAIN)
                .setContentEncoding("utf-8")
                .setMessageId(orderId).build();
        //RabbitMQConfig.EXCHANGE_ORDER  RabbitMQConfig.QUEUE_ORDER
        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_ORDER, RabbitMQConfig.ROUTINGKEY_ORDER, message, correlationData);

    }
}
