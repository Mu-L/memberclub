/**
 * @(#)AftersaleOrderDao.java, 一月 01, 2025.
 * <p>
 * Copyright 2025 fenbi.com. All rights reserved.
 * FENBI.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.infrastructure.mybatis.mappers;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.memberclub.common.annotation.QueryMaster;
import com.memberclub.domain.entity.AftersaleOrder;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * author: 掘金五阳
 */
@Mapper
public interface AftersaleOrderDao extends BaseMapper<AftersaleOrder> {

    public static final String TABLE_NAME = "aftersale_order";

    Integer insertIgnoreBatch(List<AftersaleOrder> orders);

    @QueryMaster
    @Select({
            "SELECT * FROM ", TABLE_NAME,
            " WHERE user_id=#{userId} AND id=#{id} "
    })
    public AftersaleOrder queryById(@Param("userId") long userId, @Param("id") Long id);


    @Update({
            "UPDATE ", TABLE_NAME, " SET status=#{status} ,utime=#{utime} ",
            " WHERE user_id=#{userId} AND id=#{id} "
    })
    public int update2Succ(@Param("userId") long userId, @Param("id") Long id,
                           @Param("status") int status, @Param("utime") long utime);
}