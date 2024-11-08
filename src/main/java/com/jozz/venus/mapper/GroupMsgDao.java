package com.jozz.venus.mapper;

import com.jozz.venus.domain.GroupMsg;

public interface GroupMsgDao {
    int deleteByPrimaryKey(Integer id);

    int insert(GroupMsg record);

    int insertSelective(GroupMsg record);

    GroupMsg selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(GroupMsg record);

    int updateByPrimaryKey(GroupMsg record);
}