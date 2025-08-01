package com.example.Models;
import java.util.List;

public class CompanyResponse {
    private boolean Result;
    private String ErrorCode;
    private String Message;

    public CompanyData Data;

    public CompanyData getData() {
        return Data;
    }

    public static class CompanyData {
        public List<BuildingDetails> buildingDetails;
    }

    public static class BuildingDetails {
        private String Buildingname;
        private String Buildingcode;

        public String getBuildingname() {
            return Buildingname;
        }

        public String getBuildingcode() {
            return Buildingcode;
        }
    }
}


