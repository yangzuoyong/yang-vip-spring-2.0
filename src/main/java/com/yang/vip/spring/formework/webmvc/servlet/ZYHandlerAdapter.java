package com.yang.vip.spring.formework.webmvc.servlet;

import com.yang.vip.spring.formework.annotation.ZYRequestParam;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class ZYHandlerAdapter {
    public boolean supports(Object handler){return handler instanceof ZYHandlerMapping;}
    ZYModelAndView handle(HttpServletRequest request,
                          HttpServletResponse response,
                          Object handler)throws Exception{
        ZYHandlerMapping handlerMapping = (ZYHandlerMapping)handler;
        //把方法的形参列表和request的参数列表所在顺序进行一一对应
        Map<String,Integer> paramIndexMaping = new HashMap<String, Integer>();
        //提取方法中加了注解的参数
        //把方法上的注解拿到，得到的是一个二维数组
        //因为一个参数可以有多个注解，而一个方法又有多个参数
        Annotation[][] pa = handlerMapping.getMethod().getParameterAnnotations();
        for (int i = 0; i < pa.length; i++) {
            for (Annotation a : pa[i]) {
                if(a instanceof ZYRequestParam){
                    String paramName=((ZYRequestParam)a).value();
                    if(!"".equals(paramName.trim())){
                        paramIndexMaping.put(paramName,i);
                    }
                }
            }
        }
        //提取方法中的request和response参数
        Class<?>[] paramsTypes = handlerMapping.getMethod().getParameterTypes();
        for (int i = 0; i < paramsTypes.length; i++) {
            Class<?> paramsType = paramsTypes[i];
            if(paramsType == HttpServletRequest.class || paramsType==HttpServletResponse.class){
                paramIndexMaping.put(paramsType.getName(),i);
            }
        }
        //获得方法的形参列表
        Map<String,String[]> params = request.getParameterMap();
        //实参列表
        Object[] paramValues = new Object[paramsTypes.length];
        for (Map.Entry<String, String[]> param : params.entrySet()) {
            String value = Arrays.toString(param.getValue()).replaceAll("\\[|\\]","").replaceAll("\\s","");
            if(!paramIndexMaping.containsKey(param.getKey())){continue;}
            int index = paramIndexMaping.get(param.getKey());
            paramValues[index] = caseStringValue(value,paramsTypes[index]);
              
        }
        if(paramIndexMaping.containsKey(HttpServletRequest.class.getName())){
            int regIndex = paramIndexMaping.get(HttpServletRequest.class.getName());
            paramValues[regIndex] = request;
        }
        if(paramIndexMaping.containsKey(HttpServletResponse.class.getName())){
            int respIndex = paramIndexMaping.get(HttpServletResponse.class.getName());
            paramValues[respIndex] = response;
        }
        Object result = handlerMapping.getMethod().invoke(handlerMapping.getController(),paramValues);
        if(null == result || result instanceof Void){return  null;}
        boolean isModelAndView = handlerMapping.getMethod().getReturnType() == ZYModelAndView.class;
        return isModelAndView ? (ZYModelAndView)result:null;
    }

    private Object caseStringValue(String value, Class<?> paramsType) {
        if(String.class == paramsType){return value;}
        if(Integer.class == paramsType){
            return Integer.valueOf(value);
        }else  if(Double.class==paramsType){
            return Double.valueOf(value);
        }else if(Float.class ==paramsType){
            return Float.valueOf(value);
        }
        return value==null ?null:value;
    }
}
