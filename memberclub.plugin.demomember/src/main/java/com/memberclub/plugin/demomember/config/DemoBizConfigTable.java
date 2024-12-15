/**
 * @(#)DemoBizConfigTable.java, 十二月 15, 2024.
 * <p>
 * Copyright 2024 fenbi.com. All rights reserved.
 * FENBI.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.plugin.demomember.config;

import com.memberclub.common.annotation.Route;
import com.memberclub.common.extension.ExtensionImpl;
import com.memberclub.domain.common.BizTypeEnum;
import com.memberclub.sdk.extension.config.BizConfigTable;

/**
 * author: 掘金五阳
 */
@ExtensionImpl(desc = "Demo 业务配置表", bizScenes = {@Route(bizType = BizTypeEnum.DEMO_MEMBER)})
public class DemoBizConfigTable implements BizConfigTable {


}