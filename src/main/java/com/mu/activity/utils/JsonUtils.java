package com.mu.activity.utils;

import cn.hutool.json.JSONUtil;

/**
 * @author 沐
 * Date: 2023-03-10 11:03
 * version: 1.0
 */
public class JsonUtils {
    public static String objectToJson(Object o) {
        return JSONUtil.toJsonStr(o);
    }
    public static <T> T jsonToObject(String json, Class<T> beanClass) {
        return JSONUtil.toBean(json, beanClass);
    }
}
