package com.firsthachathoners.powershare;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class AccountActivity extends AppCompatActivity {
    public static final String PREFS_NAME = "UserPrefs";
    public static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    public static final String KEY_USERNAME = "username";
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        // Retrieve user details from intent
        String username = getIntent().getStringExtra("USERNAME");
        String fullName = getIntent().getStringExtra("FULL_NAME");
        String email = getIntent().getStringExtra("EMAIL");
        String phoneNumber = getIntent().getStringExtra("PHONE_NUMBER");
        String credit = getIntent().getStringExtra("CREDIT");

        // Find and set values in TextViews
        TextView usernameTextView = findViewById(R.id.text_username);
        TextView fullNameTextView = findViewById(R.id.text_full_name);
        TextView emailTextView = findViewById(R.id.text_email);
        TextView phoneNumberTextView = findViewById(R.id.text_phone_number);
        TextView creditTextView = findViewById(R.id.text_credit);

        // Set values for each TextView, using "N/A" as fallback if data is missing
        usernameTextView.setText("Username: " + (username != null ? username : "N/A"));
        fullNameTextView.setText("Full Name: " + (fullName != null ? fullName : "N/A"));
        emailTextView.setText("Email: " + (email != null ? email : "N/A"));
        phoneNumberTextView.setText("Phone Number: " + (phoneNumber != null ? phoneNumber : "N/A"));
        creditTextView.setText("Credit: " + (credit != null ? credit : "N/A"));

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);

        Button logoutButton = findViewById(R.id.btn_logout);
        Button backButton = findViewById(R.id.btn_back);

        // Logout action
        logoutButton.setOnClickListener(view -> {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear(); // Removes all keys (isLoggedIn, username, etc.)
            editor.apply();

            Intent intent = new Intent(AccountActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish(); // Close AccountActivity
        });

        // Back action to MapsActivity
        backButton.setOnClickListener(view -> {
            finish();
        }); // Simpler if MapsActivity is the parent
    }
}