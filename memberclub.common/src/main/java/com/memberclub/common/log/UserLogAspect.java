/**
 * @(#)UserLogAspect.java, 十二月 15, 2024.
 * <p>
 * Copyright 2024 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.common.log;

import com.memberclub.common.util.JsonUtils;
import com.memberclub.domain.common.BizTypeEnum;
import org.apache.commons.beanutils.PropertyUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * author: 掘金五阳
 */
@Aspect
@Component
public class UserLogAspect {

    @Pointcut("@annotation(UserLog) && execution(public * *(..))")
    public void pointcut() {
    }

    @Around(value = "pointcut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        //无参方法不处理
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        Object[] args = joinPoint.getArgs();

        //获取注解
        UserLog userLogAnnotation = method.getAnnotation(UserLog.class);
        if (userLogAnnotation != null && args != null && args.length > 0) {
            //使用工具类获取userId。
            String userId = null;
            String tradeId = null;
            Object bizTypeObject = null;
            try {
                userId = String.valueOf(PropertyUtils.getProperty(args[0], userLogAnnotation.userId()));
            } catch (Exception e) {
            }
            try {
                tradeId = String.valueOf(PropertyUtils.getProperty(args[0], userLogAnnotation.tradeId()));
            } catch (Exception e) {
            }

            try {
                bizTypeObject = PropertyUtils.getProperty(args[0], userLogAnnotation.bizType());
            } catch (Exception e) {
            }


            String bizType = null;
            if (bizTypeObject != null) {
                if (bizTypeObject instanceof BizTypeEnum) {
                    bizType = String.valueOf(((BizTypeEnum) bizTypeObject).getCode());
                } else {
                    bizType = String.valueOf(bizTypeObject);
                }
            }

            // 放到MDC中
            String msg = String.format(" [domain:%s] [method:%s] [bizType:%s] [userId:%s] [tradeId:%s] ",
                    userLogAnnotation.domain().name(), method.getName(), bizType, userId, tradeId);

            MDC.put("msg", msg);
        }

        try {
            CommonLog.info("method:{}开始执行, request:{}", method.getName(), JsonUtils.toJson(args));
            Object response = joinPoint.proceed();
            CommonLog.info("method:{}执行成功, request:{}, response:{}",
                    method.getName(), JsonUtils.toJson(args), JsonUtils.toJson(response));
            return response;
        } catch (Exception e) {
            CommonLog.error("method:{}执行异常, request:{}",
                    method.getName(), JsonUtils.toJson(args), e);
            throw e;
        } finally {
            //清理MDC
            MDC.clear();
        }

    }
}