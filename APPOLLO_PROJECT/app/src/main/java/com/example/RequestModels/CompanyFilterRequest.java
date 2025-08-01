package com.example.RequestModels;
public class CompanyFilterRequest {
    private String Companycode;
    public CompanyFilterRequest(String companycode) {
        this.Companycode = companycode;
    }

    public String getCompanycode() {
        return Companycode;
    }

    public void setCompanycode(String companycode) {
        Companycode = companycode;
    }
}

