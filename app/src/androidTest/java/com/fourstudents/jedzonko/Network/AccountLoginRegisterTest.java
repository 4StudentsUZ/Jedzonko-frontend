package com.fourstudents.jedzonko.Network;

import android.Manifest;

import androidx.test.core.app.ActivityScenario;
import androidx.test.rule.GrantPermissionRule;

import com.fourstudents.jedzonko.MainActivity;
import com.fourstudents.jedzonko.R;
import com.fourstudents.jedzonko.ToastMatcher;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isCompletelyDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

public class AccountLoginRegisterTest {
    ActivityScenario<MainActivity> activityScenario;

    @Rule
    public GrantPermissionRule grantPermissionRule = GrantPermissionRule.grant(
            Manifest.permission.INTERNET
    );

    @Before
    public void setUp() {
        activityScenario = ActivityScenario.launch(MainActivity.class);
    }

    @Test
    public void registerInputCannotBeInvalid() {
        String invalidEmail = "a@a";
        String invalidPasswordShort = "abc";
        String invalidPassword = "12345678";
        String invalidPassword2 = "123456789";

        // navigate to AccountFragment
        onView(withId(R.id.navAccount))
                .perform(click());

        // AccountFragment
        onView(withId(R.id.fragment_account_parent))
                .check(matches(isCompletelyDisplayed()));
        onView(withId(R.id.registerText))
                .perform(click());
        onView(withId(R.id.fragment_account_register_parent))
                .check(matches(isCompletelyDisplayed()));
        onView(withId(R.id.registerButton))
                .perform(click());

        // Not all input given = missing_input_data message
        onView(withText(R.string.missing_input_data))
                .inRoot(new ToastMatcher())
                .check(matches(isCompletelyDisplayed()));

        // Input one edittext with invalid data, still not all input given = missing_input_data message
        onView(withId(R.id.usernameText))
                .perform(typeText(invalidEmail));
        onView(withId(R.id.registerButton))
                .perform(click());
        onView(withText(R.string.missing_input_data))
                .inRoot(new ToastMatcher())
                .check(matches(isCompletelyDisplayed()));

        // Input remaining invalid data,
        onView(withId(R.id.passwordText))
                .perform(typeText(invalidPasswordShort));
        onView(withId(R.id.passwordText2))
                .perform(typeText(invalidPassword2));
        onView(withId(R.id.registerCheckBox))
                .perform(click());

        // After inputing all fields it should validate email first
        onView(withId(R.id.registerButton))
                .perform(click());
        onView(withText(R.string.account_register_email_not_valid))
                .inRoot(new ToastMatcher())
                .check(matches(isCompletelyDisplayed()));
    }
}
