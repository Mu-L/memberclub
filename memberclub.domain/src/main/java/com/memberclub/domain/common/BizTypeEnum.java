/**
 * @(#)BizTypeEnum.java, 十二月 14, 2024.
 * <p>
 * Copyright 2024 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.domain.common;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * 业务身份用于在系统交易流程中区分不同的业务，如以下业务身份包含普通会员类、视频会员、音乐会员、优惠卡包、在线课程等不同产品形态，
 * MemberClub能快速开发实现虚拟商品的交易能力，这需要系统能识别业务身份，并且根据业务身份进行流程编排和能力扩展。
 *
 * @author 掘金五阳
 */
public enum BizTypeEnum {

    DEFAULT(0, "default_biz"),
    DEMO_MEMBER(1, "demo_member"),
    VIDEO_MEMBER(3, "video_member"),
    MUSIC_MEMBER(4, "music_member"),
    DOUYIN_COUPON_PACKAGE(2, "douyin_coupon_package"),//douyin 优惠券包，支持过期退、多份数购买
    LESSON(8, "lesson"),
    ;

    private int code;

    @Getter
    private String name;

    BizTypeEnum(int code, String name) {
        this.code = code;
        this.name = name;
    }

    @JsonCreator
    public static BizTypeEnum findByCode(int value) throws IllegalArgumentException {
        for (BizTypeEnum item : BizTypeEnum.values()) {
            if (item.code == value) {
                return item;
            }
        }

        throw new IllegalArgumentException("Invalid BizTypeEnum code: " + value);
    }

    public boolean isEqual(int bizType) {
        return this.code == bizType;
    }

    public BizScene toBizScene() {
        return BizScene.of(code);
    }

    public BizScene toBizScene(String sceneName) {
        return BizScene.of(code, sceneName);
    }

    @Override
    public String toString() {
        return this.name;
    }

    @JsonValue
    public int getCode() {
        return this.code;
    }
}
