/**
 * @(#)TestDemoMember.java, 十二月 21, 2024.
 * <p>
 * Copyright 2024 fenbi.com. All rights reserved.
 * FENBI.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.starter.demomember;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.memberclub.common.log.CommonLog;
import com.memberclub.common.util.EncrptUtils;
import com.memberclub.common.util.JsonUtils;
import com.memberclub.common.util.PeriodUtils;
import com.memberclub.common.util.TimeRange;
import com.memberclub.common.util.TimeUtil;
import com.memberclub.domain.common.BizTypeEnum;
import com.memberclub.domain.common.MemberOrderStatusEnum;
import com.memberclub.domain.common.MemberPerformHisStatusEnum;
import com.memberclub.domain.common.OrderSystemTypeEnum;
import com.memberclub.domain.common.PerformItemStatusEnum;
import com.memberclub.domain.common.PeriodTypeEnum;
import com.memberclub.domain.context.aftersale.apply.AftersaleApplyCmd;
import com.memberclub.domain.context.aftersale.apply.AftersaleApplyResponse;
import com.memberclub.domain.context.aftersale.contant.AftersaleSourceEnum;
import com.memberclub.domain.context.aftersale.contant.AftersaleUnableCode;
import com.memberclub.domain.context.aftersale.contant.RefundTypeEnum;
import com.memberclub.domain.context.aftersale.preview.AfterSalePreviewCmd;
import com.memberclub.domain.context.aftersale.preview.AfterSalePreviewResponse;
import com.memberclub.domain.context.perform.PerformCmd;
import com.memberclub.domain.context.perform.PerformResp;
import com.memberclub.domain.dataobject.CommonUserInfo;
import com.memberclub.domain.dataobject.order.MemberOrderExtraInfo;
import com.memberclub.domain.dataobject.order.MemberOrderLocationInfo;
import com.memberclub.domain.dataobject.perform.SkuBuyDetailDO;
import com.memberclub.domain.dataobject.sku.MemberSkuSnapshotDO;
import com.memberclub.domain.dataobject.sku.SkuPerformConfigDO;
import com.memberclub.domain.dataobject.sku.SkuPerformItemConfigDO;
import com.memberclub.domain.dataobject.sku.SkuSaleInfo;
import com.memberclub.domain.dataobject.sku.SkuSettleInfo;
import com.memberclub.domain.dataobject.sku.SkuViewInfo;
import com.memberclub.domain.dataobject.sku.rights.RightViewInfo;
import com.memberclub.domain.dataobject.task.OnceTaskDO;
import com.memberclub.domain.dataobject.task.TaskContentDO;
import com.memberclub.domain.dataobject.task.perform.PerformTaskContentDO;
import com.memberclub.domain.entity.MemberOrder;
import com.memberclub.domain.entity.MemberPerformHis;
import com.memberclub.domain.entity.MemberPerformItem;
import com.memberclub.domain.entity.OnceTask;
import com.memberclub.domain.facade.AssetDO;
import com.memberclub.infrastructure.mapstruct.AftersaleConvertor;
import com.memberclub.infrastructure.mapstruct.ConvertorMethod;
import com.memberclub.infrastructure.mapstruct.PerformConvertor;
import com.memberclub.infrastructure.mybatis.mappers.MemberOrderDao;
import com.memberclub.infrastructure.mybatis.mappers.MemberPerformHisDao;
import com.memberclub.infrastructure.mybatis.mappers.MemberPerformItemDao;
import com.memberclub.infrastructure.mybatis.mappers.OnceTaskDao;
import com.memberclub.sdk.aftersale.service.AftersaleBizService;
import com.memberclub.sdk.perform.service.PerformBizService;
import com.memberclub.starter.mock.MockBaseTest;
import lombok.SneakyThrows;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.assertj.core.util.Lists;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * author: 掘金五阳
 */
public class TestDemoMember extends MockBaseTest {

    @Autowired
    private MemberPerformHisDao memberPerformHisDao;

