package com.mu.activity.utils;

/**
 * @author 沐
 * Date: 2023-03-10 11:01
 * version: 1.0
 */
public class BeanUtils {
    public static void copyProperties(Object source, Object target) {
        org.springframework.beans.BeanUtils.copyProperties(source, target);
    }
}
