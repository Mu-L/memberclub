/**
 * @(#)MemberOrderDO.java, 十二月 14, 2024.
 * <p>
 * Copyright 2024 fenbi.com. All rights reserved.
 * FENBI.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.domain.dataobject.buy;

import com.memberclub.domain.dataobject.perform.MemberSubOrderDO;
import lombok.Data;

import java.util.List;

/**
 * @author 掘金五阳
 */
@Data
public class MemberOrderDO {

    private int bizType;

    private long userId;

    private int orderSystemType;

    private String orderId;

    private String userInfo;

    private String skuDetails;

    private String actPriceFen;

    private String originPriceFen;

    private int status;

    private long stime;

    private long etime;

    private long utime;

    private long ctime;

    private List<MemberSubOrderDO> subOrders;
}