package com.example.Models;
import java.util.List;
public class LocationResponse {
    private boolean Result;
    private String ErrorCode;
    private String Message;
    private LocationData Data;

    public boolean isResult() {
        return Result;
    }

    public String getErrorCode() {
        return ErrorCode;
    }

    public String getMessage() {
        return Message;
    }

    public LocationData getData() {
        return Data;
    }

    public static class LocationData {
        private List<AssetClassDetails> AssetClassDetails;

        public List<AssetClassDetails> getAssetClassDetails() {
            return AssetClassDetails;
        }

        public static class AssetClassDetails {
            private String AssetClass;
            private  String AssetClassName;
            private String Assetclassdescription;
            public String getAssetClass() { return AssetClass; }

            public String getAssetClassName() { return AssetClassName; }

            public String getAssetclassdescription() { return Assetclassdescription; }
        }
    }
}
