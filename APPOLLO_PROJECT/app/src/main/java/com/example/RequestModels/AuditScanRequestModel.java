package com.example.RequestModels;

public class AuditScanRequestModel {
    private String Assetsubcode;
    private String Auditcode;

    public AuditScanRequestModel(String assetsubcode, String auditcode) {
        this.Assetsubcode = assetsubcode;
        this.Auditcode = auditcode;
    }

    public AuditScanRequestModel() {
    }
    public String getAssetsubcode() {
        return Assetsubcode;
    }

    public void setAssetsubcode(String assetsubcode) {
        Assetsubcode = assetsubcode;
    }

    public String getAuditcode() {
        return Auditcode;
    }

    public void setAuditcode(String auditcode) {
        Auditcode = auditcode;
    }
}

