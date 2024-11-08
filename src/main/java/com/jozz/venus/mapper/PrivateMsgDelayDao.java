package com.jozz.venus.mapper;


import com.jozz.venus.domain.PrivateMsgDelay;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface PrivateMsgDelayDao {
    List<PrivateMsgDelay> selectByUserId(Long userId);

    int insert(PrivateMsgDelay privateMsgDelay);

    int deleteById(Integer id);
}

