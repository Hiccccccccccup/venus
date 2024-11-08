package com.jozz.venus.controller;

import com.jozz.venus.mapper.PrivateMsgDelayDao;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

/**
 * @Author: yipeng
 * @Date: 2021/6/28 23:11
 */
@Slf4j
@RestController
@RequestMapping("/redis")
public class RedisController {

    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private PrivateMsgDelayDao privateMsgDelayDao;


    @GetMapping("/login/{userId}")
    public String insert(@PathVariable("userId") Long userId) {
        LocalDate now = LocalDate.now();
        String format = "20241107";
//        String format = now.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        redisTemplate.opsForValue().setBit("login_status:" + format, userId, true);
        return null;
    }

    @GetMapping("/isOnline/{userId}")
    public Boolean query(@PathVariable("userId") Long userId){
        Boolean login_status = redisTemplate.opsForValue().getBit("login_status", userId);
        return login_status;
    }
    @GetMapping("/bitCount/{key}")
    public Object bitCount(@PathVariable("key") String key){
        Object execute = getBitCount(key);
        return execute;
    }

    private Object getBitCount(String key) {
        Object execute = redisTemplate.execute((RedisCallback) (redisConnection) -> {
            byte[] rawKey = redisTemplate.getStringSerializer().serialize(key);
            return redisConnection.bitCount(rawKey);
        });
        return execute;
    }


    @GetMapping("/bitAnd")
    public Object bitAnd(){
        bitOp(RedisStringCommands.BitOperation.AND, "login_status:and", "login_status:20241108", "login_status:20241107");
        Object execute = getBitCount("login_status:and");
        return execute;
    }

    public void bitOp(RedisStringCommands.BitOperation bitOperation, String destKey, String... srcKeys) {
        redisTemplate.execute((RedisCallback) (redisConnection) -> {
            byte[] rawDestKey = redisTemplate.getStringSerializer().serialize(destKey);
            byte[][] rawSrcKeys = new byte[srcKeys.length][];
            for (int i = 0; i < srcKeys.length; i++) {
                rawSrcKeys[i] = redisTemplate.getStringSerializer().serialize(srcKeys[i]);
            }
            redisConnection.bitOp(bitOperation, rawDestKey, rawSrcKeys);
            return null;
        });
    }
}
