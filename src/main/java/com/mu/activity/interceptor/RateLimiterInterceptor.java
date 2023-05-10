package com.mu.activity.interceptor;

import com.google.common.util.concurrent.RateLimiter;
import com.mu.activity.common.contants.ResultStatus;
import com.mu.activity.excption.BusinessException;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author 沐
 * Date: 2023-03-11 15:20
 * version: 1.0
 */
@Component("rateLimiterInterceptor")
public class RateLimiterInterceptor implements HandlerInterceptor {
    private RateLimiter rateLimiter;

    @PostConstruct
    public void init() {
        rateLimiter = RateLimiter.create(100);
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (handler instanceof HandlerMethod) {
            HandlerMethod hm = (HandlerMethod) handler;
            com.mu.activity.common.annotations.RateLimiter annotation = hm.getMethodAnnotation(com.mu.activity.common.annotations.RateLimiter.class);
            if (annotation != null) {
                if (!rateLimiter.tryAcquire(1)) {//用于尝试获取一个"令牌"
                    throw new BusinessException(ResultStatus.RATELIMITE);
                }
            }
        }
        return true;
    }
}
