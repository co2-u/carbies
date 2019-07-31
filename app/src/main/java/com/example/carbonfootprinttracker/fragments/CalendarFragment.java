package com.example.carbonfootprinttracker.fragments;

import android.content.Context;
import android.media.Image;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

//import com.applandeo.materialcalendarview.EventDay;
//import com.applandeo.materialcalendarview.listeners.OnDayClickListener;
import com.example.carbonfootprinttracker.R;
import com.example.carbonfootprinttracker.adapters.CalendarAdapter;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.HashSet;

import androidx.annotation.Nullable;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CalendarFragment extends Fragment{

    //declare the calendar itself
    //declare and set up the adapter

    @BindView(R.id.btnNext) ImageButton btnNext;
    @BindView(R.id.btnPrevious) ImageButton btnPrevious;
    @BindView(R.id.gridView) GridView gridView;

    private static final int DAYS_COUNT = 42;
    protected CalendarAdapter calendarAdapter;
    private ArrayList<Date> cells;

    private Calendar currentDate = Calendar.getInstance();
    Context context;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calendar, container, false);
        ButterKnife.bind(view);
        return view;

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        context = getContext();
        cells = new ArrayList<>();
        calendarAdapter = new CalendarAdapter(context, cells);

        updateCalendar();
    }

    //    public CalendarFragment(Context context){
//        super(context);
//    }
//
//    public CalendarFragment(Context context, @Nullable AttributeSet attrs){
//        super(context, attrs);
//    }

    //displays the dates correctly in the calendar
    public void updateCalendar()
    {
        Calendar calendar = (Calendar)currentDate.clone();

        // determine the cell for current month's beginning
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        int monthBeginningCell = calendar.get(Calendar.DAY_OF_WEEK) - 2;

        // move calendar backwards to the beginning of the week
        calendar.add(Calendar.DAY_OF_MONTH, - monthBeginningCell);

        // fill cells
        while (cells.size() < DAYS_COUNT)
        {
            cells.add(calendar.getTime());
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        // update grid
        gridView.setAdapter(calendarAdapter);

        // update title
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE,d MMM,yyyy");
        String[] dateToday = sdf.format(currentDate.getTime()).split(",");
//        txtDateDay.setText(dateToday[0]);
//        txtDisplayDate.setText(dateToday[1]);
//        txtDateYear.setText(dateToday[2]);
    }







}
