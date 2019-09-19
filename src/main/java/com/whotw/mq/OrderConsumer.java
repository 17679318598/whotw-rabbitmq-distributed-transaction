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
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.support.CorrelationData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.Map;

import static javax.swing.UIManager.get;

/**
 * @author administrator
 * @description OrderConsumer
 * @date 2019/9/17 17:41
 */
@Component
@Slf4j
public class OrderConsumer {
    @Autowired
    private DispatchOrderRepository dispatchOrderRepository;
    @Autowired
    private OrderRepository orderRepository;

    @RabbitListener(queues = RabbitMQConfig.QUEUE_ORDER)
    public void process(Message message, Channel channel) throws Exception {
        String msgId = message.getMessageProperties().getMessageId();
        String msg = new String(message.getBody(), "utf-8");
        //表示消息已经消费了没必要往下执行
        if (msgId == null) {
            log.info("-----------------消费消息成功，消息[{}]", msg);
            return;
        }
        //记录重试次数
        int cout = Retry.RETRY_TYPE_MAP.get(Retry.RetryType.THIRD).get();
        if (cout > 0) {
            log.info("-------------重试第[{}]次", cout);
        }else{
            //日志记录
        }
        log.info("------------------普通队列接收消息{}，消息id[{}]", msg, msgId);
        String orderId = JSONObject.parseObject(msg).getString("order_id");
        RabbitMQConfig.Type type = RabbitMQConfig.Type.ACCEPT;
        long tag = message.getMessageProperties().getDeliveryTag();
        DispatchOrder dispatchOrder = new DispatchOrder();
        Order order = orderRepository.findByOrderId(orderId);
        try {
            order.setStatus(Order.OrderType.SUCCESS.getStatus());
            //假设为某个核心系统
            if (dispatchOrderRepository.findByOrderId(orderId) == null) {
                dispatchOrderRepository.save(dispatchOrder.setCreateTime(new Date()).setOrderId(orderId));
            }
//            int i = 1 / 0;//模拟重试
        } catch (Exception e) {
            //重试
            if (Retry.retry(Retry.RetryType.THIRD)) {
                order.setStatus(Order.OrderType.PROCESSING.getStatus());
                //未超过重试次数进行重试
                type = RabbitMQConfig.Type.RETRY;
            } else {
                order.setStatus(Order.OrderType.FAIL.getStatus());
                //超过重试次数，消费被拒，放入死信队列
                type = RabbitMQConfig.Type.REJECT;
            }
        } finally {
            orderRepository.save(order);
            if (RabbitMQConfig.Type.ACCEPT.equals(type)) {
                //手动消费消息
                channel.basicAck(tag, true);
            } else if (RabbitMQConfig.Type.RETRY.equals(type)) {
                //执行如下或抛出异常进行重试，重新执行该方法
                channel.basicNack(tag, false, true);
            } else {
                //消息被拒，交给死信队列处理
                channel.basicNack(tag, false, false);
            }
        }
    }

}
