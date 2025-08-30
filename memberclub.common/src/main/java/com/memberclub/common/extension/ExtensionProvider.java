/**
 * @(#)ExtensionImpl.java, 十二月 14, 2024.
 * <p>
 * Copyright 2024 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.common.extension;

import com.memberclub.common.annotation.Route;
import org.springframework.stereotype.Service;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 所有的扩展点实现类均需要标注该注解，用于描述该扩展点所适用的业务身份和业务场景
 * {@link ExtensionManager} 负责解析加载扩展点实现类
 *
 * @author 掘金五阳
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Service
public @interface ExtensionProvider {

    /**
     * <p>
     * 扩展点实现类需要标注自己适用的业务身份和业务场景
     * </p>
     *
     * @return
     */
    public Route[] bizScenes();


    /**
     * 描述信息，强制实现者添加注释说明
     *
     * @return
     */
    public String desc();
}