package com.whotw.entity;

import java.io.Serializable;
import javax.persistence.*;
import lombok.Data;
import java.util.Date;
import java.util.List;

/**
*  dispatch_order
* @author whotw 2019-09-17
*/
@Entity
@Data
@Table(name="dispatch_order")
public class DispatchOrder implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    /**
    * id
    */
    private Integer id;

    /**
    * order_id
    */
    private String orderId;

    /**
    * create_time
    */
    private Date createTime;

    public DispatchOrder() {
    }

    public DispatchOrder setId(Integer id) {
        this.id = id;
        return this;
    }

    public DispatchOrder setOrderId(String orderId) {
        this.orderId = orderId;
        return this;
    }

    public DispatchOrder setCreateTime(Date createTime) {
        this.createTime = createTime;
        return this;
    }
}