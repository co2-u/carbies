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
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
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
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ProfileFragment extends Fragment {
    private static final String TAG = "ProfileFragment";

    @BindView(R.id.ivProfileImage2) ImageView ivProfileImage;
    @BindView(R.id.tvUsername2) TextView tvUsername;
    @BindView(R.id.btFollow) Button btFollow;
    @BindView(R.id.rvProfileCarbies) RecyclerView rvProfileCarbies;

    private ParseUser user;
    private List<Carbie> carbies;
    private CommunityCarbiesAdapter communityCarbiesAdapter;
    private FragmentManager fragmentManager;
    private Context context;

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

        try{
            user = getArguments().getParcelable("user");
        } catch (NullPointerException e) {
            Log.d(TAG, "User wasn't passed into profile fragment");
            e.printStackTrace();
            return;
        }

        tvUsername.setText(user.getUsername());

        queryUsersCarbies();
    }

    private void queryUsersCarbies() {
        ParseQuery<Carbie> query = ParseQuery.getQuery(Carbie.class);
        query.include(Carbie.KEY_USER);
        query.whereEqualTo(Carbie.KEY_USER, user);
        query.whereEqualTo(Carbie.KEY_IS_FAVORITED, false);
        query.addDescendingOrder(Carbie.KEY_CREATED_AT);
        query.findInBackground(new FindCallback<Carbie>() {
            @Override
            public void done(List<Carbie> objects, ParseException e) {
                if (e == null) {
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

    @Override
    public void onResume() {
        super.onResume();
        AppCompatActivity mainActivity = (AppCompatActivity) getActivity();
        mainActivity.findViewById(R.id.tvName).setVisibility(TextView.GONE);
        mainActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        mainActivity.findViewById(R.id.bottomNavigation).setVisibility(TextView.GONE);
    }

    @Override
    public void onStop() {
        super.onStop();
        AppCompatActivity mainActivity = (AppCompatActivity) getActivity();
        mainActivity.findViewById(R.id.tvName).setVisibility(TextView.VISIBLE);
        mainActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(false);
//        mainActivity.findViewById(R.id.bottomNavigation).setVisibility(TextView.VISIBLE);
    }
}
