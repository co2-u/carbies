package com.example.carbonfootprinttracker.fragments;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.carbonfootprinttracker.R;

public class CurrentScoreFragment extends Fragment {

    //TODO Butterknife

    public static final String TAG = "CurrentScoreFragment";
    private ImageView ivQualScore;
    private TextView tvQuantScore;
    private TextView tvGeneralTips;
    private final String GREEN_SCORE = "good job";
    private final String YELLOW_SCORE = "watch out";
    private final String RED_SCORE = "oof";
    private final int MAX_CO2 = 8050;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        // Defines the xml file for the fragment
        return inflater.inflate(R.layout.fragment_current_score, parent, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // Setup any handles to view objects here
        ivQualScore = view.findViewById(R.id.ivQualScore);
        tvQuantScore = view.findViewById(R.id.tvQuantScore);
        tvGeneralTips = view.findViewById(R.id.tvGeneralTips);

        setScore();
    }

    private void setScore() {
//        tvQuantScore.setText(getCurrentScore().toString());
//        if (getCurrentScore() > MAX_CO2) {
//            ivQualScore.setBackgroundColor(Color.RED);
//            tvGeneralTips.setText(RED_SCORE);
//        } else if (getCurrentScore() < MAX_CO2 && getCurrentScore() >= MAX_CO2 - 100) {
//            ivQualScore.setBackgroundColor(Color.YELLOW);
//            tvGeneralTips.setText(YELLOW_SCORE);
//        } else {
//            ivQualScore.setBackgroundColor(Color.GREEN);
//            tvGeneralTips.setText(GREEN_SCORE);
//        }
    }
}