    @Autowired
    private AftersaleBizService aftersaleBizService;

    @Autowired
    private OnceTaskDao onceTaskDao;

    @SneakyThrows
    @Test
    public void testDefaultMemberAndRetry() {
        MemberOrder memberOrder = buildMemberOrder();
        memberOrderDao.insert(memberOrder);

        Mockito.reset(couponGrantFacade);

        Mockito.doThrow(new RuntimeException("mock error"))
                .when(couponGrantFacade).grant(Mockito.any());

        MemberOrder orderInDb = memberOrderDao.selectByTradeId(memberOrder.getUserId(), memberOrder.getTradeId());
        System.out.println(JsonUtils.toJson(orderInDb));

        PerformCmd cmd = buildCmd(memberOrder);

        try {
            PerformResp resp = performBizService.perform(cmd);
            Assert.assertTrue(resp.isSuccess());
            verifyData(cmd);
        } catch (Exception e) {
            CommonLog.error("首次调用出现异常", e);
        }

        Mockito.doCallRealMethod()
                .when(couponGrantFacade).grant(Mockito.any());
        Thread.sleep(1500);
        verifyData(cmd);
    }


    @SneakyThrows
    @Test
    public void testDefaultMemberAndMutilPeriodCardAndPeriodPerform() {
        int buyCount = 1;
        MemberOrder memberOrder = buildMemberOrder(buyCount, 3);
        memberOrderDao.insert(memberOrder);

        MemberOrder orderInDb = memberOrderDao.selectByTradeId(memberOrder.getUserId(), memberOrder.getTradeId());
        System.out.println(JsonUtils.toJson(orderInDb));

        PerformCmd cmd = buildCmd(memberOrder);

        PerformResp resp = performBizService.perform(cmd);
        Assert.assertTrue(resp.isSuccess());
        verifyData(cmd, 1);

        verifyTaskData(cmd, 2);

        List<OnceTask> tasks = onceTaskDao.queryTasksByUserId(cmd.getUserId());

        List<OnceTaskDO> taskDOS = tasks.stream().map(task -> {
            OnceTaskDO taskDO = PerformConvertor.INSTANCE.toOnceTaskDO(task);
            TaskContentDO contentDO = ConvertorMethod.toTaskContentDO(task.getContent(), task.getTaskContentClassName());
            taskDO.setTradeId(((PerformTaskContentDO) contentDO).getTradeId());
            taskDO.setContent(contentDO);
            return taskDO;
        }).collect(Collectors.toList());
        for (OnceTaskDO taskDO : taskDOS) {
            performBizService.periodPerform(taskDO);
        }
        verifyData(cmd, 3);
        //Thread.sleep(1000000);
    }


    @SneakyThrows
    @Test
    public void testDefaultMemberAndMutilPeriodCard() {
        int buyCount = 1;
        MemberOrder memberOrder = buildMemberOrder(buyCount, 3);
        memberOrderDao.insert(memberOrder);

        MemberOrder orderInDb = memberOrderDao.selectByTradeId(memberOrder.getUserId(), memberOrder.getTradeId());
        System.out.println(JsonUtils.toJson(orderInDb));

        PerformCmd cmd = buildCmd(memberOrder);

        PerformResp resp = performBizService.perform(cmd);
        Assert.assertTrue(resp.isSuccess());
        verifyData(cmd, 1);

        verifyTaskData(cmd, 2);
        //Thread.sleep(1000000);
    }

    @SneakyThrows
    @Test
    public void testDefaultMemberAndBuyCount() {
        int buyCount = 3;
        MemberOrder memberOrder = buildMemberOrder(buyCount, 1);
        memberOrderDao.insert(memberOrder);

        MemberOrder orderInDb = memberOrderDao.selectByTradeId(memberOrder.getUserId(), memberOrder.getTradeId());
        System.out.println(JsonUtils.toJson(orderInDb));

        PerformCmd cmd = buildCmd(memberOrder);

        PerformResp resp = performBizService.perform(cmd);
        Assert.assertTrue(resp.isSuccess());
        verifyData(cmd, buyCount);

        //Thread.sleep(1000000);
    }


