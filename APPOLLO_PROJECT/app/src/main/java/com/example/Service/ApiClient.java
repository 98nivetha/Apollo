package com.example.Service;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    static final String BASE_URL = "http://apolloservice.agaraminfotech.com/";
    // private static final String BASE_URL = "http://10.0.2.2:5000/";
    // private static final String BASE_URL = "http://192.168.1.37:8011/";
    private static Retrofit retrofit = null;

    public static Retrofit getClient() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
