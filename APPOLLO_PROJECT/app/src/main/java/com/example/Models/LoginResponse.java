package com.example.Models;
import com.google.gson.annotations.SerializedName;

public class LoginResponse {
    public boolean result;
    public Object errorCode;
    public String message;

    @SerializedName("Data")
    public Data data;

    public boolean isResult() {
        return result;
    }

    public Object getErrorCode() {
        return errorCode;
    }

    public String getMessage() {
        return message;
    }

    public Data getData() {
        return data;
    }

    public String getUsername() {
        if (data != null && data.userDetails != null) {
            return data.userDetails.username;
        }
        return null;
    }

    public static class Data {
        @SerializedName("UserDetails")
        public UserDetails userDetails;
    }

    public static class UserDetails {
        @SerializedName("Userid")
        public int userId;

        @SerializedName("Usercode")
        public String usercode;

        @SerializedName("Username")
        public String username;

        @SerializedName("Loginid")
        public String loginid;

        @SerializedName("Password")
        public String password;

        @SerializedName("Departmentcode")
        public String departmentcode;

        @SerializedName("Departmentname")
        public String departmentname;

        @SerializedName("Emailid")
        public String emailid;

        @SerializedName("Mobileno")
        public String mobileno;

        @SerializedName("Gender")
        public String gender;

        @SerializedName("Defaultrole")
        public String defaultrole;

        @SerializedName("Status")
        public String status;

        @SerializedName("Statusname")
        public String statusname;

        @SerializedName("Createdby")
        public String createdby;

        @SerializedName("Createdon")
        public String createdon;

        @SerializedName("Createddate")
        public String createddate;

        @SerializedName("Modifiedby")
        public String modifiedby;

        @SerializedName("Modifiedon")
        public String modifiedon;

        @SerializedName("Modifieddate")
        public String modifieddate;
    }
}
