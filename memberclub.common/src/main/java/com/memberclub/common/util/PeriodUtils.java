/**
 * @(#)PeriodUtils.java, 十二月 14, 2024.
 * <p>
 * Copyright 2024 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.common.util;

import com.memberclub.domain.exception.ResultCode;

/**
 * @author 掘金五阳
 */
public class PeriodUtils {

    public static TimeRange buildTimeRangeFromBaseTime(int periodDays) {
        return buildTimeRangeFromBaseTime(TimeUtil.now(), periodDays, true);
    }


    public static TimeRange buildTimeRangeFromBaseTime(long baseTime, int periodDays, boolean containsFirstDay) {
        if (containsFirstDay) {
            periodDays = periodDays - 1;//包含当天
        }
        if (periodDays < 0) {
            throw ResultCode.CONFIG_DATA_ERROR.newException("配置数据有误, periodDays 不能小于 0");
        }

        long now = baseTime;
        long etime = TimeUtil.plusGivenDayEtime(now, periodDays);
        return new TimeRange(now, etime);
    }
}