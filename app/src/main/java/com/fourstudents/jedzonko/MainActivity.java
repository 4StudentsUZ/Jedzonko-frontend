package com.fourstudents.jedzonko;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.fourstudents.jedzonko.Fragments.Account.AccountFragment;
import com.fourstudents.jedzonko.Fragments.Recipe.RecipeFragment;
import com.fourstudents.jedzonko.Fragments.Search.SearchFragment;
import com.fourstudents.jedzonko.Fragments.Shop.ShopsFragment;
import com.fourstudents.jedzonko.Fragments.ShoppingList.ShoppingListFragment;
import com.fourstudents.jedzonko.Network.JedzonkoService;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class MainActivity extends AppCompatActivity {

    public static final String BACK_STACK_ROOT_TAG = "root_fragment";
    public static final String SHARED_PREFERENCES_NAME = "userPreferences";
    public static final String PREFERENCES_TOKEN = "token";
    public static final String PREFERENCES_USERID = "userid";


    private MenuItem menuItem;

    //Http interceptor
    OkHttpClient okHttpClient = new OkHttpClient().newBuilder().addInterceptor(new Interceptor() {
        @NotNull
        @Override
        public okhttp3.Response intercept(@NotNull Interceptor.Chain chain) throws IOException {
            Request originalRequest = chain.request();
            Request.Builder builder = originalRequest.newBuilder();
            if (token.length() > 0) {
                builder.header("Authorization", "Bearer " + token);
            }

            Request newRequest = builder.build();
            return chain.proceed(newRequest);
        }
    }).build();
    // Fragment related variables
    //// AddRecipe Photo Data
    public byte[] imageData = null;
    public Integer imageRotation = null;
    public byte[] productImageData = null;
    public Integer productImageRotation = null;
    public String scannedBarcode = null;
    //// Instance of JedzonkoService
    public JedzonkoService api =
            new Retrofit.Builder()
            .baseUrl(JedzonkoService.BASE_URL)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
            .create(JedzonkoService.class);
    //// Login Auth
    public String token = "";
    public int userid = -1;
    SharedPreferences sharedPreferences;


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loadUserPreferences();
        setupBottomNavigationView();
    }

    private void loadUserPreferences() {
        sharedPreferences = getSharedPreferences(SHARED_PREFERENCES_NAME, MODE_PRIVATE);
        token = sharedPreferences.getString(PREFERENCES_TOKEN, "");
        userid = sharedPreferences.getInt(PREFERENCES_USERID, -1);
    }

    @Override
    public void onBackPressed() {
        int backStackCount = getSupportFragmentManager().getBackStackEntryCount();
        if (backStackCount == 1) {
            finish();
        } else {
            super.onBackPressed();
        }
    }

    @SuppressLint("NonConstantResourceId")
    private void setupBottomNavigationView() {
//        Fragment searchFragment = new SearchFragment();
//        Fragment shoppingListFragment = new ShoppingListFragment();
//        Fragment recipesFragment = new RecipeFragment();
//        Fragment shopsFragment = new ShopsFragment();
//        Fragment accountFragment = new AccountFragment();

        ((BottomNavigationView) findViewById(R.id.bottomNavigationView)).setOnNavigationItemSelectedListener(item -> {
            if (menuItem != null) {
                menuItem.setEnabled(true);
            }
            switch (item.getItemId()) {
                case R.id.navSearch:
                    setCurrentFragment(new SearchFragment());
                    break;
                case R.id.navShoppingList:
                    setCurrentFragment(new ShoppingListFragment());
                    break;
                case R.id.navRecipes:
                    setCurrentFragment(new RecipeFragment());
                    break;
                case R.id.navShops:
                    setCurrentFragment(new ShopsFragment());
                    break;
                case R.id.navAccount:
                    setCurrentFragment(new AccountFragment());
                    break;
            }
            menuItem = item;
            menuItem.setEnabled(false);
            return true;
        });

        ((BottomNavigationView) findViewById(R.id.bottomNavigationView)).setSelectedItemId(R.id.navRecipes);
    }

    private void setCurrentFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();

        if (!fragment.equals(fragmentManager.findFragmentById(R.id.mainFrameLayout))) {
            fragmentManager.popBackStack(BACK_STACK_ROOT_TAG, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            fragmentManager
                    .beginTransaction()
                    .replace(R.id.mainFrameLayout, fragment, BACK_STACK_ROOT_TAG)
                    .addToBackStack(BACK_STACK_ROOT_TAG)
                    .commit();
        }
    }
}