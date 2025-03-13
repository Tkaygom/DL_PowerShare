package com.firsthachathoners.powershare;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class AccountActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        // Retrieve user details from intent
        String username = getIntent().getStringExtra("USERNAME");
        String creditSession = getIntent().getStringExtra("CREDIT_SESSION");

        // Find and set values in TextViews
        TextView usernameTextView = findViewById(R.id.text_username);
        TextView creditTextView = findViewById(R.id.text_credit);

        usernameTextView.setText("Username: " + (username != null ? username : "N/A"));
        creditTextView.setText("Credit Session: " + (creditSession != null ? creditSession : "N/A"));

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE);

        Button logoutButton = findViewById(R.id.btn_logout);
        Button backButton = findViewById(R.id.btn_back);

        // Logout action
        logoutButton.setOnClickListener(view -> {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("isLoggedIn", false);
            editor.apply();

            Intent intent = new Intent(AccountActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish(); // Close AccountActivity
        });

        // Back action to MapsActivity
        backButton.setOnClickListener(view -> {
            Intent intent = new Intent(AccountActivity.this, MapsActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish(); // Close current activity
        });
    }
}