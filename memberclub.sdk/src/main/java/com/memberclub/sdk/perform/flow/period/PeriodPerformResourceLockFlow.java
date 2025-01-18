/**
 * @(#)PeriodPerformResourceLockFlow.java, 十二月 29, 2024.
 * <p>
 * Copyright 2024 fenbi.com. All rights reserved.
 * FENBI.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.sdk.perform.flow.period;

import com.memberclub.common.flow.FlowNode;
import com.memberclub.domain.context.perform.period.PeriodPerformContext;
import com.memberclub.sdk.perform.service.domain.PerformLockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * author: 掘金五阳
 */
@Service
public class PeriodPerformResourceLockFlow extends FlowNode<PeriodPerformContext> {

    @Autowired
    private PerformLockService performLockService;

    @Override
    public void process(PeriodPerformContext context) {
        long lockValue = performLockService.lock(context.getBizType(),
                null,
                context.getUserId(),
                context.getTradeId());
        context.setLockValue(lockValue);
    }

    @Override
    public void success(PeriodPerformContext context) {
        performLockService.unlock(context.getBizType(),
                context.getUserId(),
                context.getTradeId(),
                context.getLockValue());
    }

    @Override
    public void rollback(PeriodPerformContext context, Exception e) {
        performLockService.unlock(context.getBizType(),
                context.getUserId(),
                context.getTradeId(),
                context.getLockValue());
    }
}