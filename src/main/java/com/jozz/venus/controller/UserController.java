package com.jozz.venus.controller;

import com.jozz.venus.domain.User;
import com.jozz.venus.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("user")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("hello")
    public String test(){
        return "hello ssm";
    }

    @GetMapping("save")
    public String save(){
        userService.save();
        return "ok";
    }

    @GetMapping("exist")
    public String exist(){
        boolean exist = userService.exist();
        return "ok";
    }

    @GetMapping("findOne")
    public String findOne(){
        User user = userService.findOne();
        return "ok";
    }
}
