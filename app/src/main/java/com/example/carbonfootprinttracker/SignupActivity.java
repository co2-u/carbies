package com.example.carbonfootprinttracker;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
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
    private static final String TAG = "MainActivty";

    @BindView(R.id.etUsername) EditText etUsername;
    @BindView(R.id.etPassword) EditText etPassword;
    @BindView(R.id.etEmail) EditText etEmail;
    @BindView(R.id.etConfirmPassword) EditText etConfirmPassword;
    @BindView(R.id.btnSignup) Button btnSignup;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        ButterKnife.bind(this);

    }

    @OnClick(R.id.btnSignup)
    public void signup() {

        final String username = etUsername.getText().toString();
        final String password = etPassword.getText().toString();
        final String email = etEmail.getText().toString();
        final String confirmPassword = etConfirmPassword.getText().toString();

        //check if username is empty, check if email is empty
        if (username.isEmpty()){
            return;}
        if (email.isEmpty()){
            return;}

        if (password.contentEquals(confirmPassword)) {
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
                        e.printStackTrace();
                    }
                }
            });
        } else {
            Toast.makeText(this, "Passwords don't match. Please try again", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onBackPressed() { // Return to LoginActivity when back button pressed
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}



