package com.project.mapper;

import com.project.domain.Cloudfile;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CloudfileMapper {
    int deleteByPrimaryKey(Integer fileId);

    int insert(Cloudfile record);

    int insertSelective(Cloudfile record);

    Cloudfile selectByPrimaryKey(Integer fileId);

    int updateByPrimaryKeySelective(Cloudfile record);

    int updateByPrimaryKey(Cloudfile record);
    //@author DKR 分页获取用户云空间文件目录列表
    List<Cloudfile> getCloudFilesLimited(Integer userId, @Param("offset") Integer offset, @Param("pageSize") Integer pageSize);
    //@author DKR 获取用户云空间所有文件目录列表
    List<Cloudfile> queryAllFiles(Integer userId);
    //@author DKR 搜索获取用户云空间文件目录列表
    List<Cloudfile> searchFileByKey(String key, Integer userId);

}
