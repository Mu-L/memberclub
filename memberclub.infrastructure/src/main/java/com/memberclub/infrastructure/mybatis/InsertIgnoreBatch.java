/**
 * @(#)InsertIgnoreBatch.java, 十二月 17, 2024.
 * <p>
 * Copyright 2024 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.infrastructure.mybatis;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.core.enums.SqlMethod;
import com.baomidou.mybatisplus.core.injector.AbstractMethod;
import com.baomidou.mybatisplus.core.metadata.TableFieldInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.core.toolkit.sql.SqlScriptUtils;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.apache.ibatis.executor.keygen.Jdbc3KeyGenerator;
import org.apache.ibatis.executor.keygen.KeyGenerator;
import org.apache.ibatis.executor.keygen.NoKeyGenerator;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlSource;

import java.util.List;
import java.util.function.Predicate;

/**
 * 批量新增数据,自选字段 insert
 * <p> 不同的数据库支持度不一样!!!  只在 mysql 下测试过!!!  只在 mysql 下测试过!!!  只在 mysql 下测试过!!! </p>
 * <p> 除了主键是 <strong> 数据库自增的未测试 </strong> 外理论上都可以使用!!! </p>
 * <p> 如果你使用自增有报错或主键值无法回写到entity,就不要跑来问为什么了,因为我也不知道!!! </p>
 * <p>
 * 自己的通用 mapper 如下使用:
 * <pre>
 * int   insertIgnoreBatch(List entityList);
 * </pre>
 * </p>
 *
 * <li> 注意: 这是自选字段 insert !!,如果个别字段在 entity 里为 null 但是数据库中有配置默认值, insert 后数据库字段是为 null 而不是默认值 </li>
 *
 * <p>
 * 常用的 {@link Predicate}:
 * </p>
 *
 * <li> 例1: t -> !t.isLogicDelete() , 表示不要逻辑删除字段 </li>
 * <li> 例2: t -> !t.getProperty().equals("version") , 表示不要字段名为 version 的字段 </li>
 * <li> 例3: t -> t.getFieldFill() != FieldFill.UPDATE) , 表示不要填充策略为 UPDATE 的字段 </li>
 *
 * @author miemie
 * @since 2018-11-29
 */

@SuppressWarnings("serial")
public class InsertIgnoreBatch extends AbstractMethod {

    /**
     * 字段筛选条件
     */
    @Setter
    @Accessors(chain = true)
    private Predicate<TableFieldInfo> predicate;

    /**
     * 默认方法名
     */
    public InsertIgnoreBatch() {
        super("insertIgnoreBatch");
    }

    /**
     * 默认方法名
     *
     * @param predicate 字段筛选条件
     */
    public InsertIgnoreBatch(Predicate<TableFieldInfo> predicate) {
        super("insertIgnoreBatch");
        this.predicate = predicate;
    }

    /**
     * @param name      方法名
     * @param predicate 字段筛选条件
     * @since 3.5.0
     */
    public InsertIgnoreBatch(String name, Predicate<TableFieldInfo> predicate) {
        super(name);
        this.predicate = predicate;
    }

    @SuppressWarnings("Duplicates")
    @Override
    public MappedStatement injectMappedStatement(Class<?> mapperClass, Class<?> modelClass, TableInfo tableInfo) {
        KeyGenerator keyGenerator = NoKeyGenerator.INSTANCE;
        String sqlFormat = "<script>\nINSERT IGNORE INTO %s %s VALUES %s\n</script>";

        List<TableFieldInfo> fieldList = tableInfo.getFieldList();
        String insertSqlColumn = tableInfo.getKeyInsertSqlColumn(true, false) +
                this.filterTableFieldInfo(fieldList, predicate, TableFieldInfo::getInsertSqlColumn, EMPTY);
        String columnScript = LEFT_BRACKET + insertSqlColumn.substring(0, insertSqlColumn.length() - 1) + RIGHT_BRACKET;
        String insertSqlProperty = tableInfo.getKeyInsertSqlProperty(true, ENTITY_DOT, false) +
                this.filterTableFieldInfo(fieldList, predicate, i -> i.getInsertSqlProperty(ENTITY_DOT), EMPTY);
        insertSqlProperty = LEFT_BRACKET + insertSqlProperty.substring(0, insertSqlProperty.length() - 1) + RIGHT_BRACKET;
        String valuesScript = SqlScriptUtils.convertForeach(insertSqlProperty, "list", null, ENTITY, COMMA);
        String keyProperty = null;
        String keyColumn = null;
        // 表包含主键处理逻辑,如果不包含主键当普通字段处理
        if (tableInfo.havePK()) {
            if (tableInfo.getIdType() == IdType.AUTO) {
                /* 自增主键 */
                keyGenerator = Jdbc3KeyGenerator.INSTANCE;
                keyProperty = tableInfo.getKeyProperty();
                keyColumn = tableInfo.getKeyColumn();
            } else {
                if (null != tableInfo.getKeySequence()) {
                    keyGenerator = TableInfoHelper.genKeyGenerator(this.methodName, tableInfo, builderAssistant);
                    keyProperty = tableInfo.getKeyProperty();
                    keyColumn = tableInfo.getKeyColumn();
                }
            }
        }
        String sql = String.format(sqlFormat, tableInfo.getTableName(), columnScript, valuesScript);
        System.out.println("Sql:" + sql);
        SqlSource sqlSource = languageDriver.createSqlSource(configuration, sql, modelClass);
        return this.addInsertMappedStatement(mapperClass, modelClass, getMethod(SqlMethod.INSERT_ONE), sqlSource, keyGenerator, keyProperty, keyColumn);
    }

}
