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
    @BindView(R.id.tvDailyScore) TextView tvDailyScore;

    Context context;
    DailySummary dailySummary;

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
        } catch (NullPointerException e) {
            Log.e("DSF", "Daily Summary was not passed in to Daily Summary Fragment");
            e.printStackTrace();
        }
        tvWalked.setText( "" + Math.floor(dailySummary.getMilesWalked() * 100) / 100);
        tvBiked.setText( "" + Math.floor(dailySummary.getMilesBiked() * 100) / 100);
        tvGas.setText( "" + Math.floor(dailySummary.getMilesGasDriven() * 100) / 100);
        tvElectric.setText( "" + Math.floor(dailySummary.getMilesEDriven() * 100) / 100);
        tvCarpooled.setText( "" + Math.floor(dailySummary.getMilesCarpooled() * 100) / 100);
        tvPTransport.setText( "" + Math.floor(dailySummary.getMilesPublicTransport() * 100) / 100);
        tvDailyScore.setText("" + dailySummary.getScore());
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
        mainActivity.findViewById(R.id.calendarTab).setVisibility(TextView.GONE);
        mainActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
    @Override
    public void onStop() {
        super.onStop();
        AppCompatActivity mainActivity = (AppCompatActivity) getActivity();
        mainActivity.findViewById(R.id.tvName).setVisibility(TextView.VISIBLE);
        mainActivity.findViewById(R.id.calendarTab).setVisibility(TextView.VISIBLE);
        mainActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(false);
    }
}
