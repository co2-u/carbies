package com.example.carbonfootprinttracker.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import butterknife.BindView;
import butterknife.ButterKnife;

import com.example.carbonfootprinttracker.R;
import com.example.carbonfootprinttracker.models.Carbie;
import com.parse.ParseException;
import com.parse.SaveCallback;

import java.util.ArrayList;

public class ConfirmationFragment extends Fragment {
    @BindView(R.id.etCarbieName)
    EditText etCarbieName;
    @BindView(R.id.btnConfirmNo)
    EditText btnConfirmNo;
    @BindView(R.id.btnConfirmYes)
    EditText btnConfirmYes;
    private final String TAG = "ConfirmationFragment";
    private Carbie carbie;

    public ConfirmationFragment(Carbie carbie) {
        super();
        this.carbie = carbie;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_confirmation_screen, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        String title = etCarbieName.getText().toString();
        //TODO make a new carbie in compose fragment??
        carbie.setTitle("title");
        carbie.setScore();
        btnConfirmNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToMainFragment();
            }
        });
        btnConfirmYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    carbie.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e != null) {
                            Log.d(TAG, "Error while saving");
                            e.printStackTrace();
                            return;
                        }
                        Log.d(TAG, "Success!");
                    }
                });
                goToMainFragment();
            }
        });
    }

    private void goToMainFragment() {
        Fragment fragment = new CurrentScoreFragment();
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.fragmentPlaceholder, fragment)
                .commit();
    }

}
