package com.firsthachathoners.powershare;




import java.util.concurrent.TimeUnit;
//import com.firsthachathoners.powershare.BuildConfig;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by safa on 17.02.2018.
 * Edited by Tkay_gom on 03.03.2025
 */

public class APIClient {
    private static final String BASE_URL = "http://192.168.2.10:5001/"; // Use port 5001
    private static Retrofit retrofit = null;

    public static Retrofit getClient() {
        if (retrofit == null) {
            // Create HTTP client with interceptor
            OkHttpClient.Builder httpClient = new OkHttpClient.Builder();



            // Add timeouts
            httpClient
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS);

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(httpClient.build())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}