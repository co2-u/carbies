package com.example.carbonfootprinttracker.fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.carbonfootprinttracker.MainActivity;
import com.example.carbonfootprinttracker.R;
import com.example.carbonfootprinttracker.adapters.CommunityCarbiesAdapter;
import com.example.carbonfootprinttracker.models.Carbie;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ProfileFragment extends Fragment {
    private static final String TAG = "ProfileFragment";

    @BindView(R.id.ivCircleProfile) ImageView ivProfileImage;
    @BindView(R.id.tvUsername2) TextView tvUsername;
    @BindView(R.id.tvScoreProfile) TextView tvScore;
    @BindView(R.id.btFollow) Button btFollow;
    @BindView(R.id.rvProfileCarbies) RecyclerView rvProfileCarbies;

    private ParseUser user;
    private List<Carbie> carbies;
    private CommunityCarbiesAdapter communityCarbiesAdapter;
    private FragmentManager fragmentManager;
    private Context context;
    private int score;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        carbies = new ArrayList<>();
        context = getContext();
        fragmentManager = getFragmentManager();

        communityCarbiesAdapter = new CommunityCarbiesAdapter(context, carbies, fragmentManager);
        rvProfileCarbies.setAdapter(communityCarbiesAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        rvProfileCarbies.setLayoutManager(linearLayoutManager);

        score = 0;

        try{
            user = getArguments().getParcelable("user");
        } catch (NullPointerException e) {
            Log.d(TAG, "User wasn't passed into profile fragment");
            e.printStackTrace();
            return;
        }

        if (isFollowing()) {
            btFollow.setText("Unfollow");
            btFollow.setBackground(context.getResources().getDrawable(R.drawable.no_confirm_button));
        } else {
            btFollow.setText("Follow");
            btFollow.setBackground(context.getResources().getDrawable(R.drawable.yes_confirm_button));
        }

        ParseFile photoFile = user.getParseFile("profileImage");
        if (photoFile != null) {
            String preUrl = photoFile.getUrl();
            String completeURL = preUrl.substring(0, 4) + "s" + preUrl.substring(4, preUrl.length());
            Glide.with(getContext())
                    .load(completeURL)
                    .into(ivProfileImage);
        } else {
            ivProfileImage.setBackground(getContext().getResources().getDrawable(R.drawable.ic_account_circle));
        }

        btFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFollowing()) {
                    unfollowUser();
                } else {
                    followUser();
                }
                onViewCreated(view, savedInstanceState);
            }
        });

        tvUsername.setText(user.getUsername());

        queryUsersCarbies();
    }

    private void queryUsersCarbies() {
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
        query.whereEqualTo(Carbie.KEY_USER, user);
        query.whereEqualTo(Carbie.KEY_IS_FAVORITED, false);
        query.addDescendingOrder(Carbie.KEY_CREATED_AT);
        query.findInBackground(new FindCallback<Carbie>() {
            @Override
            public void done(List<Carbie> objects, ParseException e) {
                if (e == null) {
                    for (Carbie carbie: objects) {
                        Date createdAt = carbie.getCreatedAt();
                        if (createdAt.before(calendarB.getTime()) && createdAt.after(calendarA.getTime())) {
                            score += carbie.getInt("score");
                        }
                    }
                    tvScore.setText("" + score);
                    carbies.addAll(objects);
                    communityCarbiesAdapter.notifyDataSetChanged();
                } else {
                    Log.d(TAG, "Failed to query carbies in profile fragment.");
                    e.printStackTrace();
                }
            }
        });
    }

    private boolean isFollowing() {
        JSONArray following = ParseUser.getCurrentUser().getJSONArray("following");
        if (following != null) {
            for (int i = 0; i < following.length(); i++) {
                try {
                    if ((following.get(i)).equals(user.getObjectId())) {
                        return true;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    private void followUser() {
        ParseUser.getCurrentUser().add("following", user.getObjectId());
        ParseUser.getCurrentUser().saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    e.printStackTrace();
                } else {
                    Log.d(TAG, "" + ParseUser.getCurrentUser().getUsername() + " followed " + user.getUsername());
                }
            }
        });
    }

    private void unfollowUser() {
        ParseUser.getCurrentUser().removeAll("following", Collections.singleton(user.getObjectId()));
        ParseUser.getCurrentUser().saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    e.printStackTrace();
                } else {
                    Log.d(TAG, "" + ParseUser.getCurrentUser().getUsername() + " followed " + user.getUsername());
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        MainActivity mainActivity = (MainActivity) getActivity();
        mainActivity.findViewById(R.id.tvName).setVisibility(TextView.GONE);
        mainActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mainActivity.setCalendarTabVisibility(false);
    }

    @Override
    public void onStop() {
        super.onStop();
        MainActivity mainActivity = (MainActivity) getActivity();
        mainActivity.findViewById(R.id.tvName).setVisibility(TextView.VISIBLE);
        mainActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        mainActivity.setCalendarTabVisibility(true);
    }
}
