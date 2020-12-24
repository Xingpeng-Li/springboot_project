package project.system.service;


import org.springframework.web.multipart.MultipartFile;
import project.system.domain.Cloudfile;
import project.system.vo.CloudFileVo;

import java.util.List;
public interface CloudFileService {
    boolean uploadFile(MultipartFile file, Integer userId);
    boolean copyFile(Cloudfile cloudfile, Integer userId);
    List<CloudFileVo> getMyFiles(Integer userId, Integer pageNumber, Integer pageSize);
    Cloudfile getFileById(Integer fileId);
    boolean deleteFile(Integer fileId);
    List<CloudFileVo> getAllFiles(Integer userId);
    List<CloudFileVo> searchFiles(String key, Integer userId);
}
