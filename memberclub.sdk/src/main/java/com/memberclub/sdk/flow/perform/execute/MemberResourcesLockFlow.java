/**
 * @(#)MemberResourcesLockFlow.java, 十二月 15, 2024.
 * <p>
 * Copyright 2024 fenbi.com. All rights reserved.
 * FENBI.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.sdk.flow.perform.execute;

import com.memberclub.common.exception.ResultCode;
import com.memberclub.common.extension.ExtensionManger;
import com.memberclub.common.flow.FlowNode;
import com.memberclub.common.log.CommonLog;
import com.memberclub.common.monitor.Monitor;
import com.memberclub.common.util.TimeUtil;
import com.memberclub.domain.dataobject.perform.PerformContext;
import com.memberclub.infrastructure.lock.DistributeLock;
import com.memberclub.sdk.common.LockMode;
import com.memberclub.sdk.config.SwitchEnum;
import com.memberclub.sdk.extension.config.BizConfigTable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * author: 掘金五阳
 */
@Service
public class MemberResourcesLockFlow extends FlowNode<PerformContext> {

    @Autowired
    private ExtensionManger extensionManger;

    @Autowired
    private DistributeLock distributeLock;

    @Override
    public void process(PerformContext context) {
        BizConfigTable table = extensionManger.getExtension(context.toDefaultScene(), BizConfigTable.class);

        String key = buildKey(context, table);
        if (context.getLockValue() == null) {
            String value = String.valueOf(TimeUtil.now());
            context.setLockValue(value);
        }

        boolean locked = distributeLock.lock(key, context.getLockValue(), SwitchEnum.LOCK_TIMEOUT_SECONDS.getInt(context.getBizType().toBizType()));
        if (!locked) {
            CommonLog.error("加锁失败,需要再次重试 key:{}, value:{}", key, context.getLockValue());
            Monitor.PERFORM.report(context.getBizType().toBizType(), "lock_fail");
            ResultCode.LOCK_ERROR.throwException();
        }
        CommonLog.error("加锁成功 key:{}, value:{}", key, context.getLockValue());
        Monitor.PERFORM.report(context.getBizType().toBizType(), "lock_succ");
    }

    private String buildKey(PerformContext context, BizConfigTable table) {
        String format = "%s_%s_lock";
        String key = null;
        if (table.getLockMode() == LockMode.LOCK_ORDER) {
            key = String.format(format, context.getBizType().toBizType(), context.getTradeId());
        } else if (table.getLockMode() == LockMode.LOCK_USER) {
            key = String.format(format, context.getBizType().toBizType(), context.getUserId());
        }
        return key;
    }

    @Override
    public void success(PerformContext context) {
        BizConfigTable table = extensionManger.getExtension(context.toDefaultScene(), BizConfigTable.class);
        String key = buildKey(context, table);
        CommonLog.error("尝试解锁 key:{}, value:{}", key, context.getLockValue());
        Monitor.PERFORM.report(context.getBizType().toBizType(), "success_unlock_attempt");

        //内部重试解锁,务必确保解锁成功
        distributeLock.unlock(key, context.getLockValue());
    }

    @Override
    public void rollback(PerformContext context) {
        BizConfigTable table = extensionManger.getExtension(context.toDefaultScene(), BizConfigTable.class);
        String key = buildKey(context, table);
        CommonLog.error("回滚阶段尝试解锁 key:{}, value:{}", key, context.getLockValue());
        Monitor.PERFORM.report(context.getBizType().toBizType(), "rollback_unlock_attempt");
        if (table.getLockMode() == LockMode.LOCK_ORDER) {
            distributeLock.unlock(key, context.getLockValue());
        } else if (table.getLockMode() == LockMode.LOCK_USER) {
            if (context.getRetryTimes() >= SwitchEnum.PERFORM_RETRY_MAX_TIME.getInt(context.getBizType().toBizType())) {
                distributeLock.unlock(key, context.getLockValue());
            } else {
                CommonLog.warn("用户维度锁,在履约失败,暂时不释放");
            }
        }
    }
}