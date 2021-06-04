package com.fourstudents.jedzonko.Fragments.Account;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.fourstudents.jedzonko.MainActivity;
import com.fourstudents.jedzonko.Network.JedzonkoService;
import com.fourstudents.jedzonko.Network.Responses.RegisterResponse;
import com.fourstudents.jedzonko.R;
import com.google.gson.JsonObject;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterAccountFragment extends Fragment implements Callback<RegisterResponse> {

    EditText usernameText;
    EditText passwordText;
    EditText passwordText2;
    CheckBox registerCheckBox;
    Button registerButton;
    TextView exampleText;

    JedzonkoService api;

    @VisibleForTesting()
    Toast toast;

    public RegisterAccountFragment() {
        super(R.layout.fragment_account_register);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        api = ((MainActivity) requireActivity()).api;
        initToolbar(view);
        initViews(view);
    }

    private void initToolbar(View view) {
        Toolbar toolbar = view.findViewById(R.id.custom_toolbar);
        toolbar.setTitle(R.string.title_register_account);
    }

    private void initViews(View view) {
        usernameText = view.findViewById(R.id.usernameText);
        passwordText = view.findViewById(R.id.passwordText);
        passwordText2 = view.findViewById(R.id.passwordText2);
        registerCheckBox = view.findViewById(R.id.registerCheckBox);
        registerButton = view.findViewById(R.id.registerButton);
        exampleText = view.findViewById(R.id.exampleText);

        exampleText.setOnClickListener(v -> {
            usernameText.setText("sikreto2020@protonmail.com");
            passwordText.setText("12345678");
            passwordText2.setText("12345678");
        });

        registerButton.setOnClickListener(v -> {
            if (isInputValid()) {
                JsonObject object = new JsonObject();
                object.addProperty("username", usernameText.getText().toString().trim());
                object.addProperty("password", passwordText.getText().toString().trim());
                Call<RegisterResponse> call = api.register(object);
                call.enqueue(this);
            }
        });

    }

    private boolean isInputValid() {
        String usernameInput = usernameText.getText().toString().trim();
        String passwordInput = passwordText.getText().toString().trim();
        String password2Input = passwordText2.getText().toString().trim();

        if (TextUtils.isEmpty(usernameInput) || TextUtils.isEmpty(passwordInput) || TextUtils.isEmpty(password2Input)) {
            toast = Toast.makeText(requireContext(), R.string.missing_input_data, Toast.LENGTH_SHORT);
            toast.show();
            return false;
        }

        if (!isValidEmail(usernameInput)) {
            toast = Toast.makeText(requireContext(), R.string.account_register_email_not_valid, Toast.LENGTH_SHORT);
            toast.show();
            return false;
        }

        if (passwordInput.length() < 8) {
            toast = Toast.makeText(requireContext(), R.string.account_register_password_too_short, Toast.LENGTH_SHORT);
            toast.show();
            return false;
        }

        if (!passwordInput.equals(password2Input)) {
            toast = Toast.makeText(requireContext(), R.string.account_register_password_not_match, Toast.LENGTH_SHORT);
            toast.show();
            return false;
        }

        if (!registerCheckBox.isChecked()) {
            toast = Toast.makeText(requireContext(), R.string.account_register_no_consent, Toast.LENGTH_SHORT);
            toast.show();
            return false;
        }

        return true;
    }

    private boolean isValidEmail(String input) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(input).matches();
    }

    @Override
    public void onResponse(@NotNull Call<RegisterResponse> call, @NotNull Response<RegisterResponse> response) {
        if (response.isSuccessful()) {
            Toast.makeText(requireContext(), R.string.account_register_thanks, Toast.LENGTH_LONG).show();
            getParentFragmentManager().popBackStack();
        } else if (response.errorBody() != null) {
            try {
                Toast.makeText(requireContext(), response.errorBody().string(), Toast.LENGTH_LONG).show();
            } catch (IOException e) {
                Toast.makeText(requireContext(), e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onFailure(@NotNull Call<RegisterResponse> call, @NotNull Throwable t) {
        Toast.makeText(requireContext(), R.string.service_connect_error, Toast.LENGTH_LONG).show();
    }
}