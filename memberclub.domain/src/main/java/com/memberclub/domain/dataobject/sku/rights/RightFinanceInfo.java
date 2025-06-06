/**
 * @(#)RightSettleInfo.java, 十二月 28, 2024.
 * <p>
 * Copyright 2024 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.domain.dataobject.sku.rights;

import lombok.Data;

/**
 * @see com.memberclub.domain.dataobject.perform.item.PerformItemFinanceInfo
 * author: 掘金五阳
 */
@Data
public class RightFinanceInfo {

    /**
     * 承包商 Id
     */
    public String contractorId;

    /**
     * 结算价格
     */
    public Integer settlePriceFen;

    /**
     * 结算资产类型
     */
    public Integer financeAssetType;

    /**
     * 是否可结算
     */
    public Boolean financeable;
}