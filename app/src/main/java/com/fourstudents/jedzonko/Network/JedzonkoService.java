package com.fourstudents.jedzonko.Network;

import com.fourstudents.jedzonko.Network.Responses.LoginResponse;
import com.fourstudents.jedzonko.Network.Responses.ProductResponse;
import com.fourstudents.jedzonko.Network.Responses.RecipeResponse;
import com.fourstudents.jedzonko.Network.Responses.RegisterResponse;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface JedzonkoService {

    String BASE_URL = "https://uz-recipes-rest.herokuapp.com/";

    @POST("users/login")
    Call<LoginResponse> login(@Body JsonObject object);

    @POST("users/register")
    Call<RegisterResponse> register(@Body JsonObject object);

    @PUT("products/create/")
    Call<ProductResponse> addProduct(@Body JsonObject object);

    @POST("recipes/create/")
    Call<RecipeResponse> addRecipe(@Body JsonObject object);

    @GET("users/all/")
    Call<List<RegisterResponse>> getAllUsers(@Body JsonObject object);

    @GET("users/{id}")
    Call<RegisterResponse> getUser(@Header("Authorization") String token, @Path("id") int id);

    @GET("recipes/get/all")
    Call<List<RecipeResponse>> getRecipes();


}
