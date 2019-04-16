package com.yang.vip.spring.formework.aop.support;

import com.yang.vip.spring.formework.aop.aspect.ZYAfterReturningAdviceInterceptor;
import com.yang.vip.spring.formework.aop.aspect.ZYAfterThrowingAdviceInterceptor;
import com.yang.vip.spring.formework.aop.aspect.ZYMethodBeforeAdviceInterceptor;
import com.yang.vip.spring.formework.aop.config.ZYAopConfig;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ZYAdvisedSupport {
    private Class<?> targetClass;
    private Object target;
    private ZYAopConfig config;
    private Pattern pointCutClassPattern;
    private transient Map<Method, List<Object>> methodCache;
    public ZYAdvisedSupport(ZYAopConfig config){this.config = config;}
    public List<Object> getInterceptorsAndDynamicInterceptionAdvice(Method method,Class<?> targetClass)throws Exception{
        List<Object> cached = methodCache.get(method);
        if(cached == null){
            Method m=targetClass.getMethod(method.getName(),method.getParameterTypes());
            cached =methodCache.get(m);
            this.methodCache.put(m,cached);
        }
        return cached;
    }
    public Class<?> getTargetClass() {
        return targetClass;
    }

    public Object getTarget() {
        return target;
    }

    public void setTargetClass(Class<?> targetClass) {
        this.targetClass = targetClass;
        parse();
    }

    public void setTarget(Object target) {
        this.target = target;
    }
    public boolean pointCutMatch(){return pointCutClassPattern.matcher(this.targetClass.toString()).matches();}
    private void parse() {
        String pointCut = config.getPointCut()
                .replaceAll("\\.","\\\\.")
                .replaceAll("\\\\.\\*",".*")
                .replaceAll("\\(","\\\\(")
                .replaceAll("\\)","\\\\)");
        //pointCut=public .* com.gupaoedu.vip.spring.demo.service..*Service..*(.*)
        String pointCutForClassRegex = pointCut.substring(0,pointCut.lastIndexOf("\\(")-4);
        pointCutClassPattern = Pattern.compile("class "+pointCutForClassRegex.substring(pointCutForClassRegex.indexOf(" ")+1));
        try {
            methodCache = new HashMap<Method, List<Object>>();
            Pattern pattern = Pattern.compile(pointCut);
            Class aspectClass = Class.forName(this.config.getAspectClass());
            Map<String,Method> aspectMethods = new HashMap<String, Method>();
            for (Method method : aspectClass.getMethods()) {
                aspectMethods.put(method.getName(),method);
            }
            for (Method method : this.targetClass.getMethods()) {
                String methodString = method.toString();
                if(methodString.contains("throws")){
                    methodString = methodString.substring(0,methodString.lastIndexOf("throws")).trim();
                }
                Matcher matcher = pattern.matcher(methodString);
                if(matcher.matches()){
                    //执行器链
                    List<Object> advices = new LinkedList<Object>();
                    //把每一个方法包装成MethodIterceptor
                    //before
                    if(!(null==config.getAspectBefore()||"".equals(config.getAspectBefore()))){
                        //创建一个Advice
                        advices.add(new ZYMethodBeforeAdviceInterceptor(aspectMethods.get(this.config.getAspectBefore()),aspectClass.newInstance()));
                    }
                    //after
                    if (!(null == config.getAspectAfter() || "".equals(config.getAspectAfter()))){
                        //创建一个Advice
                        advices.add(new ZYAfterReturningAdviceInterceptor(aspectMethods.get(config.getAspectAfter()),aspectClass.newInstance()));
                    }
                    //afterThrowing
                    if(!(null == config.getAspectAfterThrow() || "".equals(config.getAspectAfterThrow()))){
                        ZYAfterThrowingAdviceInterceptor throwingAdvice=
                                new ZYAfterThrowingAdviceInterceptor(
                                        aspectMethods.get(config.getAspectAfterThrow()),
                                        aspectClass.newInstance());
                        throwingAdvice.setThrowingName(config.getAspectAfterThrowingName());
                        advices.add(throwingAdvice);
                    }
                    methodCache.put(method,advices);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}