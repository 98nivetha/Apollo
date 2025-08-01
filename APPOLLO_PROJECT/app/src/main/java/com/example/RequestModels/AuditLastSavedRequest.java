package com.example.RequestModels;

public class AuditLastSavedRequest {
    private String AuditCode;

    public  AuditLastSavedRequest(String auditCode){
        this.AuditCode = auditCode;
    }

    public String getAuditid() {
        return AuditCode;
    }

    public void setAuditid(String auditid) {
        this.AuditCode = auditid;
    }

}
