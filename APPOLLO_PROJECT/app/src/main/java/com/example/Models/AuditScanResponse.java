package com.example.Models;
import java.util.List;

public class AuditScanResponse {
    private boolean Result;
    private String ErrorCode;
    private String Message;
    private List<ScanResult> Data;
    public boolean isResult() {
        return Result;
    }

    public void setResult(boolean result) {
        Result = result;
    }
    public String getErrorCode() {
        return ErrorCode;
    }

    public void setErrorCode(String errorCode) {
        ErrorCode = errorCode;
    }
    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }

    public List<ScanResult> getData() {
        return Data;
    }

    public void setData(List<ScanResult> data) {
        Data = data;
    }

    public static class ScanResult {
        private String Assetsubcode;
        private String Auditcode;
        private String Status;
        private String Message;

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

        public String getStatus() {
            return Status;
        }

        public void setStatus(String status) {
            Status = status;
        }

        public String getMessage() {
            return Message;
        }

        public void setMessage(String message) {
            Message = message;
        }
    }
}

