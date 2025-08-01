package com.example.myapplication;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;


public class AboutUsActivity extends AppCompatActivity {

    private TextView appNameTextView, appDescriptionTextView, appVersionTextView, developerInfoTextView,mailInfoTextView;
    private ImageView appLogoImageView,agaramLogoImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aboutus);

        // Bind views
        appNameTextView = findViewById(R.id.appName);
        appDescriptionTextView = findViewById(R.id.appDescription);
        appVersionTextView = findViewById(R.id.appVersion);
        developerInfoTextView = findViewById(R.id.developerInfo);
        mailInfoTextView = findViewById(R.id.mailInfo);

        appDescriptionTextView.setText("The mobile app simplifies asset management by using barcode scanning to track and access asset details instantly.\n" +
                "Users can search assets, view information, and conduct audits directly from their Android device.");
        developerInfoTextView.setText("© 2025 Agaram Team");
        developerInfoTextView.setAutoLinkMask(Linkify.EMAIL_ADDRESSES);
        developerInfoTextView.setMovementMethod(LinkMovementMethod.getInstance());
        developerInfoTextView.setLinkTextColor(Color.BLUE);

        developerInfoTextView.setOnClickListener(v -> {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.agaraminfotech.com/"));
            startActivity(browserIntent);
        });

        mailInfoTextView.setOnClickListener(v -> {
            Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
            emailIntent.setData(Uri.parse("mailto:support@agaraminfotech.com"));
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Support Request");
            emailIntent.putExtra(Intent.EXTRA_TEXT, "Hi Agaram Team,\n");

            if (emailIntent.resolveActivity(getPackageManager()) != null) {
                startActivity(emailIntent);
            } else {
                Toast.makeText(AboutUsActivity.this, "No email app found", Toast.LENGTH_SHORT).show();
            }
        });


        // Set dynamic version
        appVersionTextView.setText("Version " + getAppVersion());
    }

    private String getAppVersion() {
        try {
            PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            return packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            return "N/A";
        }
    }
}
