package com.yang.vip.spring.formework.beans.support;

import com.yang.vip.spring.formework.beans.config.ZYBeanDefinition;
import com.yang.vip.spring.formework.context.support.ZYAbstractApplicationContext;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ZYDefaultListableBeanFactory extends ZYAbstractApplicationContext {
    //存储注册信息的BeanDefinition,伪IOC容器
    protected final Map<String, ZYBeanDefinition> beanDefinitionMap = new ConcurrentHashMap<String,ZYBeanDefinition>();
}
