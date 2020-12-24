package project.system.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import project.system.domain.Cloudfile;

import java.util.List;

@Mapper
public interface CloudfileMapper {
    int deleteByPrimaryKey(Integer fileId);

    int insert(Cloudfile record);

    int insertSelective(Cloudfile record);

    Cloudfile selectByPrimaryKey(Integer fileId);

    int updateByPrimaryKeySelective(Cloudfile record);

    int updateByPrimaryKey(Cloudfile record);
    //@author 李星鹏 分页获取用户云空间文件目录列表
    List<Cloudfile> getCloudFilesLimited(Integer userId, @Param("offset") Integer offset, @Param("pageSize") Integer pageSize);
    //@author 李星鹏 获取用户云空间所有文件目录列表
    List<Cloudfile> queryAllFiles(Integer userId);
    //@author 李星鹏 搜索获取用户云空间文件目录列表
    List<Cloudfile> searchFileByKey(String key, Integer userId);

}
