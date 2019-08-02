package com.example.carbonfootprinttracker.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.example.carbonfootprinttracker.MainActivity;
import com.example.carbonfootprinttracker.R;
import com.example.carbonfootprinttracker.adapters.TabAdapter;
import com.google.android.material.tabs.TabLayout;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PageFragment extends Fragment {

    public static final String ARG_PAGE = "ARG_PAGE";
    @BindView(R.id.viewpager)
    ViewPager viewPager;
    @BindView(R.id.sliding_tabs)
    TabLayout tabLayout;
    private TabAdapter tabAdapter;

    private int mPage;

    public static Fragment newInstance(int page) {
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        Fragment fragment;
        if (page == 1) {
            fragment = new DailyLogFragment();
        } else {
            fragment = new FavoritesFragment();
        }
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_page, container, false);
//        TextView textView = (TextView) view;
//        textView.setText("Fragment #" );
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        tabAdapter = new TabAdapter(getFragmentManager(), getContext());
        viewPager.setAdapter(tabAdapter);
        // Give the TabLayout the ViewPager
        tabLayout.setupWithViewPager(viewPager);
    }
}