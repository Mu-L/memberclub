/**
 * @(#)MemberOrderStatusEnum.java, 十二月 15, 2024.
 * <p>
 * Copyright 2024 fenbi.com. All rights reserved.
 * FENBI.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.domain.context.purchase.common;

/**
 * @author yuhaiqiang
 */
public enum MemberOrderStatusEnum {

    INIT(0, "初始化"),
    FAIL(8, "提单失败"),
    SUBMITED(9, "已提单"),
    CANCELED(19, "已取消"),
    PAYED(29, "已支付"),
    PERFORMING(30, "履约中"),
    PERFORMED(35, "已履约"),
    PORTION_REFUNDED(40, "部分退"),
    COMPLETE_REFUNDED(49, "已完全退款");

    private int value;

    private String name;

    MemberOrderStatusEnum(int value, String name) {
        this.value = value;
        this.name = name;
    }

    public static Boolean isPerformEnabled(int status) {
        return status < PERFORMED.getCode();
    }

    public static Boolean isPerformed(int status) {
        return status == PERFORMED.getCode();
    }

    public static boolean nonPerformed(int status) {
        return status < PERFORMED.getCode();
    }

    public static boolean isRefunded(int status) {
        return status == MemberOrderStatusEnum.COMPLETE_REFUNDED.getCode();
    }


    public static MemberOrderStatusEnum findByCode(int value) throws IllegalArgumentException {
        for (MemberOrderStatusEnum item : MemberOrderStatusEnum.values()) {
            if (item.value == value) {
                return item;
            }
        }

        return null;
    }

    @Override
    public String toString() {
        return this.name;
    }

    public int getCode() {
        return this.value;
    }
}
