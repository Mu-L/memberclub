/**
 * @(#)ExtensionManger.java, 十二月 14, 2024.
 * <p>
 * Copyright 2024 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.common.extension;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Lists;
import com.google.common.collect.Table;
import com.memberclub.common.annotation.Route;
import com.memberclub.common.log.CommonLog;
import com.memberclub.common.util.ApplicationContextUtils;
import com.memberclub.domain.common.BizScene;
import com.memberclub.domain.common.BizTypeEnum;
import com.memberclub.domain.common.SceneEnum;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.ClassUtils;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * <p>
 * 业务中台要接入很多的业务方，每个业务方并不是完全相同。很多时候无法完全复用，需要改造系统适应新的业务。
 * 新增业务代码时，务必要保证原有业务不受影响，如果没有插件扩展点能力，就会充斥大量的 if else 。
 * if (biz == BizA || biz == BizB) {
 * //do some thing
 * //这部分逻辑相同
 * if (biz == BizA) {
 * //差异化处理
 * }
 * <p>
 * if(biz == BizB) {
 * //差异化逻辑
 * }
 * }
 * 例如上面的代码，不同的业务线若有差异化逻辑，需要新增分支单独处理。想象一下，当有 10 多个业务接入了你的系统，那么一定让人抓狂……
 * 任何一个人都无法保证对 10 多种业务完全熟悉，每个人可能只负责 1 个业务，然而如果没有代码逻辑的隔离，维护者只能在千丝万缕中，才能找到目标代码逻辑。更可怕的是，每次新增一个业务，需要在原有的屎山中继续💩，不断新增 if else。直到有一天，有一个倒霉蛋改错了代码，导致其他重要业务受影响，引发线上故障。
 * 想象一下，当你改了几行代码以后，要求测试同学，回归10 多个业务线的全部逻辑？这显然不现实。
 * 以上的问题和痛点可归纳为：代码隔离性和业务扩展点问题。解决这两类问题有如下手段！
 * <p>
 * 使用流程引擎，为不同的业务配置不同的流程执行链
 * 使用插件扩展引擎，不同的业务实现差异化部分。
 * </p>
 * <p>
 * <p>
 * ## 定义扩展点
 * 扩展点需要实现 {@link BaseExtension} ，如购买域扩展点{@link PurchaseExtension }
 * <p>
 * <p>
 * 之后的工作需要加载扩展点和获取扩展点，这部分工作由 {@link ExtensionManager} 完成
 * <p>
 *
 * @author 掘金五阳
 * @see BaseExtension
 */
@Service
public class ExtensionManager {


    @Autowired
    private ApplicationContext context;

    private Map<String, Object> extensionBeanMap = new HashMap<>();

    @Getter
    private Table<BizTypeEnum, String, List<Object>> bizExtensionMeta = HashBasedTable.create();

    public static <T> T extension(BizScene bizScene, Class<T> tClass) {
        return ApplicationContextUtils.getContext().getBean(ExtensionManager.class).getExtension(bizScene, tClass);
    }

    /**
     * <p>
     * ExtensionManage 类在 Spring 启动阶段，从 ApplicationContext 上下文加载 有 ExtensionProvider  注解的修饰的 Bean。
     * * 注解上声明了 适用的业务线和业务域，并且将以上信息 映射到  Table 中。 Table 类是 guava 提供的类似于 HashMap 的工具类，
     * * 和 Map 不同的是，获取 Table 中的 value 需要 key 和 subKey 两层映射
     * </p>
     */
    @PostConstruct
    public void init() {
        /**
         *
         所有的扩展点实现类必须要求添加 {@link ExtensionProvider }注解
         */
        String[] beanNames = context.getBeanNamesForAnnotation(ExtensionProvider.class);


        for (String beanName : beanNames) {
            Object bean = context.getBean(beanName);
            Set<Class<?>> interfaces =
                    ClassUtils.getAllInterfacesForClassAsSet(bean.getClass());

            ExtensionProvider extension = AnnotationUtils.findAnnotation(bean.getClass(), ExtensionProvider.class);
            Route[] routes = extension.bizScenes();


            for (Class<?> anInterface : interfaces) {
                if (BaseExtension.class.isAssignableFrom(anInterface)) {
                    for (Route route : routes) {
                        for (SceneEnum scene : route.scenes()) {
                            String key = buildKey(anInterface, route.bizType().getCode(), scene.getValue());


                            Object value = extensionBeanMap.put(key, bean);
                            if (value != null) {
                                CommonLog.error("注册 Extension key:{}冲突", key);
                                throw new RuntimeException("注册 Extension 冲突");
                            }
                            CommonLog.info("注册 Extension key:{}, 接口:{}, 实现类:{}", key, anInterface.getSimpleName(), bean.getClass().getSimpleName());

                            List<Object> extensions = bizExtensionMeta.get(route.bizType(), anInterface.getSimpleName());
                            if (extensions == null) {
                                bizExtensionMeta.put(route.bizType(), anInterface.getSimpleName(), Lists.newArrayList(bean));
                            }
                        }
                    }
                }
            }
        }
    }

    private String buildKey(Class<?> anInterface, int bizType, String scene) {
        String key = String.format("%s_%s_%s", anInterface.getSimpleName(), bizType, scene);
        return key;
    }

    public <T> T getExtension(BizScene bizScene, Class<T> tClass) {
        if (!tClass.isInterface()) {
            throw new RuntimeException(String.format("%s 需要是一个接口", tClass.getSimpleName()));
        }
        if (!BaseExtension.class.isAssignableFrom(tClass)) {
            throw new RuntimeException(String.format("%s 需要继承 BaseExtension 接口", tClass.getSimpleName()));
        }

        String key = buildKey(tClass, bizScene.getBizType(), bizScene.getScene());
        T value = (T) extensionBeanMap.get(key);

        if (value == null) {
            key = buildKey(tClass, bizScene.getBizType(), SceneEnum.DEFAULT_SCENE.getValue());
            value = (T) extensionBeanMap.get(key);
        }
        if (value == null) {
            key = buildKey(tClass, BizTypeEnum.DEFAULT.getCode(), bizScene.getScene());
            value = (T) extensionBeanMap.get(key);
        }

        if (value == null) {
            key = buildKey(tClass, BizTypeEnum.DEFAULT.getCode(), SceneEnum.DEFAULT_SCENE.getValue());
            value = (T) extensionBeanMap.get(key);
        }

        if (value == null) {
            throw new RuntimeException(String.format("%s 没有找到实现类%s", tClass.getSimpleName(), bizScene.getKey()));
        }
        return value;
    }

    public BizSceneBuildExtension getSceneExtension(BizScene bizScene) {
        return getExtension(bizScene, BizSceneBuildExtension.class);
    }/*

    public static class ExtensionGetter<T> {
        private Class<T> tClass;

        private BizScene bizScene;

        public ExtensionGetter bizScene(BizScene bizScene) {
            this.bizScene = bizScene;
            return this;
        }

        public ExtensionGetter extension(Class<T> tClass) {
            this.tClass = tClass;
            return this;
        }

        public <T> T get() {
            return ExtensionManager.extension(bizScene, tClass);
        }
    }*/
}