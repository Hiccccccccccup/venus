package com.jozz.venus.mapper;


import com.jozz.venus.domain.Order;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @Author: yipeng
 * @Date: 2021/6/28 21:19
 */
//@Repository
@Mapper
public interface OrderDao {

    List<Order> getOrders(Order order);

    int addOrder(Order orderInfo);

}

