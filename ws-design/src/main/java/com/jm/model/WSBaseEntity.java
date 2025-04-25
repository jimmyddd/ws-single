package com.jm.model;


public class WSBaseEntity {
    private String action;
    private Long timeStamp;
    private String subName;
    private String subId;

    public WSBaseEntity() {
    }

    public String getAction() {
        return this.action;
    }

    public Long getTimeStamp() {
        return this.timeStamp;
    }

    public String getSubName() {
        return this.subName;
    }

    public String getSubId() {
        return this.subId;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public void setTimeStamp(Long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public void setSubName(String subName) {
        this.subName = subName;
    }

    public void setSubId(String subId) {
        this.subId = subId;
    }

    public String toString() {
        return "WSBaseEntity(action=" + this.getAction() + ", timeStamp=" + this.getTimeStamp() + ", subName=" + this.getSubName() + ", subId=" + this.getSubId() + ")";
    }

}
