package project.system.controller;

import com.project.response.CommonReturnType;
import com.project.service.UploadService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import project.system.error.BusinessException;
import project.system.error.EmBusinessError;

import javax.annotation.Resource;

/*
@author WL
@CreateDate 2020-7-15
@update 2020-7-16
@description 提供聊天发送文件/图片相关api
*/
@RestController
public class FileController extends BaseController {

    @Resource
    private UploadService uploadService;

    @RequestMapping(value = "/upload/{type}",method = RequestMethod.POST)
    public CommonReturnType uploadImage(@RequestParam("file") MultipartFile file, @PathVariable String type) {
        String url = null;
        if ("image".equals(type)) {
            url = uploadService.uploadImage(file);
        } else if ("file".equals(type)) {
            url = uploadService.uploadFile(file);
        } else {
            throw new BusinessException(EmBusinessError.FILE_UPLOAD_ERROR);
        }
        if (url == null) {
            throw new BusinessException(EmBusinessError.FILE_UPLOAD_ERROR);
        }
        return CommonReturnType.create(url);
    }
}
