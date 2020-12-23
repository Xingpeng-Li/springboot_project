package project.system.view;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/*
@author 李星鹏
@CreateDate 2020-10-25
@update 2020-10-25 实现上传、复制、删除、分页获取文件列表业务逻辑
        2020-11-6 修复了一些bug,实现了获取全部文件列表、搜索文件的业务逻辑
@description 云空间文件服务类
*/
@ApiModel(description = "返回的文件视图类")
public class CloudFileView {
    @ApiModelProperty(value = "文件id",notes = "唯一标识")
    private Integer fileId;
    @ApiModelProperty("文件名")
    private String fileName;
    @ApiModelProperty(value = "文件大小",notes = "字节数")
    private Double fileSize;
    @ApiModelProperty(value = "文件后缀",notes = "不带.")
    private String fileSuffix;
    @ApiModelProperty(value = "文件在云存储的url",notes = "可直接下载")
    private String fileUrl;
    @ApiModelProperty("文件上传时间")
    private String fileUploadTime;

    public Integer getFileId() {
        return fileId;
    }

    public void setFileId(Integer fileId) {
        this.fileId = fileId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Double getFileSize() {
        return fileSize;
    }

    public void setFileSize(Double fileSize) {
        this.fileSize = fileSize;
    }

    public String getFileSuffix() {
        return fileSuffix;
    }

    public void setFileSuffix(String fileSuffix) {
        this.fileSuffix = fileSuffix;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public String getFileUploadTime() {
        return fileUploadTime;
    }

    public void setFileUploadTime(String fileUploadTime) {
        this.fileUploadTime = fileUploadTime;
    }
}
