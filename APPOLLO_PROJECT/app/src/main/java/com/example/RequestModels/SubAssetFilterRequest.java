package com.example.RequestModels;
import com.google.gson.annotations.SerializedName;

public class SubAssetFilterRequest {
    @SerializedName("Subassetcode")
    private String subassetcode;

    public SubAssetFilterRequest(String subassetcode) {
        this.subassetcode = subassetcode;
    }

    public String getSubassetcode() {
        return subassetcode;
    }

    public void setSubassetcode(String subassetcode) {
        this.subassetcode = subassetcode;
    }
}
