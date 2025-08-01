package com.example.RequestModels;


public class AssetClasRequestModel {
    private String Assetclasscode;

    public AssetClasRequestModel() {}

    public AssetClasRequestModel(String assetclasscode) {
        this.Assetclasscode = assetclasscode;
    }

    public String getAssetclasscode() {
        return Assetclasscode;
    }

    public void setAssetclasscode(String assetclasscode) {
        this.Assetclasscode = assetclasscode;
    }
}
