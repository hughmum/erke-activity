package com.mu.activity.interceptor;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.mu.activity.common.Result;
import com.mu.activity.common.annotations.Access;
import com.mu.activity.common.contants.ResultStatus;
import com.mu.activity.dao.UserDOMapper;
import com.mu.activity.entity.UserDO;
import com.mu.activity.excption.BusinessException;
import com.mu.activity.utils.JwtUtils;
import com.mu.activity.utils.RedisUtils;
import lombok.extern.slf4j.Slf4j;
import org.omg.PortableInterceptor.USER_EXCEPTION;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.ws.handler.Handler;

import java.lang.annotation.Annotation;
import java.util.concurrent.TimeUnit;

import static com.mu.activity.common.contants.UserConstants.USER_INFO;
import static com.mu.activity.common.contants.UserConstants.USER_PREFIX;

/**
 * @author 沐
 * Date: 2023-03-10 10:00
 * version: 1.0
 */
@Component("loginInterceptor")
@Slf4j
public class LoginInterceptor  implements HandlerInterceptor {

    @Resource
    private UserDOMapper userDOMapper;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        UserDO userDO = parseToken(request);
        USER_INFO.set(userDO);
        log.info(request.getRequestURI());
        if (handler instanceof HandlerMethod) {
            HandlerMethod hm = (HandlerMethod) handler;
            //检查类上有没有access注解
            Access access = hm.getClass().getAnnotation(Access.class);
            Annotation[] annotations = hm.getClass().getAnnotations();
            for(Annotation annotation : annotations){
                System.out.println(annotation.getClass().getName());
            }
            if (access != null) {
                return true;
            }
            //检查方法上有没有access注解
            access = hm.getMethodAnnotation(Access.class);
            if (access != null) {
                return true;
            }
            //如果没有写注解，那就只能验证token了
            if (USER_INFO.get() == null) {
                throw new BusinessException(ResultStatus.LOGIN_EXPIRED);
            }
        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        USER_INFO.remove();
    }

    public UserDO parseToken(HttpServletRequest request) {
        String token = request.getHeader("token");
        if (token == null) return null;
        Long id = null;
        try {
            //TODO: 2023/3/10 处理token续期问题
            id = JwtUtils.verifyToken(token);
        } catch (JWTVerificationException e) {
            log.error("token parse error: {}",e.getMessage());
            return null;
        }
        assert id != null;
        UserDO userDO = RedisUtils.get(USER_PREFIX + id);
        if (userDO == null) {
            userDO = userDOMapper.selectByPrimaryKey(id);
            //TODO: 2023/3/10 缓存时间应该统一配置
            RedisUtils.set(USER_PREFIX + id, userDO, 1, TimeUnit.HOURS);
        }
        //将用户信息放在threadLocal中
        return userDO;
    }
}
