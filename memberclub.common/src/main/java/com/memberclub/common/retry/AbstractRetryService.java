/**
 * @(#)AbstractRetryService.java, 十二月 31, 2024.
 * <p>
 * Copyright 2024 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.common.retry;

import com.memberclub.common.util.ApplicationContextUtils;
import org.apache.commons.lang3.ClassUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;

/**
 * author: 掘金五阳
 */
public abstract class AbstractRetryService implements RetryService {

    public static final Logger LOG = LoggerFactory.getLogger(AbstractRetryService.class);


    @Override
    public void consumeMessage(RetryMessage msg) {
        try {
            LOG.info("开始消费重试消息 currentTimes:{}, msg:{}", msg.getRetryTimes(), msg);
            RetryLocalContext.setRetryTimes(msg.getBeanName(), msg.getMethodName(), msg.getRetryTimes());
            Object bean = ApplicationContextUtils.getContext().getBean(msg.getBeanName());
            Class<?> clazz = Class.forName(msg.getBeanClassName());

            Class<?>[] argsClassArray = new Class[msg.getArgsClassList().size()];

            for (int i = 0; i < msg.getArgsClassList().size(); i++) {
                argsClassArray[i] = ClassUtils.getClass(msg.getArgsClassList().get(i));
            }

            Method m = ReflectionUtils.findMethod(clazz, msg.getMethodName(), argsClassArray);

            Object[] argsArray = new Object[msg.getArgsClassList().size()];
            for (int i = 0; i < msg.getArgsList().size(); i++) {
                argsArray[i] = RetryUtil.fromJson(msg.getArgsList().get(i), argsClassArray[i]);
            }

            try {
                ReflectionUtils.invokeMethod(m, bean, argsArray);
            } catch (Exception e) {
                if (!msg.isThrowException()) {
                    LOG.error("调用重试方法异常:{}", argsArray, e);
                }
            }
        } catch (Exception e) {
            LOG.error("调用重试消息方法,重试组件异常:{}", msg, e);
        } finally {
            RetryLocalContext.clear();
        }
    }
}