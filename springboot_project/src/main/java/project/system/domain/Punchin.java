package project.system.domain;

import java.util.Date;

public class Punchin {
    private Integer punchinId;

    private Date puchinTime;

    private Integer userId;

    private String punchinLocation;

    public Integer getPunchinId() {
        return punchinId;
    }

    public void setPunchinId(Integer punchinId) {
        this.punchinId = punchinId;
    }

    public Date getPuchinTime() {
        return puchinTime;
    }

    public void setPuchinTime(Date puchinTime) {
        this.puchinTime = puchinTime;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getPunchinLocation() {
        return punchinLocation;
    }

    public void setPunchinLocation(String punchinLocation) {
        this.punchinLocation = punchinLocation == null ? null : punchinLocation.trim();
    }
}