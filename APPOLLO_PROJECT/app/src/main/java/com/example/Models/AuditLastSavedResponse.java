package com.example.Models;

import com.google.gson.annotations.SerializedName;

public class AuditLastSavedResponse {

    @SerializedName("Result")
    private boolean result;

    @SerializedName("Message")
    private String message;

    @SerializedName("Data")
    private AuditLastDataContainer data;

    public boolean isResult() {
        return result;
    }

    public String getMessage() {
        return message;
    }

    public AuditLastDataContainer getData() {  // ✅ return updated container
        return data;
    }
}
