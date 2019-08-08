package com.example.carbonfootprinttracker.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.carbonfootprinttracker.MainActivity;
import com.example.carbonfootprinttracker.R;
import com.example.carbonfootprinttracker.models.DailySummary;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DailySummaryFragment extends Fragment {

    @BindView(R.id.ivShare)
    ImageView ivShareScore;
    @BindView(R.id.tvWalked)
    TextView tvWalked;
    @BindView(R.id.tvBiked)
    TextView tvBiked;
    @BindView(R.id.tvGas)
    TextView tvGas;
    @BindView(R.id.tvElectric)
    TextView tvElectric;
    @BindView(R.id.tvCarpooled)
    TextView tvCarpooled;
    @BindView(R.id.tvPTransport)
    TextView tvPTransport;
    @BindView(R.id.tvDailyTitle)
    TextView tvDailyTitle;
    @BindView(R.id.tvDailyScore) TextView tvDailyScore;
    @BindView(R.id.tvCarbiesSaved) TextView tvCarbiesSaved;

    Context context;
    DailySummary dailySummary;
    int date;
    int month;
    int day;

    @Nullable
    @Override
    //this is creating the view
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_daily_summary, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        context = getContext();
        try {
            dailySummary = getArguments().getParcelable("dailySummary");
            date = getArguments().getInt("date");
            month = getArguments().getInt("month");
            day = getArguments().getInt("day");
        } catch (NullPointerException e) {
            Log.e("DSF", "Daily Summary was not passed in to Daily Summary Fragment");
            e.printStackTrace();
        }
        tvWalked.setText( "" + Math.floor(dailySummary.getMilesWalked() * 100) / 100.0);
        tvBiked.setText( "" + Math.floor(dailySummary.getMilesBiked() * 100) / 100.0);
        tvGas.setText( "" + Math.floor(dailySummary.getMilesGasDriven() * 100) / 100.0);
        tvElectric.setText( "" + Math.floor(dailySummary.getMilesEDriven() * 100) / 100.0);
        tvCarpooled.setText( "" + Math.floor(dailySummary.getMilesCarpooled() * 100) / 100.0);
        tvPTransport.setText( "" + Math.floor(dailySummary.getMilesPublicTransport() * 100) / 100.0);
        tvDailyScore.setText("" + dailySummary.getScore().intValue());
        tvDailyTitle.setText(getDay(day) + ", " + getMonth(month) + " " + date);
        tvCarbiesSaved.setText(carbiesSaved());
        ivShareScore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, "I just tracked and decreased" +
                        "my carbon output for the day using CO2&U! My daily carbon score is " + MainActivity.score);
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        AppCompatActivity mainActivity = (AppCompatActivity) getActivity();
        mainActivity.findViewById(R.id.tvName).setVisibility(TextView.GONE);
        mainActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        mainActivity.getSupportActionBar().setDisplayShowTitleEnabled(true);
        mainActivity.findViewById(R.id.bottomNavigation).setVisibility(TextView.GONE);
    }
    @Override
    public void onStop() {
        super.onStop();
        AppCompatActivity mainActivity = (AppCompatActivity) getActivity();
        mainActivity.findViewById(R.id.tvName).setVisibility(TextView.VISIBLE);
        mainActivity.findViewById(R.id.bottomNavigation).setVisibility(TextView.VISIBLE);
        mainActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(false);
//        mainActivity.getSupportActionBar().setDisplayShowTitleEnabled(false);

    }

    private String getMonth(int month) {
        String m = "";
        switch (month) {
            case 0:
                m = "January";
                break;
            case 1:
                m = "February";
                break;
            case 2:
                m = "March";
                break;
            case 3:
                m = "April";
                break;
            case 4:
                m = "May";
                break;
            case 5:
                m = "June";
                break;
            case 6:
                m = "July";
                break;
            case 7:
                m = "August";
                break;
            case 8:
                m = "Septepmber";
                break;
            case 9:
                m = "October";
                break;
            case 10:
                m = "November";
                break;
            case 11:
                m = "December";
                break;
        }
        return m;
    }

    private String getDay(int day) {
        String d = "";
        switch (day) {
            case 0:
                d = "Sunday";
                break;
            case 1:
                d = "Monday";
                break;
            case 2:
                d = "Tuesday";
                break;
            case 3:
                d = "Wednesday";
                break;
            case 4:
                d = "Thursday";
                break;
            case 5:
                d = "Friday";
                break;
            case 6:
                d = "Saturday";
                break;
        }
        return d;
    }

    private String carbiesSaved() {
        String message = "";
        double totalMileage = dailySummary.getMilesBiked() + dailySummary.getMilesCarpooled() + dailySummary.getMilesEDriven() +
                dailySummary.getMilesGasDriven() + dailySummary.getMilesPublicTransport() + dailySummary.getMilesWalked();
        Double medScore = totalMileage * 430.0;
        Double busScore = totalMileage * 290;
        Double milesSaved = medScore.intValue() - dailySummary.getScore();
        if (milesSaved > 0) {
            message = "By choosing greener modes of transportation you saved " + milesSaved.intValue() + " grams of CO2 as compared to driving a medium gasoline car. Great job!";
        } else if (milesSaved == 0) {
            message = "By taking the bus instead of using a car you could save " + (dailySummary.getScore() - busScore.intValue()) + " grams of CO2";
        } else {
            message = "By using less green modes of transportation you used " + Math.abs(milesSaved.intValue()) + " more grams of CO2 than a medium car.";
        }
        return message;
    }





}
