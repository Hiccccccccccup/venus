package com.jozz.venus.domain;

import java.util.Date;

public class GroupMsgPushOffset {
    /**
     *
     */
    private Integer id;

    /**
     * 用户id
     */
    private Integer userId;

    /**
     * 群组id
     */
    private Integer groupId;

    /**
     * 已接收的最后一条消息id
     */
    private Integer lastMsgId;

    /**
     *
     */
    private Date createTime;

    /**
     *
     */
    private Date updateTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getGroupId() {
        return groupId;
    }

    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }

    public Integer getLastMsgId() {
        return lastMsgId;
    }

    public void setLastMsgId(Integer lastMsgId) {
        this.lastMsgId = lastMsgId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}