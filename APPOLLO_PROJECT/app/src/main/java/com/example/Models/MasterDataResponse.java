package com.example.Models;

import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;
import java.util.List;

public class MasterDataResponse {
    private boolean Result;
    private String ErrorCode;
    private String Message;
    private Data Data;

    public boolean isResult() {
        return Result;
    }

    public String getErrorCode() {
        return ErrorCode != null ? ErrorCode : "";
    }

    public String getMessage() {
        return Message != null ? Message : "";
    }

    public Data getData() {
        return Data != null ? Data : new Data();
    }

    public class Data {
        @SerializedName("D_CompanyDetails")
        private List<CompanyDetails> D_CompanyDetails;

        @SerializedName("D_BuildingDetails")
        private List<BuildingDetails> D_BuildingDetails;

        @SerializedName("D_LocationAndFloorDetails")
        private List<LocationAndFloorDetails> D_LocationAndFloorDetails;

        @SerializedName("D_AssetClassDetails")
        private List<AssetClassDetails> D_AssetClassDetails;

        @SerializedName("D_SubAssetDetails")
        private List<SubAssetClassDetails> D_SubAssetDetails;

        @SerializedName("D_AssetNameDetails")
        private List<AssetNameDetails> D_AssetNameDetails;

        public List<AssetNameDetails> getD_AssetNameDetails() {
            return D_AssetNameDetails != null ? D_AssetNameDetails : new ArrayList<>();
        }

        public List<CompanyDetails> getD_CompanyDetails() {
            return D_CompanyDetails != null ? D_CompanyDetails : new ArrayList<>();
        }

        public List<BuildingDetails> getD_BuildingDetails() {
            return D_BuildingDetails != null ? D_BuildingDetails : new ArrayList<>();
        }

        public List<LocationAndFloorDetails> getD_LocationAndFloorDetails() {
            return D_LocationAndFloorDetails != null ? D_LocationAndFloorDetails : new ArrayList<>();
        }

        public List<AssetClassDetails> getD_AssetClassDetails() {
            return D_AssetClassDetails != null ? D_AssetClassDetails : new ArrayList<>();
        }

        public List<SubAssetClassDetails> getD_SubAssetDetails() {
            return D_SubAssetDetails != null ? D_SubAssetDetails : new ArrayList<>();
        }
    }

    public class CompanyDetails {
        private String Companyname;
        private String Companycode;

        public String getCompanyname() {
            return Companyname != null ? Companyname : "";
        }

        public String getCompanycode() {
            return Companycode != null ? Companycode : "";
        }
    }

    public class BuildingDetails {
        private String Buildingname;
        private String Companycode;
        private String Buildingcode;

        public String getBuildingname() {
            return Buildingname != null ? Buildingname : "";
        }

        public String getBuildingcode() {
            return Buildingcode != null ? Buildingcode : "";
        }

        public String getCompanycode() {
            return Companycode != null ? Companycode : "";
        }
    }

    public class LocationAndFloorDetails {
        private String Floorname;
        private String Locationname;
        private String Buildingcode;

        public String getFloorname() {
            return Floorname != null ? Floorname : "";
        }

        public String getLocationname() {
            return Locationname != null ? Locationname : "";
        }

        public String getBuildingcode() {
            return Buildingcode != null ? Buildingcode : "";
        }
    }

    public class AssetClassDetails {
        private String Assetclasscode;
        private String Assetclassdescription;

        public String getAssetclasscode() {
            return Assetclasscode != null ? Assetclasscode : "";
        }

        public String getAssetclassdescription() {
            return Assetclassdescription != null ? Assetclassdescription : "";
        }
    }

    public class SubAssetClassDetails {
        private String Subassetcode;
        private String AssetName;

        public String getSubassetcode() {
            return Subassetcode != null ? Subassetcode : "";
        }

        public String getAssetName() {
            return AssetName != null ? AssetName : "";
        }
    }

    public class AssetNameDetails {
        private String AssetName;

        public String getAssetName() {
            return AssetName != null ? AssetName : "";
        }
    }
}
