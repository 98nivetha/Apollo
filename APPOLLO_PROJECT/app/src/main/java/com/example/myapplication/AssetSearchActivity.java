package com.example.myapplication;
import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import com.airbnb.lottie.LottieAnimationView;
import com.example.Models.AssetClassResponse;
import com.example.Models.AssetNameResponse;
import com.example.Models.BuildingResponse;
import com.example.Models.FilterResponse;
import com.example.Models.LocationResponse;
import com.example.Models.MasterDataResponse;
import com.example.RequestModels.AssetClasRequestModel;
import com.example.RequestModels.AssetViewModel;
import com.example.RequestModels.BuildingFilterRequest;
import com.example.RequestModels.LocationRequestModel;
import com.example.RequestModels.SubAssetCodeListByAssetNameRequestModel;
import com.example.RequestModels.SubAssetFilterRequest;
import com.example.RequestModels.getMasterDataModel;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AssetSearchActivity extends AppCompatActivity {
    Spinner companyDropdown, buildingDropdown, floorDropdown, locationDropdown,
            assetClassDropdown, assetDescriptionDropdown;
    Button clrButton, scanButton;
    private SurfaceView previewView;
    private CameraSource cameraSource;
    private BarcodeDetector barcodeDetector;
    private String lastScannedData = "";
    private String selectedSubAssetCode = null;
    private String selectedAssetClassCode = null;
    private List<LocationResponse.LocationData.AssetClassDetails> lastFetchedAssetClassDetails = new ArrayList<>();
    private static final int REQUEST_CAMERA_PERMISSION = 201;
    private boolean isSearchEnabled = false;
    List<BuildingResponse.LocationDetails> allLocationList = new ArrayList<>();
    private Map<String, String> floorNameCodeMap = new HashMap<>();
    private Map<String, String> locationNameCodeMap = new HashMap<>();
    private boolean scanLocked = false;

    ImageView searchIcon;
    private final String ZEBRA_SCAN_ACTION = "com.symbol.datawedge.data";
    private final String ZEBRA_SCAN_EXTRA_KEY = "com.symbol.datawedge.data_string";
    AutoCompleteTextView subAssetDrop;
    private boolean isZebraDevice = Build.MANUFACTURER.equalsIgnoreCase("Zebra Technologies");
    List<String> allSubAssets = new ArrayList<>();
    LottieAnimationView lottieLoader;
    private boolean isClearing = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assetsearch);
        companyDropdown = findViewById(R.id.company);
        buildingDropdown = findViewById(R.id.buildingName);
        floorDropdown = findViewById(R.id.floor);
        locationDropdown = findViewById(R.id.location);
        assetClassDropdown = findViewById(R.id.assetClass);
        assetDescriptionDropdown = findViewById(R.id.asset);
        subAssetDrop = findViewById(R.id.subAsset);
        lottieLoader = findViewById(R.id.lottieLoader);
        getMasterData();
        scanButton = findViewById(R.id.assetSearchButton);
        clrButton = findViewById(R.id.clearButton);
        previewView = findViewById(R.id.previewView);
        searchIcon = findViewById(R.id.subAssetSearchIcon);
        clrButton.setOnClickListener(v -> {
            isClearing = true;
            String subAssetValue = subAssetDrop.getText().toString().trim();
            boolean allDropdownsFilled =
                    companyDropdown.getSelectedItem().equals("Select Company") &&
                            buildingDropdown.getSelectedItem().equals("Select Building") &&
                            floorDropdown.getSelectedItem().equals("Select Floor") &&
                            locationDropdown.getSelectedItem().equals("Select Location") &&
                            assetClassDropdown.getSelectedItem().equals("Select AssetClass") &&
                            assetDescriptionDropdown.getSelectedItem().equals("Select Asset") &&
                            (subAssetValue.isEmpty() || subAssetValue.equals("Select Sub Asset") || subAssetValue.equals("Select Sub Asset"));

            if (allDropdownsFilled) {
                ToastUtil.showBlueToast(AssetSearchActivity.this, "Nothing to Clear", 3000);
            } else {
                updateSearchIconVisibility();
                searchIcon.setVisibility(View.VISIBLE);
                selectedSubAssetCode = null; // Clear the selection flag
                subAssetDrop.setText(""); // Clear the field
                previewView.setVisibility(View.GONE);
                getMasterData();
                lastScannedData = "";
            }
            new Handler().postDelayed(() -> isClearing = false, 800); // adjust delay as needed

        });

        searchIcon.setOnClickListener(v -> {
            isSearchEnabled = true;
            if (selectedSubAssetCode != null && isSearchEnabled) {
                fetchDetailsForSubAsset(selectedSubAssetCode);
            } else {
                ToastUtil.showBlueToast(AssetSearchActivity.this, "Please select a valid sub-asset code first", 3000);
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


    private void updateSearchIconVisibility() {
        boolean allDropdownsFilled = companyDropdown.getSelectedItem().equals("Select Company") &&
                buildingDropdown.getSelectedItem().equals("Select Building") &&
                floorDropdown.getSelectedItem().equals("Select Floor") &&
                locationDropdown.getSelectedItem().equals("Select Location") &&
                assetClassDropdown.getSelectedItem().equals("Select AssetClass") &&
                assetDescriptionDropdown.getSelectedItem().equals("Select Asset");

        if (allDropdownsFilled) {
            searchIcon.setVisibility(View.VISIBLE);
        } else {
            searchIcon.setVisibility(View.GONE);
        }
    }


    private void setupSpinner(Spinner spinner, List<String> data) {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                AssetSearchActivity.this,
                R.layout.custom_spinner_item,
                data
        ) {
            @Override
            public boolean isEnabled(int position) {
                String item = getItem(position);
                return item != null &&
                        !item.equals("-- Select --") &&
                        !item.equals("-- NO VALUE (null) --");
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                TextView textView = (TextView) LayoutInflater.from(getContext())
                        .inflate(R.layout.custom_spinner_item, parent, false);
                String item = getItem(position);
                textView.setText(item != null ? item : "-- Select --");
                boolean isEnabled = isEnabled(position);
                textView.setTextColor(isEnabled ? Color.BLACK : Color.GRAY);

                return textView;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                TextView textView = (TextView) LayoutInflater.from(getContext())
                        .inflate(R.layout.custom_spinner_item, parent, false);
                String item = getItem(position);
                textView.setText(item != null ? item : "-- NO VALUE (null) --");
                return textView;
            }
        };

        adapter.setDropDownViewResource(R.layout.custom_spinner_item);
        spinner.setAdapter(adapter);
    }


    private void setupDropdownArrow(final Spinner spinner, final ImageView arrowIcon) {
        arrowIcon.setImageResource(R.drawable.ic_arrow_down);

        spinner.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                // Show arrow up when dropdown opens
                arrowIcon.setImageResource(R.drawable.ic_arrow_up);

                // Schedule arrow down after estimated dropdown close (approx. 500ms)
                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    arrowIcon.setImageResource(R.drawable.ic_arrow_down);
                }, 800); // delay should match dropdown closing
            }
            return false;
        });
    }


    private void fetchDetailsForSubAsset(String subAssetCode) {
        SubAssetFilterRequest request = new SubAssetFilterRequest(subAssetCode);
        lottieLoader.setVisibility(View.VISIBLE);
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        apiService.getAssetDataBySubAsset(request).enqueue(new Callback<FilterResponse>() {
            @Override
            public void onResponse(Call<FilterResponse> call, Response<FilterResponse> response) {
                lottieLoader.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    List<FilterResponse.SubassetDetails> subassetList = response.body().getData().getSubassetcodeList();
                    if (!subassetList.isEmpty() && isSearchEnabled) {
                        FilterResponse.SubassetDetails item = subassetList.get(0);
                        setupSpinner(companyDropdown, List.of(item.getCompanyName()));
                        setupSpinner(buildingDropdown, List.of(item.getBuildingName()));
                        setupSpinner(floorDropdown, List.of(item.getFloorName()));
                        setupSpinner(locationDropdown, List.of(item.getLocationName()));
                        setupSpinner(assetClassDropdown, List.of(item.getAssetClassDescription()));
                        setupSpinner(assetDescriptionDropdown, List.of(item.getAssetName()));
                        ToastUtil.showGreenToast(AssetSearchActivity.this, "Details loaded for selected SubAsset", 3000);
                    } else {
                        ToastUtil.showRedToast(AssetSearchActivity.this, "No matching data found!", 3000);
                    }
                } else {
                    ToastUtil.showRedToast(AssetSearchActivity.this, "API Error: " + response.message(), 3000);
                }
            }

            @Override
            public void onFailure(Call<FilterResponse> call, Throwable t) {
                ToastUtil.showRedToast(AssetSearchActivity.this, "Failed to fetch subasset details: " + t.getMessage(), 3000);
            }
        });
    }

    private void getMasterData() {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        getMasterDataModel requestModel = new getMasterDataModel(0);
        lottieLoader.setVisibility(View.VISIBLE);
        Call<MasterDataResponse> call = apiService.getMasterData(requestModel);
        call.enqueue(new Callback<MasterDataResponse>() {
            @Override
            public void onResponse(Call<MasterDataResponse> call, Response<MasterDataResponse> response) {
                lottieLoader.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    MasterDataResponse.Data data = response.body().getData();
                    List<String> companyList = new ArrayList<>();
                    companyList.add("Select Company");
                    for (MasterDataResponse.CompanyDetails company : data.getD_CompanyDetails()) {
                        companyList.add(company.getCompanyname());
                    }
                    setupSpinner(companyDropdown, companyList);
                    setupDropdownArrow(companyDropdown, findViewById(R.id.dropdown_arrow));

                    companyDropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            if (position > 0) {
                                Object selectedItem = parent.getItemAtPosition(position);
                                if (selectedItem != null && response != null && response.body() != null && response.body().getData() != null) {
                                    String selectedCompanyName = selectedItem.toString();
                                    MasterDataResponse.Data data = response.body().getData();

                                    List<MasterDataResponse.CompanyDetails> companyList = data.getD_CompanyDetails();
                                    String selectedCompanyCode = null;

                                    if (companyList != null) {
                                        for (MasterDataResponse.CompanyDetails company : companyList) {
                                            if (company != null && selectedCompanyName.equals(company.getCompanyname())) {
                                                selectedCompanyCode = company.getCompanycode();
                                                break;
                                            }
                                        }
                                    }

                                    if (selectedCompanyCode != null) {
                                        List<MasterDataResponse.BuildingDetails> buildingList = data.getD_BuildingDetails();
                                        List<String> filteredBuildingNames = new ArrayList<>();
                                        Map<String, String> buildingNameToCodeMap = new HashMap<>();
                                        filteredBuildingNames.add("Select Building");

                                        if (buildingList != null) {
                                            for (MasterDataResponse.BuildingDetails building : buildingList) {
                                                if (building != null &&
                                                        building.getCompanycode() != null &&
                                                        building.getCompanycode().equals(selectedCompanyCode) &&
                                                        building.getBuildingname() != null) {

                                                    filteredBuildingNames.add(building.getBuildingname());
                                                    buildingNameToCodeMap.put(building.getBuildingname(), building.getBuildingcode());
                                                }
                                            }
                                        }

                                        setupSpinner(buildingDropdown, filteredBuildingNames);
                                    }
                                }
                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {
                        }
                    });

                    List<String> buildingList = new ArrayList<>();
                    buildingList.add("Select Building");
                    if (data.getD_BuildingDetails() != null) {
                        for (MasterDataResponse.BuildingDetails building : data.getD_BuildingDetails()) {
                            buildingList.add(building.getBuildingname());
                        }
                    } else {
                        ToastUtil.showRedToast(AssetSearchActivity.this, "No building data available", 3000);
                    }

                    setupSpinner(buildingDropdown, buildingList);
                    setupDropdownArrow(buildingDropdown, findViewById(R.id.dropdown_arrow1));
//                    buildingDropdown.setOnTouchListener((v, event) -> {
//                        if (isClearing) return false;
//                        if (event.getAction() == MotionEvent.ACTION_DOWN) {
//                            if (companyDropdown.getSelectedItemPosition() <= 0) {
//                                ToastUtil.showRedToast(AssetSearchActivity.this, "Please Select Company", 3000);
//                                return true;
//                            }
//
//                            buildingSpinnerTouched = true;
//                        }
//                        return false;
//                    });


                    buildingDropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                            if (isClearing) return;
//                            if (buildingSpinnerTouched) {
//                                if (companyDropdown.getSelectedItemPosition() <= 0) {
//                                    ToastUtil.showRedToast(AssetSearchActivity.this, "Please Select Company ", 3000);
//                                    buildingDropdown.setSelection(0);
//                                    return;
//                                }
//                            }

                            if (position > 0) {
                                Object selectedItem = parent.getItemAtPosition(position);
                                if (selectedItem != null) {
                                    String selectedBuildingName = selectedItem.toString();

                                    if (response != null && response.body() != null && response.body().getData() != null) {
                                        MasterDataResponse.Data data = response.body().getData();

                                        List<MasterDataResponse.BuildingDetails> buildingList = data.getD_BuildingDetails();
                                        String selectedBuildingCode = null;

                                        if (buildingList != null) {
                                            for (MasterDataResponse.BuildingDetails building : buildingList) {
                                                if (building != null && selectedBuildingName.equals(building.getBuildingname())) {
                                                    selectedBuildingCode = building.getBuildingcode();
                                                    break;
                                                }
                                            }
                                        }

                                        if (selectedBuildingCode != null) {
                                            fetchLocationAndFloorByBuilding(selectedBuildingCode);
                                        }
                                    }
                                }
                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {
                        }
                    });


                    List<String> floorList = new ArrayList<>();

                    floorList.add("Select Floor");
                    for (MasterDataResponse.LocationAndFloorDetails floor : data.getD_LocationAndFloorDetails()) {
                        floorList.add(floor.getFloorname());
                    }
                    setupSpinner(floorDropdown, floorList);
                    setupDropdownArrow(floorDropdown, findViewById(R.id.dropdown_arrow2));
//                    floorDropdown.setOnTouchListener((v, event) -> {
//                        if (isClearing) return false;
//                        if (event.getAction() == MotionEvent.ACTION_DOWN) {
//                            if (buildingDropdown.getSelectedItemPosition() <= 0) {
//                                ToastUtil.showRedToast(AssetSearchActivity.this, "Please Select Building", 3000);
//                                return true;
//                            }
//
//                            floorSpinnerTouched = true;
//                        }
//                        return false;
//                    });
                    floorDropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                            if (isClearing) return;
//                            if (floorSpinnerTouched) {
//                                if (floorDropdown.getSelectedItemPosition() <= 0) {
//                                    ToastUtil.showRedToast(AssetSearchActivity.this, "Please Select Building ", 3000);
//                                    floorDropdown.setSelection(0);
//                                    return;
//                                }
//                            }
                            if (position > 0) {
                                Object selectedItem = parent.getItemAtPosition(position);
                                if (selectedItem != null && floorNameCodeMap != null) {
                                    String selectedFloorName = selectedItem.toString();
                                    String selectedFloorCode = floorNameCodeMap.get(selectedFloorName);

                                    if (selectedFloorCode != null && allLocationList != null) {
                                        List<String> filteredLocationNames = new ArrayList<>();
                                        filteredLocationNames.add("Select Location");

                                        for (BuildingResponse.LocationDetails detail : allLocationList) {
                                            if (detail != null && selectedFloorCode.equals(detail.getFloorCode())) {
                                                String locationName = detail.getLocationname();
                                                if (locationName != null && !filteredLocationNames.contains(locationName)) {
                                                    filteredLocationNames.add(locationName);
                                                }
                                            }
                                        }
                                        setupSpinner(locationDropdown, filteredLocationNames);
                                    }
                                }
                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {
                        }
                    });


                    List<String> locationList = new ArrayList<>();
                    locationList.add("Select Location");
                    for (MasterDataResponse.LocationAndFloorDetails location : data.getD_LocationAndFloorDetails()) {
                        locationList.add(location.getLocationname());
                    }
                    setupSpinner(locationDropdown, locationList);
                    setupDropdownArrow(locationDropdown, findViewById(R.id.dropdown_arrow3));
//                    locationDropdown.setOnTouchListener((v, event) -> {
//                        if (event.getAction() == MotionEvent.ACTION_DOWN) {
//                            if (floorDropdown.getSelectedItemPosition() <= 0) {
//                                ToastUtil.showRedToast(AssetSearchActivity.this, "Please Select Floor", 3000);
//                                return true;
//                            }
//
//                            locationSpinnerTouched = true;
//                        }
//                        return false;
//                    });
                    locationDropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                            if (isLocationSpinnerInitialized) {
//                                if (floorSpinnerTouched) {
//                                    if (floorDropdown.getSelectedItemPosition() <= 0) {
//                                        ToastUtil.showRedToast(AssetSearchActivity.this, "Please Select Floor ", 3000);
//                                        floorDropdown.setSelection(0);
//                                        return;
//                                    }
//                                }
                                if (position > 0) {
                                    Object selectedItem = parent.getItemAtPosition(position);
                                    if (selectedItem != null) {
                                        String selectedLocationName = selectedItem.toString().trim();
                                        if (selectedLocationName != null && locationNameCodeMap != null) {
                                            String selectedLocationCode = locationNameCodeMap.get(selectedLocationName);
                                            if (selectedLocationCode != null) {
                                                fetchAssetClassByLocationCode(selectedLocationCode);
                                            } else {
                                                ToastUtil.showRedToast(AssetSearchActivity.this, "Invalid location selected", 3000);
                                            }
                                        }
                                    } else {
                                        ToastUtil.showRedToast(AssetSearchActivity.this, "No location selected", 3000);
                                    }
                                }
                            }
//                            else {
//                                isLocationSpinnerInitialized = true;
//                            }
//                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {
                        }
                    });


                    List<String> assetClassList = new ArrayList<>();
                    assetClassList.add("Select AssetClass");
                    for (MasterDataResponse.AssetClassDetails asset : data.getD_AssetClassDetails()) {
                        String desc = asset.getAssetclassdescription();
                        if (desc != null && !desc.trim().isEmpty()) {
                            assetClassList.add(desc);
                        }
                    }

                    setupSpinner(assetClassDropdown, assetClassList);
                    setupDropdownArrow(assetClassDropdown, findViewById(R.id.dropdown_arrow4));