    @SneakyThrows
    @Test
    public void testDefaultMember() {
        MemberOrder memberOrder = buildMemberOrder();
        memberOrderDao.insert(memberOrder);

        MemberOrder orderInDb = memberOrderDao.selectByTradeId(memberOrder.getUserId(), memberOrder.getTradeId());
        System.out.println(JsonUtils.toJson(orderInDb));

        PerformCmd cmd = buildCmd(memberOrder);

        PerformResp resp = performBizService.perform(cmd);
        Assert.assertTrue(resp.isSuccess());
        verifyData(cmd);

        //Thread.sleep(1000000);
    }


    @Test
    public void testDefaultMemberRefundAndApply() {
        MemberOrder memberOrder = buildMemberOrder();
        memberOrderDao.insert(memberOrder);

        MemberOrder orderInDb = memberOrderDao.selectByTradeId(memberOrder.getUserId(), memberOrder.getTradeId());
        System.out.println(JsonUtils.toJson(orderInDb));

        PerformCmd cmd = buildCmd(memberOrder);

        PerformResp resp = performBizService.perform(cmd);
        Assert.assertTrue(resp.isSuccess());
        verifyData(cmd);

        AfterSalePreviewCmd previewCmd = new AfterSalePreviewCmd();
        previewCmd.setUserId(cmd.getUserId());
        previewCmd.setBizType(BizTypeEnum.DEMO_MEMBER);
        previewCmd.setTradeId(cmd.getTradeId());
        previewCmd.setSource(AftersaleSourceEnum.User);
        previewCmd.setOperator(String.valueOf(cmd.getUserId()));
        previewCmd.setOrderId(cmd.getOrderId());
        previewCmd.setOrderSystemTypeEnum(cmd.getOrderSystemType());


        AfterSalePreviewResponse response = aftersaleBizService.preview(previewCmd);
        Assert.assertEquals(true, response.isAftersaleEnabled());
        Assert.assertEquals(RefundTypeEnum.ALL_REFUND, response.getRefundType());

        AftersaleApplyCmd applyCmd = AftersaleConvertor.INSTANCE.toAftersaleApplyCmd(previewCmd);
        applyCmd.setDigests(response.getDigests());
        applyCmd.setDigestVersion(response.getDigestVersion());

        AftersaleApplyResponse applyResponse = aftersaleBizService.apply(applyCmd);
        Assert.assertTrue(applyResponse.isSuccess());
    }


