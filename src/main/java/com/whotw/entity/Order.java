package com.whotw.entity;

import java.io.Serializable;
import javax.persistence.*;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * order
 *
 * @author whotw 2019-09-17
 */
@Entity
@Data
@Table(name = "pay_order")
public class Order implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    /**
     * id
     */
    private Integer id;

    /**
     * status
     */
    private String status;

    /**
     * name
     */
    private String name;

    /**
     * order_id
     */
    private String orderId;

    /**
     * money
     */
    private Double money;

    /**
     * create_time
     */
    private Date createTime;

    /**
     * update_time
     */
    private Date updateTime;

    public Order() {
    }

    public Order setId(Integer id) {
        this.id = id;
        return this;
    }

    public Order setStatus(String status) {
        this.status = status;
        return this;
    }

    public Order setName(String name) {
        this.name = name;
        return this;
    }

    public Order setOrderId(String orderId) {
        this.orderId = orderId;
        return this;
    }

    public Order setMoney(Double money) {
        this.money = money;
        return this;
    }

    public Order setCreateTime(Date createTime) {
        this.createTime = createTime;
        return this;
    }

    public Order setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
        return this;
    }

    public enum OrderType {
        //成功
        SUCCESS("1", "成功"),
        //成功
        FAIL("2", "失败"),
        //成功
        PROCESSING("3", "正在处理"),
        //成功
        ERROR("4", "异常"),;
        private String status;
        private String msg;

        OrderType(String status, String msg) {
            this.status = status;
            this.msg = msg;
        }

        public String getStatus() {
            return status;
        }

        public String getMsg() {
            return msg;
        }
    }
}