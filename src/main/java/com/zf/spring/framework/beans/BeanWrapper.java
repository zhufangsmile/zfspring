package com.zf.spring.framework.beans;

public class BeanWrapper {

    private Object wrapperInstance;

    private Class wrapperClass;

    public BeanWrapper(Object wrapperInstance) {
        this.wrapperInstance = wrapperInstance;
        wrapperClass = wrapperInstance.getClass();
    }

    public Object getWrapperInstance() {
        return wrapperInstance;
    }

    public void setWrapperInstance(Object wrapperInstance) {
        this.wrapperInstance = wrapperInstance;
    }

    public Class getWrapperClass() {
        return wrapperClass;
    }

    public void setWrapperClass(Class wrapperClass) {
        this.wrapperClass = wrapperClass;
    }
}
