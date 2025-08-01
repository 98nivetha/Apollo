package com.example.myapplication;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.airbnb.lottie.LottieAnimationView;
import com.example.Models.AuditLastDataContainer;
import com.example.Models.AuditLastDatasListResponse;
import com.example.Models.AuditLastSavedResponse;
import com.example.Models.AuditScanResponse;
import com.example.Models.AuditVerificationDetailsResponse;
import com.example.RequestModels.AuditIDRequestModel;
import com.example.RequestModels.AuditLastSavedRequest;
import com.example.RequestModels.AuditScanRequestModel;
import com.example.Service.ApiClient;
import com.example.Service.ApiService;
import com.example.Service.RetrofitClient;
import com.example.Service.ToastUtil;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import android.Manifest;

public class AuditScanningDetailActivity extends AppCompatActivity {
    LottieAnimationView lottieLoader;
    private int auditid;
    private String auditCode;
    TextView assetCode;
    Button assetAuditButton;
    Button submitButton;
    TextView jsonvalue;
    private SurfaceView previewView;
    private CameraSource cameraSource;
    private BarcodeDetector barcodeDetector;
    private static final int REQUEST_CAMERA_PERMISSION = 201;
    private List<AuditScanRequestModel> requestList = new ArrayList<>();
    private boolean isDuplicateAsset = false;
    private String lastScannedData = "";
    private static final String TAG = "AssetScanner";

    private final String ZEBRA_SCAN_ACTION = "com.symbol.datawedge.data";
    private final String ZEBRA_SCAN_EXTRA_KEY = "com.symbol.datawedge.data_string";

