package com.jozz.venus.handler;

import org.springframework.beans.factory.FactoryBean;

import java.lang.reflect.Proxy;

public class MyFactoryBean<T> implements FactoryBean<T> {

    private Class<T> interfaceType;

    public MyFactoryBean(Class<T> interfaceType) {
        this.interfaceType = interfaceType;
    }

    @Override
    public T getObject() throws Exception {
        //利用动态代理生成MyDao的实例对象
        Object instance = Proxy.newProxyInstance(MyFactoryBean.class.getClassLoader(), new Class[]{interfaceType}, new DaoProxy(interfaceType));
        return (T) instance;
    }

    @Override
    public Class<?> getObjectType() {
        return interfaceType;
    }

}
