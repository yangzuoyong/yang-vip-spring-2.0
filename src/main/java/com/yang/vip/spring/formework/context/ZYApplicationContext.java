package com.yang.vip.spring.formework.context;

import com.yang.vip.spring.formework.annotation.ZYAutowired;
import com.yang.vip.spring.formework.annotation.ZYController;
import com.yang.vip.spring.formework.annotation.ZYService;
import com.yang.vip.spring.formework.beans.ZYBeanWrapper;
import com.yang.vip.spring.formework.beans.config.ZYBeanDefinition;
import com.yang.vip.spring.formework.beans.config.ZYBeanPostProcessor;
import com.yang.vip.spring.formework.beans.support.ZYBeanDefinitionReader;
import com.yang.vip.spring.formework.beans.support.ZYDefaultListableBeanFactory;
import com.yang.vip.spring.formework.core.ZYBeanFactory;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 按之前源码分析的套路，IOC、DI、MVC、AOP
 */
@Slf4j
public class ZYApplicationContext extends ZYDefaultListableBeanFactory implements ZYBeanFactory {
    private String[] configLocations;
    private ZYBeanDefinitionReader reader;
    //单例的IOC容器缓存
    private Map<String, Object> singlettonObjects = new ConcurrentHashMap<String, Object>();
    //通用的IOC容器
    private Map<String, ZYBeanWrapper> factoryBeanInstanceCache = new ConcurrentHashMap<String, ZYBeanWrapper>();

    public ZYApplicationContext(String... configLocations) {
        this.configLocations = configLocations;
        try {
            this.refresh();
        } catch (Exception e) {
            log.error(String.format("ZYApplicationContext invoke refresh() throw Exception:%s"), e.getMessage());
        }
    }

    @Override
    public void refresh() throws Exception {
        //1.定位：定位配置文件
        reader = new ZYBeanDefinitionReader(this.configLocations);
        //2.加载配置文件，扫描相关的类，把它们封装成BeanDefinition
        List<ZYBeanDefinition> beanDefinitions = reader.loadBeanDefinitions();
        //3.注册，把配置信息放到容器中（伪IOC容器）
        this.doRegisterBeanDefinition(beanDefinitions);
        //4.把不是延时加载的类，提前初始化
        doAutowrited();
    }

    /**
     * 处理非延时加载的类
     */
    private void doAutowrited() {
        for (Map.Entry<String, ZYBeanDefinition> beanDefinitionEntry : super.beanDefinitionMap.entrySet()) {
            String beanName = beanDefinitionEntry.getKey();
            if (!beanDefinitionEntry.getValue().isLazyInit()) {
                try {
                    this.getBean(beanName);
                } catch (Exception e) {
                    e.printStackTrace();
                    log.error(String.format("getBean Exception:%s", e.getMessage()));
                }
            }
        }
    }

    /**
     * 注册，把配置信息放到容器中（伪IOC容器）
     */
    private void doRegisterBeanDefinition(List<ZYBeanDefinition> beanDefinitions) throws Exception {
        for (ZYBeanDefinition beanDefinition : beanDefinitions) {
            if (super.beanDefinitionMap.containsKey(beanDefinition.getFactoryBeanName())) {
                throw new Exception(String.format("The“%s” is exists!!!", beanDefinition.getBeanClassName()));
            }
            super.beanDefinitionMap.put(beanDefinition.getFactoryBeanName(), beanDefinition);
        }
    }

    @Override
    public Object getBean(String beanName) throws Exception {
        ZYBeanDefinition beanDefinition = this.beanDefinitionMap.get(beanName);
        Object instance = null;
        ZYBeanPostProcessor postProcessor = new ZYBeanPostProcessor();
        postProcessor.postProcessAfterInitialization(instance, beanName);
        instance = instantiateBean(beanName,beanDefinition);
        //将对象封装到BeanWrapper中
        ZYBeanWrapper beanWrapper=new ZYBeanWrapper(instance);
        //将BeanWrapper存到IOC容器里面
        this.factoryBeanInstanceCache.put(beanName,beanWrapper);
        postProcessor.postProcessAfterInitialization(instance,beanName);
        //注入
        populateBean(beanName,new ZYBeanDefinition(),beanWrapper);
        return this.factoryBeanInstanceCache.get(beanName).getWrappedInstance();
    }

    private void populateBean(String beanName, ZYBeanDefinition zyBeanDefinition, ZYBeanWrapper beanWrapper) {
        Object instance = beanWrapper.getWrappedInstance();
        Class<?> clazz = beanWrapper.getWrappedClass();
        //判断只有加了注解的类，才执行依赖注入
        if(!(clazz.isAnnotationPresent(ZYController.class) || clazz.isAnnotationPresent(ZYService.class))){
            return;
        }
        //获得所有的fields
        Field[] fields = clazz.getFields();
        for (Field field : fields) {
            if (!field.isAnnotationPresent(ZYAutowired.class)) {
                continue;
            }
            ZYAutowired autowired = field.getAnnotation(ZYAutowired.class);
            String autowiredBeanName = autowired.value().trim();
            if("".equals(autowiredBeanName)){
                autowiredBeanName = field.getType().getName();
            }
            //强制访问
            field.setAccessible(true);
            if(this.factoryBeanInstanceCache.get(autowiredBeanName)==null){continue;}
            try {
                field.set(instance,this.factoryBeanInstanceCache.get(autowiredBeanName).getWrappedInstance());
            } catch (IllegalAccessException e) {
                log.error(e.getMessage());
            }
        }
    }

    private Object instantiateBean(String beanName, ZYBeanDefinition beanDefinition) {
        //1.拿到要实例化的对象的类名
        String className = beanDefinition.getBeanClassName();
        //2.通过反射实例化，得到一个对象
        Object instance = null;
        try {
            //假设默认就是单例,细节暂且不考虑，先把主线拉通
            if(this.singlettonObjects.containsKey(className)){
                instance = this.singlettonObjects.get(className);
            } else {
                Class<?> clazz = Class.forName(className);
                instance = clazz.newInstance();
                this.singlettonObjects.put(className,instance);
                this.singlettonObjects.put(beanDefinition.getFactoryBeanName(),instance);
            }
        } catch (Exception e) {
            log.error(String.format("class.forName() Eeception :%s",e.getMessage()));
        }
        return instance;
    }

    @Override
    public Object getBean(Class<?> beanClass) throws Exception {
        return getBean(beanClass.getClass());
    }

    public String[] getBeanDefinitionNames(){
        return this.beanDefinitionMap.keySet().toArray(new String[this.beanDefinitionMap.size()]);
    }

    public int getBeanDefinitionCount(){return this.beanDefinitionMap.size();}

    public Properties getConfig(){return this.reader.getConfig();}
}
