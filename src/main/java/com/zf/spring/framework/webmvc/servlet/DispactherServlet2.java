package com.zf.spring.framework.webmvc.servlet;

import com.zf.spring.framework.annotation.Controller;
import com.zf.spring.framework.annotation.RequestMapping;
import com.zf.spring.framework.context.ApplicationContext;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DispactherServlet2 extends HttpServlet {

    private ApplicationContext applicationContext;

    private List<HandlerMapping> handlerMappings = new ArrayList<HandlerMapping>();

    private Map<HandlerMapping, HandlerAdapter> handlerAdapters = new HashMap<HandlerMapping, HandlerAdapter>();

    @Override
    public void init(ServletConfig config) throws ServletException {
        applicationContext = new ApplicationContext(config.getInitParameter("contextConfigLocation".replaceAll("classpath:","")));
        //=========MVC 九大组件========
        initStrategies(applicationContext);

    }

    private void initStrategies(ApplicationContext applicationContext) {
        //初始化handlerMapping
        initHandlerMapping(applicationContext);
        //初始化参数适配器
        initHandlerAdapters(applicationContext);
        //初始化视图转换器
        initViewResolvers(applicationContext);
    }

    private void initViewResolvers(ApplicationContext applicationContext) {
    }

    private void initHandlerAdapters(ApplicationContext applicationContext) {
        for (HandlerMapping handlerMapping : handlerMappings) {
            this.handlerAdapters.put(handlerMapping, new HandlerAdapter());
        }
    }

    private void initHandlerMapping(ApplicationContext applicationContext) {
        if (applicationContext.getBeanDefinitionCount() == 0) {
            return;
        }

        for (String beanDefinitionName : applicationContext.getBeanDefinitionNames()){
            Object instance = applicationContext.getBean(beanDefinitionName);
            Class<?> clazz = instance.getClass();
            if (!clazz.isAnnotationPresent(Controller.class)) {
                continue;
            }
            String baseUrl = "";
            if (clazz.isAnnotationPresent(RequestMapping.class)) {
                RequestMapping requestMapping = clazz.getAnnotation(RequestMapping.class);
                baseUrl = requestMapping.value();
            }

            for (Method method : clazz.getMethods()) {

                if (!method.isAnnotationPresent(RequestMapping.class)) {
                    continue;
                }
                RequestMapping requestMapping = method.getAnnotation(RequestMapping.class);

                String regex = ("/" + baseUrl + "/" + requestMapping.value().replaceAll("\\*", ".*")).replaceAll("/+", "/");

                Pattern pattern = Pattern.compile(regex);
                handlerMappings.add(new HandlerMapping(pattern, instance, method));
            }



        }
    }


    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doDispath(req, resp);
    }

    private void doDispath(HttpServletRequest req, HttpServletResponse resp) {
        //根据url获取handler
        HandlerMapping handler = getHandler(req);
        if (handler == null) {
            
        }
        
        //根据handler获取handlerAdapter
        HandlerAdapter ha = getHandlerAdapter(handler);
        ModelAndView mv = ha.handler(req, resp, handler);

    }

    private HandlerAdapter getHandlerAdapter(HandlerMapping handler) {
        if (this.handlerAdapters.isEmpty()) {
            return null;
        }
        return this.handlerAdapters.get(handler);

    }

    private HandlerMapping getHandler(HttpServletRequest req) {// web/.*
        String url = req.getRequestURI();
        String contextPath = req.getContextPath();
        url = url.replace(contextPath, "").replaceAll("/+", "/");
        for (HandlerMapping handlerMapping : handlerMappings) {
            Matcher matcher = handlerMapping.getPattern().matcher(url);
            if (!matcher.matches()) {
                continue;
            }
            return handlerMapping;
        }
        return null;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doGet(req, resp);
    }
}