    @Test
    public void testDefaultMemberRefund() {
        MemberOrder memberOrder = buildMemberOrder();
        memberOrderDao.insert(memberOrder);

        MemberOrder orderInDb = memberOrderDao.selectByTradeId(memberOrder.getUserId(), memberOrder.getTradeId());
        System.out.println(JsonUtils.toJson(orderInDb));

        PerformCmd cmd = buildCmd(memberOrder);

        PerformResp resp = performBizService.perform(cmd);
        Assert.assertTrue(resp.isSuccess());
        verifyData(cmd);

        AfterSalePreviewCmd previewCmd = new AfterSalePreviewCmd();
        previewCmd.setUserId(cmd.getUserId());
        previewCmd.setBizType(BizTypeEnum.DEMO_MEMBER);
        previewCmd.setTradeId(cmd.getTradeId());
        previewCmd.setSource(AftersaleSourceEnum.User);
        previewCmd.setOperator(String.valueOf(cmd.getUserId()));
        previewCmd.setOrderId(cmd.getOrderId());
        previewCmd.setOrderSystemTypeEnum(cmd.getOrderSystemType());


        AfterSalePreviewResponse respose = aftersaleBizService.preview(previewCmd);
        Assert.assertEquals(true, respose.isAftersaleEnabled());
        Assert.assertEquals(RefundTypeEnum.ALL_REFUND, respose.getRefundType());

        /*******************部分使用,结果为部分退********/
        for (Map.Entry<String, List<AssetDO>> entry : couponGrantFacade.assetBatchCode2Assets.entrySet()) {
            entry.getValue().get(0).setStatus(1);
        }
        respose = aftersaleBizService.preview(previewCmd);
        Assert.assertEquals(true, respose.isAftersaleEnabled());
        Assert.assertEquals(RefundTypeEnum.PORTION_RFUND, respose.getRefundType());


        AftersaleApplyCmd applyCmd = new AftersaleApplyCmd();
        applyCmd = PerformConvertor.INSTANCE.toApplyCmd(previewCmd);
        applyCmd.setDigests(respose.getDigests());
        applyCmd.setDigestVersion(respose.getDigestVersion());
        //applyCmd.setDigestVersion(0);
        AftersaleApplyResponse aftersaleApplyResponse = aftersaleBizService.apply(applyCmd);
        Assert.assertTrue(aftersaleApplyResponse.isSuccess());

        /*******************已用尽,结果为不可退********/

        for (Map.Entry<String, List<AssetDO>> entry : couponGrantFacade.assetBatchCode2Assets.entrySet()) {
            for (AssetDO assetDO : entry.getValue()) {
                assetDO.setStatus(1);
            }
        }
        respose = aftersaleBizService.preview(previewCmd);
        Assert.assertEquals(false, respose.isAftersaleEnabled());
        Assert.assertEquals(AftersaleUnableCode.USE_OUT_ERROR.toInt(), respose.getUnableCode());
    }


    private void verifyData(PerformCmd cmd) {
        verifyData(cmd, 1);
    }

    private void verifyData(PerformCmd cmd, int buyCount) {
        List<MemberPerformHis> hisList = memberPerformHisDao.selectByUserId(cmd.getUserId());
        for (MemberPerformHis memberPerformHis : hisList) {
            Assert.assertEquals(MemberPerformHisStatusEnum.PERFORM_SUCC.toInt(), memberPerformHis.getStatus());
        }
        Assert.assertEquals(1, hisList.size());
        List<MemberPerformItem> items = memberPerformItemDao.selectByTradeId(cmd.getUserId(), cmd.getTradeId());
        for (MemberPerformItem item : items) {
            Assert.assertEquals(PerformItemStatusEnum.PERFORM_SUCC.toInt(), item.getStatus());
        }
        Assert.assertEquals(buyCount, items.size());

        MemberOrder orderFromDb = memberOrderDao.selectByTradeId(cmd.getUserId(), cmd.getTradeId());
        Assert.assertEquals(MemberOrderStatusEnum.PERFORMED.toInt(), orderFromDb.getStatus());

        System.out.println(JsonUtils.toJson(hisList));
    }

    private void verifyTaskData(PerformCmd cmd, int taskSize) {
        List<OnceTask> tasks = onceTaskDao.queryTasksByUserId(cmd.getUserId());
        Assert.assertEquals(taskSize, tasks.size());
    }


    @SneakyThrows
    private PerformCmd buildCmd(MemberOrder memberOrder) {
        PerformCmd cmd = new PerformCmd();
        cmd.setOrderId(memberOrder.getOrderId());
        cmd.setActPriceFen(memberOrder.getActPriceFen());
        cmd.setBizType(BizTypeEnum.DEMO_MEMBER);
        cmd.setOrderSystemType(OrderSystemTypeEnum.COMMON_ORDER);
        cmd.setOriginPriceFen(memberOrder.getOriginPriceFen());
        cmd.setUserId(memberOrder.getUserId());
        cmd.setTradeId(String.format("%s_%s", cmd.getOrderSystemType().toInt(), cmd.getOrderId()));
        return cmd;
    }


    public AtomicLong userIdGenerator = new AtomicLong(RandomUtils.nextInt());

    public AtomicLong orderIdGenerator = new AtomicLong(System.currentTimeMillis());

    private MemberOrder buildMemberOrder() {
        return buildMemberOrder(1, 1);
    }


