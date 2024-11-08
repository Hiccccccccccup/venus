package com.jozz.venus.handler;

import org.springframework.beans.factory.FactoryBean;

import java.lang.reflect.Proxy;

public class DaoFactoryBean<T> implements FactoryBean<T> {

    private Class<T> interfaceType;

    public DaoFactoryBean(Class<T> interfaceType) {
        this.interfaceType = interfaceType;
    }

    @Override
    public T getObject() throws Exception {
        //利用动态代理生成Dao的实例对象
        Object instance = Proxy.newProxyInstance(DaoFactoryBean.class.getClassLoader(), new Class[]{interfaceType}, new DaoProxy(interfaceType));
        return (T) instance;
    }

    @Override
    public Class<?> getObjectType() {
        return interfaceType;
    }

}
