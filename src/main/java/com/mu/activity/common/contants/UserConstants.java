package com.mu.activity.common.contants;

import com.mu.activity.entity.UserDO;

/**
 * @author 沐
 * Date: 2023-03-10 0:04
 * version: 1.0
 */
public class UserConstants {
    public static final String USER_PREFIX = "user_";
    public static final ThreadLocal<UserDO> USER_INFO = new ThreadLocal<>();
}
