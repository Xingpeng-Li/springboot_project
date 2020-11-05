package com.project.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ReportService {
    void sendReport(MultipartFile file,String addWatermark, String type, Integer reporter, String approver, String secondApprover) throws IOException;

    String getReportUrl(Integer notificationId);

    void returnReport(MultipartFile file, Integer notificationId) throws IOException;
}
