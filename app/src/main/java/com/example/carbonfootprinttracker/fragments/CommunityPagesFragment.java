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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.carbonfootprinttracker.R;
import com.example.carbonfootprinttracker.adapters.CommunityCarbiesAdapter;
import com.example.carbonfootprinttracker.models.Carbie;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CommunityPagesFragment extends Fragment {
    private static final String TAG = "CommunityPagesFragment";
    public static final String ARG_PAGE = "ARG_PAGE";

    @BindView(R.id.rvCarbies) RecyclerView rvCarbies;
    @BindView(R.id.pbLoading) ProgressBar pbLoading;
    @BindView(R.id.tvMessage) TextView tvMessage;

    private CommunityCarbiesAdapter communityCarbiesAdapter;
    private List<Carbie> mCarbies;
    private FragmentManager fragmentManager;
    private Context context;
    private int mPage;
    private Set<String> following;

    public CommunityPagesFragment (int page) {
        this.mPage = page;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_daily_log, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        fragmentManager = getActivity().getSupportFragmentManager();
        context = getContext();
        following = new HashSet<>();

        tvMessage.setVisibility(View.GONE);
        if (mPage == 0) {
            tvMessage.setText("No one you follow has logged Carbies today yet!");
        } else { //mPage == 1
            tvMessage.setText("No one has logged Carbies today yet!");
        }

        rvCarbies.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));
        mCarbies = new ArrayList<>();
        communityCarbiesAdapter = new CommunityCarbiesAdapter(context, mCarbies, fragmentManager);
        rvCarbies.setAdapter(communityCarbiesAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        rvCarbies.setLayoutManager(linearLayoutManager);

        JSONArray followingArray = ParseUser.getCurrentUser().getJSONArray("following");
        if (followingArray != null) {
            for (int i = 0; i < followingArray.length(); i++) {
                try {
                    following.add((String) followingArray.get(i));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        queryCarbies();
    }

    private void queryCarbies() {
        ParseQuery<Carbie> query = getQuery();
        query.findInBackground(new FindCallback<Carbie>() {
            @Override
            public void done(List<Carbie> objects, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Error with query");
                    e.printStackTrace();
                } else {
                    if (mPage == 0) { // Following page
                        for (Carbie carbie: objects) {
                            if (!carbie.getUser().getBoolean("isPrivate")
                                && following.contains(carbie.getUser().getObjectId())) {
                                mCarbies.add(carbie);
                            }
                        }
                    } else { // All page (public users)
                        for (Carbie carbie: objects) {
                            if (!carbie.getUser().getBoolean("isPrivate")) {
                                mCarbies.add(carbie);
                            }
                        }
                    }
                    communityCarbiesAdapter.notifyDataSetChanged();
                    pbLoading.setVisibility(ProgressBar.INVISIBLE);
                    setMessageVisibility();
                }
            }
        });
    }

    private ParseQuery<Carbie> getQuery() {
        pbLoading.setVisibility(ProgressBar.VISIBLE);

        ParseQuery<Carbie> query = ParseQuery.getQuery(Carbie.class);
        query.include(Carbie.KEY_USER);
        query.whereEqualTo(Carbie.KEY_IS_FAVORITED, false);
        query.addDescendingOrder(Carbie.KEY_CREATED_AT);
        return query;
    }

    private void setMessageVisibility() {
        if (mCarbies.size() > 0) {
            tvMessage.setVisibility(TextView.GONE);
        } else {
            tvMessage.setVisibility(TextView.VISIBLE);
        }
    }
}
