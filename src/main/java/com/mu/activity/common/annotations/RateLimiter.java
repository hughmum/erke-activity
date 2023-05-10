package com.mu.activity.common.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @author 沐
 * Date: 2023-03-10 10:48
 * version: 1.0
 */
@Retention(RUNTIME)
@Target({TYPE, METHOD})
public @interface RateLimiter {
//    /**
//     * 时间窗口
//     */
//    int seconds() default 5;
//
//    /**
//     * 访问次数
//     */
//    int maxCount() default 10;
}
