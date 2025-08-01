package com.example.RequestModels;
import com.google.gson.annotations.SerializedName;

public class BuildingFilterRequest {
    @SerializedName("Buildingcode")
    private String buildingCode;

    public BuildingFilterRequest(String buildingCode) {
        this.buildingCode = buildingCode;
    }

    public String getBuildingCode() {
        return buildingCode;
    }

    public void setBuildingCode(String buildingCode) {
        this.buildingCode = buildingCode;
    }
}

