package com.example.RequestModels;

public class AuditRequestModel {
    private String Processstage;

    public AuditRequestModel(String processstage) {
        this.Processstage = processstage;
    }

    public String getProcessstage() {
        return Processstage;
    }

    public void setProcessstage(String processstage) {
        this.Processstage = processstage;
    }
}
