/**
 * @(#)AftersaleOrderDO.java, 一月 01, 2025.
 * <p>
 * Copyright 2025 fenbi.com. All rights reserved.
 * FENBI.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.domain.dataobject.aftersale;

import com.memberclub.domain.common.BizTypeEnum;
import com.memberclub.domain.context.aftersale.contant.AftersaleSourceEnum;
import com.memberclub.domain.context.aftersale.contant.RefundTypeEnum;
import lombok.Data;

import java.util.List;

/**
 * author: 掘金五阳
 */
@Data
public class AftersaleOrderDO {

    private BizTypeEnum bizType;

    private long userId;

    private AftersaleSourceEnum source;

    private Long id;

    private String operator;

    private String reason;

    private String tradeId;

    private List<ApplySkuDetail> applySkuDetails;

    private AftersaleOrderExtraDO extra;

    private Integer actPayPriceFen;

    private Integer actRefundPriceFen;

    private Integer recommendRefundPriceFen;

    private AftersaleOrderStatusEnum status;

    private RefundTypeEnum refundType;

    private long utime;

    private long ctime;
}