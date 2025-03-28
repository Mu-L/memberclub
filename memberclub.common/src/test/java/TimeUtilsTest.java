/**
 * @(#)TimeUtilsTest.java, 十二月 14, 2024.
 * <p>
 * Copyright 2024 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

import com.memberclub.common.util.PeriodUtils;
import com.memberclub.common.util.TimeRange;
import com.memberclub.common.util.TimeUtil;
import org.junit.Test;

/**
 * @author 掘金五阳
 */
public class TimeUtilsTest {

    @Test
    public void testTime() {
        String str = TimeUtil.format(TimeUtil.plusGivenDayEtimeFromNow(2));
        System.out.println(str);
    }

    @Test
    public void testRange() {
        TimeRange range = PeriodUtils.buildTimeRangeFromBaseTime(TimeUtil.now(), 2, true);
        System.out.println(range);
    }

}