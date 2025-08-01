package com.example.RequestModels;

public class AssetViewModel {
    private String Subassetcode;
    public AssetViewModel(String subassetcode) {
        this.Subassetcode = subassetcode;
    }
    public String getSubassetcode() {
        return Subassetcode;
    }

    public void setSubassetcode(String subassetcode) {
        this.Subassetcode = subassetcode;
    }
}
