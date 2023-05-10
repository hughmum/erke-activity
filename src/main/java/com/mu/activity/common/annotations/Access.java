package com.mu.activity.common.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 这个注解不需要校验登录状态
 * @author 沐
 * Date: 2023-03-10 10:47
 * version: 1.0
 */
@Target({TYPE, ElementType.METHOD})
@Retention(RUNTIME)
public @interface Access {
}
