package com.example.carbonfootprinttracker.adapters;

import android.graphics.Typeface;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.content.Context;
import android.graphics.Color;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.carbonfootprinttracker.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;

public class CalendarAdapter extends ArrayAdapter<Date> {

    private LayoutInflater inflater;
    private Calendar calendar;

    public CalendarAdapter(Context context, ArrayList<Date> days, Calendar calendar)
    {
        super(context, R.layout.fragment_calendar, days);
        inflater = LayoutInflater.from(context);
        this.calendar = calendar;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent)
    {
//        Calendar calendar = Calendar.getInstance();

        // day in question
        Date date = getItem(position);
        int day = date.getDate();
        int month = date.getMonth();
        int year = date.getYear();

        // today
//        Date today = new Date();

        // inflate item if it does not exist yet
        if (view == null)
            view = inflater.inflate(R.layout.control_calendar_day, parent, false);

        // clear styling
        ((TextView)view).setTypeface(null, Typeface.NORMAL);
        ((TextView)view).setTextColor(Color.BLACK);

        if (month != calendar.get(Calendar.MONTH) || year != calendar.get(Calendar.YEAR)-1900)
        {
            // if this day is outside current month, grey it out
            ((TextView) view).setTextColor(Color.parseColor("#E0E0E0"));
        }
        else if (day == Calendar.getInstance().getTime().getDate() && month == Calendar.getInstance().getTime().getMonth()
                && year == Calendar.getInstance().getTime().getYear())
        {
            // if it is today, set it to blue/bold
            ((TextView)view).setTextColor(Color.RED);
            ((TextView) view).setGravity(Gravity.CENTER);
//            view.setBackgroundResource(R.drawable.button_accept);
        } else {
//            Log.e("ca", "month is: " + month);
//            Log.e("ca", "calendar month is: " + calendar.get(Calendar.MONTH) );
        }

        // set text
        ((TextView)view).setText(String.valueOf(date.getDate()));

        return view;
    }
}