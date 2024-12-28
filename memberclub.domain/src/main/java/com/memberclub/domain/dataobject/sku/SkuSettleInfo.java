/**
 * @(#)SkuSettleInfo.java, 十二月 28, 2024.
 * <p>
 * Copyright 2024 fenbi.com. All rights reserved.
 * FENBI.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.domain.dataobject.sku;

import lombok.Data;

/**
 * author: 掘金五阳
 */
@Data
public class SkuSettleInfo {
    
    /**
     * 承包商 Id
     */
    public String contractorId;

    /**
     * 结算价格
     */
    public Integer settlePriceFen;
}