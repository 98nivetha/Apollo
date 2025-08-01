package com.example.RequestModels;
import com.google.gson.annotations.SerializedName;

public class FilterRequest {
    @SerializedName("Companycode")
    private String companyCode;
    public FilterRequest(String companyCode) {
        this.companyCode = companyCode;
    }
    public String getCompanyCode() {
        return companyCode;
    }
    public void setCompanyCode(String companyCode) {
        this.companyCode = companyCode;
    }

}
