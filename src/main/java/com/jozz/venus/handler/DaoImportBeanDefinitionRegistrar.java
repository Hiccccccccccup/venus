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
public class DaoImportBeanDefinitionRegistrar implements ImportBeanDefinitionRegistrar  {
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
            definition.getConstructorArgumentValues().addGenericArgumentValue(beanClazz);
            //指定Bean工厂
            definition.setBeanClass(DaoFactoryBean.class);
            //这里采用的是byType方式注入，类似的还有byName等
            definition.setAutowireMode(GenericBeanDefinition.AUTOWIRE_BY_TYPE);
            beanDefinitionRegistry.registerBeanDefinition(beanClazz.getSimpleName(), definition);
        }
    }
}
