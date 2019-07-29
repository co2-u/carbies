package com.example.carbonfootprinttracker.fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
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

import butterknife.BindView;
import butterknife.ButterKnife;

public class FavoritesFragment extends  Fragment{
    private final String TAG = "FavoritesFragment";
    boolean isDailyLogFragment = false;


    @BindView(R.id.rvCarbies) RecyclerView rvCarbies;
    @BindView(R.id.pbLoading) ProgressBar pbLoading;
    @BindView(R.id.tvMessage)
    TextView tvMessage;

    protected CarbiesAdapter carbiesAdapter;
    protected List<Carbie> mCarbies;
    protected FragmentManager fragmentManager;
    protected Context context;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_daily_log, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        tvMessage.setText("You haven't added any Carbies to your favorites yet!");
        fragmentManager = getFragmentManager();
        context = getContext();

        rvCarbies.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));
        mCarbies = new ArrayList<>();
        carbiesAdapter = new CarbiesAdapter(context, fragmentManager, mCarbies, getActivity(), isDailyLogFragment);
        rvCarbies.setAdapter(carbiesAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        rvCarbies.setLayoutManager(linearLayoutManager);

        ItemClickSupport.addTo(rvCarbies).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                AddFavoriteFragment addFavoriteFragment = new AddFavoriteFragment();
                Bundle args = new Bundle();
                args.putParcelable("carbie", mCarbies.get(position));
                addFavoriteFragment.setArguments(args);
                addFavoriteFragment.show(fragmentManager, "favorites_fragment");
            }
        });
        queryCarbies();
    }

    protected void queryCarbies() {
        Log.e(TAG, "queried");
        pbLoading.setVisibility(ProgressBar.VISIBLE);

        ParseQuery<Carbie> query = ParseQuery.getQuery(Carbie.class);
        query.include(Carbie.KEY_USER);
        query.whereEqualTo(Carbie.KEY_IS_FAVORITED, true);

        query.whereEqualTo(Carbie.KEY_USER, ParseUser.getCurrentUser());
        query.addDescendingOrder(Carbie.KEY_CREATED_AT);
        query.findInBackground(new FindCallback<Carbie>() {
            @Override
            public void done(List<Carbie> objects, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Error with query");
                    e.printStackTrace();
                    return;
                } else {
                    Log.e(TAG, "" + objects.size());
                    mCarbies.addAll(objects);
                    carbiesAdapter.notifyDataSetChanged();
                    pbLoading.setVisibility(ProgressBar.INVISIBLE);
                    setMessageVisibility();
                }
            }
        });
    }

    public void setMessageVisibility() {
        if (mCarbies.size() > 0) {
            tvMessage.setVisibility(TextView.GONE);
        } else {
            tvMessage.setVisibility(TextView.VISIBLE);
        }
    }
}