/**
 * @(#)FlowChainService.java, 十二月 15, 2024.
 * <p>
 * Copyright 2024 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.sdk.perform.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

/**
 * author: 掘金五阳
 */
@Deprecated
@Service
public class LiteFlowChainService {

    @Autowired
    ApplicationContext applicationContext;

    public <T> LiteFlowChain<T> newChain(Class<T> tClass) {
        LiteFlowChain<T> chain = new LiteFlowChain<>(applicationContext);
        return chain;
    }
}