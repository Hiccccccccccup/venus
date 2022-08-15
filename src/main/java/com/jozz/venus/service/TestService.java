package com.jozz.venus.service;

import com.jozz.venus.dao.OrderDao;
import com.jozz.venus.domain.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TestService {
    @Autowired
    private OrderDao orderDao;
    public void save(){
        Order order = new Order();
        order.setId(14L);
        order.setUserName("NO112");
        orderDao.save(order);
    }

    public boolean exist(){
        return orderDao.exist(1);
    }
}
