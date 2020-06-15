package com.zf.spring.framework.webmvc.servlet;

import com.zf.spring.framework.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class HandlerAdapter {

    public ModelAndView handler(HttpServletRequest req, HttpServletResponse resp, HandlerMapping handler) throws Exception {
        Map<String, Integer> paramIndexMap = new HashMap<String, Integer>();
        Annotation[][] pa = handler.getMethod().getParameterAnnotations();
        for (int i = 0; i < pa.length; i++) {
            Annotation[] annotations = pa[i];
            for (Annotation a : annotations) {
                if (a instanceof RequestParam) {
                    String paramName = ((RequestParam) a).value();
                    if (!"".equals(paramName)) {
                        paramIndexMap.put(paramName, i);
                    }
                }
            }
        }


        //参数 req和resp和位置
        Class<?>[] parameterTypes = handler.getMethod().getParameterTypes();
        for (int i = 0; i < parameterTypes.length; i++) {
            Class<?> parameterType = parameterTypes[i];
            if (parameterType == HttpServletRequest.class || parameterType == HttpServletResponse.class) {
                paramIndexMap.put(parameterType.getName(), i);
            }
        }

        //实参列表
        Map<String, String[]> parameterMap = req.getParameterMap();

        Object[] paramValues = new Object[parameterTypes.length];

        for (Map.Entry<String, String[]> param : parameterMap.entrySet()) {
            String value = Arrays.toString(parameterMap.get(param.getKey()))
                    .replaceAll("\\[|\\]", "")
                    .replaceAll("\\s","");

            if (!paramIndexMap.containsKey(param.getKey())) {
                continue;
            }
            Integer index = paramIndexMap.get(param.getKey());
            paramValues[index] = caseStringVlaue(value,parameterTypes[index]);
        }

        if(paramIndexMap.containsKey(HttpServletRequest.class.getName())){
            int index = paramIndexMap.get(HttpServletRequest.class.getName());
            paramValues[index] = req;
        }

        if(paramIndexMap.containsKey(HttpServletResponse.class.getName())){
            int index = paramIndexMap.get(HttpServletResponse.class.getName());
            paramValues[index] = resp;
        }


        Object result = handler.getMethod().invoke(handler.getController(),paramValues);

        if(result == null || result instanceof Void){return null;}

        boolean isModelAndView = handler.getMethod().getReturnType() == ModelAndView.class;
        if (isModelAndView){
            return (ModelAndView)result;
        }
        return  null;
    }

    private Object caseStringVlaue(String value, Class<?> paramType) {
        if(String.class == paramType){
            return value;
        }
        if(Integer.class == paramType){
            return Integer.valueOf(value);
        }else if(Double.class == paramType){
            return Double.valueOf(value);
        }else {
            if(value != null){
                return value;
            }
            return null;
        }
    }
}
