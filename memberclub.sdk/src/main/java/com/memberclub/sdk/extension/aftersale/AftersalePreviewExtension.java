/**
 * @(#)AftersalePreviewExtension.java, 十二月 22, 2024.
 * <p>
 * Copyright 2024 fenbi.com. All rights reserved.
 * FENBI.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.sdk.extension.aftersale;

import com.memberclub.common.extension.BaseExtension;
import com.memberclub.domain.dataobject.aftersale.preview.AftersalePreviewContext;

/**
 * @author yuhaiqiang
 */
public interface AftersalePreviewExtension extends BaseExtension {

    public void preview(AftersalePreviewContext context);
}