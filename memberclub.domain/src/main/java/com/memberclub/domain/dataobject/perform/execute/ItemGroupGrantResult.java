/**
 * @(#)GrantResult.java, 十二月 15, 2024.
 * <p>
 * Copyright 2024 fenbi.com. All rights reserved.
 * FENBI.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.domain.dataobject.perform.execute;

import lombok.Data;

import java.util.Map;

/**
 * author: 掘金五阳
 */
@Data
public class ItemGroupGrantResult {

    private boolean success;

    private Map<String, ItemGrantResult> grantMap;
}