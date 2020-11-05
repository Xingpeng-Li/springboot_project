package project.system.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import project.system.domain.Announcement;

import java.util.List;

@Mapper
public interface AnnouncementMapper {
    int deleteByPrimaryKey(Integer announcementId);

    int insert(Announcement record);

    int insertSelective(Announcement record);

    Announcement selectByPrimaryKey(Integer announcementId);

    int updateByPrimaryKeySelective(Announcement record);

    int updateByPrimaryKey(Announcement record);

    //新添加---李星鹏
    //获得系统公告
    List<Announcement> getSystemAnnouncement();
    //根据公告类型以及公告sender获得公告
    List<Announcement> getAnnouncement(@Param("type")String type, @Param("sender")Integer sender);
}
