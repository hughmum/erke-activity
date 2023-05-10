package com.mu.activity.utils;

import cn.hutool.core.util.IdUtil;

/**
 * @author 沐
 * Date: 2023-03-10 11:02
 * version: 1.0
 */
public class IdUtils {
    public static long generatrId() {
        return IdUtil.getSnowflake().nextId();
    }
}
