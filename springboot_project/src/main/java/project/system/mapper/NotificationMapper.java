package project.system.mapper;

import org.apache.ibatis.annotations.Mapper;
import project.system.domain.Notification;

@Mapper
public interface NotificationMapper {
    int deleteByPrimaryKey(Integer notificationId);

    int insert(Notification record);

    int insertSelective(Notification record);

    Notification selectByPrimaryKey(Integer notificationId);

    int updateByPrimaryKeySelective(Notification record);

    int updateByPrimaryKey(Notification record);

    Notification[] selectByReceiverId(Integer receiverId);

    int getUncheckedCount(Integer userId);
}
