package com.example.carbonfootprinttracker.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.example.carbonfootprinttracker.R;
import com.example.carbonfootprinttracker.adapters.CommunityPagerAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CommunityFragment extends Fragment {
    private static final String TAG = "CommunityFragment";

    @BindView(R.id.vpCommunity) ViewPager vpCommunity;
    @BindView(R.id.tlCommunity) TabLayout tlCommunity;
    @BindView(R.id.fabFollow) FloatingActionButton fabFollow;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_community, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        vpCommunity.setAdapter(new CommunityPagerAdapter(getChildFragmentManager(), getContext()));
        tlCommunity.setupWithViewPager(vpCommunity);
        tlCommunity.setInlineLabel(true);
        tlCommunity.getTabAt(0).setIcon(R.drawable.ic_account_multiple);
        tlCommunity.getTabAt(1).setIcon(R.drawable.ic_web);
        fabFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FollowFragment followFragment = new FollowFragment();
                getFragmentManager().beginTransaction()
                        .replace(R.id.fragmentPlaceholder, followFragment)
                        .addToBackStack(TAG)
                        .commit();
            }
        });
    }

    public void onResume() {
        super.onResume();
        AppCompatActivity mainActivity = (AppCompatActivity) getActivity();
        mainActivity.findViewById(R.id.tvName).setVisibility(TextView.GONE);
        mainActivity.getSupportActionBar().hide();
    }

    @Override
    public void onStop() {
        super.onStop();
        AppCompatActivity mainActivity = (AppCompatActivity) getActivity();
        mainActivity.findViewById(R.id.tvName).setVisibility(TextView.VISIBLE);
        mainActivity.getSupportActionBar().show();
    }
}
