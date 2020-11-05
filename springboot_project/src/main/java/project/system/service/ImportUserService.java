package project.system.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ImportUserService {
    int importUser(MultipartFile file, Integer userId) throws IOException;
}
