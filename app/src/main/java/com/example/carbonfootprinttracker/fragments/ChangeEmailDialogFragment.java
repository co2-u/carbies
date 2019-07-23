package com.example.carbonfootprinttracker.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.example.carbonfootprinttracker.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ChangeEmailDialogFragment extends AppCompatDialogFragment {
    private static final String TAG = "ChangeEmailDialog";

    @BindView(R.id.etNewProperty) EditText etNewEmail;
    @BindView(R.id.btAccept) Button btAccept;
    @BindView(R.id.btCancel) Button btCancel;
    @BindView(R.id.tvCurrentProperty) TextView tvCurrentEmail;
    @BindView(R.id.progressBar) ProgressBar progressBar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_email_dialog, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        btAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String newEmail = etNewEmail.getText().toString();
                if (newEmail.isEmpty()) {
                    etNewEmail.setText("");
                    Toast.makeText(getContext(), "Missing new email", Toast.LENGTH_SHORT).show();
                } else if (newEmail.equals(ParseUser.getCurrentUser().getEmail())) {
                    etNewEmail.setText("");
                    Toast.makeText(getContext(), "New email must not be current email", Toast.LENGTH_SHORT).show();
                } else {
                    progressBar.setVisibility(ProgressBar.VISIBLE);
                    checkEmailAvailability(newEmail);
                }
            }
        });

        btCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        tvCurrentEmail.setText("Email: " + ParseUser.getCurrentUser().getEmail());
    }

    private void checkEmailAvailability(String newEmail) {
        ParseQuery<ParseUser> query = ParseQuery.getQuery(ParseUser.class);
        query.whereEqualTo("email", newEmail);
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                if (e == null) {
                    if (objects.size() > 0) {
                        progressBar.setVisibility(ProgressBar.GONE);
                        Toast.makeText(getContext(), "Email already exists", Toast.LENGTH_SHORT).show();
                    } else {
                        changeEmail(newEmail);
                    }
                } else {
                    Log.d(TAG, "Error while querying emails.");
                }
            }
        });
    }

    private void changeEmail(String newEmail) {
        ParseUser user = ParseUser.getCurrentUser();
        user.setEmail(newEmail);
        user.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    progressBar.setVisibility(ProgressBar.GONE);
                    dismiss();
                    getFragmentManager().beginTransaction().replace(R.id.fragmentPlaceholder, new SettingsFragment()).commit();
                    Log.d(TAG, "Successfully saved new email.");
                } else {
                    Log.d(TAG, "Error while saving new email.");
                    e.printStackTrace();
                }
            }
        });
    };
}
