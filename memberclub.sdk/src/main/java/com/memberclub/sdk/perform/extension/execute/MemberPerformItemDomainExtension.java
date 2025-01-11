/**
 * @(#)PerformItemDomainExtension.java, 一月 11, 2025.
 * <p>
 * Copyright 2025 fenbi.com. All rights reserved.
 * FENBI.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.sdk.perform.extension.execute;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.memberclub.common.extension.BaseExtension;
import com.memberclub.domain.context.perform.PerformItemContext;
import com.memberclub.domain.dataobject.perform.MemberPerformItemDO;
import com.memberclub.domain.entity.MemberPerformItem;

/**
 * author: 掘金五阳
 * 数据库层扩展点
 */
public interface MemberPerformItemDomainExtension extends BaseExtension {

    /**
     * @param item
     * @param context
     * @param wrapper 如果你觉得 wrapper的调用方式不符合你的场景,可以丢弃,在业务的扩展点中自行编写 Dao层调用代码
     */
    public void onPerformSuccess(MemberPerformItemDO item, PerformItemContext context, LambdaUpdateWrapper<MemberPerformItem> wrapper);
}