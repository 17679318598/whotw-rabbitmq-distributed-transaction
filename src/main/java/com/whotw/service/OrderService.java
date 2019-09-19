package com.whotw.service;

import com.fasterxml.jackson.databind.util.JSONPObject;
import com.whotw.dao.DispatchOrderRepository;
import com.whotw.dao.OrderRepository;
import com.whotw.entity.DispatchOrder;
import com.whotw.entity.Order;
import com.whotw.mq.OrderProducer;
import com.whotw.utils.UUIDUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.concurrent.Executors;

/**
 * @author administrator
 * @description OrderService
 * @date 2019/9/17 15:56
 */
@RestController
public class OrderService {
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private OrderProducer producer;

    public String pay() {
        //生成订单
        Order order = new Order();
        try {
            order.setCreateTime(new Date()).setMoney(22.00).setName("黄焖鸡").setOrderId(UUIDUtils.gen32()).setStatus(Order.OrderType.PROCESSING.getStatus());
            Order resOrder = orderRepository.save(order);
//            int i = 1 / 0;//模拟订单消费异常
            //投递消息到队列中
            producer.send(resOrder.getOrderId());
        } catch (Exception e) {
            //将订单设为异常订单【job处理】
            orderRepository.save(order.setStatus(Order.OrderType.ERROR.getStatus()));
        }
        return Order.OrderType.SUCCESS.name();
    }

    @RequestMapping("test")
    public String test() {
       /* 模拟高并发
       for (int i = 0; i <1000 ; i++) {
            Executors.newFixedThreadPool(20).execute(() -> {
                pay();
            });
        }
        return "success";*/
        return pay();
    }
}
