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
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isCompletelyDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4ClassRunner.class)
public class RecipeUITest {

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
    public void addRecipeNavigation() {

        // Enter AddRecipeFragment
        onView(withId(R.id.floatingActionButton_add_recipe))
                .perform(click());
        onView(withId(R.id.fragment_recipe_add_parent))
                .check(matches(isCompletelyDisplayed()));

        // Enter CameraFragment
        onView(withId(R.id.cameraPhotoButton))
                .perform(click());
        onView(withId(R.id.fragment_camera_parent))
                .check(matches(isCompletelyDisplayed()));

        // Go back to AddRecipeFragment
        pressBack();
        onView(withId(R.id.fragment_recipe_add_parent))
                .check(matches(isCompletelyDisplayed()));

        // Enter AddTagFragment
        onView(withId(R.id.addTagButton))
                .perform(click());
        onView(withId(R.id.dialog_add_tag_parent))
                .check(matches(isDisplayed()));
        onView(withText(R.string.dialog_add_tag))
                .perform(click());
        onView(withId(R.id.fragment_recipe_add_tag_parent))
                .check(matches(isCompletelyDisplayed()));

        // Go back to AddRecipeFragment
        pressBack();
        onView(withId(R.id.fragment_recipe_add_parent))
                .check(matches(isCompletelyDisplayed()));

        // Enter AddProductFragment
        onView(withText(R.string.add_ingredients_button))
                .perform(click());
        onView(withId(R.id.dialog_add_ingredient_parent))
                .check(matches(isDisplayed()));
        onView(withText(R.string.dialog_add_product))
                .perform(click());
        onView(withId(R.id.fragment_add_product_parent))
                .check(matches(isCompletelyDisplayed()));

        // Enter CameraFragment to make photo
        onView(withId(R.id.addImageCameraButton))
                .perform(click());
        onView(withId(R.id.fragment_camera_parent))
                .check(matches(isCompletelyDisplayed()));

        // Go back to AddProductFragment
        pressBack();
        onView(withId(R.id.fragment_add_product_parent))
                .check(matches(isCompletelyDisplayed()));

        // Enter CameraFragment to scan barcode
        onView(withId(R.id.scanBarcodeButton))
                .perform(click());
        onView(withId(R.id.fragment_camera_barcode_parent))
                .check(matches(isCompletelyDisplayed()));

        // Go back to AddProductFragment
        pressBack();
        onView(withId(R.id.fragment_add_product_parent))
                .check(matches(isCompletelyDisplayed()));

        // Go back to AddRecipeFragment
        pressBack();
        onView(withId(R.id.fragment_recipe_add_parent))
                .check(matches(isCompletelyDisplayed()));

        // Go back to RecipeFragment
        pressBack();
        onView(withId(R.id.fragment_recipe_parent))
                .check(matches(isCompletelyDisplayed()));

    }
}
