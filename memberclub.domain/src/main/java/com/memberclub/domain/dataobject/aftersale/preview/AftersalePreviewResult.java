/**
 * @(#)AftersalePreviewResult.java, 十二月 22, 2024.
 * <p>
 * Copyright 2024 fenbi.com. All rights reserved.
 * FENBI.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.domain.dataobject.aftersale.preview;

import com.memberclub.domain.dataobject.aftersale.RefundTypeEnum;
import lombok.Data;

/**
 * author: 掘金五阳
 */
@Data
public class AftersalePreviewResult {

    private boolean aftersaleEnabled;

    private RefundTypeEnum refundType;


}