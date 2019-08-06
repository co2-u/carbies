package com.example.carbonfootprinttracker.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.bumptech.glide.Glide;
import com.example.carbonfootprinttracker.ChangeProfilePictureActivity;
import com.example.carbonfootprinttracker.LoginActivity;
import com.example.carbonfootprinttracker.MainActivity;
import com.example.carbonfootprinttracker.R;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SettingsFragment extends Fragment {

    private static final String TAG = "SettingsFragment";

    @BindView(R.id.btLogout) public Button btLogout;
    @BindView(R.id.btChangeUsername) public Button btChangeUsername;
    @BindView(R.id.btChangeEmail) public Button btChangeEmail;
    @BindView(R.id.btChangePassword) public Button btChangePassword;
    @BindView(R.id.btnMoreInfo) public Button btnMoreInfo;
    @BindView(R.id.tvUsername) public TextView tvUsername;
    @BindView(R.id.ivCircleProfile) public ImageView ivProfileImage;
    @BindView(R.id.switchUserPrivacy) public Switch switchPrivacy;
    @BindView(R.id.progressBar5) public ProgressBar pbLoading;

    private FragmentManager fragmentManager;
    private ParseUser user;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings, parent, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        fragmentManager = getFragmentManager();
        user = ParseUser.getCurrentUser();
        tvUsername.setText(user.getUsername());
        Log.d(TAG, Boolean.toString(ParseUser.getCurrentUser().getParseFile("profileImage") == null) );

        if (ParseUser.getCurrentUser().getParseFile("profileImage") != null) {
            Glide.with(getContext()).load(ParseUser.getCurrentUser().getParseFile("profileImage").getUrl()).dontAnimate().into(ivProfileImage);
        }

        if (user.getBoolean("isPrivate")) {
            switchPrivacy.setChecked(true);
        } else {
            switchPrivacy.setChecked(false);
        }

        ParseFile photoFile = user.getParseFile("profileImage");
        if (photoFile != null) {
            String preUrl = photoFile.getUrl();
            String completeURL = preUrl.substring(0, 4) + "s" + preUrl.substring(4, preUrl.length());
            Glide.with(getContext())
                    .load(completeURL)
                    .into(ivProfileImage);
        } else {
            ivProfileImage.setBackground(getContext().getResources().getDrawable(R.drawable.ic_account_circle));
        }

        btLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Logged out.");
                ParseUser.logOut();
                ((MainActivity)getActivity()).cancelAlarm();
                Intent intent = new Intent(getContext(), LoginActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });

        btChangeUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               showChangeUsernameDialog();
            }
        });

        btChangeEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showChangeEmailDialogFragment();
            }
        });

        btChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showChangePasswordDialogFragment();
            }
        });

        ivProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showChangeProfilePictureActivity();
            }
        });

        btnMoreInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentManager.beginTransaction().replace(R.id.fragmentPlaceholder, new InfoFragment()).addToBackStack("SettingsFragment").commit();
            }
        });

        switchPrivacy.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                pbLoading.setVisibility(View.VISIBLE);
                ParseUser.getCurrentUser().put("isPrivate", isChecked);
                ParseUser.getCurrentUser().saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        pbLoading.setVisibility(View.INVISIBLE);
                        if (e != null) {
                            Log.d(TAG, "failed to switch user privacy");
                            e.printStackTrace();
                            switchPrivacy.setChecked(!isChecked);
                        } else {
                            Log.d(TAG, "Successfully switched user privacy");
                        }
                    }
                });
            }
        });
    }

    private void showChangeEmailDialogFragment() {
        ChangeEmailDialogFragment emailDialogFragment = new ChangeEmailDialogFragment();
        emailDialogFragment.show(fragmentManager, "email_dialog");
    }

    private void showChangeUsernameDialog() {
        ChangeUsernameDialogFragment usernameDialogFragment = new ChangeUsernameDialogFragment();
        usernameDialogFragment.show(fragmentManager, "username_dialog");
    }

    private void showChangePasswordDialogFragment() {
        ChangePasswordDialogFragment changePasswordDialogFragment = new ChangePasswordDialogFragment();
        changePasswordDialogFragment.show(fragmentManager, "password_dialog");
    }

    private void showChangeProfilePictureActivity() {
        Intent intent = new Intent(getContext(), ChangeProfilePictureActivity.class);
        startActivity(intent);
    }
}
