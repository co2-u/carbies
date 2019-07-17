package com.example.carbonfootprinttracker.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.carbonfootprinttracker.R;

public class ComposeFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Defines the xml file for the fragment
        return inflater.inflate(R.layout.fragment_mode_of_transportation, container, false);
    }

//    public void cancel(View view) {
//        Intent i = new Intent();
//        setResult(RESULT_CANCELED, i);
//        finish();
//    }

}
