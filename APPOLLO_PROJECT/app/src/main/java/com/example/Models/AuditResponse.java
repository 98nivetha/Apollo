package com.example.Models;
import java.util.List;

public class AuditResponse {
    private Data Data;

    public Data getData() {
        return Data;
    }

    public static class Data {
        private List<AuditDetails> D_UpcomingDetails;

        public List<AuditDetails> getD_UpcomingDetails() {
            return D_UpcomingDetails;
        }

        private List<AuditDetails> D_InprocessDetails;

        public List<AuditDetails> getD_InprocessDetails() {
            return D_InprocessDetails;
        }

        private List<AuditDetails> D_CompletedDetails;

        public List<AuditDetails> getD_CompletedDetails() {
            return D_CompletedDetails;
        }
    }

    public static class AuditDetails {
        private String Auditid;
        private String Auditcode;
        private String Auditconductfrom;
        private String Auditconductto;
        private String Auditstatus;
        private String Auditcompletion;
        private String Buildingname;
        private String Totalcount;
        private String Incount;

        public String getAuditid() {
            return Auditid;
        }

        public String getAuditcode() {
            return Auditcode;
        }

        public String getAuditconductfrom() {
            return Auditconductfrom;
        }

        public String getAuditconductto() {
            return Auditconductto;
        }

        public String getAuditstatus() {
            return Auditstatus;
        }

        public String getAuditcompletion() {
            return Auditcompletion;
        }

        public String getBuildingname() {
            return Buildingname;
        }

        public String getTotalcount() {
            return Totalcount;
        }

        public String getIncount() {
            return Incount;
        }
    }
}
