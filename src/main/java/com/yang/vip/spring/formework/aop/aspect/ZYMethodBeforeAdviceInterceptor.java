package com.yang.vip.spring.formework.aop.aspect;

import com.yang.vip.spring.formework.aop.intercept.ZYMethodInterceptor;
import com.yang.vip.spring.formework.aop.intercept.ZYMethodInvocation;

import java.lang.reflect.Method;

public class ZYMethodBeforeAdviceInterceptor extends ZYAbstractAspectAdvice implements ZYAdvice, ZYMethodInterceptor {
    private ZYJoinPoint joinPoint;
    public ZYMethodBeforeAdviceInterceptor(Method aspectMethod, Object aspectTarget) {
        super(aspectMethod, aspectTarget);
    }

    @Override
    public Object invoke(ZYMethodInvocation mi) throws Throwable {
        //从被织入的代码中才能拿到，JoinPoint
        this.joinPoint = mi;
        this.before(mi.getMethod(),mi.getArguments(),mi.getThis());
        return mi.proceed();
    }

    private void before(Method method, Object[] arguments, Object aThis) throws Throwable{
        //传送了给织入参数
        super.invokeAdviceMethod(this.joinPoint,null,null);
    }
}
