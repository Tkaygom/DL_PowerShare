package com.firsthachathoners.powershare;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.snackbar.Snackbar;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    public static final String EXTRA_MESSAGE = "com.firsthachathoners.powershare.MESSAGE";
    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "foo@example.com:hello", "bar@example.com:world"
    };

    private AutoCompleteTextView mUsernameView;
    private EditText mPasswordView;
    private View mLoginFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mUsernameView = findViewById(R.id.username);
        mPasswordView = findViewById(R.id.password);
        mLoginFormView = findViewById(R.id.login_form);
    }

    public void startMapAct(View view) {
        String enteredUsername = mUsernameView.getText().toString();
        String enteredPassword = mPasswordView.getText().toString();

        // Check dummy credentials first
        if (isValidDummyCredential(enteredUsername, enteredPassword)) {
            Intent intent = new Intent(this, AccountActivity.class);
            intent.putExtra("USERNAME", enteredUsername);
            intent.putExtra("CREDIT_SESSION", "Demo Credit: 100");
            startActivity(intent);
            return; // Stop execution after successful dummy login
        }

        // Proceed with API authentication
        HTTPInterface httpInterface = APIClient.getClient().create(HTTPInterface.class);
        Call<UserResponse> newCall = httpInterface.getUserDetails(enteredUsername);

        newCall.enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    UserResponse userInfo = response.body();

                    Intent intent = new Intent(LoginActivity.this, AccountActivity.class);
                    intent.putExtra("USERNAME", userInfo.getUsername());
                    intent.putExtra("CREDIT_SESSION", userInfo.getCreditSession());
                    startActivity(intent);
                } else {
                    Snackbar.make(mLoginFormView, "Login Failed. Please check your credentials and try again.", Snackbar.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                Snackbar.make(view, "Network error, please try again", Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    private boolean isValidDummyCredential(String username, String password) {
        for (String credential : DUMMY_CREDENTIALS) {
            String[] parts = credential.split(":");
            if (parts.length == 2 && parts[0].equals(username) && parts[1].equals(password)) {
                return true;
            }
        }
        return false;
    }
}