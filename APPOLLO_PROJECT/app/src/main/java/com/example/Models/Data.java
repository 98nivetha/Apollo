package com.example.Models;
import java.util.List;

public class Data {

    private List<AuditDetail> D_UpcomingDetails;
    private List<AuditDetail> D_InprocessDetails;
    private List<AuditDetail> D_CompletedDetails;

    public List<AuditDetail> getD_UpcomingDetails() {
        return D_UpcomingDetails;
    }

    public void setD_UpcomingDetails(List<AuditDetail> d_UpcomingDetails) {
        D_UpcomingDetails = d_UpcomingDetails;
    }

    public List<AuditDetail> getD_InprocessDetails() {
        return D_InprocessDetails;
    }

    public void setD_InprocessDetails(List<AuditDetail> d_InprocessDetails) {
        D_InprocessDetails = d_InprocessDetails;
    }

    public List<AuditDetail> getD_CompletedDetails() {
        return D_CompletedDetails;
    }

    public void setD_CompletedDetails(List<AuditDetail> d_CompletedDetails) {
        D_CompletedDetails = d_CompletedDetails;
    }
}
