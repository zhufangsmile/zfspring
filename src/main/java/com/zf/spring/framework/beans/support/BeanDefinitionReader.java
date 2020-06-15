package com.zf.spring.framework.beans.support;

import com.zf.spring.framework.beans.config.BeanDefinition;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class BeanDefinitionReader {

    private Properties contextConfig = new Properties();

    private List<String> registerBeanClasses = new ArrayList<String>();

    public BeanDefinitionReader(String[] configLocations) {
        //加载配置文件
        doLoadConfig(configLocations[0]);
        //扫描包
        doScanner(contextConfig.getProperty("scanPackage"));
    }

    private void doScanner(String scanPackage) {
        //com.zf.demo
        URL url = this.getClass().getClassLoader().getResource("/" + scanPackage.replaceAll("\\.", "/"));
        File classpath = new File(url.getFile());

        for (File file : classpath.listFiles()) {
            if (file.isDirectory()) {
                doScanner(scanPackage+ "." + file.getName());
            }else {
                if (!file.getName().endsWith(".class")) {
                    return;
                }else {

                    String className = scanPackage + "." + file.getName().replace(".class", "");
                    registerBeanClasses.add(className);
                }

            }
        }

    }

    private void doLoadConfig(String configLocation) {
        InputStream is = this.getClass().getClassLoader().getResourceAsStream(configLocation.replace("classpath:", ""));
        try {
            contextConfig.load(is);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != is) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }


    public List<BeanDefinition> loadBeanDefinitions() {
        List<BeanDefinition> result = new ArrayList<BeanDefinition>();

        try {
            for (String className : registerBeanClasses) {
                Class clazz = Class.forName(className);
                //如果是接口
                if (clazz.isInterface()) {
                    continue;
                }
                result.add(doCreateBeanDefinition(toLowerFirstCase(clazz.getSimpleName()), className));

                //全类型   TODO 用处？？？
                for (Class i : clazz.getInterfaces()) {
                    result.add(doCreateBeanDefinition(i.getName() ,className));
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    private BeanDefinition doCreateBeanDefinition(String beanFactoryName, String beanClassName) {
        BeanDefinition beanDefinition = new BeanDefinition();
        beanDefinition.setBeanClassName(beanClassName);
        beanDefinition.setFactoryBeanName(beanFactoryName);
        return beanDefinition;
    }

    private String toLowerFirstCase(String simpleName) {
        char[] chars = simpleName.toCharArray();
        chars[0] += 32;
        return new String(chars);
    }

    public Properties getConfig() {
        return this.contextConfig;
    }
}
