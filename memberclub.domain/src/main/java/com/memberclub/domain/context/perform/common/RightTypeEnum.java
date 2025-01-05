/**
 * @(#)RightTypeEnum.java, 十二月 22, 2024.
 * <p>
 * Copyright 2024 fenbi.com. All rights reserved.
 * FENBI.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.domain.context.perform.common;

/**
 * @author yuhaiqiang
 */
public enum RightTypeEnum {

    COUPON(1, "会员立减券"),
    DISCOUNT_COUPON(2, "会员折扣券"),
    ;

    private int value;

    private String name;

    RightTypeEnum(int value, String name) {
        this.value = value;
        this.name = name;
    }

    public static RightTypeEnum findByCode(int value) throws IllegalArgumentException {
        for (RightTypeEnum item : RightTypeEnum.values()) {
            if (item.value == value) {
                return item;
            }
        }

        throw new IllegalArgumentException("Invalid RightTypeEnum value: " + value);
    }

    @Override
    public String toString() {
        return this.name;
    }

    public int getCode() {
        return this.value;
    }
}
