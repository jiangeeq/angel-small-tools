package com.jpsite.utils;

import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.Sets;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.util.ClassUtils;

import java.util.Arrays;
import java.util.Optional;
import java.util.Set;

/**
 * 全局环境, 用于配置信息及其它业务信息传递，比如把契约扫描的包、契约的实现类传递给cache模块
 */
public class GlobalEnvironment {

    private static FeignCelebiContractEnvironment contractEnvironment;

    /**
     * 获取契约扫描基础包SET集合
     *
     * @param environment spring环境
     * @return 扫描基础包列表
     */
    public static Set<String> getScanBasePackage(Environment environment) {
        contractEnvironment = new FeignCelebiContractEnvironment(environment);
        return Sets.newHashSet(contractEnvironment.getBasePackages());
    }

    /**
     * 获取契约扫描基础包SET集合
     *
     * @param environment spring环境
     * @return 扫描基础包列表
     */
    public static String[] getScanBasePackages(Environment environment) {
        contractEnvironment = new FeignCelebiContractEnvironment(environment);
        return contractEnvironment.getBasePackages();
    }

    /**
     * 获取契约实现类数组
     *
     * @param environment spring环境
     * @return 契约实现类数组
     */
    public static Class<?>[] getContractImplClasses(Environment environment) {
        Preconditions.checkNotNull(environment);
        PropertySource<?> propertySource = getPropertySource(environment, "env_contract_key");
        if (propertySource == null) {
            return null;
        }
        return (Class<?>[]) propertySource.getProperty("env_contract_impl_classes_key");
    }


    /**
     * 检查容器中是否已经有了，服务接口的实现
     *
     * @param beanType    目前容器中已有的实现
     * @param environment spring环境
     * @return 是否在容器内发现，已有的服务接口实现
     */
    public static boolean checkBeanIsServerImpl(Environment environment, Class<?> beanType) {
        Class<?>[] contractImplClasses = getContractImplClasses(environment);
        if (null == contractImplClasses) {
            return false;
        }
        Class originalBeanType = ClassUtils.isCglibProxyClass(beanType) ? beanType.getSuperclass() : beanType;
        return Arrays.asList(contractImplClasses).contains(originalBeanType);
    }

    /**
     * 根据key获取属性pair
     *
     * @param environment spring环境
     * @param key         键
     * @return 熟悉pair
     */
    public static PropertySource<?> getPropertySource(Environment environment, String key) {
        Preconditions.checkNotNull(environment);
        MutablePropertySources propertySources = ((StandardEnvironment) environment).getPropertySources();
        return propertySources.get(key);
    }

    public static class FeignCelebiContractEnvironment {

        public static final String BASE_PACKAGES_KEY = "celebi.client.packages";

        public static final String CONFIG_SEPARATOR = ",";

        public static final String[] DEFAULT_BASE_PACKAGES = {"com.pp", "com.ppmoney", "com.whtr"};

        /**
         * spring environment
         */
        private Environment environment;

        FeignCelebiContractEnvironment() {
        }

        FeignCelebiContractEnvironment(Environment environment) {
            this.environment = environment;
        }

        /**
         * 获取扫描{@link ServiceContract}注解的basePackage，如果spring Environment中
         * 不存在{@value BASE_PACKAGES_KEY}配置项，则返回默认的basePackages值。
         *
         * @return basePackage数组
         */
        String[] getBasePackages() {
            return Optional.ofNullable(environment)
                    .map(e -> e.getProperty(BASE_PACKAGES_KEY))
                    .map(this::parseBasePackages)
                    .orElse(DEFAULT_BASE_PACKAGES);
        }

        /**
         * 解析给定的basePackage配置，以{@value CONFIG_SEPARATOR}分隔
         *
         * @param config basePackage配置
         * @return basePackage数组
         */
        String[] parseBasePackages(String config) {
            return Splitter.on(CONFIG_SEPARATOR)
                    .omitEmptyStrings()
                    .trimResults()
                    .splitToList(Strings.nullToEmpty(config))
                    .toArray(new String[0]);
        }

        void setEnvironment(Environment environment) {
            this.environment = environment;
        }
    }
}
