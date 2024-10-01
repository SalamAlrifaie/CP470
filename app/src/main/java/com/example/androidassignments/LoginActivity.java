package com.example.androidassignments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.widget.Button;
import android.widget.EditText;
import android.view.View;
import android.content.Intent;

import android.util.Patterns;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private static final String PREFS_NAME = "MyPrefsFile";
    private static final String EMAIL_KEY = "DefaultEmail";
    private EditText emailField;
    private Button loginButton;
    private EditText passwordField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        // uncomment the following line for debugging (toast msg)
        //print(getString(R.string.LoginActivityStart));

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        emailField = findViewById(R.id.emailField);
        loginButton = findViewById(R.id.loginButton);
        passwordField = findViewById(R.id.passwordField);

        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String storedEmail = sharedPreferences.getString(EMAIL_KEY, "email@domain.com");
        emailField.setText(storedEmail);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailField.getText().toString();
                String password = passwordField.getText().toString();

                if (!isValidEmail(email)) {
                    Toast.makeText(LoginActivity.this, getString(R.string.invalidEmail), Toast.LENGTH_SHORT).show();
                    return;
                }

                if (password.isEmpty()) {
                    Toast.makeText(LoginActivity.this, getString(R.string.emptyPassword), Toast.LENGTH_SHORT).show();
                    return;
                }

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(EMAIL_KEY, email);
                editor.commit();

                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    public void print(String message) {
        Toast toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
        toast.show();
    }

    private boolean isValidEmail(CharSequence email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "onResume called in LoginActivity");
    }
    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG, "onStart called in LoginActivity");
    }
    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG, "onPause called in LoginActivity");
    }
    @Override
    protected void onStop() {
        super.onStop();
        Log.i(TAG, "onStop called in LoginActivity");
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy called in LoginActivity");
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.i(TAG, "onSaveInstanceState called in LoginActivity");
    }
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Log.i(TAG, "onRestoreInstanceState called in LoginActivity");
    }
}