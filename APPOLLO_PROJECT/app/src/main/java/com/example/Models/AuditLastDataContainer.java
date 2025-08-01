package com.example.Models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class AuditLastDataContainer {
    @SerializedName("recentData")
    private List<AuditLastDatasListResponse> recentData;

    @SerializedName("TotalVerifiedAsseyByAuditcode")
    private int totalVerifiedAssets;

    @SerializedName("ScanningAssets")
    private int scannedAssets;

    public List<AuditLastDatasListResponse> getRecentData() {
        return recentData;
    }

    public int getTotalVerifiedAssets() {
        return totalVerifiedAssets;
    }

    public int getScannedAssets() {
        return scannedAssets;
    }
}


