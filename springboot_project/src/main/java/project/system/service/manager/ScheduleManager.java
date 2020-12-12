//package project.system.service.manager;
//
//import io.openvidu.java.client.OpenVidu;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Component;
//
//import javax.annotation.Resource;
//
///*
//@author WL
//@CreateDate 2020-7-09
//@update
//@description 定时任务
//*/
//@Slf4j
//@Component
//public class ScheduleManager {
//
//    @Resource
//    private OpenVidu openVidu;
//    /**
//     * 同步OpenVidu的活跃会话
//     */
//    @Scheduled(fixedDelay=5000,initialDelay=1000)
//    public void refreshOpenvidusTask() {
//        try {
//            log.debug("同步OpenVidu的活跃会话==>开始");
//            openVidu.fetch();
//            log.debug("同步OpenVidu的活跃会话==>结束");
//        } catch (Exception e) {
//            log.error("同步OpenVidu的活跃会话异常", e);
//        }
//    }
//}
