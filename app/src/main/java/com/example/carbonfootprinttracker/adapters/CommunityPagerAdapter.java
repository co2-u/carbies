package com.example.carbonfootprinttracker.adapters;

import android.content.Context;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.carbonfootprinttracker.fragments.CommunityPagesFragment;

public class CommunityPagerAdapter extends FragmentPagerAdapter {
    final int PAGE_COUNT = 2;

    private Fragment[] tabs;
    private String tabTitles[];
    private Context context;

    public CommunityPagerAdapter (FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
        tabs = new Fragment[] {
                new CommunityPagesFragment(0),
                new CommunityPagesFragment(1)
        };
        tabTitles = new String[] { "All", "Following" };
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public Fragment getItem(int position) {
        return tabs[position];
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles[position];
    }
}
