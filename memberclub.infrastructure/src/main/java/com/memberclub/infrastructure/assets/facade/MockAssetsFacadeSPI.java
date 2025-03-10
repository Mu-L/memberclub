/**
 * @(#)MockCouponGrantFacade.java, 十二月 18, 2024.
 * <p>
 * Copyright 2024 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.infrastructure.assets.facade;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.memberclub.common.util.TimeUtil;
import com.memberclub.domain.facade.AssetDO;
import com.memberclub.domain.facade.AssetFetchRequestDO;
import com.memberclub.domain.facade.AssetFetchResponseDO;
import com.memberclub.domain.facade.AssetReverseRequestDO;
import com.memberclub.domain.facade.AssetReverseResponseDO;
import com.memberclub.domain.facade.AssetStatusEnum;
import com.memberclub.domain.facade.GrantItemDO;
import com.memberclub.domain.facade.GrantRequestDO;
import com.memberclub.domain.facade.GrantResponseDO;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * author: 掘金五阳
 */
@ConditionalOnProperty(name = "memberclub.infrastructure.asset", havingValue = "local")
@Service
public class MockAssetsFacadeSPI implements AssetsFacadeSPI {

    private AtomicLong couponIdGenerator = new AtomicLong(System.currentTimeMillis());

    public Map<String, List<AssetDO>> assetBatchCode2Assets = Maps.newHashMap();

    @Override
    public GrantResponseDO grant(GrantRequestDO requestDO) {
        GrantResponseDO responseDO = new GrantResponseDO();
        responseDO.setCode(0);
        Map<String, List<AssetDO>> map = Maps.newHashMap();
        for (GrantItemDO grantItem : requestDO.getGrantItems()) {
            List<AssetDO> coupons = Lists.newArrayList();
            String batchCode = RandomStringUtils.randomAlphabetic(10);
            for (int assetCount = grantItem.getAssetCount(); assetCount > 0; assetCount--) {
                AssetDO coupon = new AssetDO();
                coupon.setStime(grantItem.getStime());
                coupon.setEtime(grantItem.getEtime());
                coupon.setRightType(grantItem.getRightType());
                coupon.setCtime(TimeUtil.now());
                coupon.setBatchCode(batchCode);
                coupon.setPriceFen(500);
                coupon.setAssetId(couponIdGenerator.incrementAndGet());
                coupon.setAssetType(1);
                coupon.setUserId(requestDO.getUserId());
                coupons.add(coupon);
            }
            assetBatchCode2Assets.put(batchCode, coupons);
            map.put(grantItem.getItemToken(), coupons);
        }
        responseDO.setItemToken2AssetsMap(map);
        return responseDO;
    }

    @Override
    public AssetFetchResponseDO fetch(AssetFetchRequestDO request) {
        AssetFetchResponseDO resp = new AssetFetchResponseDO();

        Map<String, List<AssetDO>> map = Maps.newHashMap();
        for (String assetBatch : request.getAssetBatchs()) {
            List<AssetDO> assets = assetBatchCode2Assets.get(assetBatch);
            if (CollectionUtils.isNotEmpty(assets)) {
                map.put(assetBatch, assets);
            }
        }
        resp.setAssetBatchCode2AssetsMap(map);
        return resp;
    }

    @Override
    public AssetReverseResponseDO reverse(AssetReverseRequestDO request) {
        AssetReverseResponseDO resp = new AssetReverseResponseDO();

        Map<String, List<AssetDO>> map = Maps.newHashMap();
        for (String assetBatch : request.getAssetBatchs()) {
            List<AssetDO> assets = assetBatchCode2Assets.get(assetBatch);
            for (AssetDO asset : assets) {
                if (asset.getStatus() == AssetStatusEnum.UNUSE.getCode()) {
                    asset.setStatus(AssetStatusEnum.FREEZE.getCode());
                }
            }
            map.put(assetBatch, assets);
        }
        resp.setAssetBatchCode2AssetsMap(map);
        return resp;
    }
}