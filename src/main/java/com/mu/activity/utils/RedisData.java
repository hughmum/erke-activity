package com.mu.activity.utils;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author 沐
 * Date: 2023-05-09 20:42
 * version: 1.0
 */
@Data
public class RedisData {
    private LocalDateTime expireTime;
    private Object data;
}
