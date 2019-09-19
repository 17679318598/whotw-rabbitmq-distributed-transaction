package com.whotw.dao;

import com.whotw.entity.Order;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * order
 *
 * @author whotw 2019-09-17
 */
@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {
    List<Order> findAllByStatus(String status);

    Order findByOrderId(String orderId);
}
