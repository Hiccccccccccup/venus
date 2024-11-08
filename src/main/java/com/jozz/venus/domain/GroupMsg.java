package com.jozz.venus.domain;

import java.util.Date;

public class GroupMsg {
    /**
    * 
    */
    private Integer id;

    /**
    * 
    */
    private Integer groupId;

    /**
    * 
    */
    private Integer fromId;

    /**
    * 
    */
    private String payload;

    /**
    * 
    */
    private Date createTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getGroupId() {
        return groupId;
    }

    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }

    public Integer getFromId() {
        return fromId;
    }

    public void setFromId(Integer fromId) {
        this.fromId = fromId;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}