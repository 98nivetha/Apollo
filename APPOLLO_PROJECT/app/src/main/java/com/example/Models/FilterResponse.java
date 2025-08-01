package com.example.Models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class FilterResponse {

    @SerializedName("Result")
    private boolean result;

    @SerializedName("ErrorCode")
    private String errorCode;

    @SerializedName("Message")
    private String message;

    @SerializedName("Data")
    private Data data;

    public boolean isResult() {
        return result;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getMessage() {
        return message;
    }

    public Data getData() {
        return data;
    }

    public static class Data {
        @SerializedName("BuildingDetails")
        private List<BuildingDetails> buildingDetails;

        @SerializedName("LocationDetails")
        private List<LocationDetails> locationDetails;

        @SerializedName("AssetClassDetails")
        private List<AssetClassDetails> assetClassDetails;

        @SerializedName("AssetDetails")
        private List<AssetDetails> assetDetails;

        @SerializedName("SubassetcodeList")
        private List<SubassetDetails> subassetcodeList;

        @SerializedName("D_UpcomingDetails")
        private List<String> dUpcomingDetails;

        @SerializedName("D_InprocessDetails")
        private List<String> dInprocessDetails;

        @SerializedName("D_CompletedDetails")
        private List<String> dCompletedDetails;

        public List<LocationDetails> getLocationDetails() {
            return locationDetails;
        }

        public List<BuildingDetails> getBuildingDetails() {
            return buildingDetails;
        }

        public List<AssetClassDetails> getAssetClassDetails() {
            return assetClassDetails;
        }

        public List<AssetDetails> getAssetDetails() {
            return assetDetails;
        }

        public List<SubassetDetails> getSubassetcodeList() {
            return subassetcodeList; // Return the list of subasset code items
        }
    }

    public static class SubassetDetails {
        @SerializedName("Companyname")
        private String companyName;

        @SerializedName("Companycode")
        private String companyCode;

        @SerializedName("Buildingcode")
        private String buildingCode;

        @SerializedName("Buildingname")
        private String buildingName;

        @SerializedName("Floorcode")
        private String floorCode;

        @SerializedName("Floorname")
        private String floorName;

        @SerializedName("Locationname")
        private String locationName;

        @SerializedName("Locationcode")
        private String locationCode;

        @SerializedName("Subassetcode")
        private String subassetCode;

        @SerializedName("Subassetpartscode")
        private String subassetPartsCode;

        @SerializedName("AssetName")
        private String assetName;

        @SerializedName("Assetclass")
        private String assetClass;

        @SerializedName("Assetclassdescription")
        private String assetClassDescription;

        public String getCompanyName() {
            return companyName;
        }

        public String getBuildingName() {
            return buildingName;
        }

        public String getFloorName() {
            return floorName;
        }

        public String getLocationName() {
            return locationName;
        }

        public String getSubassetCode() {
            return subassetCode;
        }

        public String getAssetName() {
            return assetName;
        }

        public String getAssetClass() {
            return assetClass;
        }

        public String getAssetClassDescription() {
            return assetClassDescription;
        }
    }

    public static class BuildingDetails {
        @SerializedName("Buildingcode")
        private String buildingCode;

        @SerializedName("Buildingname")
        private String buildingName;

        public String getBuildingCode() {
            return buildingCode;
        }

        public String getBuildingName() {
            return buildingName;
        }

    }

    public static class LocationDetails {

        @SerializedName("Locationcode")
        private String locationCode;

        @SerializedName("Locationname")
        private String locationName;

        @SerializedName("Locationfloor")
        private String locationFloor;

        @SerializedName("Buildingcode")
        private String buildingCode;

        @SerializedName("Buildingname")
        private String buildingName;

        @SerializedName("Floorcode")
        private String floorCode;

        @SerializedName("Floorname")
        private String floorName;

        @SerializedName("Status")
        private String status;

        @SerializedName("Statusname")
        private String statusName;

        public String getLocationName() {
            return locationName;
        }

        public String getBuildingCode() {
            return buildingCode;
        }

        public String getLocationFloor() {
            return locationFloor;
        }

        public String getBuildingName() {
            return buildingName;
        }

        public String getFloorName() {
            return floorName;
        }
    }

    public static class AssetClassDetails {
        @SerializedName("Assetclasscode")
        private String assetClassCode;

        @SerializedName("Assetclassname")
        private String assetClassName;

        public String getAssetClassCode() {
            return assetClassCode;
        }

        public String getAssetClassName() {
            return assetClassName;
        }
    }

    public static class AssetDetails {
        @SerializedName("Assetcode")
        private String assetCode;

        @SerializedName("Assetname")
        private String assetName;

        @SerializedName("Assetdescription")
        private String assetDescription;

        @SerializedName("Assetstatus")
        private String assetStatus;

        public String getAssetCode() {
            return assetCode;
        }

        public String getAssetName() {
            return assetName;
        }

        public String getAssetDescription() {
            return assetDescription;
        }

        public String getAssetStatus() {
            return assetStatus;
        }
    }
}
