package com.whotw.utils;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author administrator
 * @description 重试工具类
 * @date 2019/9/17 14:31
 */
public class Retry {
    /**
     * 重试类型MAP
     */
    public static final Map<RetryType, AtomicInteger> RETRY_TYPE_MAP = new ConcurrentHashMap<>();

    static {
        for (RetryType retryType : RetryType.values()) {
            RETRY_TYPE_MAP.put(retryType, new AtomicInteger(0));
        }
    }

    /**
     * 重试
     */
    public static boolean retry(RetryType retryType) {
        //当前重试类型
        AtomicInteger currentRetryType = RETRY_TYPE_MAP.get(retryType);
        //小于最大重试次数才进行重试
        if (currentRetryType.get() < retryType.getMaxRetryCount()) {
            currentRetryType.getAndAdd(1);
            return true;
        }
        //超过最大重试返回false进行业务处理
        clear(retryType);
        return false;
    }

    /**
     * 超过重试》业务处理》清理重试次数
     */
    private static void clear(RetryType retryType) {
        RETRY_TYPE_MAP.get(retryType).getAndSet(0);
    }

    public enum RetryType {
        //调用三方异常
        THIRD("THIRD", 5),
        //本地业务异常
        LOCAL_BUSINESS("THIRD", 5),
        //mq队列异常
        MQ("MQ", 5),;
        private String retryType;
        private int maxRetryCount;

        RetryType(String retryType, int maxRetryCount) {
            this.retryType = retryType;
            this.maxRetryCount = maxRetryCount;
        }

        public String getRetryType() {
            return retryType;
        }

        public int getMaxRetryCount() {
            return maxRetryCount;
        }
    }

    public static void main(String[] args) {
        System.out.println(UUID.randomUUID().toString().replaceAll("-", "").length());
    }
}
