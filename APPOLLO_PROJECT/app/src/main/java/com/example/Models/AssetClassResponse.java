package com.example.Models;
import com.google.gson.annotations.SerializedName;
import java.util.List;


public class AssetClassResponse {

    @SerializedName("Result")
    private boolean result;

    @SerializedName("Message")
    private String message;

    @SerializedName("Data")
    private Data data;

    public boolean isResult() {
        return result;
    }

    public String getMessage() {
        return message;
    }

    public Data getData() {
        return data;
    }

    public static class Data {
        @SerializedName("AssetDetails")
        private List<AssetDetails> assetDetails;

        public List<AssetDetails> getAssetDetails() {
            return assetDetails;
        }
    }

    public static class AssetDetails {
        @SerializedName("Subassetcode")
        private String subassetcode;

        @SerializedName("AssetName")
        private String assetName;

        @SerializedName("Subassetpartscode")
        private String subassetpartscode;

        public String getSubassetcode() {
            return subassetcode;
        }

        public String getAssetName() {
            return assetName;
        }

        public String getSubassetpartscode() {
            return subassetpartscode;
        }
    }
}