//                    assetClassDropdown.setOnTouchListener((v, event) -> {
//                        if (event.getAction() == MotionEvent.ACTION_DOWN) {
//                            if (locationDropdown.getSelectedItemPosition() <= 0) {
//                                ToastUtil.showRedToast(AssetSearchActivity.this, "Please Select Location", 3000);
//                                return true;
//                            }
//
//                            assetClassSpinnerTouched = true;
//                        }
//                        return false;
//                    });
                    assetClassDropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                            if (floorSpinnerTouched) {
//                                if (locationDropdown.getSelectedItemPosition() <= 0) {
//                                    ToastUtil.showRedToast(AssetSearchActivity.this, "Please Select Location ", 3000);
//                                    locationDropdown.setSelection(0);
//                                    return;
//                                }
//                            }
                            if (position > 0) {
                                String selectedAssetClassName = parent.getItemAtPosition(position).toString().trim();

                                for (LocationResponse.LocationData.AssetClassDetails item : lastFetchedAssetClassDetails) {
                                    if (selectedAssetClassName != null && item.getAssetClassName() != null &&
                                            item.getAssetClassName().equalsIgnoreCase(selectedAssetClassName)) {

                                        selectedAssetClassCode = item.getAssetClass();
                                        fetchAssetNameByAssetClass(selectedAssetClassCode);
                                        break;
                                    }
                                }
                            } else {
                                selectedAssetClassCode = null;
                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {
                        }
                    });


                    List<String> assetDescriptionList = new ArrayList<>();
                    assetDescriptionList.add("Select Asset");
                    for (MasterDataResponse.AssetNameDetails asset : data.getD_AssetNameDetails()) {
                        assetDescriptionList.add(asset.getAssetName());
                    }
                    setupSpinner(assetDescriptionDropdown, assetDescriptionList);
                    setupDropdownArrow(assetDescriptionDropdown, findViewById(R.id.dropdown_arrow5));

                    assetDescriptionDropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            if (position > 0) {
                                Object selectedItem = parent.getItemAtPosition(position);
                                if (selectedItem != null) {
                                    updateSearchIconVisibility();
                                    String selectedAssetName = selectedItem.toString().trim();
                                    fetchSACodeByAssetName(selectedAssetName);
                                } else {
                                }
                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {
                        }
                    });


                    List<String> subAssetList = new ArrayList<>();
                    subAssetList.add("Select Sub Asset");
                    for (MasterDataResponse.SubAssetClassDetails subAsset : data.getD_SubAssetDetails()) {
                        subAssetList.add(subAsset.getSubassetcode());
                    }

                    SubAssetFilterAdapter subAssetAdapter = new SubAssetFilterAdapter(
                            AssetSearchActivity.this,
                            android.R.layout.simple_dropdown_item_1line,
                            subAssetList
                    );
                    subAssetDrop.setAdapter(subAssetAdapter);
                    subAssetDrop.setOnClickListener(v -> subAssetDrop.showDropDown());
                    subAssetAdapter.setMatchMode(SubAssetFilterAdapter.MatchMode.CONTAINS);

                    if (selectedSubAssetCode != null) {
                        subAssetDrop.setText(selectedSubAssetCode, false);
                    }

                    subAssetDrop.setOnItemClickListener((parent, view, position, id) -> {
                        Object selectedItem = parent.getItemAtPosition(position);

                        if (selectedItem != null) {
                            String currentSelected = selectedItem.toString();
                            selectedSubAssetCode = currentSelected;

                            boolean allDropdownsFilled =
                                    !companyDropdown.getSelectedItem().toString().equals("Select Company") &&
                                            !buildingDropdown.getSelectedItem().toString().equals("Select Building") &&
                                            !floorDropdown.getSelectedItem().toString().equals("Select Floor") &&
                                            !locationDropdown.getSelectedItem().toString().equals("Select Location") &&
                                            !assetClassDropdown.getSelectedItem().toString().equals("Select AssetClass") &&
                                            !assetDescriptionDropdown.getSelectedItem().toString().equals("Select Asset");

                            if (!allDropdownsFilled && !isSearchEnabled) {
                                getMasterData();
                            }

                        } else {
                            selectedSubAssetCode = null;
                            isSearchEnabled = false;
                        }
                    });


                    scanButton.setOnClickListener(view1 -> {
                        String company = companyDropdown.getSelectedItem().toString();
                        String building = buildingDropdown.getSelectedItem().toString();
                        String floor = floorDropdown.getSelectedItem().toString();
                        String location = locationDropdown.getSelectedItem().toString();
                        String assetClassCode = assetClassDropdown.getSelectedItem().toString();
                        String asset = assetDescriptionDropdown.getSelectedItem().toString();
                        String subAsset = subAssetDrop.getText().toString();

                        if (company.equals("Select Company") || building.equals("Select Building") ||
                                floor.equals("Select Floor") || location.equals("Select Location") ||
                                assetClassCode.equals("Select AssetClass") || asset.equals("Select Asset") || asset.equals("Select Sub Asset")) {
                            ToastUtil.showBlueToast(AssetSearchActivity.this, "Please fill all dropdowns", 3000);
                            return;
                        }

                        if (selectedSubAssetCode == null || selectedSubAssetCode.isEmpty()) {
                            ToastUtil.showBlueToast(AssetSearchActivity.this, "Please select a SubAsset first", 3000);
                            return;
                        }
                      previewView.setVisibility(View.VISIBLE);
                        setupScanner();

                    });

                } else {
                    ToastUtil.showRedToast(AssetSearchActivity.this, "Empty response!", 3000);
                }
            }

            @Override
            public void onFailure(Call<MasterDataResponse> call, Throwable t) {
//                progressBar.setVisibility(View.GONE);
                ToastUtil.showRedToast(AssetSearchActivity.this, "API Failed:" + t.getMessage(), 3000);
            }

            private void fetchLocationAndFloorByBuilding(String buildingCode) {
                BuildingFilterRequest request = new BuildingFilterRequest(buildingCode);

                apiService.getLocationAndFloorByBuilding(request).enqueue(new Callback<BuildingResponse>() {
                    @Override
                    public void onResponse(Call<BuildingResponse> call, Response<BuildingResponse> response) {
                        allLocationList.clear();

                        if (response.isSuccessful() && response.body() != null) {
                            List<BuildingResponse.LocationDetails> locationDetails = response.body().getData().getLocationDetails();
                            allLocationList.addAll(locationDetails);

                            List<String> locationNames = new ArrayList<>();
                            List<String> floorNames = new ArrayList<>();

                            floorNameCodeMap.clear();

                            locationNames.add("Select Location");
                            floorNames.add("Select Floor");

                            Set<String> addedFloorNames = new HashSet<>();


                            for (BuildingResponse.LocationDetails detail : locationDetails) {
                                String locationName = detail.getLocationname();
                                String locationCode = detail.getLocationCode();
                                if (locationName != null && !locationNames.contains(locationName)) {
                                    locationNames.add(locationName);
                                    locationNameCodeMap.put(locationName.trim(), locationCode);
                                }

                                String floorName = detail.getLocationfloor();
                                String floorCode = detail.getFloorCode();

                                if (floorName != null && !addedFloorNames.contains(floorName)) {
                                    floorNames.add(floorName);
                                    floorNameCodeMap.put(floorName, floorCode);
                                    addedFloorNames.add(floorName);
                                }
                            }
                            setupSpinner(locationDropdown, locationNames);
                            setupSpinner(floorDropdown, floorNames);
                        } else {
                            ToastUtil.showRedToast(AssetSearchActivity.this, "No location/floor found!", 3000);
                        }
                    }

                    @Override
                    public void onFailure(Call<BuildingResponse> call, Throwable t) {
                        ToastUtil.showRedToast(AssetSearchActivity.this, "Failed" + t.getMessage(), 3000);
                    }
                });
            }

            private void fetchAssetClassByLocationCode(String locationCode) {
                LocationRequestModel request = new LocationRequestModel(locationCode);
                Gson gson = new Gson();
                String jsonRequest = gson.toJson(request);
                apiService.getAssetDetailsByLocation(request).enqueue(new Callback<LocationResponse>() {
                    @Override
                    public void onResponse(Call<LocationResponse> call, Response<LocationResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            List<String> assetClassList = new ArrayList<>();
                            assetClassList.add("Select AssetClass");

                            List<LocationResponse.LocationData.AssetClassDetails> assetClassDetails = response.body().getData().getAssetClassDetails();
                            lastFetchedAssetClassDetails = assetClassDetails;

                            for (LocationResponse.LocationData.AssetClassDetails assetClass : assetClassDetails) {
                                String assetClassName = assetClass.getAssetClassName();
                                assetClassList.add(assetClassName);
                            }
                            setupSpinner(assetClassDropdown, assetClassList);
                        } else {
                            ToastUtil.showRedToast(AssetSearchActivity.this, "No asset classes found for this location!", 3000);
                        }
                    }

                    @Override
                    public void onFailure(Call<LocationResponse> call, Throwable t) {
                        ToastUtil.showRedToast(AssetSearchActivity.this, "Failed to fetch asset classes", 3000);
                    }
                });
            }

            private void fetchSACodeByAssetName(String assetName) {
                if (assetName == null || assetName.isEmpty()) {
                    return;
                }
                SubAssetCodeListByAssetNameRequestModel request = new SubAssetCodeListByAssetNameRequestModel(assetName);
                apiService.getSubAssetCodeByAssetName(request).enqueue(new Callback<AssetNameResponse>() {
                    @Override
                    public void onResponse(Call<AssetNameResponse> call, Response<AssetNameResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            List<String> subCodeByAssetNameList = new ArrayList<>();
                            subCodeByAssetNameList.add("Select Sub Asset Code");

                            List<AssetNameResponse.SubassetcodeList> subassetCodeDetails = response.body().getData().getSubassetcodeList();
                            for (AssetNameResponse.SubassetcodeList sastCodeDetails : subassetCodeDetails) {
                                String subAssetCode = sastCodeDetails.getSubassetcode(); // fixed variable name
                                subCodeByAssetNameList.add(subAssetCode);
                            }
                            runOnUiThread(() -> {
                                SubAssetFilterAdapter subAssetAdapter = new SubAssetFilterAdapter(
                                        AssetSearchActivity.this,
                                        android.R.layout.simple_dropdown_item_1line,
                                        subCodeByAssetNameList
                                );
                                subAssetAdapter.setMatchMode(SubAssetFilterAdapter.MatchMode.CONTAINS);
                                subAssetDrop.setAdapter(subAssetAdapter);
                            });

                        } else {
                            ToastUtil.showRedToast(AssetSearchActivity.this, "No sub-asset codes found for this asset!", 3000);
                        }
                    }

                    @Override
                    public void onFailure(Call<AssetNameResponse> call, Throwable t) {
                        ToastUtil.showRedToast(AssetSearchActivity.this, "Failed to fetch sub-asset codes: ", 3000);
                    }
                });
            }

        });
    }

    private void setupScanner() {
        subAssetDrop.setKeyListener(new EditText(this).getKeyListener());
        subAssetDrop.setFocusable(true);
        subAssetDrop.setFocusableInTouchMode(true);
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
                    ActivityCompat.requestPermissions(AssetSearchActivity.this,
                            new String[]{Manifest.permission.CAMERA}, 201);
                    return;
                }
                try {
                    cameraSource.start(previewView.getHolder());
                } catch (IOException e) {
                    e.printStackTrace();
//                    isScannerActive = false;
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
                ToastUtil.showRedToast(AssetSearchActivity.this, "Scanner stopped", 3000);
            }

            @Override
            public void receiveDetections(@NonNull Detector.Detections<Barcode> detections) {
                SparseArray<Barcode> barcodes = detections.getDetectedItems();

                if (barcodes.size() > 0) {
                    String scannedValue = barcodes.valueAt(0).displayValue;

                    if (!scannedValue.equals(lastScannedData)) {
                        lastScannedData = scannedValue;
                        runOnUiThread(() -> {
                            fetchAssetDetails(scannedValue);
                        });
                    }
                }
            }
        });
    }






    private void restoreSubAssetInput() {
        runOnUiThread(() -> {
            subAssetDrop.setKeyListener(new EditText(this).getKeyListener());
            subAssetDrop.setFocusable(true);
            subAssetDrop.setFocusableInTouchMode(true);
            subAssetDrop.setClickable(true);
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


    private final BroadcastReceiver zebraReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.d("DWReceiver", "Received intent: " + action);

            if ("com.symbol.datawedge.data".equals(action)) {
                String scannedData = intent.getStringExtra("com.symbol.datawedge.data_string");
                Log.d("DWReceiver", "Scanned Data: " + scannedData);

                if (subAssetDrop != null) {
                    if (!TextUtils.isEmpty(subAssetDrop.getText().toString())) {
                        subAssetDrop.setText("");
                        subAssetDrop.setEnabled(true);
                    }

                    subAssetDrop.setText(scannedData);
                    selectedSubAssetCode = scannedData;
                    subAssetDrop.setEnabled(false);
                }

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




    private void fetchAssetDetails(String assetNo) {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        AssetViewModel assetViewModel = new AssetViewModel(assetNo);
        Call<JsonObject> call = apiService.assetView(assetViewModel);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                subAssetDrop.setEnabled(true);
                if (response.isSuccessful() && response.body() != null) {
                    restoreSubAssetInput();
                    JsonObject data = response.body().getAsJsonObject("Data");
                    if (data != null && data.has("AssetDetails")) {
                        JsonArray assetDetails = data.getAsJsonArray("AssetDetails");
                        if (assetDetails.size() > 0) {
                            JsonObject firstAsset = assetDetails.get(0).getAsJsonObject();
                            String scannedCode = firstAsset.get("Subassetcode").getAsString();
                            boolean allDropdownsFilled =
                                    !companyDropdown.getSelectedItem().equals("Select Company") &&
                                            !buildingDropdown.getSelectedItem().equals("Select Building") &&
                                            !floorDropdown.getSelectedItem().equals("Select Floor") &&
                                            !locationDropdown.getSelectedItem().equals("Select Location") &&
                                            !assetClassDropdown.getSelectedItem().equals("Select AssetClass") &&
                                            !assetDescriptionDropdown.getSelectedItem().equals("Select Asset");
                            if (scannedCode != null) {
                                String typedSubAssetCode = subAssetDrop.getText().toString();
                                boolean isValidSubAssetCode = false;
                                for (int i = 0; i < subAssetDrop.getAdapter().getCount(); i++) {
                                    if (typedSubAssetCode.equals(subAssetDrop.getAdapter().getItem(i).toString())) {
                                        isValidSubAssetCode = true;
                                        break;
                                    }
                                }

                                if (!isValidSubAssetCode) {
                                    ToastUtil.showRedToast(AssetSearchActivity.this, "Sub Asset Code is either Missing or does not Match", 3000);
                                    lastScannedData = "";
                                    return;
                                }
                                if (scannedCode.equals(typedSubAssetCode)) {
                                    if (!allDropdownsFilled) {
                                        ToastUtil.showBlueToast(AssetSearchActivity.this, "Please fill all dropdowns", 5000);
                                    } else {
                                        showAssetFoundDialog();
                                        stopScanner();
                                    }
                                } else {
                                    ToastUtil.showRedToast(AssetSearchActivity.this, "Scanned code does not match Sub Asset Code", 3000);
                                    lastScannedData = "";
                                }
                            } else {
                                ToastUtil.showRedToast(AssetSearchActivity.this, "Sub Asset must be filled", 3000);
                            }

                        } else {
                            ToastUtil.showRedToast(AssetSearchActivity.this, "No Asset Details", 3000);
                            lastScannedData = "";
                        }
                    } else {
                        ToastUtil.showRedToast(AssetSearchActivity.this, "Data not found", 3000);
                        lastScannedData = "";
                    }
                } else {
                    ToastUtil.showRedToast(AssetSearchActivity.this, "Something went wrong", 3000);
                    lastScannedData = "";
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

                restoreSubAssetInput();
                lastScannedData = "";
            }
        });
    }

    private void fetchAssetNameByAssetClass(String assetClassCode) {
        if (assetClassCode == null || assetClassCode.isEmpty()) {
            ToastUtil.showRedToast(AssetSearchActivity.this, "Invalid Asset Class Code", 3000);
            return;
        }
        AssetClasRequestModel request = new AssetClasRequestModel(assetClassCode);
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        apiService.getAssetByAssetClassCode(request).enqueue(new Callback<AssetClassResponse>() {
            @Override
            public void onResponse(Call<AssetClassResponse> call, Response<AssetClassResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    AssetClassResponse.Data data = response.body().getData();
                    List<String> assetNames = new ArrayList<>();
                    assetNames.add("Select Asset");
                    Set<String> uniqueAssetNames = new LinkedHashSet<>();
                    for (AssetClassResponse.AssetDetails asset : data.getAssetDetails()) {
                        if (asset.getAssetName() != null) {
                            uniqueAssetNames.add(asset.getAssetName().trim());
                        }
                    }
                    assetNames.addAll(uniqueAssetNames);
                    setupSpinner(assetDescriptionDropdown, assetNames);
                } else {
                    ToastUtil.showRedToast(AssetSearchActivity.this, "Response failed!", 3000);
                }
            }

            @Override
            public void onFailure(Call<AssetClassResponse> call, Throwable t) {
                ToastUtil.showRedToast(AssetSearchActivity.this, "API Error!", 3000);
            }
        });
    }

    private void stopScanner() {
        if (cameraSource != null) {
            cameraSource.stop();
        }
    }

    private void showAssetFoundDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Asset Found");
        builder.setMessage("The scanned asset matches the selected asset.");
        builder.setIcon(R.drawable.tickk);
        builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
        previewView.setVisibility(View.GONE);

        AlertDialog dialog = builder.create();
        dialog.show();

        if (cameraSource != null) {
            try {
                cameraSource.stop();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA_PERMISSION && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
        setupScanner();
        } else {
            ToastUtil.showBlueToast(AssetSearchActivity.this, "Camera permission required", 3000);
        }
    }
}
