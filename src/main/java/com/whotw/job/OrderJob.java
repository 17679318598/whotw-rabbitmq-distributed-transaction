package com.whotw.job;

import com.alibaba.fastjson.JSON;
import com.whotw.dao.OrderRepository;
import com.whotw.entity.Order;
import com.whotw.mq.OrderProducer;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * @author administrator
 * @description OrderJob
 * @date 2019/9/17 18:20
 */
@Component
@Slf4j
public class OrderJob {
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private OrderProducer producer;

    /**
     * 补单【一般设置为凌晨12点或2点执行，按需求来】
     */
    @Scheduled(cron = "0 0/1 * * * ?")
    public void errorOrderProcess() {
        log.info("-----------每隔一分钟执行一次补单");
        List<Order> order = orderRepository.findAllByStatus(Order.OrderType.ERROR.getStatus());
        if (!CollectionUtils.isEmpty(order)) {
            order.forEach((currentOrder -> {
                producer.send(currentOrder.getOrderId());
            }));
        }
    }
}
