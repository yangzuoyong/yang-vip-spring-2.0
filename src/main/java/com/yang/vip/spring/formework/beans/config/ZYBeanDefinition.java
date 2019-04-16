package com.yang.vip.spring.formework.beans.config;

import lombok.Data;

@Data
public class ZYBeanDefinition {
    private String beanClassName;
    private boolean lazyInit = false;
    private String factoryBeanName;
    private boolean isSingleton = true;
}
