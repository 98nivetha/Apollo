package com.example.myapplication;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.OptIn;
import androidx.appcompat.app.AppCompatActivity;
import androidx.media3.common.util.UnstableApi;
import com.airbnb.lottie.LottieAnimationView;
import com.example.Models.AuditResponse;
import com.example.RequestModels.AuditRequestModel;
import com.example.Service.ApiClient;
import com.example.Service.ApiService;
import com.google.gson.Gson;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuditProcessStageActivity extends AppCompatActivity {
    private LinearLayout auditDetailsContainer;
    private TextView titleText;
    LottieAnimationView lottieLoader;
    @OptIn(markerClass = UnstableApi.class) @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auditprocessstage);
        auditDetailsContainer = findViewById(R.id.auditDetailsContainer);
        titleText = findViewById(R.id.titleText);
        lottieLoader = findViewById(R.id.lottieLoader);
        String processstage = getIntent().getStringExtra("stage");
        ImageButton backToHome = findViewById(R.id.btnBackToHome);
        backToHome.setOnClickListener(v -> {
            v.post(() -> {
                Intent intent = new Intent(AuditProcessStageActivity.this, AuditProcessStageActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0); // disables animation
                finish();
            });
        });

        if (processstage == null) {
            finish();
            return;
        }
        titleText.setText(processstage);
        callAuditFilterApi(processstage);

    }
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
        }

    }

    private void callAuditFilterApi(String processstage) {
        AuditRequestModel request = new AuditRequestModel(processstage);
        lottieLoader.setVisibility(View.VISIBLE);
        Gson gson = new Gson();
        String requestJson = gson.toJson(request);
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<AuditResponse> call = apiService.getAuditDetailsByProcessstage(request);
        call.enqueue(new Callback<AuditResponse>() {
            @Override
            public void onResponse(Call<AuditResponse> call, Response<AuditResponse> response) {
                lottieLoader.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    List<AuditResponse.AuditDetails> auditList = null;

                    switch (processstage) {
                        case "Completed":
                            auditList = response.body().getData().getD_CompletedDetails();
                            break;
                        case "Upcoming":
                            auditList = response.body().getData().getD_UpcomingDetails();
                            break;
                        case "InProgress":
                            auditList = response.body().getData().getD_InprocessDetails();
                            break;
                    }
                    if (auditList != null && !auditList.isEmpty()) {
                        for (AuditResponse.AuditDetails audit : auditList) {
                            View auditView = LayoutInflater.from(AuditProcessStageActivity.this)
                                    .inflate(R.layout.audit_item, auditDetailsContainer, false);

                            TextView auditIdText = auditView.findViewById(R.id.auditIdText);
                            TextView auditConductFromText = auditView.findViewById(R.id.auditConductFromText);
                            TextView auditConductToText = auditView.findViewById(R.id.auditConductToText);
                            TextView auditCompletionText = auditView.findViewById(R.id.auditCompletionText);
                            TextView auditBuildingText = auditView.findViewById(R.id.buildingName);
                            auditIdText.setText("Audit Code: " + getSafeText(audit.getAuditcode(), "N/A"));
                            auditConductFromText.setText("From: " + getSafeText(audit.getAuditconductfrom(), "N/A"));
                            auditConductToText.setText("To: " + getSafeText(audit.getAuditconductto(), "N/A"));

                            String completion = audit.getAuditcompletion();
                            auditCompletionText.setText("Completion: " + (TextUtils.isEmpty(completion) ? "0%" : completion + ""));

                            String totalCountStr = audit.getTotalcount();
                            String inCountStr = audit.getIncount();
                            int totalCount = TextUtils.isEmpty(totalCountStr) ? 0 : Integer.parseInt(totalCountStr);
                            int inCount = TextUtils.isEmpty(inCountStr) ? 0 : Integer.parseInt(inCountStr);
                            String formattedText = inCount + "/" + totalCount;
                            auditBuildingText.setText("Count: " + formattedText);

                            auditView.setOnClickListener(v -> {
                                String stage =processstage;
                                if (stage != null && stage.equalsIgnoreCase("Completed")) {
                                    Toast.makeText(AuditProcessStageActivity.this, "This audit is already completed.", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                Intent intent = new Intent(AuditProcessStageActivity.this, AuditScanningDetailActivity.class);
                                intent.putExtra("audID", Integer.parseInt(audit.getAuditid()));
                                intent.putExtra("audCode", audit.getAuditcode());
                                startActivity(intent);
                            });
                            auditDetailsContainer.addView(auditView);
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "No audits found", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<AuditResponse> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private String getSafeText(String value, String defaultValue) {
        return TextUtils.isEmpty(value) ? defaultValue : value;
    }

}
