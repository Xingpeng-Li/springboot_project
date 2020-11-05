package com.project.mapper;

import com.project.domain.SocketMessage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/*
@author WL
@CreateDate 2020-7-15
@update 2020-7-16,2020-7-17
@description 主要负责从数据库中查找消息记录
*/
@Mapper
public interface SocketMessageMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(SocketMessage record);

    int insertSelective(SocketMessage record);

    SocketMessage selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(SocketMessage record);

    int updateByPrimaryKey(SocketMessage record);

    List<SocketMessage> getSocketMessageByGroup(@Param("date") Date date, @Param("deptId") Integer deptId, @Param("companyId") Integer companyId);

    List<SocketMessage> getSocketMessageByUser(@Param("launchId") String launchId, @Param("receiveId") String receiveId, @Param("date") Date date);
}
