package com.example.Models;


import com.google.gson.annotations.SerializedName;

public class AuditLastDatasListResponse {

    @SerializedName("Scanid")
    private int scanId;

    @SerializedName("Auditid")
    private Integer auditId;

    @SerializedName("Perassteno")
    private String perAssetNo;

    @SerializedName("Subassetcode")
    private String subAssetCode;

    @SerializedName("Locationcode")
    private String locationCode;

    @SerializedName("Remarks")
    private String remarks;

    @SerializedName("Phase")
    private String phase;

    @SerializedName("Createdby")
    private String createdBy;

    @SerializedName("Createdon")
    private String createdOn;

    @SerializedName("Modifiedby")
    private String modifiedBy;

    @SerializedName("Modifiedon")
    private String modifiedOn;

    @SerializedName("Auditcode")
    private String auditCode;

    // Getters
    public int getScanId() {
        return scanId;
    }

    public Integer getAuditId() {
        return auditId;
    }

    public String getPerAssetNo() {
        return perAssetNo;
    }

    public String getSubAssetCode() {
        return subAssetCode;
    }

    public String getLocationCode() {
        return locationCode;
    }

    public String getRemarks() {
        return remarks;
    }

    public String getPhase() {
        return phase;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public String getCreatedOn() {
        return createdOn;
    }

    public String getModifiedBy() {
        return modifiedBy;
    }

    public String getModifiedOn() {
        return modifiedOn;
    }

    public String getAuditCode() {
        return auditCode;
    }
}
