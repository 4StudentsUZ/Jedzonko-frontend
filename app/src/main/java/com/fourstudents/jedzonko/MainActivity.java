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
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private static final String BACK_STACK_ROOT_TAG = "root_fragment";
    private MenuItem menuItem;

    // Fragment related variables
    public byte[] imageData = null;
    public Integer imageRotation = null;

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
        Fragment accountFragment = new AccountFragment();

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
                    setCurrentFragment(accountFragment);
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
                    .replace(R.id.mainFrameLayout, fragment)
                    .addToBackStack(BACK_STACK_ROOT_TAG)
                    .commit();
        }
    }
}