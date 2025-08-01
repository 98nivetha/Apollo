package com.example.RequestModels;

public class getMasterDataModel {
    private int Assetid;

    public getMasterDataModel(int assetid) {
        this.Assetid = assetid;
    }

    public int getAssetid() {
        return Assetid;
    }

    public void setAssetid(int assetid) {
        this.Assetid = assetid;
    }
}
