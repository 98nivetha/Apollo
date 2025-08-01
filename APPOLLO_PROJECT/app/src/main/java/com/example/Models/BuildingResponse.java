package com.example.Models;
import java.util.List;


public class BuildingResponse {
    private boolean Result;
    private String ErrorCode;
    private String Message;
    private BuildingData Data;

    public BuildingData getData() {
        return Data;
    }

    public static class BuildingData {
        private List<LocationDetails> LocationDetails;

        public List<LocationDetails> getLocationDetails() {
            return LocationDetails;
        }
    }

    public static class LocationDetails {
        private String Locationname;
        private String Locationcode;
        private String Floorname;
        private String Floorcode;

        public String getLocationname() {
            return Locationname;
        }

        public String getLocationfloor() {
            return Floorname;
        }

        public String getFloorCode() {
            return Floorcode;
        }
        public String getLocationCode() {
            return Locationcode;
        }
    }
}


