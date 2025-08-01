package com.example.myapplication;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;

public class AssetAuditActivity extends AppCompatActivity {

    LinearLayout upcomingCard, inProgressCard, completedCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assetaudit);
        upcomingCard = findViewById(R.id.upcomingAudit);
        inProgressCard = findViewById(R.id.inProgressAudit);
        completedCard = findViewById(R.id.completedAudit);

        upcomingCard.setOnClickListener(v -> {
            Intent intent = new Intent(AssetAuditActivity.this, AuditProcessStageActivity.class);
            intent.putExtra("stage", "Upcoming");
            startActivity(intent);
        });

        inProgressCard.setOnClickListener(v -> {
            Intent intent = new Intent(AssetAuditActivity.this, AuditProcessStageActivity.class);
            intent.putExtra("stage", "InProgress");
            startActivity(intent);
        });


        completedCard.setOnClickListener(v -> {
            Intent intent = new Intent(AssetAuditActivity.this, AuditProcessStageActivity.class);
            intent.putExtra("stage", "Completed");
            startActivity(intent);
        });
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
        }
    }
}
