package com.example.carbonfootprinttracker.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.bumptech.glide.Glide;
import com.example.carbonfootprinttracker.R;
import com.example.carbonfootprinttracker.models.Carbie;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.SaveCallback;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailsFragment extends Fragment {
    private static final String TAG = "DetailsFragment";
    private static final Integer MAX_CARBON = 2000;

    @BindView(R.id.tvTitle)
    TextView tvTitle;
    @BindView(R.id.tvStartPoint2)
    TextView tvStartPoint2;
    @BindView(R.id.tvEndPoint2)
    TextView tvEndPoint2;
    @BindView(R.id.tvMode2)
    TextView tvMode2;
    @BindView(R.id.tvDistance2)
    TextView tvDistance2;
    @BindView(R.id.tvScore2)
    TextView tvScore2;
    @BindView(R.id.tvSuggestion)
    TextView tvSuggestion;
    @BindView(R.id.ivMapShot)
    ImageView ivMapShot;

    private FragmentManager fragmentManager;
    private Carbie carbie;
    private ParseFile photoFile;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_details, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fragmentManager = getFragmentManager();

        try {
            carbie = getArguments().getParcelable("carbie");
            tvTitle.setText(carbie.getTitle());
            tvStartPoint2.setText(carbie.getStartLocation());
            tvEndPoint2.setText(carbie.getEndLocation());
            tvMode2.setText(carbie.getTransportation());
            tvDistance2.setText(carbie.getDistance().toString());
            tvScore2.setText(Integer.toString(carbie.getScore()));
            setMessage(carbie.getTransportation());
            setScoreColor(carbie.getScore());
            photoFile = carbie.getMapShot();

        } catch (NullPointerException e) {
            Log.d(TAG, "Carbie was not passed in from DailyLog");
            e.printStackTrace();
            return;
        }

        if (photoFile != null) {
            String preURL = photoFile.getUrl();
            String completeURL = preURL.substring(0, 4) + "s" + preURL.substring(4, preURL.length());
            Glide.with(getContext())
                    .load(completeURL)
                    .into(ivMapShot);
        } else {
            ivMapShot.setVisibility(View.GONE);
        }

    }

    private void setMessage(String transportation) {
        if (carbie.getDistance() < 1 && (carbie.getTransportation().equals("Walk") == false)
            && (carbie.getTransportation().equals("Bike") == false)) {
            tvSuggestion.setText(getString(R.string.oneMileSuggestion));
        } else if (carbie.getDistance() < 5 && (carbie.getTransportation().equals("Walk") == false)
                   && (carbie.getTransportation().equals("Bike") == false)) {
            tvSuggestion.setText(getString(R.string.fiveMileSuggestion));
        } else {
            switch (carbie.getTransportation()) {
                case "SmallCar":
                    tvSuggestion.setText(getString(R.string.carSuggestion));
                    break;
                case "MediumCar":
                    tvSuggestion.setText(getString(R.string.carSuggestion));
                    break;
                case "LargeCar":
                    tvSuggestion.setText(getString(R.string.carSuggestion));
                    break;
                case "Hybrid":
                    tvSuggestion.setText(getString(R.string.hybridSuggestion));
                    break;
                case "FossilFuel":
                    tvSuggestion.setText(getString(R.string.electricSuggestion));
                    break;
                case "Renewable":
                    tvSuggestion.setText(getString(R.string.electricSuggestion));
                    break;
                case "Bus":
                    tvSuggestion.setText(getString(R.string.publicTransportSuggestion));
                    break;
                case "Bike":
                    tvSuggestion.setText(getString(R.string.bikeSuggestion));
                    break;
                case "Rail":
                    tvSuggestion.setText(getString(R.string.publicTransportSuggestion));
                    break;
                case "Walk":
                    tvSuggestion.setText(getString(R.string.walkSuggestion));
                    break;
                case "Rideshare":
                    tvSuggestion.setText(getString(R.string.walkSuggestion));
                    break;
            }
        }
    }

    private void setScoreColor(int score){
        if (carbie.getScore() > MAX_CARBON && score <= MAX_CARBON * 1.1){
            tvScore2.setTextColor(Color.parseColor("#FFE401")); //yellow
        } else if (carbie.getScore() > MAX_CARBON * 1.1){
            tvScore2.setTextColor(Color.parseColor("#EC0000")); //red
        } else{
            tvScore2.setTextColor(Color.parseColor("#55C21B")); //green
        }
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
}
