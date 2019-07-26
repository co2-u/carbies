package com.example.carbonfootprinttracker.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.carbonfootprinttracker.CarbonApp;
import com.example.carbonfootprinttracker.ItemClickSupport;
import com.example.carbonfootprinttracker.R;
import com.example.carbonfootprinttracker.SwipeToDeleteCallback;
import com.example.carbonfootprinttracker.adapters.CarbiesAdapter;
import com.example.carbonfootprinttracker.models.Carbie;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.ButterKnife;

public class FavoritesFragment extends DailyLogFragment {
    private final String TAG = "FavoritesFragment";
    boolean isDailyLogFragment = false;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        fragmentManager = getFragmentManager();
        context = getContext();

        rvCarbies.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));
        mCarbies = new ArrayList<>();
        carbiesAdapter = new CarbiesAdapter(context, fragmentManager, mCarbies, getActivity(), isDailyLogFragment);
        rvCarbies.setAdapter(carbiesAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        rvCarbies.setLayoutManager(linearLayoutManager);

        queryCarbies();
    }

    @Override
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
        query.whereEqualTo(Carbie.KEY_IS_FAVORITED, true);

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
                    setMessageVisibility();
                }
            }
        });
    }
}
