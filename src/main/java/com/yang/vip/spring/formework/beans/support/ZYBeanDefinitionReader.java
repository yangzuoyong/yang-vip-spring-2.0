package com.yang.vip.spring.formework.beans.support;

import com.yang.vip.spring.formework.beans.config.ZYBeanDefinition;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@Slf4j
public class ZYBeanDefinitionReader {
    private List<String> registyBeanClasses = new ArrayList<String>();
    private Properties config = new Properties();
    //固定配置文件中key,相对于xml的规范
    private final String SCAN_PACKAGE = "scanPackage";

    public ZYBeanDefinitionReader(String... locations) {
        //通过URL定位找到其所对应的文件，然后转换成文件流
        InputStream is = this.getClass().getClassLoader().getResourceAsStream(locations[0].replace("classpath:", ""));
        try {
            config.load(is);
        } catch (IOException e) {
            log.error(String.format("ZYBeanDefinitionReader invoke config.load throw IOException:%s", e.getMessage()));
        } finally {
            if (null != is) {
                try {
                    is.close();
                } catch (IOException e) {
                    log.error(String.format("Is close throw IOException:%s", e.getMessage()));
                }
            }
        }
        this.doScanner(config.getProperty(SCAN_PACKAGE));
    }

    /**
     * 转换为文件路径，实际上就是把.替换成/
     */
    private void doScanner(String scanPackage) {
        URL url = this.getClass().getResource(String.format("/%s", scanPackage.replaceAll("\\.", "/")));
        File classPath = new File(url.getFile());
        for (File file : classPath.listFiles()) {
            if (file.isDirectory()) {
                this.doScanner(scanPackage + "." + file.getName());
            } else {
                if (!file.getName().endsWith(".class")) {
                    continue;
                }
                String className = (scanPackage + "." + file.getName().replace(".class", ""));
                registyBeanClasses.add(className);
            }
        }
    }

    public Properties getConfig() {
        return this.config;
    }

    /**
     * 把配置文件中扫描到的所有的配置信息转换为ZYBeanDefinition对象，以便于之后IOC操作方便
     */
    public List<ZYBeanDefinition> loadBeanDefinitions() {
        List<ZYBeanDefinition> result = new ArrayList<ZYBeanDefinition>();
        for (String className : registyBeanClasses) {
            try {
                Class<?> beanClass = Class.forName(className);
                //如果是一个接口，是不能实例化的，只能用实现类来实例化
                if (beanClass.isInterface()) {
                    continue;
                }
                //beanName有三种情况:
                //1、默认是类名首字母小写
                //2、自定义名字
                //3、接口注入
                result.add(this.doCreateBeanDefinition(toLowerFirstCase(beanClass.getSimpleName()), beanClass.getName()));

            } catch (Exception e) {
                log.error(String.format("class.forName(className) throw Exception", e.getMessage()));
            }
        }
        return result;
    }

    /**
     * 首字母大写
     */
    private String toLowerFirstCase(String simpleName) {
        char[] chars = simpleName.toCharArray();
        //之所以加，是因为大小写字母的ASCII码相差32，
        // 而且大写字母的ASCII码要小于小写字母的ASCII码
        //在Java中，对char做算学运算，实际上就是对ASCII码做算学运算
        chars[0] += 32;
        return String.valueOf(chars);
    }

    /**
     * 把每一个配信息解析成一个BeanDefinition
     */
    private ZYBeanDefinition doCreateBeanDefinition(String factoryBeanName, String beanCLassName) {
        ZYBeanDefinition beanDefinition = new ZYBeanDefinition();
        beanDefinition.setBeanClassName(beanCLassName);
        beanDefinition.setFactoryBeanName(factoryBeanName);
        return beanDefinition;
    }
}
