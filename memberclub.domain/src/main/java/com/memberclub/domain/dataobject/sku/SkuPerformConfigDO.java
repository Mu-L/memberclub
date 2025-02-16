/**
 * @(#)MemberSkuPerformConfig.java, 十二月 14, 2024.
 * <p>
 * Copyright 2024 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.domain.dataobject.sku;

import lombok.Data;

import java.util.List;

/**
 * @author 掘金五阳
 */
@Data
public class SkuPerformConfigDO {
    
    private List<SkuPerformItemConfigDO> configs;

}