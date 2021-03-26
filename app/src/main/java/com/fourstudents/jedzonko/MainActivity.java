package com.fourstudents.jedzonko;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Fragment searchFragment = new SearchFragment();
        Fragment shoppingListFragment = new ShoppingListFragment();
        Fragment recipesFragment = new RecipesFragment();
        Fragment shopsFragment = new ShopsFragment();
        Fragment accountFragment = new AccountFragment();

        ((BottomNavigationView) findViewById(R.id.bottomNavigationView)).setOnNavigationItemSelectedListener(item -> {
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