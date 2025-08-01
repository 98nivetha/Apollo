package com.example.RequestModels;

public class SubAssetCodeListByAssetNameRequestModel {
    private String Assetname;

    public SubAssetCodeListByAssetNameRequestModel(String assetName) {
        this.Assetname = assetName;
    }

    public String getAssetname() {
        return Assetname;
    }

    public String setAssetName() {
        return Assetname;
    }
}
