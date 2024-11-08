package com.jozz.venus.controller;

import com.jozz.venus.domain.User;
import com.jozz.venus.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.util.Base64;

@RestController
@RequestMapping("user")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("hello")
    public @ResponseBody byte[] test(){
        return "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAIAAACQd1PeAAAADElEQVR42mP4//8/AAX+Av4zEpUUAAAAAElFTkSuQmCC".getBytes();
    }

    @GetMapping("/open/{mailId}")
    public void test1(HttpServletResponse response, @PathVariable Long mailId){
        //邮件ID、哪个客户(不在库客户客户名称非必填)，是否打开、打开时间、打开次数、邮件类别(templateType)、邮件标题(mailTitle)
        //1.根据mailId查询邮件发送记录表
        //2.新增记录到邮件用户浏览记录表
        //邮件发送记录表,增加客户ID,客户名称,邮件模板code,邮件标题
        //邮件用户浏览记录表(mail_scan_log):主键id,邮件id,客户ID,邮件模板code,邮件模板标题,创建时间
        String msg = "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAIAAACQd1PeAAAADElEQVR42mP4//8/AAX+Av4zEpUUAAAAAElFTkSuQmCC";
        byte[] decode = Base64.getDecoder().decode(msg.getBytes());
        try {
            OutputStream os = response.getOutputStream();
            os.write(decode);
            os.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
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
