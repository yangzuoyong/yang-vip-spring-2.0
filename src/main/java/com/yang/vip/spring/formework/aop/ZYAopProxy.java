package com.yang.vip.spring.formework.aop;

public interface ZYAopProxy {
    Object getProxy();
    Object getProxy(ClassLoader classLoader);
}
