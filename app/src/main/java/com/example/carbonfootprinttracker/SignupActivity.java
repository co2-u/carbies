package com.example.carbonfootprinttracker;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SignupActivity extends AppCompatActivity {
    private static final String TAG = "SignupActivity";

    @BindView(R.id.etUsername) EditText etUsername;
    @BindView(R.id.etPassword) EditText etPassword;
    @BindView(R.id.etEmail) EditText etEmail;
    @BindView(R.id.etConfirmPassword) EditText etConfirmPassword;
    @BindView(R.id.btnSignup) Button btnSignup;
    @BindView(R.id.progressBar4) ProgressBar pbLoading;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        ButterKnife.bind(this);

    }

    @OnClick(R.id.btnSignup)
    public void signup() {
        pbLoading.setVisibility(View.VISIBLE);

        final String username = etUsername.getText().toString();
        final String password = etPassword.getText().toString();
        final String email = etEmail.getText().toString();
        final String confirmPassword = etConfirmPassword.getText().toString();

        if (username.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Missing Username!", Toast.LENGTH_SHORT).show();
        } else if (email.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Missing Email!", Toast.LENGTH_SHORT).show();
        } else if (!email.matches("[^@]+@[^\\.]+\\..+")) {
            Toast.makeText(getApplicationContext(), "Invalid Email Address!", Toast.LENGTH_SHORT).show();
        } else if(password.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Missing Password!", Toast.LENGTH_SHORT).show();
        } else if (password.contentEquals(confirmPassword)) {
            ParseUser user = new ParseUser();
            user.setUsername(username);
            user.setEmail(email);
            user.setPassword(password);

            user.signUpInBackground(new SignUpCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        Log.d(TAG, "Sign up successful!");
                        Intent i = new Intent(SignupActivity.this, OnBoardingActivity.class);
                        startActivity(i);
                        finish();
                    } else {
                        Log.d(TAG, "Sign up not successful");
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                        etPassword.setText("");
                        etConfirmPassword.setText("");
                        e.printStackTrace();
                    }
                }
            });
        } else {
            Toast.makeText(this, "Passwords don't match!", Toast.LENGTH_LONG).show();
            etPassword.setText("");
            etConfirmPassword.setText("");
        }
        pbLoading.setVisibility(View.GONE);
    }

    @Override
    public void onBackPressed() { // Return to LoginActivity when back button pressed
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}



