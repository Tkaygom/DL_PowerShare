package com.firsthachathoners.powershare;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
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
    public static final String PREFS_NAME = "UserPrefs";
    public static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    public static final String KEY_USERNAME = "username";
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

        // Check if user is already logged in
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String loggedInUsername = sharedPreferences.getString("USERNAME", null);

        if (loggedInUsername != null) {
            // Redirect to AccountActivity
            Intent accountIntent = new Intent(LoginActivity.this, AccountActivity.class);
            accountIntent.putExtra("USERNAME", loggedInUsername);
            accountIntent.putExtra("CREDIT_SESSION", sharedPreferences.getString("CREDIT_SESSION", "No Credit Info"));
            startActivity(accountIntent);
            finish();
            return;
        }

        Button loginButton = findViewById(R.id.email_sign_in_button);
        loginButton.setOnClickListener(view -> {
            String enteredUsername = mUsernameView.getText().toString();
            String enteredPassword = mPasswordView.getText().toString();

            // Check dummy credentials
            if (isValidDummyCredential(enteredUsername, enteredPassword)) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("isLoggedIn", true);
                editor.putString("username", enteredUsername);
                editor.putString("CREDIT_SESSION", "Demo Credit: 100");
                editor.apply(); // Save to SharedPreferences

                Intent intentToAccount = new Intent(LoginActivity.this, AccountActivity.class);
                startActivity(intentToAccount);
                finish();
                return;
            }

            // Proceed with API authentication
            HTTPInterface httpInterface = APIClient.getClient().create(HTTPInterface.class);
            Call<UserResponse> call = httpInterface.login(enteredUsername, enteredPassword);

            call.enqueue(new Callback<UserResponse>() {
                @Override
                public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        UserResponse userInfo = response.body();

                        // Save login details in SharedPreferences
                        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putBoolean("isLoggedIn", true);
                        editor.putString("username", userInfo.getUsername());
                        editor.putString("CREDIT_SESSION", userInfo.getCreditSession());
                        editor.apply();

                        // Redirect to AccountActivity
                        Intent intentToAccount = new Intent(LoginActivity.this, AccountActivity.class);
                        intentToAccount.putExtra("USERNAME", userInfo.getUsername());
                        intentToAccount.putExtra("CREDIT_SESSION", userInfo.getCreditSession());
                        startActivity(intentToAccount);
                        finish();
                    } else {
                        Snackbar.make(mLoginFormView, "Login Failed. Please check your credentials and try again.", Snackbar.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<UserResponse> call, Throwable t) {
                    Snackbar.make(view, "Network error, please try again", Snackbar.LENGTH_SHORT).show();
                }
            });
        });
    }




    public void startMapAct(View view) {
        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);
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