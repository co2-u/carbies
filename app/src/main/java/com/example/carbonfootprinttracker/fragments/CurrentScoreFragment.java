package com.example.carbonfootprinttracker.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.carbonfootprinttracker.R;
import com.example.carbonfootprinttracker.models.Carbie;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CurrentScoreFragment extends Fragment {

    //TODO Butterknife

    public static final String TAG = "CurrentScoreFragment";
    @BindView(R.id.ivQualScore) ImageView ivQualScore;
    @BindView(R.id.tvQuantScore) TextView tvQuantScore;
    @BindView(R.id.tvGeneralTips) TextView tvGeneralTips;
    private int currentScore;
    private final String GREEN_SCORE = "good job";
    private final String YELLOW_SCORE = "watch out";
    private final String RED_SCORE = "oof";
    private final int MAX_CO2 = 9000;
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

    private void setScore() {
//        TODO split up by time
        tvQuantScore.setText(String.valueOf(currentScore));
        if (currentScore > MAX_CO2 * 1.1) {
            ivQualScore.setBackgroundColor(getResources().getColor(R.color.colorRed));
            tvGeneralTips.setText(RED_SCORE);
        } else if (currentScore < MAX_CO2 && currentScore >= MAX_CO2 * 1.1) {
            ivQualScore.setBackgroundColor(getResources().getColor(R.color.colorYellow));
            tvGeneralTips.setText(YELLOW_SCORE);
        } else {
            ivQualScore.setBackgroundColor(getResources().getColor(R.color.colorGreen));
            tvGeneralTips.setText(GREEN_SCORE);
        }
    }

    protected void queryCarbies() {
        ParseQuery<Carbie> query = ParseQuery.getQuery(Carbie.class);
        query.include(Carbie.KEY_USER);
        query.addDescendingOrder(Carbie.KEY_CREATED_AT);
        query.findInBackground(new FindCallback<Carbie>() {
            @Override
            public void done(List<Carbie> carbies, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Error with query");
                    e.printStackTrace();
                    return;
                }
                //TODO only query new carbies
                mCarbies.addAll(carbies);
                currentScore = 0;
                for (int i = 0; i < carbies.size(); i++) {
                    Carbie carbie = carbies.get(i);
                    Log.d(TAG, "Carbie:" + carbie.getTitle()
                            + "Score" + carbie.getScore());
                    currentScore += carbie.getScore();
                }
            }
        });
        setScore();
    }
}
