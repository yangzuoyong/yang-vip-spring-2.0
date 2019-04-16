package com.yang.vip.spring.formework.aop;

import com.yang.vip.spring.formework.aop.ZYAopProxy;
import com.yang.vip.spring.formework.aop.intercept.ZYMethodInvocation;
import com.yang.vip.spring.formework.aop.support.ZYAdvisedSupport;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;

public class ZYJdkDynamicAopProxy implements ZYAopProxy, InvocationHandler {
    private ZYAdvisedSupport advised;
    public ZYJdkDynamicAopProxy(ZYAdvisedSupport config){this.advised = config;}
    @Override
    public Object getProxy() {
        return getProxy(this.advised.getTargetClass().getClassLoader());
    }

    @Override
    public Object getProxy(ClassLoader classLoader) {
        return Proxy.newProxyInstance(classLoader,this.advised.getTargetClass().getInterfaces(),this);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        List<Object> interceptorsAndDynamicMethodMathcers = this.advised.getInterceptorsAndDynamicInterceptionAdvice(method,this.advised.getTargetClass());
        ZYMethodInvocation invocation = new ZYMethodInvocation(proxy,method,this.advised.getTarget(),args,interceptorsAndDynamicMethodMathcers,this.advised.getTargetClass());
        return invocation.proceed();
    }
}
