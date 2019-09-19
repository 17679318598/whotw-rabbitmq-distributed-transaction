package com.whotw.utils;

import java.util.UUID;

/**
 * @author administrator
 * @description
 * @date 2019/9/17 15:30
 */
public class UUIDUtils {
    public static String gen32() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }
}
