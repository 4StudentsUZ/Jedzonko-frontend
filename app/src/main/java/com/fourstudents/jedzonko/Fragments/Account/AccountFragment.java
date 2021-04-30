package com.fourstudents.jedzonko.Fragments.Account;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.fourstudents.jedzonko.MainActivity;
import com.fourstudents.jedzonko.Network.JedzonkoService;
import com.fourstudents.jedzonko.Network.Responses.LoginResponse;
import com.fourstudents.jedzonko.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.JsonObject;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AccountFragment extends Fragment implements Callback<LoginResponse> {

    EditText usernameText;
    EditText passwordText;
    Button loginButton;
    TextView forgotPasswordText;
    TextView registerText;
    TextView exampleText;
    TextView infoText;

    JedzonkoService api;


    public AccountFragment() {
        super(R.layout.fragment_account);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        api = ((MainActivity) requireActivity()).api;
        initToolbar(view);
        if (((MainActivity) requireActivity()).token.length() > 0) {
            initViewsAuth(view);
        } else {
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

    private void initViewsAuth(View view) {
        loginButton = view.findViewById(R.id.loginButton);
        forgotPasswordText = view.findViewById(R.id.forgotPasswordText);
        registerText = view.findViewById(R.id.registerText);
        usernameText = view.findViewById(R.id.usernameText);
        passwordText = view.findViewById(R.id.passwordText);
        exampleText = view.findViewById(R.id.exampleText);
        infoText = view.findViewById(R.id.infoText);

        loginButton.setVisibility(View.INVISIBLE);
        forgotPasswordText.setVisibility(View.INVISIBLE);
        registerText.setVisibility(View.INVISIBLE);
        usernameText.setVisibility(View.INVISIBLE);
        passwordText.setVisibility(View.INVISIBLE);
        exampleText.setVisibility(View.INVISIBLE);

        infoText.setText(((MainActivity) requireActivity()).token);
    }

    private void initViews(View view) {
        loginButton = view.findViewById(R.id.loginButton);
        forgotPasswordText = view.findViewById(R.id.forgotPasswordText);
        registerText = view.findViewById(R.id.registerText);
        usernameText = view.findViewById(R.id.usernameText);
        passwordText = view.findViewById(R.id.passwordText);
        exampleText = view.findViewById(R.id.exampleText);

        exampleText.setOnClickListener(v -> {
            usernameText.setText("sikreto2020@protonmail.com");
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
        registerText.setOnClickListener(v -> {
            getParentFragmentManager()
                    .beginTransaction()
                    .replace(R.id.mainFrameLayout, new RegisterAccountFragment(), "RegisterAccountFragment")
                    .addToBackStack("RegisterAccountFragment")
                    .commit();
        });
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
            ((MainActivity) requireActivity()).token = response.body().getToken();
            Fragment frg = getParentFragmentManager().findFragmentByTag("root_fragment");
            getParentFragmentManager().beginTransaction().detach(frg).commitNowAllowingStateLoss();
            getParentFragmentManager().beginTransaction().attach(frg).commitAllowingStateLoss();
        } else if (response.errorBody() != null) {
            try {
                Toast.makeText(requireContext(), response.errorBody().string(), Toast.LENGTH_LONG).show();
            } catch (IOException e) {
                Toast.makeText(requireContext(), e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onFailure(@NotNull Call<LoginResponse> call, @NotNull Throwable t) {

    }
}