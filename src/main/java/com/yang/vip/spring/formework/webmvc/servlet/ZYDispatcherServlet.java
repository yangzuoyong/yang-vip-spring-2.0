package com.yang.vip.spring.formework.webmvc.servlet;

import com.yang.vip.spring.formework.context.ZYApplicationContext;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServlet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class ZYDispatcherServlet extends HttpServlet {
    private final String CONTEXT_CONFIG_LOCATION="contextConfigLocation";
    private ZYApplicationContext context;
    private List<ZYHandlerMapping> handlerMappings = new ArrayList<ZYHandlerMapping>();
    private Map<ZYHandlerMapping,ZYHandlerAdapter> handlerAdapterMap = new HashMap<ZYHandlerMapping, ZYHandlerAdapter>();
    private List<ZYViewResolver> viewResolvers=new ArrayList<ZYViewResolver>();
}
