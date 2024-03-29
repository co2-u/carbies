package com.example.carbonfootprinttracker.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.carbonfootprinttracker.MainActivity;
import com.example.carbonfootprinttracker.R;
import com.example.carbonfootprinttracker.models.Carbie;
import com.example.carbonfootprinttracker.models.DailySummary;

import java.text.DecimalFormat;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DailySummaryFragment extends Fragment {

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

    private static final DecimalFormat df = new DecimalFormat("0.00");
    private static final Integer MAX_CARBON = 2000;


    Context context;
    DailySummary dailySummary;
    int date;
    int month;
    int day;
    private Carbie carbie;

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

            carbie = getArguments().getParcelable("carbie");
            tvDailyScore.setText(Integer.toString(carbie.getScore()));
            setScoreColor(carbie.getScore());

        } catch (NullPointerException e) {
            Log.e("DSF", "Daily Summary was not passed in to Daily Summary Fragment");
            e.printStackTrace();
        }
        ivShareScore = ((MainActivity) getActivity()).findViewById(R.id.ivShare);
        tvWalked.setText( "" + df.format(dailySummary.getMilesWalked()));
        tvBiked.setText( "" + df.format(dailySummary.getMilesBiked()));
        tvGas.setText( "" + df.format(dailySummary.getMilesGasDriven()));
        tvElectric.setText( "" + df.format(dailySummary.getMilesEDriven()));
        tvCarpooled.setText( "" + df.format(dailySummary.getMilesCarpooled()));
        tvPTransport.setText( "" + df.format(dailySummary.getMilesPublicTransport()));
        tvDailyScore.setText("" + dailySummary.getScore().intValue());
        tvDailyTitle.setText(getDay(day) + ", " + getMonth(month) + " " + date);
        tvCarbiesSaved.setText(carbiesSaved());
        ((TextView)getActivity().findViewById(R.id.textView13)).setText(Html.fromHtml("8000g of C0<sub><small>2</small></sub>"));
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
        ((MainActivity) getActivity()).getSupportActionBar().setTitle("Daily Summary"); //"daily summary" instead of title
    }

    private void setScoreColor(int score){
        if (carbie.getScore() > MAX_CARBON && score <= MAX_CARBON * 1.1){
            tvDailyScore.setTextColor(Color.parseColor("#FFE401")); //yellow
        } else if (carbie.getScore() > MAX_CARBON * 1.1){
            tvDailyScore.setTextColor(Color.parseColor("#EC0000")); //red
        } else{
            tvDailyScore.setTextColor(Color.parseColor("#55C21B")); //green
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        MainActivity mainActivity = (MainActivity) getActivity();
        mainActivity.findViewById(R.id.tvName).setVisibility(TextView.GONE);
        mainActivity.findViewById(R.id.ivShare).setVisibility(View.VISIBLE);
        mainActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mainActivity.getSupportActionBar().setTitle("Daily Summary");
        mainActivity.getSupportActionBar().setDisplayShowTitleEnabled(true);
        mainActivity.setCalendarTabVisibility(false);
    }
    @Override
    public void onStop() {
        super.onStop();
        MainActivity mainActivity = (MainActivity) getActivity();
        mainActivity.findViewById(R.id.tvName).setVisibility(TextView.VISIBLE);
        mainActivity.findViewById(R.id.ivShare).setVisibility(View.GONE);
        mainActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        mainActivity.getSupportActionBar().setTitle("Daily Summary");
        mainActivity.getSupportActionBar().setDisplayShowTitleEnabled(false);
        mainActivity.setCalendarTabVisibility(true);
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
