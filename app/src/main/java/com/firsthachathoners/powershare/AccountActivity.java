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
        String creditSession = getIntent().getStringExtra("CREDIT_SESSION");

        // Find and set values in TextViews
        TextView usernameTextView = findViewById(R.id.text_username);
        TextView creditTextView = findViewById(R.id.text_credit);

        usernameTextView.setText("Username: " + (username != null ? username : "N/A"));
        creditTextView.setText("Credit Session: " + (creditSession != null ? creditSession : "N/A"));

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE); // Match MapsActivity

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
