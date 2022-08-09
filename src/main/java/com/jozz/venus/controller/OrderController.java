package com.jozz.venus.controller;

import com.jozz.venus.mapper.OrderDao;
import com.jozz.venus.domain.Order;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: yipeng
 * @Date: 2021/6/28 23:11
 */
@Slf4j
@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderDao orderDao;

    @GetMapping("/insert")
    public String insert() {
        for (int i = 0; i < 1000; i++) {
            long userId = i;
            long orderId = i + 1;
            Order order = new Order();
            order.setUserId(userId);
            order.setUserName("张三"+i);
            order.setOrderId(orderId);
            orderDao.addOrder(order);
        }
        return null;
    }
}
