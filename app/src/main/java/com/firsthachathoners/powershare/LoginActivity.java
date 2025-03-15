package com.firsthachathoners.powershare;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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
    public static final String PREFS_NAME = "UserPrefs";
    public static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    public static final String KEY_USERNAME = "username";

    private AutoCompleteTextView mUsernameView;
    private EditText mPasswordView;
    private View mLoginFormView;
    private TextView tvRegisterLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize views
        mUsernameView = findViewById(R.id.username);
        mPasswordView = findViewById(R.id.password);
        mLoginFormView = findViewById(R.id.login_form);
        tvRegisterLink = findViewById(R.id.tv_register_link);
        Button loginButton = findViewById(R.id.email_sign_in_button);

        // Check existing login
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String loggedInUsername = sharedPreferences.getString("USERNAME", null);

        if (loggedInUsername != null) {
            redirectToAccount(loggedInUsername, sharedPreferences);
            return;
        }

        // Setup register link
        tvRegisterLink.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegistrationActivity.class));
            finish();
        });

        // Login button handler
        loginButton.setOnClickListener(view -> handleLogin(sharedPreferences));
    }

    private void handleLogin(SharedPreferences sharedPreferences) {
        String enteredUsername = mUsernameView.getText().toString();
        String enteredPassword = mPasswordView.getText().toString();

        if (isValidDummyCredential(enteredUsername, enteredPassword)) {
            saveDummyCredentials(sharedPreferences, enteredUsername);
            redirectToAccount(enteredUsername, sharedPreferences);
            return;
        }

        // API authentication
        HTTPInterface httpInterface = APIClient.getClient().create(HTTPInterface.class);
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername(enteredUsername);
        loginRequest.setPassword(enteredPassword);

        Call<UserResponse> call = httpInterface.login(loginRequest);
        call.enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    handleSuccessfulLogin(response.body());
                } else {
                    Snackbar.make(mLoginFormView, "Login Failed", Snackbar.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                Snackbar.make(mLoginFormView, "Network error", Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    private void redirectToAccount(String username, SharedPreferences prefs) {
        Intent intent = new Intent(this, AccountActivity.class);
        intent.putExtra("USERNAME", username);
        intent.putExtra("CREDIT_SESSION", prefs.getString("CREDIT_SESSION", "No Credit Info"));
        startActivity(intent);
        finish();
    }

    private void saveDummyCredentials(SharedPreferences prefs, String username) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("isLoggedIn", true);
        editor.putString("username", username);
        editor.putString("CREDIT_SESSION", "Demo Credit: 100");
        editor.apply();
    }

    private void handleSuccessfulLogin(UserResponse userInfo) {
        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("isLoggedIn", true);
        editor.putString("username", userInfo.getUsername());
        editor.putString("CREDIT_SESSION", userInfo.getCreditSession());
        editor.apply();

        Intent intentToAccount = new Intent(LoginActivity.this, AccountActivity.class);
        intentToAccount.putExtra("USERNAME", userInfo.getUsername());
        intentToAccount.putExtra("CREDIT_SESSION", userInfo.getCreditSession());
        startActivity(intentToAccount);
        finish();
    }

    public void startMapAct(View view) {
        startActivity(new Intent(this, MapsActivity.class));
    }

    private boolean isValidDummyCredential(String username, String password) {
        for (String credential : DUMMY_CREDENTIALS) {
            String[] pieces = credential.split(":");
            if (pieces[0].equals(username) && pieces[1].equals(password)) {
                return true;
            }
        }
        return false;
    }
}