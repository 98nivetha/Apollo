package com.example.Models;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

public class AssetViewResponse {

    @SerializedName("Result")
    private boolean result;

    @SerializedName("ErrorCode")
    private String errorCode;

    @SerializedName("Message")
    private String message;

    @SerializedName("Data")
    private JsonObject data;

    // Getters
    public boolean isResult() {
        return result;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getMessage() {
        return message;
    }

    public JsonObject getData() {
        return data;
    }

    public JsonArray getAssetDetailsArray() {
        if (data != null && data.has("AssetDetails")) {
            return data.getAsJsonArray("AssetDetails");
        }
        return null;
    }

    public JsonObject getFirstAssetDetail() {
        JsonArray array = getAssetDetailsArray();
        if (array != null && array.size() > 0) {
            return array.get(0).getAsJsonObject();
        }
        return null;
    }
}
