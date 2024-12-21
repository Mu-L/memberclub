/**
 * @(#)RetryableContext.java, 十二月 21, 2024.
 * <p>
 * Copyright 2024 fenbi.com. All rights reserved.
 * FENBI.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.domain.common;

/**
 * author: 掘金五阳
 */
public interface RetryableContext {

    public int getRetryTimes();

    public void setRetryTimes(int retryTimes);
}