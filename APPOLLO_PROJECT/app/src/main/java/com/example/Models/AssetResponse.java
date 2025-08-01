package com.example.Models;
import java.util.List;

public class AssetResponse {
    private boolean Result;
    private String ErrorCode;
    private String Message;
    private Data Data;

    public boolean isResult() {
        return Result;
    }

    public void setResult(boolean result) {
        Result = result;
    }

    public String getErrorCode() {
        return ErrorCode;
    }

    public void setErrorCode(String errorCode) {
        ErrorCode = errorCode;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }

    public Data getData() {
        return Data;
    }

    public void setData(Data data) {
        Data = data;
    }

    public static class Data {
        private List<AssetDetails> AssetDetails;


        public List<AssetDetails> getAssetDetails() {
            return AssetDetails;
        }

        public void setAssetDetails(List<AssetDetails> assetDetails) {
            AssetDetails = assetDetails;
        }
    }

    public static class AssetDetails {
        private String PerAssetNo;
        private String Subassetcode;
        private String AssetName;

        public String getPerAssetNo() {
            return PerAssetNo;
        }

        public void setPerAssetNo(String perAssetNo) {
            PerAssetNo = perAssetNo;
        }

        public String getSubassetcode() {
            return Subassetcode;
        }

        public void setSubassetcode(String subassetcode) {
            Subassetcode = subassetcode;
        }

        public String getAssetName() {
            return AssetName;
        }

        public void setAssetName(String assetName) {
            AssetName = assetName;
        }
    }
}

