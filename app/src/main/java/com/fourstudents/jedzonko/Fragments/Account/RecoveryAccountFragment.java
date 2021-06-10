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
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RecoveryAccountFragment extends Fragment {

    EditText usernameText;
    Button recoveryButton;

    JedzonkoService api;

    @VisibleForTesting()
    Toast toast;

    public RecoveryAccountFragment() {
        super(R.layout.fragment_account_recovery);
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
        toolbar.setTitle(R.string.title_register_recovery);
    }

    private void initViews(View view) {
        usernameText = view.findViewById(R.id.usernameText);
        recoveryButton = view.findViewById(R.id.recoveryButton);

        recoveryButton.setOnClickListener(v -> {
            if (isInputValid()) {
                String username = usernameText.getText().toString().trim();
                JsonObject object = new JsonObject();
                object.addProperty("username", username);
                Call<Void> call = api.recoveryUser(object);
                call.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(@NotNull Call<Void> call, @NotNull Response<Void> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(requireContext(), R.string.account_recovery_start, Toast.LENGTH_LONG).show();

                            ResetAccountFragment resetAccountFragment = new ResetAccountFragment();
                            Bundle recipeBundle = new Bundle();

                            recipeBundle.putString("username", username);
                            resetAccountFragment.setArguments(recipeBundle);

                            getParentFragmentManager().popBackStack();
                            getParentFragmentManager()
                                    .beginTransaction()
//                                    .remove(Objects.requireNonNull(getParentFragmentManager().findFragmentByTag("RecoveryAccountFragment")))
                                    .replace(R.id.mainFrameLayout, resetAccountFragment, "ResetAccountFragment")
                                    .addToBackStack("ResetAccountFragment")
                                    .commit();
                        } else if (response.errorBody() != null) {
                            if (response.code() == 404) {
                                Toast.makeText(requireContext(), R.string.account_recovery_not_found, Toast.LENGTH_LONG).show();
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
                        t.printStackTrace();
                        Toast.makeText(requireContext(), R.string.service_connect_error, Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

    }

    private boolean isInputValid() {
        String usernameInput = usernameText.getText().toString().trim();

        if (TextUtils.isEmpty(usernameInput)) {
            toast = Toast.makeText(requireContext(), R.string.missing_input_data, Toast.LENGTH_SHORT);
            toast.show();
            return false;
        }

        if (!isValidEmail(usernameInput)) {
            toast = Toast.makeText(requireContext(), R.string.account_register_email_not_valid, Toast.LENGTH_SHORT);
            toast.show();
            return false;
        }

        return true;
    }

    private boolean isValidEmail(String input) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(input).matches();
    }
}