package com.jozz.venus.annotation;

import java.lang.annotation.*;

/**
 *  Id注解
 *  主键
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD })
public @interface Id {
}