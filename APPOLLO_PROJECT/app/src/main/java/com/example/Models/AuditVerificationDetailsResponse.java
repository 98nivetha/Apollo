package com.example.Models;
import java.util.List;

public class AuditVerificationDetailsResponse {

    public boolean Result;
    public String ErrorCode;
    public String Message;
    public Data Data;

    public static class Data {
        public V_AuditVerificationDetails V_AuditVerificationDetails;
        public List<D_ItemDetails> D_ItemDetails;
        public List<V_ScanDetails> V_ScanDetails;
        public List<V_Details> V_Details;
        public List<D_MetaDetails> D_MetaDetails;
    }

    public static class V_AuditVerificationDetails {
        public int Sno;
        public int Auditid;
        public String Auditcode;
        public String Auditconductfrom;
        public String Auditconductto;
        public String Duration;
        public String Auditconductby;
        public String Auditincharge;
        public String Auditconductin;
        public String Buildingcode;
        public String Buildingname;
        public String Locationcode;
        public String Locationname;
        public String Remarks;
        public String Status;
        public String Statusname;
        public String Createdby;
        public String Createdon;
        public String Createddate;
        public String Modifiedby;
        public String Modifiedon;
        public String Modifieddate;
        public String Departmentcode;
        public String Departmentname;
        public String MailConfirmationStatus;
        public String Approvalstage;
    }

    public static class D_ItemDetails {
        public int Assetregisterid;
        public String Companycode;
        public String PerAssetNo;
        public String JdrAssetNo;
        public String AssetName;
        public int Quantity;
        public String Serialnumber;
        public String Grnnumber;
        public String Assetclass;
        public String Costcenter;
        public String Building;
        public String Status;
        public String Createdby;
        public String Createdon;
        public String Modifiedby;
        public String Modifiedon;
        public String Ponumber;
        public String Suppliername;
    }

    public static class V_ScanDetails {
    }

    public static class V_Details {
        public int Verificationid;
        public String Verificationcode;
        public String Auditcode;
        public String Perassetno;
        public String Assetsubcode;
        public String SysLocation;
        public String Syslocationname;
        public String ScanLocation;
        public String Scan;
        public String Scanstatus;
        public String PhyStatus;
        public String Physicalstatus;
        public String Feeback;
        public String Createdby;
        public String Createdon;
        public String Modifiedby;
        public String Modifiedon;
        public String Approvalstage;
    }

    public static class D_MetaDetails {
        public int Id;
        public String Metadataid;
        public String Metadatatypedescription;
        public String Metasubcode;
        public String Metadatadescription;
        public String Status;
        public String Createdby;
        public String Createdon;
        public String Modifiedby;
        public String Modifiedon;
        public int Accountingyear;
    }
}
