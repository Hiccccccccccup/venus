package com.jozz.venus.service;

import com.jozz.venus.dao.OrderDao;
import com.jozz.venus.dao.UserDao;
import com.jozz.venus.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    private UserDao userDao;
    @Autowired
    private OrderDao testDao;

    public void save(){
        User user = new User();
        user.setAge(11);
        user.setName("张三");
        userDao.save(user);
    }

    public boolean exist(){
        return userDao.exist(1L);
    }

    public User findOne(){
        return userDao.findOne(1L);
    }
}
