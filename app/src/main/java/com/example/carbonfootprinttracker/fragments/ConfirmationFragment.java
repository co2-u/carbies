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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.carbonfootprinttracker.R;
import com.example.carbonfootprinttracker.models.Carbie;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.SaveCallback;

import java.text.DecimalFormat;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ConfirmationFragment extends Fragment {
    @BindView(R.id.tvStartPoint2)
    TextView tvStartPoint2;
    @BindView(R.id.tvEndPoint2)
    TextView tvEndPoint2;
    @BindView(R.id.tvMode2)
    TextView tvMode2;
    @BindView(R.id.btnConfirmNo)
    Button btnConfirmNo;
    @BindView(R.id.btnConfirmYes)
    Button btnConfirmYes;
    @BindView(R.id.btnYesAndGo)
    Button btnYesAndGo;
    @BindView(R.id.ivMapSnapshot)
    ImageView ivMapSnapshot;
    @BindView (R.id.tvName2)
    TextView tvName2;
    @BindView (R.id.progressBar3)
    ProgressBar pbLoading;
    @BindView(R.id.tvDistance)
    TextView tvDistance;

    private static final DecimalFormat df = new DecimalFormat("0.00");
    private static final String TAG = "ConfirmationFragment";

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
            tvName2.setText(carbie.getTitle());
            tvDistance.setText(df.format(carbie.getDistance()) + " miles");
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
                showProgressBar();
                photoFile.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            carbie.setMapShot(photoFile);
                            carbie.setIsFavorited(false);
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
        });

        btnYesAndGo.setOnClickListener(new View.OnClickListener() {
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
                Uri gmmIntentUri = Uri.parse("google.navigation:q=" + carbie.getEndLocation().replaceAll(" ", "+")
                        + "&mode=" + typeOfTransport());

                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                startActivity(mapIntent);
                goToMainFragment();
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
            case "Electric":
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
    }

    @Override
    public void onStop() {
        super.onStop();
        AppCompatActivity mainActivity = (AppCompatActivity) getActivity();
        mainActivity.findViewById(R.id.tvName).setVisibility(TextView.VISIBLE);
        mainActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(false);
    }

    private void showProgressBar() {
        pbLoading.setVisibility(ProgressBar.VISIBLE);
    }

    private void hideProgressBar() {
        pbLoading.setVisibility(ProgressBar.GONE);
    }
}
