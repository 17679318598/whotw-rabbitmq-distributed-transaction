package com.whotw.mq;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.rabbitmq.client.Channel;
import com.whotw.config.RabbitMQConfig;
import com.whotw.dao.DispatchOrderRepository;
import com.whotw.dao.OrderRepository;
import com.whotw.entity.DispatchOrder;
import com.whotw.entity.Order;
import com.whotw.utils.Retry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.Map;

/**
 * @author administrator
 * @description OrderConsumer
 * @date 2019/9/17 17:41
 */
@Component
@Slf4j
public class DLOrderConsumer {
    @Autowired
    private DispatchOrderRepository dispatchOrderRepository;
    @Autowired
    private OrderRepository orderRepository;

    @RabbitListener(queues = RabbitMQConfig.DL_QUEUE_ORDER)
    public void process(Message message, Channel channel) throws Exception {
        String msgId = message.getMessageProperties().getMessageId();
        if (msgId == null) {
            //表示消息已经消费了没必要往下执行
            log.info("------------------死信队列消费消息成功{}，消息id[{}]", msgId);
            return;
        }
        String msg = new String(message.getBody(), "utf-8");
        log.info("------------------死信队列接收消息{}，消息id[{}]", msg, msgId);
        String orderId = JSONObject.parseObject(msg).getString("order_id");
        DispatchOrder dispatchOrder = new DispatchOrder();
        Order order = orderRepository.findByOrderId(orderId);
        try {
            orderRepository.save(order.setStatus(Order.OrderType.SUCCESS.getStatus()));
            if (dispatchOrderRepository.findByOrderId(orderId) == null) {
                dispatchOrderRepository.save(dispatchOrder.setCreateTime(new Date()).setOrderId(orderId));
            }
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), true);
//            int i = 1 / 0;模拟死信是否消费成功
        } catch (Exception e) {
            //死信消费失败，交给定时job消费
            orderRepository.save(order.setStatus(Order.OrderType.ERROR.getStatus()));
        }
    }
}
