package com.jozz.venus.annotation;

import java.lang.annotation.*;

/**
 *  OpenSearch doc注解
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface Document {
    /**
     * 索引名称
     * @return
     */
    String indexName();
}