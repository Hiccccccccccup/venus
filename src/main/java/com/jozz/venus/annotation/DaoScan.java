package com.jozz.venus.annotation;

import com.jozz.venus.handler.DaoImportBeanDefinitionRegistrar;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
@Import({DaoImportBeanDefinitionRegistrar.class})//主要为了加载该类
public @interface DaoScan {
    String[] value() default {};
}
