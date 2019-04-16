package com.yang.vip.spring.formework.beans;

public class ZYBeanWrapper {
    private Object wrappedInstance;
    private Class<?> wrappedClass;

    public ZYBeanWrapper(Object wrappedInstance) {
        this.wrappedInstance = wrappedInstance;
    }

    public Object getWrappedInstance() {
        return this.wrappedInstance;
    }

    public Class<?> getWrappedClass() {
        return this.wrappedInstance.getClass();
    }
}
