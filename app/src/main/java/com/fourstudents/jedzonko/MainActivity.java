package com.fourstudents.jedzonko;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.Window;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //setSupportActionBar(findViewById(R.id.custom_toolbar));
        //ActionBar toolbar = Objects.requireNonNull(getSupportActionBar());
        //Toolbar toolbar = findViewById(R.id.custom_toolbar);

        Fragment searchFragment = new SearchFragment();
        Fragment shoppingListFragment = new ShoppingListFragment();
        Fragment recipesFragment = new RecipesFragment();
        Fragment shopsFragment = new ShopsFragment();
        Fragment accountFragment = new AccountFragment();

        ((BottomNavigationView) findViewById(R.id.bottomNavigationView)).setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.navSearch:
                    //toolbar.setTitle(R.string.title_search);
                    setCurrentFragment(searchFragment);
                    break;
                case R.id.navShoppingList:
                    //toolbar.setTitle(R.string.title_slist);
                    setCurrentFragment(shoppingListFragment);
                    break;
                case R.id.navRecipes:
                    //toolbar.setTitle(R.string.title_recipes);
                    setCurrentFragment(recipesFragment);
                    break;
                case R.id.navShops:
                    //toolbar.setTitle(R.string.title_shops);
                    setCurrentFragment(shopsFragment);
                    break;
                case R.id.navAccount:
                    //toolbar.setTitle(R.string.title_account);
                    setCurrentFragment(accountFragment);
                    break;
            }
            return true;
        });

        ((BottomNavigationView) findViewById(R.id.bottomNavigationView)).setSelectedItemId(R.id.navRecipes);

    }

    private void setCurrentFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.mainFrameLayout, fragment);
        transaction.commit();
    }
}