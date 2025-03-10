package com.firsthachathoners.powershare;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class AccountActivity extends AppCompatActivity {
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
    }
}