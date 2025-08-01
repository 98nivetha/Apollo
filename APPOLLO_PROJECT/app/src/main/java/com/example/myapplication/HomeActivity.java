package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.card.MaterialCardView;

public class HomeActivity extends AppCompatActivity {
    TextView userNameText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Check login status
        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        boolean isLoggedIn = sharedPreferences.getBoolean("IsLoggedIn", false);

        if (!isLoggedIn) {
            // Redirect to Login if not logged in
            startActivity(new Intent(HomeActivity.this, LoginActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_home);

        LinearLayout assetView = findViewById(R.id.assetviewframe);
        LinearLayout assetSearch = findViewById(R.id.assetSearch);
        LinearLayout assetAud = findViewById(R.id.assetAudit);
        MaterialCardView logout = findViewById(R.id.logoutFrame);
        LinearLayout aboutUs = findViewById(R.id.aboutUs);
        userNameText = findViewById(R.id.loggedInUser);

        String savedUsername = sharedPreferences.getString("Username", "Guest");
        userNameText.setText("Welcome " + savedUsername);

        assetView.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, AssetViewActivity.class);
            startActivity(intent);
        });

        assetSearch.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, AssetSearchActivity.class);
            startActivity(intent);
        });

        assetAud.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, AssetAuditActivity.class);
            startActivity(intent);
        });

        aboutUs.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, AboutUsActivity.class);
            startActivity(intent);
        });

        logout.setOnClickListener(v -> {
            showLogoutDialog();
        });
    }

    @Override
    public void onBackPressed() {
        showLogoutDialog();
    }

    private void showLogoutDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
        builder.setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.clear(); // Clear login state
                    editor.apply();

                    Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss());
        builder.create().show();
    }
}
