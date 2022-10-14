package com.jozz.venus.handler;

import java.io.Serializable;

/**
 * 统一接口
 * @param <T>
 * @param <ID>
 */
public interface BaseDao<T,ID extends Serializable> {
    /**
     * 保存
     * @param t
     */
    void save(T t);

    /**
     * 根据ID判断数据是否存在
     * @param id
     * @return
     */
    boolean exist(ID id);

    /**
     * 根据ID删除
     * @param id
     * @return
     */
    boolean deleteById(ID id);

    /**
     * 根据ID查询
     * @param id
     * @return
     */
    T findOne(ID id);
}