    private boolean isZebraDevice = Build.MANUFACTURER.equalsIgnoreCase("Zebra Technologies");
    private final Set<String> scannedSet = new HashSet<>();
    private long lastScanTime = 0;
    private static final long SCAN_COOLDOWN_MS = 2000;
    private List<AuditScanResponse.ScanResult> duplicateAssets = new ArrayList<>();
    private final Set<String> submittedAssetCodes = new HashSet<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auditscanningdetail);
        lottieLoader = findViewById(R.id.lottieLoader);
        auditid = getIntent().getIntExtra("audID", 0);
        auditCode = getIntent().getStringExtra("audCode");
        previewView = findViewById(R.id.previewView);
        assetCode = findViewById(R.id.subAssetCode);
        submitButton = findViewById(R.id.submitButton);
        assetAuditButton = findViewById(R.id.assetAuditButton);

        if (auditid != 0) {
            getAuditDetails(auditid);
        } else {
            finish();
        }

        assetAuditButton.setOnClickListener(view -> {
//            if (!isScannerActive) {
//                isScannerActive = true;
//            previewView.setVisibility(View.VISIBLE);
            setupScanner();

//            } else {
//                Toast.makeText(this, "Scanner is already running", Toast.LENGTH_SHORT).show();
//            }
        });
        submitButton.setOnClickListener(v -> {
            if (!requestList.isEmpty()) {
                List<AuditScanRequestModel> toSubmit = new ArrayList<>();

                for (AuditScanRequestModel model : requestList) {
                    if (!submittedAssetCodes.contains(model.getAssetsubcode())) {
                        toSubmit.add(model);
                        submittedAssetCodes.add(model.getAssetsubcode()); // mark as submitted
                    }
                }

                if (!toSubmit.isEmpty()) {
                    ToastUtil.showBlueToast(AuditScanningDetailActivity.this, "Submitted. Please wait for detailed status.", 2000);
                    previewView.setVisibility(View.GONE);
                    lottieLoader.setVisibility(View.VISIBLE);

                    scannedAudits(toSubmit);

                    new Handler(Looper.getMainLooper()).postDelayed(() -> {
                        getAuditsByAuditCode(auditCode);
                    }, 4000);
                } else {
                    ToastUtil.showRedToast(AuditScanningDetailActivity.this, "Already submitted!", 2000);
                }

            } else {
                ToastUtil.showRedToast(AuditScanningDetailActivity.this, "No barcodes scanned!", 2000);
            }
        });


        createDataWedgeProfile("AssetScannerProfile");
        configureDataWedgeProfile("AssetScannerProfile");
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
        }
    }

    private void getAuditDetails(int auditid) {
        AuditIDRequestModel request = new AuditIDRequestModel(auditid);

        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<AuditVerificationDetailsResponse> call = apiService.getAuditDetailsByAuditID(request);

        call.enqueue(new Callback<AuditVerificationDetailsResponse>() {
            @Override
            public void onResponse(Call<AuditVerificationDetailsResponse> call, Response<AuditVerificationDetailsResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().Data != null) {
                    AuditVerificationDetailsResponse.V_AuditVerificationDetails audit = response.body().Data.V_AuditVerificationDetails;

                    TextView departmentText = findViewById(R.id.departmentName);
                    TextView durationText = findViewById(R.id.duration);
                    TextView buildingNameText = findViewById(R.id.buildingName);
                    TextView locationNameText = findViewById(R.id.Locationname);
                    TextView audiConductbyText = findViewById(R.id.auditConductby);
                    TextView auditInchargeText = findViewById(R.id.auditIncharge);

                    departmentText.setText("Audit Code: " + audit.Auditcode);
                    durationText.setText("Duration: " + audit.Duration);
                    buildingNameText.setText("Building Name: " + audit.Buildingname);
                    locationNameText.setText("Location Name:" + audit.Locationname);
                    audiConductbyText.setText("Audit Conducted By:" + audit.Auditconductby);
                    auditInchargeText.setText("Audit Incharge :" + audit.Auditincharge);

                    auditCode = audit.Auditcode;
                } else {
                    ToastUtil.showRedToast(AuditScanningDetailActivity.this, "No data found", 2000);
                }
            }

            @Override
            public void onFailure(Call<AuditVerificationDetailsResponse> call, Throwable t) {
                ToastUtil.showRedToast(AuditScanningDetailActivity.this, "Error: " + t.getMessage(), 2000);
            }
        });
    }

    private void scannedAudits(List<AuditScanRequestModel> requestList) {
        Log.d("scannedAudits API", "Sending to backend: " + new Gson().toJson(requestList));
        ApiService apiService = RetrofitClient.getInstance().getApi();
        lottieLoader.setVisibility(View.VISIBLE);
        Call<AuditScanResponse> call = apiService.insertOrUpdateScanedAssetForAudit(requestList);
        call.enqueue(new Callback<AuditScanResponse>() {
            @Override
            public void onResponse(Call<AuditScanResponse> call, Response<AuditScanResponse> response) {
                lottieLoader.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    List<AuditScanResponse.ScanResult> resultList = response.body().getData();
                    if (resultList != null && !resultList.isEmpty()) {
                        for (AuditScanResponse.ScanResult item : resultList) {
                            String code = item.getAssetsubcode();
                            String status = item.getStatus();
                            String msg = item.getMessage();

                            if (code != null) {
                                code = code.trim();
                                if (code.matches("^[a-zA-Z0-9\\-]+$")) {
                                } else {
                                }
                            }

                            if (status != null && status.equalsIgnoreCase("Duplicate") && msg.equalsIgnoreCase("Asset already scanned for this audit")) {
                                isDuplicateAsset = true;
                                showAssetFoundDialog();
                            }

                        }
                    } else {
                    }
                } else {
                }
            }

            @Override
            public void onFailure(Call<AuditScanResponse> call, Throwable t) {
            }
        });
    }

    private void showAssetFoundDialog() {
        runOnUiThread(() -> {
            if (!isFinishing()) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Duplicate Found");
                builder.setIcon(android.R.drawable.ic_dialog_alert);
                builder.setMessage("The scanned assets are duplicate");
                builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
                AlertDialog dialog = builder.create();
                dialog.setCancelable(false);
                dialog.setOnShowListener(d -> {
                    Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                    if (positiveButton != null) {
                        int padding = (int) (16 * getResources().getDisplayMetrics().density);
                        positiveButton.setPadding(padding, padding, padding, padding);
                        positiveButton.setTextSize(18);
                    }
                });

                dialog.show();
            }
        });
    }


    private void getAuditsByAuditCode(String auditCode) {
        ApiService apiService = RetrofitClient.getInstance().getApi();
        AuditLastSavedRequest request = new AuditLastSavedRequest(auditCode);
        Call<AuditLastSavedResponse> call = apiService.getLastSavedAuditsByAuditCode(request);
        lottieLoader.setVisibility(View.VISIBLE);
        call.enqueue(new Callback<AuditLastSavedResponse>() {
            @Override
            public void onResponse(Call<AuditLastSavedResponse> call, Response<AuditLastSavedResponse> response) {
                lottieLoader.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    AuditLastDataContainer container = response.body().getData();
                    if (container != null) {
                        List<AuditLastDatasListResponse> resultList = container.getRecentData();
                        int totalAssets = container.getTotalVerifiedAssets();
                        int scannedAssets = container.getScannedAssets();

                        if (resultList != null && !resultList.isEmpty()) {
                            for (AuditLastDatasListResponse item : resultList) {
                                String code = item.getSubAssetCode();
                                String phase = item.getPhase();
                                String createdOn = item.getCreatedOn();
                            }
                            showAssetsDialog(resultList, totalAssets, scannedAssets);
                        } else {
                        }
                    } else {
                    }
                } else {
                }
            }
            @Override
            public void onFailure(Call<AuditLastSavedResponse> call, Throwable t) {
//                Toast.makeText(getApplicationContext(), "Network Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }


    private void setupScanner() {
        barcodeDetector = new BarcodeDetector.Builder(this)
                .setBarcodeFormats(Barcode.ALL_FORMATS)
                .build();

        cameraSource = new CameraSource.Builder(this, barcodeDetector)
                .setRequestedPreviewSize(1024, 768)
                .setAutoFocusEnabled(true)
                .build();

        previewView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(@NonNull SurfaceHolder holder) {
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(AuditScanningDetailActivity.this,
                            new String[]{Manifest.permission.CAMERA}, 201);
                    return;
                }
                try {
                    cameraSource.start(previewView.getHolder());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {
            }

            @Override
            public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
                cameraSource.stop();
            }
        });

        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {
            }

            public void receiveDetections(@NonNull Detector.Detections<Barcode> detections) {
                SparseArray<Barcode> barcodes = detections.getDetectedItems();

                if (barcodes.size() > 0) {
                    String scannedValue = barcodes.valueAt(0).displayValue;

                    runOnUiThread(() -> {
                        long currentTime = System.currentTimeMillis();

                        if (scannedValue == null || scannedSet.contains(scannedValue)) return;

                        if (scannedValue.equalsIgnoreCase(lastScannedData) &&
                                (currentTime - lastScanTime) < SCAN_COOLDOWN_MS) {
                            return;
                        }

                        lastScannedData = scannedValue;
                        lastScanTime = currentTime;
                        scannedSet.add(scannedValue);

                        AuditScanRequestModel model = new AuditScanRequestModel();
                        model.setAssetsubcode(scannedValue);
                        model.setAuditcode(auditCode);
                        requestList.add(model);

                        Log.d("CameraScanner", "Scanned: " + scannedValue + ", requestList size: " + requestList.size());

                        ToastUtil.showBlueToast(AuditScanningDetailActivity.this, "Camera Scanned", 3000);
                    });
                }

            }
        });

    }

    private void createDataWedgeProfile(String profileName) {
        Intent intent = new Intent();
        intent.setAction("com.symbol.datawedge.api.ACTION");
        intent.putExtra("com.symbol.datawedge.api.CREATE_PROFILE", profileName);
        sendBroadcast(intent);

        Log.d("DWProfile", "Sent profile creation broadcast for: " + profileName);
    }


    private void configureDataWedgeProfile(String profileName) {
        Bundle configBundle = new Bundle();
        configBundle.putString("PROFILE_NAME", profileName);
        configBundle.putString("PROFILE_ENABLED", "true");
        configBundle.putString("CONFIG_MODE", "UPDATE");

        // Associate this profile with your app
        Bundle appConfig = new Bundle();
        appConfig.putString("PACKAGE_NAME", getPackageName());
        appConfig.putStringArray("ACTIVITY_LIST", new String[]{"*"});

        configBundle.putParcelableArray("APP_LIST", new Bundle[]{appConfig});

        // Intent plugin
        Bundle intentConfig = new Bundle();
        intentConfig.putString("PLUGIN_NAME", "INTENT");
        intentConfig.putString("RESET_CONFIG", "true");

        Bundle intentParams = new Bundle();
        intentParams.putString("intent_output_enabled", "true");
        intentParams.putString("intent_action", ZEBRA_SCAN_ACTION); // com.symbol.datawedge.data
        intentParams.putString("intent_delivery", "2"); // Broadcast

        intentConfig.putBundle("PARAM_LIST", intentParams);

        // Barcode plugin
        Bundle barcodeConfig = new Bundle();
        barcodeConfig.putString("PLUGIN_NAME", "BARCODE");
        barcodeConfig.putString("RESET_CONFIG", "true");

        // Final bundle to send
        configBundle.putParcelableArray("PLUGIN_CONFIG", new Bundle[]{intentConfig, barcodeConfig});

        Intent setConfigIntent = new Intent();
        setConfigIntent.setAction("com.symbol.datawedge.api.ACTION");
        setConfigIntent.putExtra("com.symbol.datawedge.api.SET_CONFIG", configBundle);

        sendBroadcast(setConfigIntent);
    }

    private BroadcastReceiver zebraReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.d("DWReceiver", "Received intent: " + action);
            if ("com.symbol.datawedge.data".equals(action)) {
                String scannedData = intent.getStringExtra("com.symbol.datawedge.data_string");
                long currentTime = System.currentTimeMillis();
                if (scannedData == null || scannedData.trim().isEmpty()) {
                    return;
                }

                if (scannedData.equalsIgnoreCase(lastScannedData) &&
                        (currentTime - lastScanTime) < SCAN_COOLDOWN_MS) {
                    return;
                }

                lastScannedData = scannedData;
                lastScanTime = currentTime;
                if (!scannedSet.contains(scannedData)) {
                    scannedSet.add(scannedData);
                }

                runOnUiThread(() -> {
                    assetCode.setText(scannedData);
                    AuditScanRequestModel model = new AuditScanRequestModel();
                    model.setAssetsubcode(scannedData);
                    model.setAuditcode(auditCode);
                    requestList.add(model);
                    ToastUtil.showBlueToast(AuditScanningDetailActivity.this, "Scanned Successfully", 3000);
                });
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter("com.symbol.datawedge.data");
        registerReceiver(zebraReceiver, filter);
        Log.d("DWReceiver", "Receiver registered");
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(zebraReceiver);
        Log.d("DWReceiver", "Receiver unregistered");
    }

    private void showAssetsDialog(List<AuditLastDatasListResponse> assets, int totalAssets, int scannedAssets) {
        if (assets == null || assets.isEmpty()) {
            return;
        }

        if (cameraSource != null) {
            try {
                cameraSource.stop();
            } catch (Exception e) {
                Log.e("DEBUG_CAMERA", "Error stopping camera: " + e.getMessage());
            }
        }

        StringBuilder message = new StringBuilder();
        message.append("Total Assets: ").append(totalAssets).append("\n");
        message.append("Scanned Assets: ").append(scannedAssets).append("\n\n");
        message.append("Scanned Asset Codes:\n");

        for (AuditLastDatasListResponse item : assets) {
            message.append("• ")
                    .append(item.getSubAssetCode() != null ? item.getSubAssetCode() : "N/A")
                    .append("\n");
        }

        runOnUiThread(() -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(AuditScanningDetailActivity.this);
            builder.setTitle("Audit Summary");
            builder.setIcon(android.R.drawable.checkbox_on_background);
            builder.setMessage(message.toString());

            builder.setPositiveButton("OK", (dialog, which) -> {
                dialog.dismiss();
                previewView.setVisibility(View.GONE);

//                new Handler(Looper.getMainLooper()).postDelayed(() -> {
//                    Intent intent = new Intent(AuditScanningDetailActivity.this, AssetAuditActivity.class);
//                    startActivity(intent);
//                    finish();
//                }, 1000);
            });

            builder.create().show();
        });
    }
}