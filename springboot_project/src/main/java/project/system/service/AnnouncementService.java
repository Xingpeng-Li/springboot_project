package project.system.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface AnnouncementService {
    void sendAnnouncement(String type, String title, String body, Integer userId, MultipartFile file);

    List<Map<String,Object>> getAnnouncement(String type, Integer userId);
}
