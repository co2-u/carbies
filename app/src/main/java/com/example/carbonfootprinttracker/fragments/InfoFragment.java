package com.example.carbonfootprinttracker.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.carbonfootprinttracker.MainActivity;
import com.example.carbonfootprinttracker.R;

public class InfoFragment extends Fragment {
    private static final String TAG = "InfoFragment";
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Defines the xml file for the fragment
        return inflater.inflate(R.layout.fragment_information, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        MainActivity mainActivity = (MainActivity) getActivity();
        mainActivity.setSettingsTabVisibility(true);
    }

    @Override
    public void onStop() {
        super.onStop();
        MainActivity mainActivity = (MainActivity) getActivity();
        mainActivity.setSettingsTabVisibility(false);
    }
}

