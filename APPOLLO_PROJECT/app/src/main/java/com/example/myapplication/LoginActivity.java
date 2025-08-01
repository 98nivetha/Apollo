package com.example.myapplication;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import com.airbnb.lottie.LottieAnimationView;
import com.example.Models.LoginResponse;
import com.example.RequestModels.LoginRequest;
import com.example.Service.ApiClient;
import com.example.Service.ApiService;
import com.example.Service.ToastUtil;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    EditText uNameEditText, pswdEditText;
    LottieAnimationView lottieLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Button loginButton = findViewById(R.id.loginButton);
        uNameEditText = findViewById(R.id.userNameEditText);
        pswdEditText = findViewById(R.id.passwordEditText);
        lottieLoader = findViewById(R.id.lottieLoader);
        ImageView passwordToggle = findViewById(R.id.passwordToggle);
        final boolean[] isPasswordVisible = {false};
        passwordToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPasswordVisible[0]) {
                    pswdEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    passwordToggle.setImageResource(R.drawable.eye_close);
                    isPasswordVisible[0] = false;
                } else {
                    pswdEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    passwordToggle.setImageResource(R.drawable.eye_open);
                    isPasswordVisible[0] = true;
                }
                pswdEditText.setSelection(pswdEditText.getText().length());
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = uNameEditText.getText().toString().trim();
                String password = pswdEditText.getText().toString().trim();

                if (username.isEmpty() || password.isEmpty()) {
                    ToastUtil.showRedToast(LoginActivity.this, "Username and Password cannot be empty", 3000);
                    return;
                }
                LoginRequest loginRequest = new LoginRequest(username, password);
                lottieLoader.setVisibility(View.VISIBLE);
                ApiService apiService = ApiClient.getClient().create(ApiService.class);
                Call<LoginResponse> call = apiService.loginUser(loginRequest);
                call.enqueue(new Callback<LoginResponse>() {
                    @Override
                    public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                        lottieLoader.setVisibility(View.GONE);
                        if (response.isSuccessful() && response.body() != null) {
                            LoginResponse loginResponse = response.body();
                            if (loginResponse.getData() != null && loginResponse.getData().userDetails != null) {
                                String user = loginResponse.getData().userDetails.username;

                                SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putBoolean("IsLoggedIn", true);
                                editor.putString("Username", username);
                                editor.apply();

                                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                                startActivity(intent);
                                finish();

                                ToastUtil.showGreenToast(LoginActivity.this, "You have logged in successfully", 800);

                            } else {
                                ToastUtil.showRedToast(LoginActivity.this, "Invalid login response", 3000);
                            }
                        } else {
                            ToastUtil.showRedToast(LoginActivity.this, "Please check your credentials", 3000);
                        }
                    }


                    @Override
                    public void onFailure(Call<LoginResponse> call, Throwable t) {
                        ToastUtil.showRedToast(LoginActivity.this, "Error: " + t.getMessage(), 3000);
                    }
                });
            }
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
