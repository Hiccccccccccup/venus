package com.jozz.venus.handler;

import com.jozz.venus.util.ESUtils;

import java.io.Serializable;
import java.lang.reflect.*;

public class DaoProxy<T, ID extends Serializable> implements InvocationHandler {
    //被代理类class
    private Class<T> interfaceType;
    //被代理类对应泛型对象class
    private Class<T> beanType;
    //索引名称
    private String indexName;
    Field[] declaredFields;
    public DaoProxy(Class<T> interfaceType) {
        this.interfaceType = interfaceType;
    }

    private void preHandle(){
        Type[] t = interfaceType.getGenericInterfaces();
        Type type = t[0];
        Type[] actualTypeArguments = ((ParameterizedType) type).getActualTypeArguments();
        beanType = (Class<T>) actualTypeArguments[0];
        //类注解处理
        boolean annotationPresent = beanType.isAnnotationPresent(Document.class);
        if (!annotationPresent) {
            throw new RuntimeException(beanType.getSimpleName() + "need annotation @Document");
        }
        Document[] annotationsByType = beanType.getAnnotationsByType(Document.class);
        for (Document document : annotationsByType) {
            indexName = document.indexName();
        }
        //类属性处理
        declaredFields = beanType.getDeclaredFields();
        for (Field declaredField : declaredFields) {
            declaredField.setAccessible(true);
        }
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (Object.class.equals(method.getDeclaringClass())) {
            return method.invoke(this, args);
        }
        System.out.println("执行业务逻辑" + method.getName());
        String methodName = method.getName();
        if ("save".equals(methodName)) {
            System.out.println("执行save逻辑");
            T arg = (T) args[0];
            //属性值获取
            for (Field declaredField : declaredFields) {
                Object o = declaredField.get(arg);
                Object name = declaredField.getName();
                System.out.println(name + "=>" + o);
            }
            ESUtils.insert(indexName, "", arg);
            System.out.println("执行save逻辑结束");
        } else if ("findOne".equals(methodName)) {
            ID id = (ID) args[0];
            T byId = ESUtils.findById("movies-new", id, beanType);
            return byId;
        }  else if ("findOne".equals(methodName)) {
            ID id = (ID) args[0];
            T byId = ESUtils.findById("movies-new", id, beanType);
            return byId;
        }
        return null;
    }
}
