/**
 * @(#)TestDemoMember.java, 十二月 21, 2024.
 * <p>
 * Copyright 2024 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.starter.mock;

import com.memberclub.domain.common.BizTypeEnum;
import com.memberclub.domain.context.common.LockContext;
import com.memberclub.domain.context.common.LockMode;
import com.memberclub.infrastructure.assets.facade.MockAssetsFacadeSPI;
import com.memberclub.infrastructure.mybatis.mappers.trade.MemberOrderDao;
import com.memberclub.infrastructure.mybatis.mappers.trade.MemberSubOrderDao;
import com.memberclub.infrastructure.order.facade.MockCommonOrderFacadeSPI;
import com.memberclub.sdk.lock.LockService;
import com.memberclub.sdk.sku.service.impl.MockSkuBizService;
import com.memberclub.starter.AppStarter;
import lombok.SneakyThrows;
import org.apache.commons.lang3.RandomUtils;
import org.h2.tools.Server;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.atomic.AtomicLong;

/**
 * author: 掘金五阳
 */

//@ActiveProfiles("ut")
@RunWith(SpringRunner.class)
@SpringBootTest(classes = AppStarter.class  /*, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT*/)
public class MockBaseTest {
    @SpyBean
    public MockAssetsFacadeSPI couponGrantFacade;

    @Autowired
    public MockCommonOrderFacadeSPI commonOrderFacadeSPI;

    @Autowired
    public MockSkuBizService mockSkuBizService;
    @Autowired
    public MemberOrderDao memberOrderDao;

    @Autowired
    public MemberSubOrderDao memberSubOrderDao;

    public static final Long DEFAULT_USER_ID = 894838959L;

    public AtomicLong userIdGenerator = new AtomicLong(RandomUtils.nextInt());

    public AtomicLong orderIdGenerator = new AtomicLong(System.currentTimeMillis());

    static boolean isRunH2Servier = false;

    @Autowired
    private LockService lockService;

    @SneakyThrows
    //@BeforeClass
    public static void startH2() {
        Server server = Server.createTcpServer("-tcp", "-tcpAllowOthers", "-webAllowOthers", "-ifNotExists", "-tcpPort",
                "9092");
        isRunH2Servier = true;
        server.start();
    }

    public void releaseLock(Long value) {
        LockContext context = LockContext.builder()
                .userId(DEFAULT_USER_ID)
                .bizType(BizTypeEnum.DEMO_MEMBER)
                .lockMode(LockMode.LOCK_USER)
                .lockValue(value)
                .lockScene("purchase")
                .build();

        lockService.unlock(context);
    }

    public static void waitH2() {
        if (isRunH2Servier) {
            System.out.println("等待输入");
            //new Scanner(System.in).nextLine();
        }
    }

    public static void main(String[] args) {
        startH2();
    }


    @Test
    public void testBase() {

    }

}