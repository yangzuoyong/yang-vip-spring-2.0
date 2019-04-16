package com.yang.vip.spring.formework.aop.intercept;

public interface ZYMethodInterceptor {
    Object invoke(ZYMethodInvocation invocation) throws Throwable;
}
