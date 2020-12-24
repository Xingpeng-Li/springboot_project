package project.system.service;


import org.springframework.web.multipart.MultipartFile;
import project.system.domain.Post;

public interface UploadService {
    String uploadImage(MultipartFile file);

    String uploadFile(MultipartFile file);

    void uploadFie(MultipartFile file, Post post);
}
