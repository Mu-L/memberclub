/**
 * @(#)BizScene.java, 十二月 14, 2024.
 * <p>
 * Copyright 2024 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.common.annotation;

import com.memberclub.common.extension.ExtensionProvider;
import com.memberclub.domain.common.BizTypeEnum;
import com.memberclub.domain.common.SceneEnum;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * ，Route类包含业务身份和业务场景的定义，用于扩展点实现类{@link ExtensionProvider}注解
 *
 * @author 掘金五阳
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Route {

    public BizTypeEnum bizType();

    public SceneEnum[] scenes() default SceneEnum.DEFAULT_SCENE;
}