package com.example.carbonfootprinttracker.fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.carbonfootprinttracker.ItemClickSupport;
import com.example.carbonfootprinttracker.R;
import com.example.carbonfootprinttracker.models.Carbie;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DailyLogFragment extends Fragment {
    private static final String TAG = "DailyLogFragment";

    @BindView(R.id.rvCarbies) RecyclerView rvCarbies;
    @BindView(R.id.pbLoading) ProgressBar pbLoading;

    private CarbiesAdapter carbiesAdapter;
    private List<Carbie> mCarbies;
    private FragmentManager fragmentManager;
    private Context context;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_daily_log, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        fragmentManager = getFragmentManager();
        context = getContext();

        rvCarbies.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));
        mCarbies = new ArrayList<>();
        carbiesAdapter = new CarbiesAdapter(context, fragmentManager, mCarbies);
        rvCarbies.setAdapter(carbiesAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        rvCarbies.setLayoutManager(linearLayoutManager);

        ItemClickSupport.addTo(rvCarbies).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                Bundle args = new Bundle();
                args.putParcelable("carbie", mCarbies.get(position));
                Fragment detailFragment = new DetailsFragment();
                detailFragment.setArguments(args);
                fragmentManager.beginTransaction().replace(R.id.fragmentPlaceholder, detailFragment).commit();
            }
        });

        queryCarbies();
    }

    // Get the current user's carbies from today and add them to recycler view
    protected void queryCarbies() {
        pbLoading.setVisibility(ProgressBar.VISIBLE);

        Date date = new Date();
        Calendar calendarA = Calendar.getInstance();
        calendarA.setTime(date);
        calendarA.set(Calendar.HOUR_OF_DAY, 0);
        Calendar calendarB = Calendar.getInstance();
        calendarB.setTime(date);
        calendarB.set(Calendar.HOUR_OF_DAY, 23);
        calendarB.set(Calendar.MINUTE, 59);

        ParseQuery<Carbie> query = ParseQuery.getQuery(Carbie.class);
        query.include(Carbie.KEY_USER);
        query.whereEqualTo(Carbie.KEY_USER, ParseUser.getCurrentUser());
        query.whereGreaterThanOrEqualTo(Carbie.KEY_CREATED_AT, calendarA.getTime());
        query.whereLessThan(Carbie.KEY_CREATED_AT, calendarB.getTime());
        query.addDescendingOrder(Carbie.KEY_CREATED_AT);
        query.findInBackground(new FindCallback<Carbie>() {
            @Override
            public void done(List<Carbie> objects, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Error with query");
                    e.printStackTrace();
                    return;
                } else {
                    mCarbies.addAll(objects);
                    carbiesAdapter.notifyDataSetChanged();
                    pbLoading.setVisibility(ProgressBar.INVISIBLE);
                }
            }
        });
    }
}
