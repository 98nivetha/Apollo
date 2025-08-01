package com.example.Service;
import com.example.Models.AssetClassResponse;
import com.example.Models.AssetNameResponse;
import com.example.Models.AuditLastSavedResponse;
import com.example.Models.AuditResponse;
import com.example.Models.AuditScanResponse;
import com.example.Models.AuditVerificationDetailsResponse;
import com.example.Models.BuildingResponse;
import com.example.Models.FilterResponse;
import com.example.Models.LocationResponse;
import com.example.Models.LoginResponse;
import com.example.Models.MasterDataResponse;
import com.example.RequestModels.AssetClasRequestModel;
import com.example.RequestModels.AssetViewModel;
import com.example.RequestModels.AuditIDRequestModel;
import com.example.RequestModels.AuditLastSavedRequest;
import com.example.RequestModels.AuditRequestModel;
import com.example.RequestModels.AuditScanRequestModel;
import com.example.RequestModels.BuildingFilterRequest;
import com.example.RequestModels.LocationRequestModel;
import com.example.RequestModels.LoginRequest;
import com.example.RequestModels.SubAssetCodeListByAssetNameRequestModel;
import com.example.RequestModels.SubAssetFilterRequest;
import com.example.RequestModels.getMasterDataModel;
import com.google.gson.JsonObject;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface ApiService {
    @POST("ASM_APOLLO_ANDROID_LOGIN")
    Call<LoginResponse> loginUser(@Body LoginRequest loginRequest);

    @POST("ASM_APOLLO_ANDROID_ASSET_VIEW")
    Call<JsonObject> assetView(@Body AssetViewModel assetViewModel);

    @POST("ASM_APOLLO_ANDROID_MASTER_GET_ALL")
    Call<MasterDataResponse> getMasterData(@Body getMasterDataModel requestModel);

    @POST("ASM_APOLLO_ANDROID_FILTER")
    Call<FilterResponse> getAssetDataBySubAsset(@Body SubAssetFilterRequest request);

    @POST("ASM_APOLLO_ANDROID_FILTER")
    Call<BuildingResponse> getLocationAndFloorByBuilding(@Body BuildingFilterRequest request);

    @POST("ASM_APOLLO_ANDROID_FILTER")
    Call<LocationResponse> getAssetDetailsByLocation(@Body LocationRequestModel request);

    @POST("ASM_APOLLO_ANDROID_FILTER")
    Call<AssetNameResponse> getSubAssetCodeByAssetName(@Body SubAssetCodeListByAssetNameRequestModel request);

    @POST("ASM_APOLLO_ANDROID_FILTER")
    Call<AuditResponse> getAuditDetailsByProcessstage(@Body AuditRequestModel request);

    @POST("ASM_APOLLO_ASSET_AUDIT_SCANNING_FetchData")
    Call<AuditVerificationDetailsResponse> getAuditDetailsByAuditID(@Body AuditIDRequestModel request);

    @Headers("Content-Type: application/json")
    @POST("ASM_APOLLO_ANDROID_AUDIT_SCAN")
    Call<AuditScanResponse> insertOrUpdateScanedAssetForAudit(
            @Body List<AuditScanRequestModel> requestModelList
    );
    @POST("ASM_APOLLO_ANDROID_FILTER")
    Call<AssetClassResponse> getAssetByAssetClassCode(@Body AssetClasRequestModel request);
    @POST("ASM_APOLLO_ANDROID_AUDIT_GET_LAST_INSERTEDDATAS")
    Call<AuditLastSavedResponse> getLastSavedAuditsByAuditCode (@Body AuditLastSavedRequest request);
}