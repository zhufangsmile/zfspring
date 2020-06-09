//package com.zf.spring.framework.webmvc.servlet;
//
//import com.zf.spring.framework.annotation.Controller;
//import com.zf.spring.framework.annotation.RequestMapping;
//import com.zf.spring.framework.annotation.RequestParam;
//import com.zf.spring.framework.context.ApplicationContext;
//import javafx.application.Application;
//
//import javax.servlet.ServletConfig;
//import javax.servlet.ServletException;
//import javax.servlet.http.HttpServlet;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.io.IOException;
//import java.lang.annotation.Annotation;
//import java.lang.reflect.Array;
//import java.lang.reflect.Method;
//import java.util.Arrays;
//import java.util.HashMap;
//import java.util.Map;
//
//public class DispatcherServlet extends HttpServlet {
//    ApplicationContext applicationContext = null;
//
//    private Map<String, Method> handlerMapping = new HashMap<String, Method>();
//    @Override
//    public void init(ServletConfig config) throws ServletException {
//        applicationContext = new ApplicationContext(config.getInitParameter("contextConfigLocation"));
//        //初始化HandlerMapping
//        doInitHandlerMapping();
//        System.out.println("init success");
//    }
//
//    private void doInitHandlerMapping() {
//
//        if (applicationContext.getBeanDefinitionCount() == 0) {
//            return;
//        }
//
//        for (String beanName : applicationContext.getBeanDefinitionNames()) {
//
//
//            Object instance = applicationContext.getBean(beanName);
//            Class<?> clazz = instance.getClass();
//            if (!clazz.isAnnotationPresent(Controller.class)) {
//                continue;
//            }
//            String baseUrl = "";
//
//            if (clazz.isAnnotationPresent(RequestMapping.class)) {
//                RequestMapping requestMapping = clazz.getAnnotation(RequestMapping.class);
//                baseUrl = requestMapping.value();
//
//            }
//
//            for (Method method : clazz.getMethods()) {
//
//                if (!method.isAnnotationPresent(RequestMapping.class)) {
//                    continue;
//                }
//
//                RequestMapping requestMapping = method.getAnnotation(RequestMapping.class);
//                String url =  ("/" + baseUrl + "/" + requestMapping.value()).replaceAll("/+", "/");
//                handlerMapping.put(url, method);
//                System.out.println("Mapped " + url + "," + method);
//
//            }
//
//        }
//
//
//
//    }
//
//
//
//    @Override
//    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//        //根据url完成方法的调度
//        try {
//            doDispatch(req, resp);
//        } catch (Exception e) {
//            e.printStackTrace();
//            resp.getWriter().write("500 Exception Detail :" + Arrays.toString(e.getStackTrace()));
//        }
//    }
//
//    private void doDispatch(HttpServletRequest req, HttpServletResponse resp) throws Exception {
//        String url = req.getRequestURI();
//        String contextPath = req.getContextPath();
//        url = url.replace(contextPath, "").replaceAll("/+", "/");
//        if (!handlerMapping.containsKey(url)) {
//            resp.getWriter().write("404 Not Found");
//        }
//        Method method = handlerMapping.get(url);
//
//
//        Class<?>[] parameterTypes = method.getParameterTypes();//形参列表
//        Map<String, String[]> parameterMap = req.getParameterMap();
//        Object[] paramValues = new Object[parameterTypes.length];
//        for (int i = 0; i < parameterTypes.length; i++) {
//            Class<?> parameterType = parameterTypes[i];
//            if (parameterType == HttpServletRequest.class) {
//                paramValues[i] = req;
//                continue;
//            }else if (parameterType == HttpServletResponse.class) {
//                paramValues[i] = resp;
//            }else if (parameterType == String.class) {
//                Annotation[][] pa = method.getParameterAnnotations();//一个参数可以有多个注解pa[0][0] 第一个参数的第一个注解
//                for (int j = 0; j< pa.length; j++) {
//                    for (Annotation annotation : pa[j]) {
//                        if (annotation instanceof RequestParam) {
//                            String paramName = ((RequestParam) annotation).value();
//                            if (!"".equals(paramName)) {
//                                paramValues[i] = Arrays.toString(parameterMap.get(paramName)).replaceAll("\\[|\\]","")
//                                        .replaceAll("\\s","");
//                            }
//                        }
//                    }
//                }
//            }
//        }
//
//
//
//        method.invoke(applicationContext.getBean(method.getDeclaringClass()), paramValues);
//
//    }
//
//    @Override
//    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//        doPost(req, resp);
//    }
//}
