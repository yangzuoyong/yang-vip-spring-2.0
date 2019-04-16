package com.yang.vip.spring.formework.aop;

import com.yang.vip.spring.formework.aop.support.ZYAdvisedSupport;

public class ZYCglibAopProxy implements ZYAopProxy {
    public ZYCglibAopProxy(ZYAdvisedSupport config) {
    }

    @Override
    public Object getProxy() {
        return null;
    }

    @Override
    public Object getProxy(ClassLoader classLoader) {
        return null;
    }
}
