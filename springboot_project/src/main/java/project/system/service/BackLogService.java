package project.system.service;

import project.system.domain.Backlog;

import java.util.Date;
import java.util.List;

public interface BackLogService {

    void createBackLog(Integer userId,String title,String description,Boolean isFinished,Boolean isOverTime, String endTime);

    List<Backlog> getNotFinishedBackLogs(Integer userId);

    List<Backlog> getFinishedBackLogs(Integer userId);

    void checkOverTimedBacklogs();

    void finishBackLog(Integer backLogId);

    void deleteBackLog(Integer backLogId);

    void updateBackLog(Integer backlogId,Integer userId, String title, String description, Boolean isFinished, Boolean isOverTime, String endTime);

    void overBackLog(Integer backLogId);
}
