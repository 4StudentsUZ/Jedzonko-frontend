package com.fourstudents.jedzonko;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.view.MenuItem;

import com.fourstudents.jedzonko.Fragments.Account.AccountFragment;
import com.fourstudents.jedzonko.Fragments.Recipe.RecipeFragment;
import com.fourstudents.jedzonko.Fragments.Search.SearchFragment;
import com.fourstudents.jedzonko.Fragments.ShoppingList.ShoppingListFragment;
import com.fourstudents.jedzonko.Fragments.Shop.ShopsFragment;
import com.fourstudents.jedzonko.Network.JedzonkoService;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Credentials;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private static final String BACK_STACK_ROOT_TAG = "root_fragment";
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
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
            .create(JedzonkoService.class);
    //// Login Auth
    public String token = "";
    public int userid = -1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupBottomNavigationView();
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

    private void setupBottomNavigationView() {
        Fragment searchFragment = new SearchFragment();
        Fragment shoppingListFragment = new ShoppingListFragment();
        Fragment recipesFragment = new RecipeFragment();
        Fragment shopsFragment = new ShopsFragment();
//        Fragment accountFragment = new AccountFragment();

        ((BottomNavigationView) findViewById(R.id.bottomNavigationView)).setOnNavigationItemSelectedListener(item -> {
            if (menuItem != null) {
                menuItem.setEnabled(true);
            }
            switch (item.getItemId()) {
                case R.id.navSearch:
                    setCurrentFragment(searchFragment);
                    break;
                case R.id.navShoppingList:
                    setCurrentFragment(shoppingListFragment);
                    break;
                case R.id.navRecipes:
                    setCurrentFragment(recipesFragment);
                    break;
                case R.id.navShops:
                    setCurrentFragment(shopsFragment);
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