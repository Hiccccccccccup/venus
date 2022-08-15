package com.jozz.venus.handler;

import java.io.Serializable;

public interface BaseDao<T,ID extends Serializable> {

    void save(T t);

    boolean exist(ID id);

    T findOne(ID id);
}
