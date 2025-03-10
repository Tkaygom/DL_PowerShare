package com.firsthachathoners.powershare;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by safa on 17.02.2018.
 * Edited by Tkay_gom on 03.03.2025
 */

public class APIClient {

    private static Retrofit retrofit = null;

    public static Retrofit getClient() {

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();


        retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.2.10:5000/") // Use the correct IP and port of your backend server
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
               .build();

        return retrofit;
    }

}