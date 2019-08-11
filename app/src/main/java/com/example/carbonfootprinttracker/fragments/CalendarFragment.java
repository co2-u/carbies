package com.example.carbonfootprinttracker.fragments;

import android.content.Context;
import android.content.res.TypedArray;
import android.media.Image;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CalendarView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

//import com.applandeo.materialcalendarview.EventDay;
//import com.applandeo.materialcalendarview.listeners.OnDayClickListener;
import com.example.carbonfootprinttracker.MainActivity;
import com.example.carbonfootprinttracker.R;
import com.example.carbonfootprinttracker.adapters.CalendarAdapter;
import com.example.carbonfootprinttracker.models.Carbie;
import com.example.carbonfootprinttracker.models.DailySummary;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.w3c.dom.Text;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.HashSet;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.ListFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CalendarFragment extends Fragment implements View.OnTouchListener, GestureDetector.OnGestureListener {

    private final String TAG = "CalendarFragment";

    @BindView(R.id.btnNext) ImageButton btnNext;
    @BindView(R.id.btnPrevious) ImageButton btnPrevious;
    @BindView(R.id.gridView) GridView gridView;
    @BindView(R.id.tvCurrentDate) TextView tvCurrentDate;
    @BindView(R.id.chart)
    LineChart chart;

    private FragmentManager fragmentManager;

    private CalendarAdapter calendarAdapter;
    private ArrayList<Date> cells;

    // how many days to show, defaults to six weeks, 42 days
    private static final int DAYS_COUNT = 42;

    // date format
    private String dateFormat;
    private Calendar currentDate = Calendar.getInstance();

    Context context;
    private List<DailySummary> mDailySummaries;
    private List<Integer> createdAt;

    // for the Calendar swipe
    private GestureDetector mGestureDetector;

    public CalendarFragment() {
    }

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
        fragmentManager = getFragmentManager();
        context = getContext();
        mDailySummaries = new ArrayList<>();
        cells = new ArrayList<>();
        DateFormat dateFormat = new SimpleDateFormat("MMMM YYYY");
        String month_name= dateFormat.format(currentDate.getTime());
        Date date = new Date();
        createdAt = new ArrayList<>();
        tvCurrentDate.setText(month_name);
        queryDailySummaries();

        gridView.setOnTouchListener(this);
        mGestureDetector = new GestureDetector(this);



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
        gridView.setAdapter(new CalendarAdapter(context, cells, mDailySummaries, calendar, gridView, fragmentManager));
    }

    protected void queryDailySummaries() {
        Log.e(TAG, "queried");

        ParseQuery<DailySummary> query = ParseQuery.getQuery(DailySummary.class);
        query.include(DailySummary.KEY_USER);
        query.whereEqualTo(DailySummary.KEY_USER, ParseUser.getCurrentUser());
        query.addDescendingOrder(DailySummary.KEY_CREATED_AT);
        query.findInBackground(new FindCallback<DailySummary>() {
            @Override
            public void done(List<DailySummary> objects, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Error with query");
                    e.printStackTrace();
                    return;
                } else {
                    Log.e(TAG, "" + objects.size());
                    mDailySummaries.addAll(objects);
                    for (DailySummary d : objects) {
                        createdAt.add(d.getCreatedAt().getDate());
                    }
                    updateCalendar();
                    updateChart();
                }
            }
        });
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        mGestureDetector.onTouchEvent(event);
        return false;
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

        DateFormat dateFormat = new SimpleDateFormat("MMMM YYYY");

        if (e1.getX() - e2.getX() > 100 && Math.abs(velocityX) > 100) {
            currentDate.add(Calendar.MONTH, 1);
            String month_name= dateFormat.format(currentDate.getTime());
            updateCalendar();
            tvCurrentDate.setText(month_name);

        }  else if (e2.getX() - e1.getX() > 100 && Math.abs(velocityX) > 100) {
            currentDate.add(Calendar.MONTH, -1);
            String month_name= dateFormat.format(currentDate.getTime());
            updateCalendar();
            tvCurrentDate.setText(month_name);
        }
        return false;
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public void onResume() {
        super.onResume();
        MainActivity mainActivity = (MainActivity) getActivity();
        mainActivity.findViewById(R.id.tvName).setVisibility(TextView.GONE);
        mainActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mainActivity.getSupportActionBar().setTitle("Calendar");
        mainActivity.getSupportActionBar().setDisplayShowTitleEnabled(true);
        mainActivity.setCalendarTabVisibility(false);

    }

    @Override
    public void onStop() {
        super.onStop();
        MainActivity mainActivity = (MainActivity) getActivity();
        mainActivity.findViewById(R.id.tvName).setVisibility(TextView.VISIBLE);
        mainActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        mainActivity.getSupportActionBar().setTitle("Calendar");
        mainActivity.getSupportActionBar().setDisplayShowTitleEnabled(false);
        mainActivity.setCalendarTabVisibility(true);
    }

    private void updateChart() {
        List<Entry> entries = new ArrayList<Entry>();

        List<Entry> valsComp1 = new ArrayList<Entry>();

        for (int i = 0; i < mDailySummaries.size(); i++) {
            DailySummary data = mDailySummaries.get(i);
            Entry e = new Entry(i, data.getScore().floatValue());
            entries.add(e);
        }

        valsComp1.addAll(entries);
        LineDataSet setComp1 = new LineDataSet(valsComp1, "Daily Score");
        setComp1.setAxisDependency(YAxis.AxisDependency.LEFT);

        List<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
        dataSets.add(setComp1);
        LineData data = new LineData(dataSets);
        chart.setData(data);
        chart.invalidate(); // refresh

//        final String[] quarters = new String[] { "Q1", "Q2", "Q3", "Q4" };
//        ValueFormatter formatter = new ValueFormatter() {
//            @Override
//            public String getAxisLabel(float value, AxisBase axis) {
//                return quarters[(int) value];
//            }
//        };
//        XAxis xAxis = mLineChart.getXAxis();
//        xAxis.setGranularity(1f); // minimum axis-step (interval) is 1
//        xAxis.setValueFormatter(formatter);

    }


}
