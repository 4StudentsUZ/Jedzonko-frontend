package com.fourstudents.jedzonko.Fragments.Account;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.fourstudents.jedzonko.Fragments.Recipe.EditRecipeFragment;
import com.fourstudents.jedzonko.MainActivity;
import com.fourstudents.jedzonko.Network.JedzonkoService;
import com.fourstudents.jedzonko.Network.Responses.LoginResponse;
import com.fourstudents.jedzonko.Network.Responses.RegisterResponse;
import com.fourstudents.jedzonko.Network.Responses.UpdateUserResponse;
import com.fourstudents.jedzonko.R;
import com.google.gson.JsonObject;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AccountFragment extends Fragment implements Callback<LoginResponse> {

    //NoAuth
    EditText usernameText;
    EditText passwordText;
    Button loginButton;
    TextView forgotPasswordText;
    TextView registerText;
    TextView exampleText;
    TextView infoText;

    //WithAuth
    EditText firstNameEdit;
    EditText lastNameEdit;

    //Helper
    JedzonkoService api;
    MainActivity activity;
    boolean isLoggedIn;


    public AccountFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        int layoutId = R.layout.fragment_account;
        activity = ((MainActivity) requireActivity());
        isLoggedIn = activity.token.length() > 0;

        if (isLoggedIn)
            layoutId = R.layout.fragment_account_profile;

        return inflater.inflate(layoutId, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        api = activity.api;

        if (isLoggedIn) {
            initToolbarAuth(view);
            initViewsAuth(view);
        } else {
            initToolbar(view);
            initViews(view);
            getParentFragmentManager().addOnBackStackChangedListener(() -> {
                usernameText.setText("");
                passwordText.setText("");
            });
        }
    }

    private void initToolbar(View view) {
        Toolbar toolbar = view.findViewById(R.id.custom_toolbar);
        toolbar.setTitle(R.string.title_account);
    }

    private void initToolbarAuth(View view) {
        initToolbar(view);
        Toolbar toolbar = view.findViewById(R.id.custom_toolbar);
        toolbar.inflateMenu(R.menu.account_profile);
        toolbar.setOnMenuItemClickListener(clickedMenuItem -> {

            if (clickedMenuItem.getItemId() == R.id.action_edit_profile) {
                JsonObject object = new JsonObject();
                object.addProperty("firstName", firstNameEdit.getText().toString().trim());
                object.addProperty("lastName", lastNameEdit.getText().toString().trim());
                Call<UpdateUserResponse> call = api.updateUser(object);
                call.enqueue(new Callback<UpdateUserResponse>() {
                    @Override
                    public void onResponse(@NotNull Call<UpdateUserResponse> call, @NotNull Response<UpdateUserResponse> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(requireContext(), "Edycja udana", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(requireContext(), "Nie działa", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(@NotNull Call<UpdateUserResponse> call, @NotNull Throwable t) {
                        t.printStackTrace();
                    }
                });
            } else if (clickedMenuItem.getItemId() == R.id.action_logout) {
                activity.userid = -1;
                activity.token = "";
                Toast.makeText(requireContext(), "Wylogowano", Toast.LENGTH_SHORT).show();
                Fragment frg = getParentFragmentManager().findFragmentByTag("root_fragment");
                getParentFragmentManager().beginTransaction().detach(frg).commitNowAllowingStateLoss();
                getParentFragmentManager().beginTransaction().attach(frg).commitAllowingStateLoss();
            }
            return false;
        });
    }

    private void initViewsAuth(View view) {
        firstNameEdit = view.findViewById(R.id.firstNameEdit);
        lastNameEdit = view.findViewById(R.id.lastNameEdit);
        Call<RegisterResponse> call = api.getUser(activity.userid);
        call.enqueue(new Callback<RegisterResponse>() {
            @Override
            public void onResponse(@NotNull Call<RegisterResponse> call, @NotNull Response<RegisterResponse> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        firstNameEdit.setText(response.body().getFirstName());
                        lastNameEdit.setText(response.body().getLastName());
                    }
                } else if (response.errorBody() != null) {
                    try {
                        Toast.makeText(requireContext(), response.errorBody().string(), Toast.LENGTH_LONG).show();
                        Log.i("Harry", response.errorBody().string());
                    } catch (IOException e) {
                        Toast.makeText(requireContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(@NotNull Call<RegisterResponse> call, @NotNull Throwable t) {

            }
        });
    }

    private void initViews(View view) {
        loginButton = view.findViewById(R.id.loginButton);
        forgotPasswordText = view.findViewById(R.id.forgotPasswordText);
        registerText = view.findViewById(R.id.registerText);
        usernameText = view.findViewById(R.id.usernameText);
        passwordText = view.findViewById(R.id.passwordText);
        exampleText = view.findViewById(R.id.exampleText);
        infoText = view.findViewById(R.id.infoText);

        exampleText.setOnClickListener(v -> {
            usernameText.setText("sikreto2021@protonmail.com");
            passwordText.setText("12345678");
        });

        loginButton.setOnClickListener(v -> {
            if (isInputValid()) {
                JsonObject object = new JsonObject();
                object.addProperty("username", usernameText.getText().toString().trim());
                object.addProperty("password", passwordText.getText().toString().trim());
                Call<LoginResponse> call = api.login(object);
                call.enqueue(this);
            }
        });
        registerText.setOnClickListener(v ->
                getParentFragmentManager()
                .beginTransaction()
                .replace(R.id.mainFrameLayout, new RegisterAccountFragment(), "RegisterAccountFragment")
                .addToBackStack("RegisterAccountFragment")
                .commit());
    }

    private boolean isInputValid() {
        String usernameInput = usernameText.getText().toString().trim();
        String passwordInput = passwordText.getText().toString().trim();

        if (TextUtils.isEmpty(usernameInput) || TextUtils.isEmpty(passwordInput)) {
            Toast.makeText(requireContext(), R.string.missing_input_data, Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!isValidEmail(usernameInput)) {
            Toast.makeText(requireContext(), "Email nie jest poprawny", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private boolean isValidEmail(String input) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(input).matches();
    }

    @Override
    public void onResponse(@NotNull Call<LoginResponse> call, @NotNull Response<LoginResponse> response) {
        if (response.isSuccessful()) {
            Toast.makeText(requireContext(), "Logowanie pomyślne", Toast.LENGTH_LONG).show();
            activity.token = response.body().getToken();
            activity.userid = response.body().getId();
            Fragment frg = getParentFragmentManager().findFragmentByTag("root_fragment");
            getParentFragmentManager().beginTransaction().detach(frg).commitNowAllowingStateLoss();
            getParentFragmentManager().beginTransaction().attach(frg).commitAllowingStateLoss();
        } else if (response.errorBody() != null) {
            try {
                Toast.makeText(requireContext(), response.errorBody().string(), Toast.LENGTH_LONG).show();
                Log.v("HarryAccountonResponse", call.request().body().toString());
            } catch (IOException e) {
                Toast.makeText(requireContext(), e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onFailure(@NotNull Call<LoginResponse> call, @NotNull Throwable t) {

    }
}