package com.fourstudents.jedzonko.UI;

import android.Manifest;

import androidx.test.core.app.ActivityScenario;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;
import androidx.test.rule.GrantPermissionRule;

import com.fourstudents.jedzonko.MainActivity;
import com.fourstudents.jedzonko.R;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isCompletelyDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4ClassRunner.class)
public class ActivityUITest {

    ActivityScenario<MainActivity> activityScenario;

    @Rule
    public GrantPermissionRule grantPermissionRule = GrantPermissionRule.grant(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.BLUETOOTH,
            Manifest.permission.CAMERA,
            Manifest.permission.INTERNET
    );

    @Before
    public void setUp() {
        activityScenario = ActivityScenario.launch(MainActivity.class);
    }

    @Test
    public void initialViewIsRecipeFragment() {
        // RecipeFragment is completely displayed
        onView(withId(R.id.fragment_recipe_parent))
                .check(matches(isCompletelyDisplayed()));

        // Other fragments are not visible
        onView(withId(R.id.fragment_shop_parent))
                .check(doesNotExist());
        onView(withId(R.id.fragment_shoppinglist_parent))
                .check(doesNotExist());
        onView(withId(R.id.fragment_account_parent))
                .check(doesNotExist());
        onView(withId(R.id.fragment_search_parent))
                .check(doesNotExist());
    }

    @Test
    public void everyNavBarItemNavigatesProperly() {
        // initialViewIsRecipeFragment makes sure that RecipeFragment is visible
        // navigate to AccountFragment
        onView(withId(R.id.navAccount))
                .perform(click());

        // AccountFragment
        onView(withId(R.id.fragment_account_parent))
                .check(matches(isCompletelyDisplayed()));
        onView(withId(R.id.fragment_recipe_parent))
                .check(doesNotExist());
        onView(withId(R.id.navShops))
                .perform(click());

        // ShopFragment
        onView(withId(R.id.fragment_shop_parent))
                .check(matches(isCompletelyDisplayed()));
        onView(withId(R.id.fragment_account_parent))
                .check(doesNotExist());
        onView(withId(R.id.navShoppingList))
                .perform(click());

        // ShoppingListFragment
        onView(withId(R.id.fragment_shoppinglist_parent))
                .check(matches(isCompletelyDisplayed()));
        onView(withId(R.id.fragment_shop_parent))
                .check(doesNotExist());
        onView(withId(R.id.navSearch))
                .perform(click());

        // SearchFragment
        onView(withId(R.id.fragment_search_parent))
                .check(matches(isCompletelyDisplayed()));
        onView(withId(R.id.fragment_shoppinglist_parent))
                .check(doesNotExist());
        onView(withId(R.id.navRecipes))
                .perform(click());

        // RecipeFragment
        onView(withId(R.id.fragment_recipe_parent))
                .check(matches(isCompletelyDisplayed()));
        onView(withId(R.id.fragment_search_parent))
                .check(doesNotExist());
    }
}
