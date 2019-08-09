package com.example.carbonfootprinttracker.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.carbonfootprinttracker.MainActivity;
import com.example.carbonfootprinttracker.R;
import com.example.carbonfootprinttracker.models.Carbie;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CurrentDaySummaryFragment extends Fragment {
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
    @BindView(R.id.tvDailyScore) TextView tvDailyScore;
    @BindView(R.id.tvCarbiesSaved) TextView tvCarbiesSaved;
    Integer currentScore;
    Context context;
    List<Carbie> mCarbies;
    private static final DecimalFormat df = new DecimalFormat("0.00");


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
        currentScore = 0;
        mCarbies = new ArrayList<>();
        queryCarbies();
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

    private void setStrings() {
        double milesWalked = 0;
        double milesBiked = 0;
        double milesGas = 0;
        double milesElectric = 0;
        double milesCarpooled = 0;
        double milesPublicTransport = 0;
        for (Carbie carbie : mCarbies) {
            double distance = carbie.getDistance();
            switch (carbie.getTransportation()) {
                case "SmallCar":
                    milesGas += distance;
                    break;
                case "MediumCar":
                    milesGas += distance;
                    break;
                case "LargeCar":
                    milesGas += distance;
                    break;
                case "Hybrid":
                    milesElectric += distance;
                    break;
                case "FossilFuel":
                    milesElectric += distance;
                    break;
                case "Renewable":
                    milesElectric += distance;
                    break;
                case "Bus":
                    milesPublicTransport += distance;
                    break;
                case "Rail":
                    milesPublicTransport += distance;
                    break;
                case "Bike":
                    milesBiked += distance;
                    break;
                case "Walk":
                    milesWalked += distance;
                    break;
                case "Rideshare":
                    milesCarpooled += distance;
                    break;
            }
        }
        tvWalked.setText("" + df.format(milesWalked));
        tvBiked.setText("" + df.format(milesBiked));
        tvGas.setText("" + df.format(milesGas));
        tvElectric.setText("" + df.format(milesElectric));
        tvCarpooled.setText("" + df.format(milesCarpooled));
        tvPTransport.setText("" + df.format(milesPublicTransport));
    }

    private void queryCarbies() {
        Date date = new Date();
        Calendar calendarA = Calendar.getInstance();
        calendarA.setTime(date);
        calendarA.set(Calendar.HOUR_OF_DAY, 0);
        Calendar calendarB = Calendar.getInstance();
        calendarB.setTime(date);
        calendarB.set(Calendar.HOUR_OF_DAY, 23);
        calendarB.set(Calendar.MINUTE, 59);
        ParseQuery<Carbie> query = ParseQuery.getQuery(Carbie.class);
        query.include(Carbie.KEY_USER);
        query.whereEqualTo(Carbie.KEY_USER, ParseUser.getCurrentUser());
//        query.whereEqualTo(Carbie.KEY_IS_FAVORITED, false);
        query.whereEqualTo(Carbie.KEY_IS_DELETED, false);
        query.whereGreaterThanOrEqualTo(Carbie.KEY_CREATED_AT, calendarA.getTime());
        query.whereLessThan(Carbie.KEY_CREATED_AT, calendarB.getTime());
        query.addDescendingOrder(Carbie.KEY_CREATED_AT);
        query.findInBackground(new FindCallback<Carbie>() {
            @Override
            public void done(List<Carbie> carbies, ParseException e) {
                if (e != null) {
                    Log.e("Summary Fragment", "Error with query");
                    e.printStackTrace();
                    return;
                }
                mCarbies.addAll(carbies);
                currentScore = 0;
                for (int i = 0; i < carbies.size(); i++) {
                    Carbie carbie = carbies.get(i);
                    currentScore += carbie.getScore();
                }
                setStrings();
                tvDailyScore.setText("" + currentScore);
                tvCarbiesSaved.setText(carbiesSaved());
            }
        });
    }
    @Override
    public void onResume() {
        super.onResume();
        MainActivity mainActivity = (MainActivity) getActivity();
        mainActivity.findViewById(R.id.tvName).setVisibility(TextView.GONE);
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
        mainActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        mainActivity.getSupportActionBar().setTitle("Daily Summary");
        mainActivity.getSupportActionBar().setDisplayShowTitleEnabled(false);
        mainActivity.setCalendarTabVisibility(true);
    }
    private String carbiesSaved() {
        //TODO make better messages ahaha
        String message = "";
        if (currentScore == 0) {
            message = "Don't forget to log your trips today!";
        } else {
            double totalMileage = 0;
            for (Carbie carbie : mCarbies) {
                totalMileage += carbie.getDistance();
            }
            Double medScore = totalMileage * 430.0;
            Double busScore = totalMileage * 290;
            int milesSaved = medScore.intValue() - MainActivity.score;
            if (milesSaved > 0) {
                message = "By choosing greener modes of transportation you saved " + milesSaved + " grams of CO2 as compared to driving a medium gasoline car. Great job!";
            } else if (milesSaved == 0) {
                message = "By taking the bus instead of using a car you could save " + (currentScore - busScore.intValue()) + " grams of CO2";
            } else {
                message = "By using less green modes of transportation you used " + Math.abs(milesSaved) + " more grams of CO2 than a medium car.";
            }
        }
        return message;
    }
}
