package com.yang.vip.spring.formework.aop.aspect;

import com.yang.vip.spring.formework.aop.intercept.ZYMethodInterceptor;
import com.yang.vip.spring.formework.aop.intercept.ZYMethodInvocation;

import java.lang.reflect.Method;

public class ZYAfterReturningAdviceInterceptor extends ZYAbstractAspectAdvice implements ZYAdvice, ZYMethodInterceptor {
    private ZYJoinPoint joinPoint;
    public ZYAfterReturningAdviceInterceptor(Method aspectMethod, Object aspectTarget) {
        super(aspectMethod, aspectTarget);
    }

    @Override
    public Object invoke(ZYMethodInvocation mi) throws Throwable {
        Object retVal = mi.proceed();
        this.joinPoint=mi;
        this.afterReturning(retVal,mi.getMethod(),mi.getArguments(),mi.getThis());
        return retVal;
    }

    private void afterReturning(Object retVal, Method method, Object[] arguments, Object aThis) throws Throwable{
        super.invokeAdviceMethod(this.joinPoint,retVal,null);
    }
}
