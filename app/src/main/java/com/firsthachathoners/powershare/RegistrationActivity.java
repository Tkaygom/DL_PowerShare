package com.firsthachathoners.powershare;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.snackbar.Snackbar;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegistrationActivity extends AppCompatActivity {
    private EditText etUsername, etEmail, etPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        // Initialize views
        etUsername = findViewById(R.id.et_username);
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        Button btnRegister = findViewById(R.id.btn_register);
        TextView tvLoginLink = findViewById(R.id.tv_login_link);

        // Handle registration button click
        btnRegister.setOnClickListener(v -> registerUser());

        // Navigate back to login
        tvLoginLink.setOnClickListener(v -> {
            startActivity(new Intent(RegistrationActivity.this, LoginActivity.class));
            finish();
        });
    }

    private void registerUser() {
        String username = etUsername.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // Basic validation
        if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            showSnackbar("All fields are required");
            return;
        }

        // Create registration request
        RegistrationRequest request = new RegistrationRequest();
        request.setUsername(username);
        request.setEmail(email);
        request.setPassword(password);

        // Call API
        HTTPInterface api = APIClient.getClient().create(HTTPInterface.class);
        Call<Void> call = api.register(request);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    showSnackbar("Account created successfully!");
                    navigateToLogin();
                } else {
                    showSnackbar("Registration failed");
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                showSnackbar("Network error: " + t.getMessage());
            }
        });
    }

    private void showSnackbar(String message) {
        Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_SHORT).show();
    }

    private void navigateToLogin() {
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }
}