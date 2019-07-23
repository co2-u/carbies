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

public class ChangeUsernameDialogFragment extends AppCompatDialogFragment {
    private static final String TAG = "ChangeUsernameDialog";

    @BindView(R.id.etNewEmail) EditText etNewUsername;
    @BindView(R.id.btAccept) Button btAccept;
    @BindView(R.id.btCancel) Button btCancel;
    @BindView(R.id.tvCurrentEmail) TextView tvCurrentUsername;
    @BindView(R.id.progressBar) ProgressBar progressBar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_username_dialog, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        btAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newUsername = etNewUsername.getText().toString();
                if (newUsername.isEmpty()) {
                    Toast.makeText(getContext(), "Missing new username", Toast.LENGTH_SHORT).show();
                } else {
                    progressBar.setVisibility(ProgressBar.VISIBLE);
                    checkUsernameAvailability(newUsername);
                }
            }
        });

        btCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        tvCurrentUsername.setText("Current Username: " + ParseUser.getCurrentUser().getUsername());
    }

    private void checkUsernameAvailability(String newUsername) {
        ParseQuery<ParseUser> query = ParseQuery.getQuery(ParseUser.class);
        query.whereEqualTo("username", newUsername);
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                if (e == null) {
                    if (objects.size() > 0) {
                        progressBar.setVisibility(ProgressBar.GONE);
                        Toast.makeText(getContext(), "Username already exists", Toast.LENGTH_SHORT).show();
                    } else {
                        changeUsername(newUsername);
                    }
                } else {
                    Log.d(TAG, "Error while querying usernames.");
                }
            }
        });
    }

    private void changeUsername(String newUsername) {
        ParseUser user = ParseUser.getCurrentUser();
        user.setUsername(newUsername);
        user.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    progressBar.setVisibility(ProgressBar.GONE);
                    dismiss();
                    getFragmentManager().beginTransaction().replace(R.id.fragmentPlaceholder, new SettingsFragment()).commit();
                } else {
                    Log.d(TAG, "Error while saving new username.");
                    e.printStackTrace();
                }
            }
        });
    };
}
