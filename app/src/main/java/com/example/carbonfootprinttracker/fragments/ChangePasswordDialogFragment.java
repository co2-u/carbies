package com.example.carbonfootprinttracker.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.example.carbonfootprinttracker.R;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ChangePasswordDialogFragment extends AppCompatDialogFragment {
    private static final String TAG = "ChangeEmailDialog";

    @BindView(R.id.etNewProperty) EditText etNewPassword;
    @BindView(R.id.etReenterNewProperty) EditText etReenterNewPassword;
    @BindView(R.id.btAccept) Button btAccept;
    @BindView(R.id.progressBar) ProgressBar progressBar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_password_dialog, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        btAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String newPassword = etNewPassword.getText().toString();
                final String reenterNewPassword = etReenterNewPassword.getText().toString();

                if (newPassword.isEmpty()) {
                    clearPasswords();
                    Toast.makeText(getContext(), "Missing new password", Toast.LENGTH_SHORT).show();
                } else if (!newPassword.equals(reenterNewPassword)) {
                    clearPasswords();
                    Toast.makeText(getContext(), "New passwords don't match.", Toast.LENGTH_SHORT).show();
                } else {
                    progressBar.setVisibility(ProgressBar.VISIBLE);
                    changePassword(newPassword);
                }
            }
        });

    }

    private void clearPasswords() {
        etNewPassword.setText("");
        etReenterNewPassword.setText("");
    }

    private void changePassword(String newPassword) {
        ParseUser user = ParseUser.getCurrentUser();
        user.setPassword(newPassword);
        user.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    progressBar.setVisibility(ProgressBar.GONE);
                    dismiss();
                    Log.d(TAG, "Successfully saved new password.");
                } else {
                    Log.d(TAG, "Error while saving new password.");
                    e.printStackTrace();
                }
            }
        });
    };
}
