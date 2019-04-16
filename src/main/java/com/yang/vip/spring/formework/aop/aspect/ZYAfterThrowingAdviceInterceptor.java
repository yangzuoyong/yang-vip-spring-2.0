package com.yang.vip.spring.formework.aop.aspect;

import com.yang.vip.spring.formework.aop.intercept.ZYMethodInterceptor;
import com.yang.vip.spring.formework.aop.intercept.ZYMethodInvocation;

import java.lang.reflect.Method;

public class ZYAfterThrowingAdviceInterceptor extends ZYAbstractAspectAdvice implements ZYAdvice, ZYMethodInterceptor {
    private String throwingName;
    public ZYAfterThrowingAdviceInterceptor(Method aspectMethod, Object aspectTarget) {
        super(aspectMethod, aspectTarget);
    }

    @Override
    public Object invoke(ZYMethodInvocation mi) throws Throwable {
        try {
            return mi.proceed();
        } catch (Throwable e) {
            invokeAdviceMethod(mi,null, e.getCause());
            throw e;
        }
    }

    public void setThrowingName(String throwingName) {
        this.throwingName = throwingName;
    }
}
