package com.zf.spring.framework.context;

import com.zf.spring.framework.annotation.AutoWired;
import com.zf.spring.framework.annotation.Controller;
import com.zf.spring.framework.annotation.Service;
import com.zf.spring.framework.beans.BeanWrapper;
import com.zf.spring.framework.beans.config.BeanDefinition;
import com.zf.spring.framework.beans.support.BeanDefinitionReader;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ApplicationContext {

    private String[] configLocations;

    private BeanDefinitionReader reader;

    private final Map<String, BeanDefinition> beanDefinitionMap = new HashMap<String, BeanDefinition>();
    private Map<String,Object> factoryBeanObjectCache = new HashMap<String, Object>();

    private Map<String, BeanWrapper> factoryBeanInstanceCache = new HashMap<String, BeanWrapper>();


    public ApplicationContext (String ... configLocations) {
        this.configLocations = configLocations;
        try {
            //1 读取配置文件
            reader = new BeanDefinitionReader(configLocations);
            //2、解析配置文件，将配置信息变成BeanDefinition对象
            List<BeanDefinition> beanDefinitions = reader.loadBeanDefinitions();
            // 把beanDefinitions注册到beanDefinitionMap中
            doRegisterBeanDefinition(beanDefinitions);
            // 初始化阶段完成
            //注入
            doAutowired();

        }catch (Exception e) {
            e.printStackTrace();
        }





    }

    private void doAutowired() {
        for (Map.Entry<String, BeanDefinition> beanDefinitionEntry : this.beanDefinitionMap.entrySet()) {
            String beanName = beanDefinitionEntry.getKey();
            getBean(beanName);
        }
    }

    private void doRegisterBeanDefinition(List<BeanDefinition> beanDefinitions) throws Exception {
        for (BeanDefinition beanDefinition : beanDefinitions) {
            if (beanDefinitionMap.containsKey(beanDefinition.getFactoryBeanName())) {
                throw new Exception("The " + beanDefinition.getFactoryBeanName() + " exists");
            }
            beanDefinitionMap.put(beanDefinition.getFactoryBeanName(), beanDefinition);
            beanDefinitionMap.put(beanDefinition.getBeanClassName(), beanDefinition);
        }
    }

    public Object getBean(Class beanClass) {
        return getBean(beanClass.getName());
    }

    public Object getBean(String beanName) {
        //获取BeanDefinition配置信息
        BeanDefinition beanDefinition = this.beanDefinitionMap.get(beanName);
        //实例化
        Object instance = instantiateBean(beanName, beanDefinition);
        //将创建出的实例包装成BeanWrapper对象
        BeanWrapper beanWrapper = new BeanWrapper(instance);
        //把beanWrapper放到ioc容器中
        this.factoryBeanInstanceCache.put(beanName, beanWrapper);
        //依赖注入
        populateBean(beanWrapper);

        return factoryBeanInstanceCache.get(beanName).getWrapperInstance();
    }

    private void populateBean(BeanWrapper beanWrapper) {
        Class wrapperClass = beanWrapper.getWrapperClass();
        Object wrapperInstance = beanWrapper.getWrapperInstance();
        if (!(wrapperClass.isAnnotationPresent(Service.class) || wrapperClass.isAnnotationPresent(Controller.class))) {
            return;
        }
        Field[] fields = wrapperClass.getDeclaredFields();
        for (Field field : fields) {
            if (!field.isAnnotationPresent(AutoWired.class)) {
                continue;
            }

            AutoWired autoWired = field.getAnnotation(AutoWired.class);
            String autoWiredBeanName = autoWired.value().trim();
            if ("".equals(autoWiredBeanName)){
                autoWiredBeanName = field.getType().getName();
            }

            field.setAccessible(true);
            try {
                if (this.factoryBeanInstanceCache.get(autoWiredBeanName) == null) {
                    continue;
                }
                field.set(wrapperInstance, this.factoryBeanInstanceCache.get(autoWiredBeanName).getWrapperInstance());
            }catch (Exception e) {
                e.printStackTrace();
                continue;
            }

        }

    }

    private Object instantiateBean(String beanName, BeanDefinition beanDefinition) {
        Object instance = null;
        try {
            Class clazz = Class.forName(beanDefinition.getBeanClassName());
            instance = clazz.newInstance();
            factoryBeanObjectCache.put(beanName, instance);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return instance;

    }

    public String[] getBeanDefinitionNames() {
       return this.beanDefinitionMap.keySet().toArray(new String[this.beanDefinitionMap.size()]);
    }

    public int getBeanDefinitionCount() {
        return this.beanDefinitionMap.size();
    }
}
