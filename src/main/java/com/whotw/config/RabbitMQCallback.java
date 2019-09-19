package com.whotw.config;

import com.whotw.dao.OrderRepository;
import com.whotw.entity.Order;
import com.whotw.mq.OrderProducer;
import com.whotw.utils.Retry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.support.CorrelationData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@Slf4j
public class RabbitMQCallback implements RabbitTemplate.ConfirmCallback, RabbitTemplate.ReturnCallback {
    @Autowired
    private OrderProducer producer;
    @Autowired
    private OrderRepository orderRepository;

    /**
     * 消息确认机制，保证消息一定投递到MQ
     */
    @Override
    public void confirm(CorrelationData correlationData, boolean ack, String cause) {
        if (ack) {
            //消息发送成功
            log.info("----------消息发送到交换机成功，消息id:{}", correlationData.getId());
        } else {
            //交换机 NOT_FOUND
            log.info("消息发送到交换机失败，原因\n{},\n -------------准备重试", cause);
        }
    }

    @Override
    public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey) {
        log.info("消息【{}】,应答码【{}】,原因【{}】,交换机【{}】------ 路由【{}】", message, replyCode, replyText, exchange, routingKey);
    }

    public void setCallBack(RabbitTemplate rabbitTemplate) {
        //消息发送到某个交换机时触发ConfirmCallback
        rabbitTemplate.setConfirmCallback(this);
        //判断消息是否能从某个交换机成功发送消息至队列
        rabbitTemplate.setMandatory(true);//必须设置，否则下述设置无效
        rabbitTemplate.setReturnCallback(this);
    }
}
