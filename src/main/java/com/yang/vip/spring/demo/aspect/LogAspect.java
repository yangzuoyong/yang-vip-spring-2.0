package com.yang.vip.spring.demo.aspect;

import com.yang.vip.spring.formework.aop.aspect.ZYJoinPoint;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;

@Slf4j
public class LogAspect {
    /**
     * 在调用一个方法之前，执行before方法
     */
    public void before(ZYJoinPoint joinPoint) {
      joinPoint.setUserAttribute("startTime_"+joinPoint.getMethod().getName(),System.currentTimeMillis());
        log.info("Invoker Before Method!!!" +
                "\nTargetObject:" +  joinPoint.getThis() +
                "\nArgs:" + Arrays.toString(joinPoint.getArguments()));
    }

    /**
     * 在调用一个方法之后，执行after方法
     */
    public void after(ZYJoinPoint joinPoint) {
        log.info("Invoker After Method!!!" +
                "\nTargetObject:" +  joinPoint.getThis() +
                "\nArgs:" + Arrays.toString(joinPoint.getArguments()));
        long startTime = (Long) joinPoint.getUserAttribute("startTime_" + joinPoint.getMethod().getName());
        long endTime = System.currentTimeMillis();
        log.info("use time :" + (endTime - startTime));
    }

    public void afterThrowing(ZYJoinPoint joinPoint, Throwable ex) {
        log.info(String.format("出现异常,\nTargetObject:%s,\nArgs:%s,\nThrows:%s", joinPoint.getThis(), Arrays.toString(joinPoint.getArguments()), ex.getMessage()));
    }
}
