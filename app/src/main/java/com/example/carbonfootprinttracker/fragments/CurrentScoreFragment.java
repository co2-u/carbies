package com.example.carbonfootprinttracker.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.carbonfootprinttracker.MainActivity;
import com.example.carbonfootprinttracker.R;
import com.example.carbonfootprinttracker.models.Carbie;
import com.example.carbonfootprinttracker.models.DailySummary;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CurrentScoreFragment extends Fragment {

    public static final String TAG = "CurrentScoreFragment";

    @BindView(R.id.ivQualScore) ImageView ivQualScore;
    @BindView(R.id.tvQuantScore) TextView tvQuantScore;
    @BindView(R.id.tvGeneralTips) TextView tvGeneralTips;
    @BindView(R.id.pbLoading) ProgressBar pbLoading;

    private int currentScore;
    private final String GREEN_SCORE = "Great job minimizing your carbon output! " +
                                       "Keep walking and biking for close distances and using other green modes of transportation.";
    private final String YELLOW_SCORE = "Careful you have slightly exceeded your recommended carbon output. " +
                                        "Try to utilize greener modes of transportation for the next day.";
    private final String RED_SCORE = "You have greatly exceeded your recommended carbon output. " +
                                     "Next time, consider using greener modes of transportation.";
    private int maxCarbon = 8000;
    private List<Carbie> mCarbies;
    private List<DailySummary> mDailySummaries;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        // Defines the xml file for the fragment
        View view = inflater.inflate(R.layout.fragment_current_score, parent, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        mCarbies = new ArrayList<>();
        mDailySummaries = new ArrayList<>();
        queryCarbies();
        ivQualScore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new CurrentDaySummaryFragment();
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.fragmentPlaceholder, fragment)
                        .addToBackStack("ComposeFragment")
                        .commit();
            }
        });

    }

    private void setScore(int currentScore) {
        queryDailySummaries();
        Date date = new Date();
        MainActivity.score = currentScore;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        if (calendar.get(Calendar.HOUR_OF_DAY) < 12) {
            maxCarbon = 4000;
        }
        tvQuantScore.setText(String.valueOf(currentScore));
        if (currentScore > maxCarbon * 1.1) {
            ivQualScore.setBackground(getResources().getDrawable(R.drawable.red_circle));
        } else if (currentScore > maxCarbon && currentScore <= maxCarbon * 1.1) {
            ivQualScore.setBackground(getResources().getDrawable(R.drawable.yellow_circle));
        } else {
            ivQualScore.setBackground(getResources().getDrawable(R.drawable.green_circle));
        }
    }

    protected void queryCarbies() {
        pbLoading.setVisibility(ProgressBar.VISIBLE);
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
        query.whereEqualTo(Carbie.KEY_IS_DELETED, false);
        query.whereGreaterThanOrEqualTo(Carbie.KEY_CREATED_AT, calendarA.getTime());
        query.whereLessThan(Carbie.KEY_CREATED_AT, calendarB.getTime());
        query.addDescendingOrder(Carbie.KEY_CREATED_AT);
        query.findInBackground(new FindCallback<Carbie>() {
            @Override
            public void done(List<Carbie> carbies, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Error with query");
                    e.printStackTrace();
                    return;
                }
                pbLoading.setVisibility(ProgressBar.INVISIBLE);
                currentScore = 0;
                mCarbies.addAll(carbies);
                currentScore = 0;
                for (int i = 0; i < carbies.size(); i++) {
                    Carbie carbie = carbies.get(i);
                    currentScore += carbie.getScore();
                }
                queryDailySummaries();
                if (getFragmentManager() != null) {
                    CurrentScoreFragment currentScoreFragment = (CurrentScoreFragment) getFragmentManager().findFragmentByTag("CurrentScoreFragment");
                    if (currentScoreFragment.isVisible()) {
                        setScore(currentScore);
                    }
                } else {
                    Log.d(TAG, "CurrentScoreFragment is not visible");
                }
            }
        });
    }

    protected void queryDailySummaries() {
        Log.e(TAG, "queried");
        String message = "";
        Calendar calendarA = Calendar.getInstance();
        calendarA.add(Calendar.DATE, -1);
        calendarA.set(Calendar.HOUR_OF_DAY, 0);
        Calendar calendarB = Calendar.getInstance();
        calendarB.add(Calendar.DATE, -1);
        calendarB.set(Calendar.HOUR_OF_DAY, 23);
        calendarB.set(Calendar.MINUTE, 59);

        ParseQuery<DailySummary> query = ParseQuery.getQuery(DailySummary.class);
        query.include(DailySummary.KEY_USER);
        query.whereEqualTo(DailySummary.KEY_USER, ParseUser.getCurrentUser());
        query.whereGreaterThanOrEqualTo(Carbie.KEY_CREATED_AT, calendarA.getTime());
        query.whereLessThan(Carbie.KEY_CREATED_AT, calendarB.getTime());
        query.addDescendingOrder(DailySummary.KEY_CREATED_AT);
        query.findInBackground(new FindCallback<DailySummary>() {
            @Override
            public void done(List<DailySummary> dailySummaries, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Error with query");
                    e.printStackTrace();
                    return;
                } else {
                    Log.e(TAG, "" + dailySummaries.size());
                    mDailySummaries.addAll(dailySummaries);
                    if (currentScore == 0) {
                        if (mDailySummaries.isEmpty()) {
                            tvGeneralTips.setText("Remember to log your trips!");
                        } else {
                            DailySummary dailySummary = mDailySummaries.get(0);
                            if (dailySummary.getScore() > maxCarbon * 1.1) {
                                tvGeneralTips.setText("Yesterday you greatly exceeded your limit. Try to use greener modes of transportation today");
                            } else if (dailySummary.getScore() > maxCarbon && dailySummary.getScore() <= maxCarbon * 1.1) {
                                tvGeneralTips.setText("Yesterday you slighly exceeded your limit. Try to use greener modes of transportation today");
                            } else {
                                tvGeneralTips.setText("Yesterday you did a great job, keep up the good work!");
                            }
                        }
                    } else {
                        if (currentScore > maxCarbon * 1.1) {
                            tvGeneralTips.setText(RED_SCORE);
                        } else if (currentScore > maxCarbon && currentScore <= maxCarbon * 1.1) {
                            tvGeneralTips.setText(YELLOW_SCORE);
                        } else {
                            tvGeneralTips.setText(GREEN_SCORE);
                        }
                    }
                }
            }
        });
    }

}
