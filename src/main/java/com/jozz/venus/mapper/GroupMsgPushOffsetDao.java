package com.jozz.venus.mapper;

import com.jozz.venus.domain.GroupMsg;
import com.jozz.venus.domain.GroupMsgPushOffset;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface GroupMsgPushOffsetDao {
    int deleteByPrimaryKey(Integer id);

    int insert(GroupMsgPushOffset record);

    GroupMsgPushOffset selectByPrimaryKey(Integer id);

    int updateByPrimaryKey(GroupMsgPushOffset record);

    List<GroupMsgPushOffset> selectByUserId(Integer userId);
    List<GroupMsg> selectOfflineMsg(Integer userId);

    int updateOffset(@Param("userId") Integer userId, @Param("groupId") Integer groupId, @Param("lastMsgId") Integer lastMsgId);
}