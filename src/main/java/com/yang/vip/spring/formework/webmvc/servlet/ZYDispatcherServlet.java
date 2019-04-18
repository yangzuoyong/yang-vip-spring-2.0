package com.yang.vip.spring.formework.webmvc.servlet;

import com.yang.vip.spring.formework.annotation.ZYController;
import com.yang.vip.spring.formework.annotation.ZYRequestMapping;
import com.yang.vip.spring.formework.context.ZYApplicationContext;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class ZYDispatcherServlet extends HttpServlet {
    private final String CONTEXT_CONFIG_LOCATION = "contextConfigLocation";
    private ZYApplicationContext context;
    private List<ZYHandlerMapping> handlerMappings = new ArrayList<ZYHandlerMapping>();
    private Map<ZYHandlerMapping, ZYHandlerAdapter> handlerAdapterMap = new HashMap<ZYHandlerMapping, ZYHandlerAdapter>();
    private List<ZYViewResolver> viewResolvers = new ArrayList<ZYViewResolver>();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            this.doDispatch(req, resp);
        } catch (Exception e) {
            resp.getWriter().write("500 Exception,Details:\r\n" + Arrays.toString(e.getStackTrace()).replaceAll("\\[|\\]", "").replaceAll(",\\s", "\r\n"));
            e.printStackTrace();
        }
    }

    private void doDispatch(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        //1、通过从request中拿到URL，去匹配一个HandlerMapping
        ZYHandlerMapping handler = getHandler(req);
        if (null == handler) {
            processDispatchResult(req, resp, new ZYModelAndView("404"));
            return;
        }
        //2、准备调用前的参数
        ZYHandlerAdapter ha = getHandlerAdapter(handler);
        //3、真正的调用方法，返回ModelAndView存储了要传页面上的值和页面模板的名称
        ZYModelAndView mv = ha.handle(req, resp, handler);
        //4、输出
        processDispatchResult(req, resp, mv);

    }

    private ZYHandlerAdapter getHandlerAdapter(ZYHandlerMapping handler) {
        if (this.handlerAdapterMap.isEmpty()) {
            return null;
        }
        ZYHandlerAdapter ha = this.handlerAdapterMap.get(handler);
        return ha.supports(handler) ? ha : null;
    }

    private void processDispatchResult(HttpServletRequest req, HttpServletResponse resp, ZYModelAndView mv) throws Exception {
        if (null == mv) {
            return;
        }
        if (this.viewResolvers.isEmpty()) {
            return;
        }
        for (ZYViewResolver viewResolver : this.viewResolvers) {
            ZYView view = viewResolver.resolveViewName(mv.getViewName(), null);
            view.render(mv.getModel(), req, resp);
            return;

        }
    }

    private ZYHandlerMapping getHandler(HttpServletRequest req) throws Exception {
        if (this.handlerAdapterMap.isEmpty()) {
            return null;
        }
        String url = req.getRequestURI();
        String contextPath = req.getContextPath();
        url = url.replace(contextPath, "").replaceAll("/+", "/");
        for (ZYHandlerMapping handler : handlerMappings) {
            Matcher matcher = handler.getPattern().matcher(url);
            if (!matcher.matches()) {
                continue;
            }
            return handler;

        }
        return null;
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        //1、初始化ApplicationContext
        context = new ZYApplicationContext(config.getInitParameter(CONTEXT_CONFIG_LOCATION));
        //2、初始化Spring MVC 九大组件
        this.initStrategies(context);
    }

    //初始化策略
    private void initStrategies(ZYApplicationContext context) {
        //多文件上传的组件
        initMultipartResolver(context);
        //初始化本地语言环境
        initLocaleResolver(context);
        //初始化模板处理器
        initThemeResolver(context);
        //handlerMapping，必须实现
        initHandlerMappings(context);
        //初始化参数适配器，必须实现
        initHandlerAdapters(context);
        //初始化异常拦截器
        initHandlerExceptionResolvers(context);
        //初始化视图预处理器
        initRequestToViewNameTranslator(context);
        //初始化视图转换器，必须实现
        initViewResolvers(context);
        //参数缓存器
        initFlashMapManager(context);
    }

    /**
     * 多文件上传的组件
     */
    private void initMultipartResolver(ZYApplicationContext context) {

    }

    /**
     * 初始化本地语言环境
     */
    private void initLocaleResolver(ZYApplicationContext context) {

    }

    /**
     * 初始化模板处理器
     */
    private void initThemeResolver(ZYApplicationContext context) {

    }

    /**
     * handlerMapping，必须实现
     */
    private void initHandlerMappings(ZYApplicationContext context) {
        String[] beanNames = context.getBeanDefinitionNames();
        try {
            for (String beanName : beanNames) {
                Object controller = context.getBean(beanName);
                Class<?> clazz = controller.getClass();
                if (!clazz.isAnnotationPresent(ZYController.class)) {
                    continue;
                }
                String baseUrl = "";
                //获取Controller的URL配置
                if (clazz.isAnnotationPresent(ZYRequestMapping.class)) {
                    ZYRequestMapping requestMapping = clazz.getAnnotation(ZYRequestMapping.class);
                    baseUrl = requestMapping.value();
                }
                //获取MEthod的配置
                Method[] methods = clazz.getMethods();
                for (Method method : methods) {
                    //没有加RequestMapping注解的直接忽略
                    if (!method.isAnnotationPresent(ZYRequestMapping.class)) {
                        continue;
                    }
                    //映射URL
                    ZYRequestMapping requestMapping = method.getAnnotation(ZYRequestMapping.class);
                    //  /demo/query
                    //  (//demo//query)
                    String regex = ("/" + baseUrl + "/" + requestMapping.value().replaceAll("\\*", ".*")
                            .replaceAll("/+", "/"));
                    Pattern pattern = Pattern.compile(regex);
                    this.handlerMappings.add(new ZYHandlerMapping(controller, method, pattern));
                    log.info(String.format("Mapped %s , %s", regex, method));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 初始化参数适配器，必须实现
     */
    private void initHandlerAdapters(ZYApplicationContext context) {
        //把一个requet请求变成一个handler，参数都是字符串的，自动配到handler中的形参
        //可想而知，他要拿到HandlerMapping才能干活
        //就意味着，有几个HandlerMapping就有几个HandlerAdapter
        for (ZYHandlerMapping handlerMapping : this.handlerMappings) {
            this.handlerAdapterMap.put(handlerMapping, new ZYHandlerAdapter());
        }
    }

    /**
     * 初始化异常拦截器
     */
    private void initHandlerExceptionResolvers(ZYApplicationContext context) {

    }

    /**
     * 初始化视图预处理器
     */
    private void initRequestToViewNameTranslator(ZYApplicationContext context) {

    }

    /**
     * 初始化视图转换器，必须实现
     */
    private void initViewResolvers(ZYApplicationContext context) {
        //拿到模板的存放目录
        String templateRoot = context.getConfig().getProperty("templateRoot");
        String templateRootPath = this.getClass().getClassLoader().getResource(templateRoot).getFile();
        File templateRootDir = new File(templateRootPath);
        String[] templates = templateRootDir.list();
        for (int i = 0; i < templates.length; i++) {
            //这里主要是为了兼容多模板，所有模仿Spring用List保存
            //简写
            this.viewResolvers.add(new ZYViewResolver(templateRoot));
        }
    }

    /**
     * 参数缓存器
     */
    private void initFlashMapManager(ZYApplicationContext context) {

    }


}
