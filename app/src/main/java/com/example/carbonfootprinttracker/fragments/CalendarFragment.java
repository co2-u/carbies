package com.example.carbonfootprinttracker.fragments;

import android.content.Context;
import android.content.res.TypedArray;
import android.media.Image;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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

import java.text.DateFormat;
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
    @BindView(R.id.tvCurrentDate) TextView tvCurrentDate;
    //@BindView(R.id.tvDay) TextView tvDay;

    protected CalendarAdapter calendarAdapter;
    private ArrayList<Date> cells;

    // how many days to show, defaults to six weeks, 42 days
    private static final int DAYS_COUNT = 42;

    // default date format
    private static final String DATE_FORMAT = "MMM yyyy";

    // date format
    private String dateFormat;

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
        DateFormat dateFormat = new SimpleDateFormat("MMMM");
        String month_name= dateFormat.format(currentDate.getTime());
        Date date = new Date();

        tvCurrentDate.setText(month_name);
        calendarAdapter = new CalendarAdapter(context, cells, currentDate);

        updateCalendar();

        //add one month and refresh the UI
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentDate.add(Calendar.MONTH, 1);
                String month_name = dateFormat.format(currentDate.getTime());
                tvCurrentDate.setText(month_name);
                updateCalendar();
            }
        });

        //subtract one month and refresh the UI
        btnPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentDate.add(Calendar.MONTH, -1);
                String month_name = dateFormat.format(currentDate.getTime());
                tvCurrentDate.setText(month_name);
                updateCalendar();

            }
        });

//        //click into a day and leads to the daily summary fragment
//        tvDay.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });
    }

    public void updateCalendar()
    {
        updateCalendar(null);
    }

    public void updateCalendar(HashSet<Date> events)
    {
        ArrayList<Date> cells = new ArrayList<>();
        Calendar calendar = (Calendar) currentDate.clone();
        int month = calendar.get(Calendar.MONTH);

        // determine the cell for current month's beginning
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        int monthBeginningCell = calendar.get(Calendar.DAY_OF_WEEK) - 1;

        // move calendar backwards to the beginning of the week
        calendar.add(Calendar.DAY_OF_MONTH, - monthBeginningCell);

        // fill cells
        while (cells.size() < DAYS_COUNT)
        {
            cells.add(calendar.getTime());
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }
        calendar.set(Calendar.MONTH, month);
        // update grid
        gridView.setAdapter(new CalendarAdapter(context, cells, calendar));

    }
}
