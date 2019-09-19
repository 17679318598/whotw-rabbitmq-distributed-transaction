package com.whotw.dao;
import com.whotw.entity.DispatchOrder;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
*  dispatch_order
* @author whotw 2019-09-17
*/
@Repository
public interface DispatchOrderRepository extends JpaRepository<DispatchOrder,Integer> {
    DispatchOrder findByOrderId(String orderId);

}
