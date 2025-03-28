/**
 * @(#)MemberTradeEvent.java, 十二月 15, 2024.
 * <p>
 * Copyright 2024 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.domain.common;

import com.google.common.collect.ImmutableList;

import java.util.List;

import static com.memberclub.domain.context.perform.common.MemberOrderPerformStatusEnum.INIT;
import static com.memberclub.domain.context.perform.common.MemberOrderPerformStatusEnum.PERFORMED;
import static com.memberclub.domain.context.perform.common.MemberOrderPerformStatusEnum.PERFORMING;

/**
 * @author wuyang
 */
public enum MemberTradeEvent {

    MEMBER_ORDER_START_PERFORM("member_order_start_perform", PERFORMING.getCode(),
            ImmutableList.of(INIT.getCode(), PERFORMING.getCode())),

    MEMBER_ORDER_SUCCESS_PERFORM("member_order_success_perform", PERFORMED.getCode(),
            ImmutableList.of(PERFORMING.getCode(), PERFORMED.getCode()));

    private List<Integer> fromStatus;

    private int toStatus;

    private String name;

    MemberTradeEvent(String name, int toStatus, List<Integer> fromStatus) {
        this.toStatus = toStatus;
        this.name = name;
        this.fromStatus = fromStatus;
    }

    public static MemberTradeEvent findByCode(int value) throws IllegalArgumentException {
        for (MemberTradeEvent item : MemberTradeEvent.values()) {
            if (item.toStatus == value) {
                return item;
            }
        }

        throw new IllegalArgumentException("Invalid MemberTradeEvent toStatus: " + value);
    }

    @Override
    public String toString() {
        return this.name;
    }

    public List<Integer> getFromStatus() {
        return fromStatus;
    }

    public MemberTradeEvent setFromStatus(List<Integer> fromStatus) {
        this.fromStatus = fromStatus;
        return this;
    }

    public int getToStatus() {
        return toStatus;
    }

    public MemberTradeEvent setToStatus(int toStatus) {
        this.toStatus = toStatus;
        return this;
    }

    public String getName() {
        return name;
    }

    public MemberTradeEvent setName(String name) {
        this.name = name;
        return this;
    }
}
