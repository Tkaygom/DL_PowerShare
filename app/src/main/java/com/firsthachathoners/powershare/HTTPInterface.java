package com.firsthachathoners.powershare;


/**
 * Created by safa on 17.02.2018.
 * edited by Kofitkay on 14th of March 2025
 */

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface HTTPInterface {

    // Authentication endpoints
    @POST("user/login")
    Call<UserResponse> login(@Body LoginRequest loginRequest);

    // New registration endpoint
    @POST("user/register")
    Call<Void> register(@Body RegistrationRequest registrationRequest);

    // User endpoints
    @GET("user/details")
    Call<UserResponse> getUserDetails(@Query("username") String username);

    // Station endpoints
    @POST("find/powerbank")
    Call<JSONData> getAllRecords(@Body ApiLocationRequest locationRequest);

    @POST("find/chargeport")
    Call<JSONData> getPSs(@Body LocationRequest locationRequest);

    // Session management
    @POST("user/sessionChange")
    Call<SesObject> updateSession(@Body SessionUpdateRequest sessionUpdate);
}