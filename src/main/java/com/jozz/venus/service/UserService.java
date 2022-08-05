package com.jozz.venus.service;

import com.jozz.venus.dao.TestDao;
import com.jozz.venus.dao.UserDao;
import com.jozz.venus.domain.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    private UserDao userDao;
    @Autowired
    private TestDao testDao;

//    public void save(){
//        User user = new User();
//        user.setAge(11);
//        user.setName("张三");
//        userDao.save(user);
//    }

    public void save(){
        Order order = new Order();
        order.setId(14L);
        order.setOrderNo("NO112");
        testDao.save(order);
    }

//    public boolean exist(){
//        return userDao.exist(1L);
//    }

    public boolean exist(){
        return testDao.exist(1);
    }
}
