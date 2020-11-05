package project.system.domain;

import java.util.Date;

public class WorkReport {
    private Integer reportId;

    private String reportType;

    private Integer reporterId;

    private String reportUrl;

    private Integer reportApprover;

    private Integer reportSecondApprover;

    private Date reportTime;

    public Integer getReportId() {
        return reportId;
    }

    public void setReportId(Integer reportId) {
        this.reportId = reportId;
    }

    public String getReportType() {
        return reportType;
    }

    public void setReportType(String reportType) {
        this.reportType = reportType == null ? null : reportType.trim();
    }

    public Integer getReporterId() {
        return reporterId;
    }

    public void setReporterId(Integer reporterId) {
        this.reporterId = reporterId;
    }

    public String getReportUrl() {
        return reportUrl;
    }

    public void setReportUrl(String reportUrl) {
        this.reportUrl = reportUrl == null ? null : reportUrl.trim();
    }

    public Integer getReportApprover() {
        return reportApprover;
    }

    public void setReportApprover(Integer reportApprover) {
        this.reportApprover = reportApprover;
    }

    public Integer getReportSecondApprover() {
        return reportSecondApprover;
    }

    public void setReportSecondApprover(Integer reportSecondApprover) {
        this.reportSecondApprover = reportSecondApprover;
    }

    public Date getReportTime() {
        return reportTime;
    }

    public void setReportTime(Date reportTime) {
        this.reportTime = reportTime;
    }
}