package com.fourstudents.jedzonko.Network;

import com.fourstudents.jedzonko.Network.Responses.LoginResponse;
import com.fourstudents.jedzonko.Network.Responses.RegisterResponse;
import com.google.gson.JsonObject;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface JedzonkoService {

    String BASE_URL = "https://uz-recipes-rest.herokuapp.com/";

    @POST("users/login")
    Call<LoginResponse> login(@Body JsonObject object);

    @POST("users/register")
    Call<RegisterResponse> register(@Body JsonObject object);

    @GET("users/all")
    Call<List<RegisterResponse>> getAllUsers(@Body JsonObject object);
}
