package com.example.carbonfootprinttracker.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.carbonfootprinttracker.MainActivity;
import com.example.carbonfootprinttracker.R;
import com.parse.ParseUser;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SettingsFragment extends Fragment {

    private static final String TAG = "SettingsFragment";

    @BindView(R.id.btLogout) public Button btLogout;
    @BindView(R.id.btMapTest) public Button btMapTest;

    private FragmentManager fragmentManager;

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

        btLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseUser.logOut();
                Log.d(TAG, "Logged out successfully");
                Intent intent = new Intent(getContext(), MainActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });

        btMapTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment mapFragment = new RouteFragment();
                fragmentManager.beginTransaction().replace(R.id.fragmentPlaceholder, mapFragment).commit();
            }
        });
    }
}
