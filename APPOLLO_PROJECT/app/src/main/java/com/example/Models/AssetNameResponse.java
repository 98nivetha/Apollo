package com.example.Models;
import java.util.List;

public class AssetNameResponse {
    private boolean Result;
    private String ErrorCode;
    private String Message;
    private AssetNameResponse.Data Data;

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

    public AssetNameResponse.Data getData() {
        return Data;
    }

    public void setData(AssetNameResponse.Data data) {
        Data = data;
    }

    public static class Data {
        private List<SubassetcodeList> SubassetcodeList;

        public List<SubassetcodeList> getSubassetcodeList() {
            return SubassetcodeList;
        }

        public void setSubassetcodeList(List<SubassetcodeList> subassetcodeList) {
            this.SubassetcodeList = subassetcodeList;
        }
    }

    public static class SubassetcodeList {
        private String Subassetcode;

        public String getSubassetcode() {
            return Subassetcode;
        }

        public void setSubassetcode(String subassetcode) {
            this.Subassetcode = subassetcode;
        }
    }
}
