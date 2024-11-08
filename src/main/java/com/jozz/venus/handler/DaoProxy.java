package com.jozz.venus.handler;

import com.jozz.venus.annotation.Document;
import com.jozz.venus.annotation.Id;
import com.jozz.venus.util.ESUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import java.io.Serializable;
import java.lang.reflect.*;
import static com.jozz.venus.enums.MethodEnum.match;

/**
 * 动态代理类
 * @param <T>
 * @param <ID>
 */
@Slf4j
public class DaoProxy<T, ID extends Serializable> implements InvocationHandler {
    /**
     * 被代理类class
     */
    private Class<T> interfaceType;
    /**
     * 被代理类对应泛型对象class
     */
    private Class<T> beanType;
    /**
     * 索引名称
     */
    private String indexName;
    /**
     * 属性
     */
    private Field[] declaredFields;
    /**
     * ID Field
     */
    private Field idField;
    /**
     * 构造方法
     */
    public DaoProxy(Class<T> interfaceType) {
        this.interfaceType = interfaceType;
        this.init();
    }

    /**
     * 初始化处理
     */
    private void init() {
        Type[] t = interfaceType.getGenericInterfaces();
        Type type = t[0];
        Type[] actualTypeArguments = ((ParameterizedType) type).getActualTypeArguments();
        beanType = (Class<T>) actualTypeArguments[0];
        //类注解处理
        boolean annotationPresent = beanType.isAnnotationPresent(Document.class);
        if (!annotationPresent) {
            throw new RuntimeException(beanType.getSimpleName() + "need to annotation @Document");
        }
        //获取索引名
        Document document = beanType.getAnnotation(Document.class);
        indexName = document.indexName();
        if (StringUtils.isBlank(indexName)) {
            throw new RuntimeException(beanType.getSimpleName() + "must set ${indexName} with annotation @Document");
        }
        //类属性处理
        declaredFields = beanType.getDeclaredFields();
        for (Field declaredField : declaredFields) {
            declaredField.setAccessible(true);
            if (declaredField.isAnnotationPresent(Id.class)) {
                idField = declaredField;
            }
        }
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (Object.class.equals(method.getDeclaringClass())) {
            //调用Object方法,不处理
            return method.invoke(this, args);
        }
        //分别处理各代理方法
        String methodName = method.getName();
        switch (match(methodName)) {
            case SAVE:{
                T arg = (T) args[0];
                //获取ID属性值
                ID id = null;
                if (idField != null) {
                    id = (ID) idField.get(arg);
                }
                ESUtils.insert(indexName, id, arg);
                return null;
            }
            case FIND_ONE:{
                ID id = (ID) args[0];
                T one = ESUtils.findById(indexName, id, beanType);
                return one;
            }
            case DELETE_BY_ID:{
                ID id = (ID) args[0];
                return ESUtils.deleteById(indexName, id);
            }
            case EXIST:{
                ID id = (ID) args[0];
                T one = ESUtils.findById(indexName, id, beanType);
                return one != null;
            }
            default:
                throw new RuntimeException("methed " + methodName + "can not be used!");
        }
    }
}