    @SneakyThrows
    private MemberOrder buildMemberOrder(int buyCount, int cycle) {
        MemberOrder memberOrder = new MemberOrder();
        memberOrder.setUserId(userIdGenerator.incrementAndGet());
        memberOrder.setOrderId(orderIdGenerator.incrementAndGet() + "");
        memberOrder.setOrderSystemType(1);
        memberOrder.setOriginPriceFen("3000");
        memberOrder.setActPriceFen("699");
        memberOrder.setBizType(1);
        memberOrder.setCtime(TimeUtil.now());
        memberOrder.setExtra("{}");
        memberOrder.setStatus(MemberOrderStatusEnum.PAYED.toInt());
        memberOrder.setTradeId(String.format("%s_%s", memberOrder.getOrderSystemType(), memberOrder.getOrderId()));

        List<SkuBuyDetailDO> skuBuyDetailDOS = Lists.newArrayList();
        SkuBuyDetailDO skuBuyDetailDO = new SkuBuyDetailDO();
        skuBuyDetailDOS.add(skuBuyDetailDO);
        skuBuyDetailDO.setBuyCount(buyCount);
        skuBuyDetailDO.setSkuId(439434);
        MemberSkuSnapshotDO snapshotDO = new MemberSkuSnapshotDO();
        skuBuyDetailDO.setSkuSnapshot(snapshotDO);

        SkuSaleInfo skuSaleInfo = new SkuSaleInfo();
        skuSaleInfo.setOriginPriceFen(3000);
        skuSaleInfo.setSalePriceFen(699);

        snapshotDO.setSaleInfo(skuSaleInfo);

        SkuSettleInfo settleInfo = new SkuSettleInfo();
        settleInfo.setContractorId("438098434");
        settleInfo.setSettlePriceFen(300);

        snapshotDO.setSettleInfo(settleInfo);

        SkuViewInfo viewInfo = new SkuViewInfo();
        viewInfo.setDisplayDesc("大额红包");
        viewInfo.setDisplayName("大额红包");
        viewInfo.setInternalDesc("大额红包 5 元");
        viewInfo.setInternalName("大额红包 5 元");
        snapshotDO.setViewInfo(viewInfo);


        SkuPerformConfigDO skuPerformConfigDO = new SkuPerformConfigDO();
        snapshotDO.setPerformConfig(skuPerformConfigDO);


        SkuPerformItemConfigDO skuPerformItemConfigDO = new SkuPerformItemConfigDO();
        skuPerformItemConfigDO.setAssetCount(4);
        skuPerformItemConfigDO.setBizType(1);
        skuPerformItemConfigDO.setCycle(cycle);
        skuPerformItemConfigDO.setPeriodType(PeriodTypeEnum.FIX_DAY.toInt());
        skuPerformItemConfigDO.setRightId(32424);
        skuPerformItemConfigDO.setPeriodCount(31);
        skuPerformItemConfigDO.setRightType(1);
        skuPerformItemConfigDO.setProviderId("1");
        RightViewInfo rightViewInfo = new RightViewInfo();
        rightViewInfo.setDisplayName("默认权益");
        skuPerformItemConfigDO.setViewInfo(rightViewInfo);

        skuPerformConfigDO.setConfigs(ImmutableList.of(skuPerformItemConfigDO));
        snapshotDO.setPerformConfig(skuPerformConfigDO);


        CommonUserInfo userInfo = new CommonUserInfo();
        userInfo.setUuid(RandomStringUtils.randomAlphabetic(12));
        userInfo.setPhone(RandomStringUtils.randomNumeric(11));
        userInfo.setMaskedPhone(EncrptUtils.generatePhoneMask(userInfo.getPhone()));


        String key = EncrptUtils.generateAESKey();

        userInfo.setKey(key);
        userInfo.setEncryptedPhone(EncrptUtils.encryptAES(userInfo.getPhone(), key));

        MemberOrderLocationInfo locationInfo = new MemberOrderLocationInfo();
        locationInfo.setActualLatitude("8493458355");
        locationInfo.setActualLongitude("48934834");
        locationInfo.setActualSecondCityId("110100");
        locationInfo.setActualThirdCityId("4384394");

        MemberOrderExtraInfo extraInfo = new MemberOrderExtraInfo();
        extraInfo.setUserInfo(userInfo);
        extraInfo.setLocationInfo(locationInfo);

        memberOrder.setExtra(JsonUtils.toJson(extraInfo));

        System.out.println("解密后的电话号码： " + EncrptUtils.decrypt(userInfo.getEncryptedPhone(), userInfo.getKey()));

        memberOrder.setSkuDetails(JsonUtils.toJson(skuBuyDetailDOS));
        return memberOrder;
    }

