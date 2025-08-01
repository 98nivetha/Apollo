package com.example.myapplication;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import android.Manifest;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.example.RequestModels.AssetViewModel;
import com.example.Service.ApiClient;
import com.example.Service.ApiService;
import com.example.Service.ToastUtil;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.io.IOException;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AssetViewActivity extends AppCompatActivity {
    LottieAnimationView lottieLoader;
    private SurfaceView previewView;
    private CameraSource cameraSource;
    private BarcodeDetector barcodeDetector;
    private String lastScannedData = "";
    private static final int REQUEST_CAMERA_PERMISSION = 201;
    TextView assetNoTextView, assetNameTextView, assetClassTextView, costCenterTextView, buildingTextView, locationTextView, assetViewTextView, assetLifeTextView, registeredDateTextView, currentLocationTextView, previousLocationTextView, writeOffByTextView, companyNameTextView;
    private final String ZEBRA_SCAN_ACTION = "com.symbol.datawedge.data";
    private final String ZEBRA_SCAN_EXTRA_KEY = "com.symbol.datawedge.data_string";

    private boolean isZebraDevice = Build.MANUFACTURER.equalsIgnoreCase("Zebra Technologies");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assetview);
        Button assetVButton = findViewById(R.id.assetViewButton);
        previewView = findViewById(R.id.previewView);
        companyNameTextView = findViewById(R.id.compName);
        assetNameTextView = findViewById(R.id.assetName);
        assetNoTextView = findViewById(R.id.assetNo);
        assetClassTextView = findViewById(R.id.assetClass);
        costCenterTextView = findViewById(R.id.costCenter);
        buildingTextView = findViewById(R.id.building);
        locationTextView = findViewById(R.id.location);
        assetViewTextView = findViewById(R.id.assetView);
        assetLifeTextView = findViewById(R.id.assetLife);
        registeredDateTextView = findViewById(R.id.registeredDate);
        currentLocationTextView = findViewById(R.id.currentLocation);
        previousLocationTextView = findViewById(R.id.previousLocation);
        writeOffByTextView = findViewById(R.id.writeOffBy);
        lottieLoader = findViewById(R.id.lottieLoader);

        assetVButton.setOnClickListener(view -> {
            previewView.setVisibility(View.VISIBLE);
            setupScanner();
        });
        createDataWedgeProfile("AssetScannerProfile");
        configureDataWedgeProfile("AssetScannerProfile");
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


    private final BroadcastReceiver zebraReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.d("DWReceiver", "Received intent: " + action);
            if ("com.symbol.datawedge.data".equals(action)) {
                String scannedData = intent.getStringExtra("com.symbol.datawedge.data_string");
                Log.d("DWReceiver", "Scanned Data: " + scannedData);
                if (scannedData != null) {
                    runOnUiThread(() -> fetchAssetDetails(scannedData));
                }
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



    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
        }

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
                // Don't start camera here if permission not granted
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(AssetViewActivity.this,
                            new String[]{Manifest.permission.CAMERA}, 201);
                } else {
                    startCamera();
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

            @Override
            public void receiveDetections(@NonNull Detector.Detections<Barcode> detections) {
                SparseArray<Barcode> barcodes = detections.getDetectedItems();
                if (barcodes.size() > 0) {
                    String scannedValue = barcodes.valueAt(0).displayValue;
                    if (!scannedValue.equals(lastScannedData)) {
                        lastScannedData = scannedValue;
                        runOnUiThread(() -> {
                            cameraSource.stop();
                            previewView.setVisibility(View.GONE);
                            fetchAssetDetails(scannedValue);
                        });
                    }
                }
            }
        });
    }

    private void startCamera() {
        try {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                cameraSource.start(previewView.getHolder());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 201) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startCamera();
            } else {
                Toast.makeText(this, "Camera permission is required", Toast.LENGTH_SHORT).show();
            }
        }
    }



    private void fetchAssetDetails(String assetNo) {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        AssetViewModel assetViewModel = new AssetViewModel(assetNo.trim());
        lottieLoader.setVisibility(View.VISIBLE);
        Gson gson = new Gson();
        String json = gson.toJson(assetViewModel);
        Call<JsonObject> call = apiService.assetView(assetViewModel);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                lottieLoader.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    JsonObject data = response.body().getAsJsonObject("Data");
                    if (data != null && data.has("AssetDetails")) {
                        JsonArray assetDetails = data.getAsJsonArray("AssetDetails");
                        if (assetDetails.size() > 0) {
                            JsonObject firstAsset = assetDetails.get(0).getAsJsonObject();
                            ToastUtil.showGreenToast(AssetViewActivity.this, "Asset Details Retrived Successfully", 3000);
                            companyNameTextView.setText(firstAsset.get("Companyname").isJsonNull() ? "-" : firstAsset.get("Companyname").getAsString());
                            assetNameTextView.setText(firstAsset.get("AssetName").isJsonNull() ? "-" : firstAsset.get("AssetName").getAsString());
                            assetClassTextView.setText(firstAsset.get("Assetclassname").isJsonNull() ? "-" : firstAsset.get("Assetclassname").getAsString());
                            assetNoTextView.setText(firstAsset.get("Perassetno").isJsonNull() ? "-" : firstAsset.get("Perassetno").getAsString());
                            costCenterTextView.setText(firstAsset.get("Costcentername").isJsonNull() ? "-" : firstAsset.get("Costcentername").getAsString());
                            buildingTextView.setText(firstAsset.get("Buildingname").isJsonNull() ? "-" : firstAsset.get("Buildingname").getAsString());
                            locationTextView.setText(firstAsset.get("Registeredlocation").isJsonNull() ? "-" : firstAsset.get("Registeredlocation").getAsString());
                            assetViewTextView.setText(firstAsset.get("Subassetcode").isJsonNull() ? "-" : firstAsset.get("Subassetcode").getAsString());
                            assetLifeTextView.setText(firstAsset.get("Assetlife").isJsonNull() ? "-" : firstAsset.get("Assetlife").getAsString());
                            registeredDateTextView.setText(firstAsset.get("Registerdate").isJsonNull() ? "-" : firstAsset.get("Registerdate").getAsString());
                            currentLocationTextView.setText(firstAsset.get("Changelocation").isJsonNull() ? "-" : firstAsset.get("Changelocation").getAsString());
                            previousLocationTextView.setText(firstAsset.get("Expireddate").isJsonNull() ? "-" : firstAsset.get("Expireddate").getAsString());
                            writeOffByTextView.setText(firstAsset.get("Writeoffby").isJsonNull() ? "-" : firstAsset.get("Writeoffby").getAsString());

                        } else {
                            ToastUtil.showRedToast(AssetViewActivity.this, "No Asset Details", 3000);
                        }
                    } else {
                        ToastUtil.showRedToast(AssetViewActivity.this, "Data not found", 3000);
                    }
                } else {
                    ToastUtil.showRedToast(AssetViewActivity.this, "Please Check the Scanning Code", 3000);
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                ToastUtil.showRedToast(AssetViewActivity.this, "Failure:" + t.getMessage(), 3000);
            }
        });
    }
}

