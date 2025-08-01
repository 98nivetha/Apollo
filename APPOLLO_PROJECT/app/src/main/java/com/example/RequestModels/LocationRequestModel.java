package com.example.RequestModels;

public class LocationRequestModel {
    private String Locationcode;

    public LocationRequestModel(String locationcode) {
        this.Locationcode = locationcode;
    }

    public String getLocationcode() {
        return Locationcode;
    }

    public void setLocationcode(String locationcode) {
        this.Locationcode = locationcode;
    }
}
