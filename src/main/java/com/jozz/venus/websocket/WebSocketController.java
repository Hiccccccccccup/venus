package com.jozz.venus.websocket;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import java.text.SimpleDateFormat;
import java.util.Date;

@Controller
@RequestMapping("/websocket")
public class WebSocketController {

    @GetMapping("push")
    public String push(){
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        String date = dateFormat.format(new Date());
        WebSocket.sendInfo(date,2);
        System.out.println(date);
        return "ok";
    }
}
