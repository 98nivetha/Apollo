package com.example.RequestModels;

public class AuditIDRequestModel {
    private Integer Auditid;

    public  AuditIDRequestModel(Integer auditid){
        this.Auditid = auditid;
    }

    public Integer getAuditid() {
        return Auditid;
    }

    public void setAuditid(Integer auditid) {
        this.Auditid = auditid;
    }
}
