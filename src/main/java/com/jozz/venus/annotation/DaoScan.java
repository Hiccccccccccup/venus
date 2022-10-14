package com.jozz.venus.annotation;

import com.jozz.venus.handler.MyImportBeanDefinitionRegistrar;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
@Import({MyImportBeanDefinitionRegistrar.class})//主要为了加载该类
public @interface DaoScan {
    String[] value() default {};
}