    @Test
    public void test() {
        System.out.println("启动 ok");
        memberPerformHisDao.selectByUserId(1000);
        MemberPerformHis his = new MemberPerformHis();
        his.setBizType(1);
        his.setUserId(1000);

        TimeRange timeRange = PeriodUtils.buildTimeRangeFromBaseTime(2);


        his.setEtime(timeRange.getEtime());
        his.setStime(timeRange.getStime());
        his.setSkuId(10);
        his.setOrderId("1001");
        his.setOrderSystemType(1);
        his.setBuyCount(1);
        his.setTradeId("1_1001");


        his.setExtra("{}");
        his.setCtime(TimeUtil.now());
        his.setUtime(TimeUtil.now());

        int count = memberPerformHisDao.insert(his);
        System.out.println("插入数量" + count);
        List<MemberPerformHis> hisLists = memberPerformHisDao.selectByUserId(1000);
        System.out.println(hisLists);
    }

    @Test
    public void testJson() {
        MemberPerformHis his = new MemberPerformHis();
        his.setBizType(1);
        his.setUserId(1000);

        TimeRange timeRange = PeriodUtils.buildTimeRangeFromBaseTime(2);


        his.setEtime(timeRange.getEtime());
        his.setStime(timeRange.getStime());
        his.setSkuId(10);
        his.setOrderId("1001");
        his.setOrderSystemType(1);
        his.setTradeId("1_1001");
        his.setExtra("{}");
        his.setCtime(TimeUtil.now());
        his.setUtime(TimeUtil.now());

        String json = JsonUtils.toJson(his);
        System.out.println(json);
        MemberPerformHis performHis = JsonUtils.fromJson(json, MemberPerformHis.class);
        System.out.println(performHis);
    }

    @Autowired
    private MemberPerformItemDao memberPerformItemDao;

    @Test
    public void testItem() {
        int cnt = 3;
        List<MemberPerformItem> items = Lists.newArrayList();
        for (int i = cnt; i > 0; i--) {
            MemberPerformItem item = new MemberPerformItem();
            item.setAssetCount(4);
            item.setBatchCode("2323");
            item.setBizType(1);
            item.setBuyIndex(1);
            item.setCtime(TimeUtil.now());
            item.setCycle(1);
            item.setPhase(1);
            item.setRightId(1);
            item.setRightType(1);
            item.setSkuId(1);
            item.setGrantType(1);
            item.setStatus(1);
            item.setTradeId("gjeigejoig" + i);
            item.setStime(TimeUtil.now());
            item.setEtime(TimeUtil.now());
            item.setUserId(1);
            item.setUtime(TimeUtil.now());
            items.add(item);
        }

        int num = memberPerformItemDao.insertIgnoreBatch(items);
        System.out.println(num);

        List<MemberPerformItem> itemsDb = memberPerformItemDao.selectByMap(ImmutableMap.of("user_id", 1));
        System.out.println(JsonUtils.toJson(itemsDb));
    }

    @Autowired
    private MemberOrderDao memberOrderDao;

    @Autowired
    private PerformBizService performBizService;

    /*@SpyBean(name = "couponGrantFacade", value = MockCouponGrantFacade.class)
    private CouponGrantFacade mockCouponGrantFacade;*/
}