package com.example.carbonfootprinttracker.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.carbonfootprinttracker.R;
import com.example.carbonfootprinttracker.models.Carbie;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailsFragment extends Fragment {

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
    Carbie carbie;

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
        carbie = getArguments().getParcelable("carbie");
        tvTitle.setText(carbie.getTitle());
        tvStartPoint2.setText(carbie.getStartLocation());
        tvEndPoint2.setText(carbie.getEndLocation());
        tvMode2.setText(carbie.getTransportation());
        tvDistance2.setText(carbie.getDistance().toString());
        tvScore2.setText(carbie.getScore().toString());
    }
}
