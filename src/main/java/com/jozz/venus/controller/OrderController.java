package com.jozz.venus.controller;

import com.jozz.venus.mapper.OrderDao;
import com.jozz.venus.domain.Order;
import com.jozz.venus.mapper.PrivateMsgDelayDao;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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
    @Autowired
    private PrivateMsgDelayDao privateMsgDelayDao;
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

    @GetMapping("/query")
    public List<Order> query(){
        Order order = new Order();
        order.setUserId(31L);
        List<Order> orders = orderDao.getOrders(order);
        return orders;
    }

    @GetMapping("/update")
    public void update(){
        Order order = new Order();
        order.setId(2L);
        order.setOrderId(3L);
        order.setUserName("里斯本");
        int i = orderDao.updateOrder(order);
    }
}
