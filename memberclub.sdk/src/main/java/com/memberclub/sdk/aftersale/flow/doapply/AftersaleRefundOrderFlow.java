/**
 * @(#)AftersaleRefundFlow.java, 一月 01, 2025.
 * <p>
 * Copyright 2025 fenbi.com. All rights reserved.
 * FENBI.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.sdk.aftersale.flow.doapply;

import com.memberclub.common.flow.FlowNode;
import com.memberclub.common.log.CommonLog;
import com.memberclub.domain.context.aftersale.apply.AfterSaleApplyContext;
import com.memberclub.sdk.aftersale.service.domain.AftersaleDomainService;
import com.memberclub.sdk.order.OrderDomainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * author: 掘金五阳
 */
@Service
public class AftersaleRefundOrderFlow extends FlowNode<AfterSaleApplyContext> {

    @Autowired
    private AftersaleDomainService aftersaleDomainService;

    @Autowired
    private OrderDomainService orderDomainService;

    @Override
    public void process(AfterSaleApplyContext context) {
        if (context.getAftersaleOrderDO().getStatus().isOrderRefund()) {
            CommonLog.info("当前状态已完成退款,不再重复执行");
            return;
        }

        CommonLog.info("开始订单退款流程");
        orderDomainService.refundOrder(context);
        
        aftersaleDomainService.onOrderRefunded(context);
    }
}