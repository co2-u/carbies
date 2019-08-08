package com.example.carbonfootprinttracker.adapters;

import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.content.Context;
import android.graphics.Color;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.carbonfootprinttracker.R;
import com.example.carbonfootprinttracker.fragments.CalendarFragment;
import com.example.carbonfootprinttracker.fragments.CurrentDaySummaryFragment;
import com.example.carbonfootprinttracker.fragments.CurrentScoreFragment;
import com.example.carbonfootprinttracker.fragments.DailySummaryFragment;
import com.example.carbonfootprinttracker.models.Carbie;
import com.example.carbonfootprinttracker.models.DailySummary;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import okhttp3.internal.cache.CacheStrategy;

public class CalendarAdapter extends ArrayAdapter<Date> {

    private LayoutInflater inflater;
    private Calendar calendar;
    private List<DailySummary> dailySummaries;
    private static final Integer MAX_CARBON_SCORE = 8000;
    private Context context;
    private GridView gridView;
    private FragmentManager fragmentManager;

    public CalendarAdapter(Context context, ArrayList<Date> days, List<DailySummary> dailySummaries, Calendar calendar, GridView gridView,
    FragmentManager fragmentManager)
    {
        super(context, R.layout.fragment_calendar, days);
        inflater = LayoutInflater.from(context);
        this.calendar = calendar;
        this.dailySummaries = dailySummaries;
        this.context = context;
        this.gridView = gridView;
        this.fragmentManager = fragmentManager;
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

        Log.d("cal", "the mon is" + calendar.get(Calendar.MONTH));
        Log.d("cal", "the year is" + calendar.get(Calendar.YEAR));

        if (month != calendar.get(Calendar.MONTH))
        {
            // if this day is outside current month, grey it out
            ((TextView) view).setTextColor(Color.parseColor("#E0E0E0"));
        }
        else //it is in the month
        {
            //SET THE COLORS
            for(DailySummary dailySummary : dailySummaries){
                Log.d("Daily Summary", "" + dailySummaries.size());
                //check their createdAt
                Date dsDate = dailySummary.getCreatedAt();
                Log.d("CalendarAdapter", "Date is " + dsDate.toString());//set the colors
                if(dsDate.getDate() == day && dsDate.getMonth() == month && dsDate.getYear() == year) {
                    Log.d("CalendarAdapter", "Dates match");//set the colors
                    if (dailySummary.getScore() <= MAX_CARBON_SCORE){
                        ((TextView)view).setTextColor(context.getResources().getColor(android.R.color.holo_green_dark));
//                        view.setBackgroundResource(R.drawable.green_calendar_circle)
                    }
                    else if (dailySummary.getScore() > MAX_CARBON_SCORE && dailySummary.getScore() <= MAX_CARBON_SCORE * 1.1){
                        ((TextView)view).setTextColor(Color.YELLOW);
//                        view.setBackgroundResource(R.drawable.yellow_circle);
                    } else {
                        ((TextView)view).setTextColor(Color.RED);
//                        view.setBackgroundResource(R.drawable.red_circle);
                    }
                }
            }
        }
        if (day == Calendar.getInstance().getTime().getDate() && month == Calendar.getInstance().getTime().getMonth()
                && year == Calendar.getInstance().getTime().getYear() && calendar.get(Calendar.MONTH) == Calendar.getInstance().getTime().getMonth()) {
            // if it is today, set it to blue
            ((TextView)view).setTextColor(Color.BLUE);
            ((TextView) view).setGravity(Gravity.CENTER);
//            view.setBackgroundResource(R.drawable.button_accept);
        }
        // set text
        ((TextView)view).setText(String.valueOf(date.getDate()));

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Date date = getItem(position);
                if (date.getDate() == Calendar.getInstance().getTime().getDate() && date.getMonth() == Calendar.getInstance().getTime().getMonth()
                        && date.getYear() == Calendar.getInstance().getTime().getYear()) {
                    Fragment fragment = new CurrentDaySummaryFragment();
                    fragmentManager.beginTransaction()
                            .replace(R.id.fragmentPlaceholder, fragment)
                            .addToBackStack("DailySummaryFragment")
                            .commit();
                } else {
                    Bundle args = new Bundle();
                    for (DailySummary dailySummary : dailySummaries) {
                        Date dsDate = dailySummary.getCreatedAt();
                        Calendar cal1 = Calendar.getInstance();
                        Calendar cal2 = Calendar.getInstance();
                        cal1.setTime(dsDate);
                        cal2.setTime(date);
                        boolean sameDay = cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR) &&
                                cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR);
                        if (sameDay) {
                            Fragment fragment = new DailySummaryFragment();
                            args = new Bundle();
                            args.putParcelable("dailySummary", dailySummary);
                            args.putInt("month", date.getMonth());
                            args.putInt("date", date.getDate());
                            args.putInt("day", date.getDay());
                            fragment.setArguments(args);
                            fragmentManager.beginTransaction()
                                    .replace(R.id.fragmentPlaceholder, fragment)
                                    .addToBackStack("DailySummaryFragment")
                                    .commit();
                        }
                    }
                    if (args.getParcelable("dailySummary") == null) {
                        if (date.getDate() < Calendar.getInstance().getTime().getDate() && date.getMonth() == Calendar.getInstance().getTime().getMonth()
                                && date.getYear() == Calendar.getInstance().getTime().getYear()) {
                            Toast.makeText(context, "No carbies logged for this date", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        });

        return view;
    }



}