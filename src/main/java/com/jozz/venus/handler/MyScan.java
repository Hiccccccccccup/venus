package com.jozz.venus.handler;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
@Import({MyImportBeanDefinitionRegistrar.class})//主要为了加载该类
public @interface MyScan {

}
