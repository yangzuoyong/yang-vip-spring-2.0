package com.yang.vip.spring.formework.webmvc.servlet;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.Locale;

@Slf4j
public class ZYViewResolver {
    private final String DEFAULT_TEMPLATE_SUFFX=".html";
    private File templateRootDir;
    public ZYViewResolver(String templateRoot){
        String templateRootPath = this.getClass().getClassLoader().getResource(templateRoot).getFile();
        templateRootDir = new File(templateRootPath);
    }

    public ZYView resolveViewName(String viewName, Locale locale) throws Exception{
        log.info("ZYViewResolver,viewName:"+viewName+",locale:"+locale);
        if(null == viewName || "".equals(viewName.trim())){return null;}
        viewName = viewName.endsWith(DEFAULT_TEMPLATE_SUFFX) ? viewName:(viewName+DEFAULT_TEMPLATE_SUFFX);
        File templateFile = new File((templateRootDir.getPath()+"/"+viewName).replaceAll("/+","/"));
        return new ZYView(templateFile);
    }
}
