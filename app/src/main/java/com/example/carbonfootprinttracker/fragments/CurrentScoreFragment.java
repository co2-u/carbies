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

import com.example.carbonfootprinttracker.R;
import com.example.carbonfootprinttracker.models.Carbie;
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
    private final String GREEN_SCORE = "good job";
    private final String YELLOW_SCORE = "watch out";
    private final String RED_SCORE = "oof";
    private int maxCarbon = 8000;
    private List<Carbie> mCarbies;

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
        queryCarbies();
    }

    private void setScore(int currentScore) {
        Date date = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        if (calendar.get(Calendar.HOUR_OF_DAY) < 12) {
            maxCarbon = 4000;
        }
        tvQuantScore.setText(String.valueOf(currentScore));
        if (currentScore > maxCarbon * 1.1) {
            ivQualScore.setBackground(getResources().getDrawable(R.drawable.red_circle));
            tvGeneralTips.setText(RED_SCORE);
        } else if (currentScore > maxCarbon && currentScore <= maxCarbon * 1.1) {
            ivQualScore.setBackground(getResources().getDrawable(R.drawable.yellow_circle));
            tvGeneralTips.setText(YELLOW_SCORE);
        } else {
            ivQualScore.setBackground(getResources().getDrawable(R.drawable.green_circle));
            tvGeneralTips.setText(GREEN_SCORE);
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
}
