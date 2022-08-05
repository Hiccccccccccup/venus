package com.jozz.venus.handler;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class DaoProxy<T,ID> implements InvocationHandler {

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (Object.class.equals(method.getDeclaringClass())) {
            return method.invoke(this, args);
        }
        System.out.println("执行业务逻辑"+method.getName());
        String methodName = method.getName();
        if ("save".equals(methodName)) {
            System.out.println("执行save逻辑");
            T arg = (T)args[0];
            //类注解处理
            boolean annotationPresent = arg.getClass().isAnnotationPresent(Document.class);
            if (annotationPresent) {
                Document[] annotationsByType = arg.getClass().getAnnotationsByType(Document.class);
                for (Document document : annotationsByType) {
                    String s = document.indexName();
                    System.out.println(s);
                }
            }
            //类属性处理
            Field[] declaredFields = arg.getClass().getDeclaredFields();
            for (Field declaredField : declaredFields) {
                declaredField.setAccessible(true);
                Object o = declaredField.get(arg);
                Object name = declaredField.getName();
                System.out.println(name + "=>" + o);
            }
            System.out.println("执行save逻辑结束");
        } else if ("exist".equals(methodName)) {
            ID id = (ID)args[0];
            return true;
        }
        return null;
    }
}
