package com.fourstudents.jedzonko.Fragments.Account;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.fourstudents.jedzonko.MainActivity;
import com.fourstudents.jedzonko.Network.JedzonkoService;
import com.fourstudents.jedzonko.R;
import com.google.gson.JsonObject;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ResetAccountFragment extends Fragment {

    EditText usernameText;
    EditText passwordText;
    EditText passwordText2;
    EditText tokenText;
    Button changePasswordButton;

    JedzonkoService api;

    @VisibleForTesting()
    Toast toast;

    public ResetAccountFragment() {
        super(R.layout.fragment_account_reset);
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
        toolbar.setTitle(R.string.title_reset_account);
    }

    private void initViews(View view) {
        usernameText = view.findViewById(R.id.usernameText);
        passwordText = view.findViewById(R.id.passwordText);
        passwordText2 = view.findViewById(R.id.passwordText2);
        tokenText = view.findViewById(R.id.tokenText);
        changePasswordButton = view.findViewById(R.id.changePasswordButton);

        if (getArguments() != null) {
            usernameText.setText(getArguments().getString("username"));
        } else {
            getParentFragmentManager().popBackStack();
        }

        changePasswordButton.setOnClickListener(v -> {
            if (isInputValid()) {
                JsonObject object = new JsonObject();
                object.addProperty("username", usernameText.getText().toString().trim());
                object.addProperty("password", passwordText.getText().toString().trim());
                object.addProperty("token", tokenText.getText().toString().trim());
                Call<Void> call = api.resetUser(object);
                call.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(@NotNull Call<Void> call, @NotNull Response<Void> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(requireContext(), R.string.account_reset_success, Toast.LENGTH_LONG).show();
                            getParentFragmentManager().popBackStack();
                        } else if (response.errorBody() != null) {
                            if (response.code() == 403) {
                                Toast.makeText(requireContext(), R.string.account_reset_wrong_token, Toast.LENGTH_LONG).show();
                                return;
                            }
                            try {
                                Toast.makeText(requireContext(), response.errorBody().string(), Toast.LENGTH_LONG).show();
                            } catch (IOException e) {
                                Toast.makeText(requireContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(@NotNull Call<Void> call, @NotNull Throwable t) {
                        Toast.makeText(requireContext(), R.string.service_connect_error, Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

    }

    private boolean isInputValid() {
        String usernameInput = usernameText.getText().toString().trim();
        String passwordInput = passwordText.getText().toString().trim();
        String password2Input = passwordText2.getText().toString().trim();
        String tokenInput = tokenText.getText().toString().trim();

        if (TextUtils.isEmpty(usernameInput) || TextUtils.isEmpty(passwordInput) || TextUtils.isEmpty(password2Input) || TextUtils.isEmpty(tokenInput)) {
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
        return true;
    }

    private boolean isValidEmail(String input) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(input).matches();
    }
}