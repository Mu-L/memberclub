/**
 * @(#)StartPerformUpdteMemberOrderFlow.java, 十二月 15, 2024.
 * <p>
 * Copyright 2024 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.sdk.perform.flow.build;

import com.memberclub.common.flow.FlowNode;
import com.memberclub.domain.context.perform.PerformContext;
import com.memberclub.sdk.memberorder.domain.MemberOrderDomainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * author: 掘金五阳
 */
@Service
public class StartPerformUpdteMemberOrderFlow extends FlowNode<PerformContext> {
    
    @Autowired
    private MemberOrderDomainService memberOrderDomainService;

    @Override
    public void process(PerformContext context) {
        int count = memberOrderDomainService.onStartPerform(context);
        context.setMemberOrderStartPerformUpdateCount(count);
    }
}