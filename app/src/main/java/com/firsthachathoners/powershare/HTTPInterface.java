package com.firsthachathoners.powershare;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import retrofit2.http.GET;
import retrofit2.http.Query;


/**
 * Created by safa on 17.02.2018.
 */

public interface HTTPInterface {



    @FormUrlEncoded
    @POST("/user/login")  // Adjust the endpoint based on your API
    Call<UserResponse> login(@Field("username") String username, @Field("password") String password);



    @GET("api/user/details")
    Call<UserResponse> getUserDetails(@Query("username") String username);

    @FormUrlEncoded
    @POST("/find/powerbank")
    Call<JSONData> getAllRecords(@Field("longitude") double longitude, @Field("latitude") double latitude, @Field("range") int range);

    @FormUrlEncoded
    @POST("/find/chargeport")
    Call<JSONData> getPSs(@Field("longitude") double longitude, @Field("latitude") double latitude, @Field("range") int range);


//Look at this later
   // @FormUrlEncoded
   // @POST("/user/info")
   // Call<Example> getUserDetails(@Field("username") String usName);

    @FormUrlEncoded
    @POST("/user/sessionChange")
    Call<SesObject> sessionChange(@Field("username") String usName, @Field("changedTo") boolean changedTo,
                         @Field("psName") String psName);
}
