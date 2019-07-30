package com.example.carbonfootprinttracker.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

//import com.applandeo.materialcalendarview.EventDay;
//import com.applandeo.materialcalendarview.listeners.OnDayClickListener;
import com.example.carbonfootprinttracker.R;

import butterknife.BindView;

public class CalendarFragment extends Fragment {

    @BindView(R.id.cvCalendar)
    CalendarView cvCalendar;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calendar, container, false);
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);




    }
}
