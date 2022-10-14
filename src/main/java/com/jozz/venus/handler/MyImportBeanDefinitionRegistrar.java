package com.jozz.venus.handler;

import com.jozz.venus.annotation.DaoScan;
import com.jozz.venus.util.ClassScaner;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;

import java.util.HashSet;
import java.util.Set;

/**
 * 代理类动态注册器
 * 将动态代理类注册到Spring管理
 */
public class MyImportBeanDefinitionRegistrar  implements ImportBeanDefinitionRegistrar  {
    /**
     * @DaoScan注解中保存basePackages的属性名
     */
    private final static String BASE_PACKAGES_ATTRIBUTE_NAME = "value";

    @Override
    public void registerBeanDefinitions(AnnotationMetadata annotationMetadata, BeanDefinitionRegistry beanDefinitionRegistry) {
        //获取@DaoScan注解上所有的value
        AnnotationAttributes daoScanAttrs = AnnotationAttributes.fromMap(annotationMetadata.getAnnotationAttributes(DaoScan.class.getName()));
        String[] basePackagesArray = daoScanAttrs.getStringArray(BASE_PACKAGES_ATTRIBUTE_NAME);
        //扫描所有basePackage,通过反射获取需要代理的接口的clazz列表
        Set<Class> beanClazzs = new HashSet<>();
        for (String basePackages : basePackagesArray) {
            beanClazzs.addAll(ClassScaner.scan(basePackages));
        }
        for (Class beanClazz : beanClazzs) {
            BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(beanClazz);
            GenericBeanDefinition definition = (GenericBeanDefinition) builder.getRawBeanDefinition();

            //在这里，我们可以给该对象的属性注入对应的实例。
            //比如mybatis，就在这里注入了dataSource和sqlSessionFactory，
            // 注意，如果采用definition.getPropertyValues()方式的话，
            // 类似definition.getPropertyValues().add("interfaceType", beanClazz);
            // 则要求在FactoryBean（本应用中即ServiceFactory）提供setter方法，否则会注入失败
            // 如果采用definition.getConstructorArgumentValues()，
            // 则FactoryBean中需要提供包含该属性的构造方法，否则会注入失败
            definition.getConstructorArgumentValues().addGenericArgumentValue(beanClazz);

            //注意，这里的BeanClass是生成Bean实例的工厂，不是Bean本身。
            // FactoryBean是一种特殊的Bean，其返回的对象不是指定类的一个实例，
            // 其返回的是该工厂Bean的getObject方法所返回的对象。
            definition.setBeanClass(DaoFactoryBean.class);

            //这里采用的是byType方式注入，类似的还有byName等
            definition.setAutowireMode(GenericBeanDefinition.AUTOWIRE_BY_TYPE);
            beanDefinitionRegistry.registerBeanDefinition(beanClazz.getSimpleName(), definition);
        }
    }
}
