/**
 * @(#)MockCouponGrantFacade.java, 十二月 18, 2024.
 * <p>
 * Copyright 2024 fenbi.com. All rights reserved.
 * FENBI.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.infrastruct.facade.impl;

import com.google.common.collect.Maps;
import com.memberclub.common.util.TimeUtil;
import com.memberclub.domain.facade.AssetDO;
import com.memberclub.domain.facade.GrantItemDO;
import com.memberclub.domain.facade.GrantRequestDO;
import com.memberclub.domain.facade.GrantResponseDO;
import com.memberclub.infrastructure.facade.CouponGrantFacade;
import org.apache.commons.lang3.RandomStringUtils;
import org.assertj.core.util.Lists;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * author: 掘金五阳
 */
public class MockCouponGrantFacade implements CouponGrantFacade {

    private AtomicLong couponIdGenerator = new AtomicLong(System.currentTimeMillis());


    @Override
    public GrantResponseDO grant(GrantRequestDO requestDO) {
        GrantResponseDO responseDO = new GrantResponseDO();
        responseDO.setCode(0);
        Map<String, List<AssetDO>> map = Maps.newHashMap();
        for (GrantItemDO grantItem : requestDO.getGrantItems()) {
            List<AssetDO> coupons = Lists.newArrayList();
            String batchCode = RandomStringUtils.random(10);
            for (int assetCount = grantItem.getAssetCount(); assetCount > 0; assetCount--) {
                AssetDO coupon = new AssetDO();
                coupon.setStime(grantItem.getStime());
                coupon.setEtime(grantItem.getEtime());
                coupon.setCtime(TimeUtil.now());
                coupon.setBatchCode(batchCode);
                coupon.setAssetId(couponIdGenerator.incrementAndGet());
                coupon.setAssetType(1);
                coupon.setUserId(requestDO.getUserId());
                coupons.add(coupon);
            }
            map.put(grantItem.getItemToken(), coupons);
        }
        responseDO.setItemToken2CouponMap(map);
        return responseDO;
    }
}