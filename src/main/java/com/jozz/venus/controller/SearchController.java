package com.jozz.venus.controller;

import com.jozz.venus.domain.ESEntity;
import com.jozz.venus.util.ESUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

@RestController
@RequestMapping("search")
public class SearchController {

    @Autowired
    private ESUtils esUtils;


    @GetMapping("createIndex")
    public String createIndex(){
        HashMap<String, Object> stringObjectHashMap = new HashMap<>();
        stringObjectHashMap.put("title","师爷高明");
        stringObjectHashMap.put("name","让子弹飞");
        stringObjectHashMap.put("cast","姜文");
        stringObjectHashMap.put("time","2H");
        stringObjectHashMap.put("year",2019);
        esUtils.updateData("movies","1",stringObjectHashMap);
        return "ok";
    }

    @GetMapping("findById")
    public String findById(){
        HashMap byId = esUtils.findById("movies", "1", HashMap.class);
        return "ok";
    }
    @GetMapping("findData")
    public String findData(){
        ESEntity esEntity = new ESEntity();
        esEntity.setPageSize(10);
        esEntity.setPageNumber(1);
        esUtils.findData("movies", esEntity);
        return "ok";
    }

}
