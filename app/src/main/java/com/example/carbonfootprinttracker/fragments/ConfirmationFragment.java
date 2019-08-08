package com.example.carbonfootprinttracker.fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.carbonfootprinttracker.R;
import com.example.carbonfootprinttracker.models.Carbie;
import com.google.android.material.textfield.TextInputLayout;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.SaveCallback;

import java.text.DecimalFormat;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ConfirmationFragment extends Fragment {
    private static final DecimalFormat df = new DecimalFormat("0.00");
    private static final String TAG = "ConfirmationFragment";

    @BindView(R.id.tvStartPoint2) TextView tvStartPoint2;
    @BindView(R.id.tvEndPoint2) TextView tvEndPoint2;
    @BindView(R.id.tvMode2) TextView tvMode2;
    @BindView(R.id.btnConfirmNo) Button btnConfirmNo;
    @BindView(R.id.btnConfirmYes) Button btnConfirmYes;
    @BindView(R.id.ivMapSnapshot) ImageView ivMapSnapshot;
    @BindView(R.id.etConfirmName) TextInputLayout etConfirmName;
    @BindView (R.id.progressBar3) ProgressBar pbLoading;
    @BindView(R.id.tvDistance) TextView tvDistance;
    @BindView(R.id.tvDuration) TextView tvDuration;

    private Carbie carbie;
    private ParseFile photoFile;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_confirmation_screen, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        try {
            carbie = getArguments().getParcelable("carbie");
            carbie.setScore();
            carbie.setUser();
            tvStartPoint2.setText(carbie.getStartLocation());
            tvEndPoint2.setText(carbie.getEndLocation());
            tvMode2.setText(carbie.getTransportation());
            tvDistance.setText(df.format(carbie.getDistance()) + " miles");
            tvDuration.setText(formatSeconds(carbie.getTripLength()));
        } catch (NullPointerException e) {
            Log.d(TAG, "Carbie not passed into ConfirmationFragment");
            e.printStackTrace();
            return;
        }

        try {
            byte[] byteArray = getArguments().getByteArray("snapshot");
            photoFile = new ParseFile("map_screenshot.png", byteArray);
            Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
            ivMapSnapshot.setImageBitmap(bitmap);
        } catch (NullPointerException e) {
            Log.d(TAG, "Map Snapshot not passed into ConfirmationFragment");
            e.printStackTrace();
            return;
        }

        btnConfirmNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToMainFragment();
            }
        });

        btnConfirmYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(etConfirmName.getEditText().getText().toString().isEmpty()) {
                    Toast.makeText(getContext(), "Please enter a title!", Toast.LENGTH_LONG).show();
                } else {
                    showProgressBar();
                    photoFile.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                carbie.setMapShot(photoFile);
                                carbie.setIsFavorited(false);
                                carbie.setIsDeleted(false);
                                carbie.setTitle(etConfirmName.getEditText().getText().toString());
                                carbie.saveInBackground(new SaveCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        if (e == null) {
                                            Log.d(TAG, "Successfully saved carbie with map snapshot.");
                                            hideProgressBar();
                                            goToMainFragment();
                                        } else {
                                            Log.d(TAG, "Error while saving carbie.");
                                            e.printStackTrace();
                                        }
                                    }
                                });
                            } else {
                                Log.d(TAG, "Error while saving photo file.");
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }
        });
    }

    private String typeOfTransport() {
        String transport = "";
        switch (carbie.getTransportation()) {
            case "SmallCar":
                transport = "d";
                break;
            case "MediumCar":
                transport = "d";
                break;
            case "LargeCar":
                transport = "d";
                break;
            case "Hybrid":
                transport = "d";
                break;
            case "Renewable":
                transport = "d";
                break;
            case "FossilFuel":
                transport = "d";
                break;
            case "Bus":
                transport = "d";
                break;
            case "Rail":
                transport = "d";
                break;
            case "Bike":
                transport = "b";
                break;
            case "Walk":
                transport = "w";
                break;
            case "Rideshare":
                transport = "d";
                break;
        }
        return transport;
    }

    private void goToMainFragment() {
        getActivity().findViewById(R.id.currentScoreTab).performClick();
    }

    @Override
    public void onResume() {
        super.onResume();
        AppCompatActivity mainActivity = (AppCompatActivity) getActivity();
        mainActivity.findViewById(R.id.tvName).setVisibility(TextView.GONE);
        mainActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mainActivity.findViewById(R.id.bottomNavigation).setVisibility(TextView.GONE);
        mainActivity.getSupportActionBar().setDisplayShowTitleEnabled(true);
        mainActivity.getSupportActionBar().setTitle("Confirm Details");
    }

    @Override
    public void onStop() {
        super.onStop();
        AppCompatActivity mainActivity = (AppCompatActivity) getActivity();
        mainActivity.findViewById(R.id.tvName).setVisibility(TextView.VISIBLE);
        mainActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        mainActivity.findViewById(R.id.bottomNavigation).setVisibility(TextView.VISIBLE);
        mainActivity.getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    private void showProgressBar() {
        pbLoading.setVisibility(ProgressBar.VISIBLE);
    }

    private void hideProgressBar() {
        pbLoading.setVisibility(ProgressBar.GONE);
    }

    private String formatSeconds(Long seconds) {
        long p1 = seconds % 60;
        long p2 = seconds / 60;
        long p3 = p2 % 60;
        p2 = p2 / 60;

        String p1s = "" + p1;
        String p2s = "" + p2;
        String p3s = "" + p3;

        if (p1 < 10) { p1s = "0" + p1; }
        if (p2 < 10) { p2s = "0" + p2; }
        if (p3 < 10) { p3s = "0" + p3; }

        String res = p2s + ":" + p3s + ":" + p1s;
        return res;
    }
}
