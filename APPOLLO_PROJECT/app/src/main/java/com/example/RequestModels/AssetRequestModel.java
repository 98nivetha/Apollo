package com.example.RequestModels;

public class AssetRequestModel {
    private String Assetclasscode;

    public AssetRequestModel(String assetclasscode) {
        this.Assetclasscode = assetclasscode;
    }

    public String getAssetclasscode() {
        return Assetclasscode;
    }

    public void setAssetclasscode(String assetclasscode) {
        this.Assetclasscode = assetclasscode;
    }
}
