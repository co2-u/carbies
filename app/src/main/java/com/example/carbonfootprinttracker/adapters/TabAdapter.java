package com.example.carbonfootprinttracker.adapters;

import android.content.Context;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.carbonfootprinttracker.fragments.LogFragment;

public class TabAdapter extends FragmentPagerAdapter {

    final int PAGE_COUNT = 2;
    private String tabTitles[] = new String[] {"Daily Log", "Frequently Used"};
    private Context context;

    public TabAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public  Fragment getItem(int position) {
        return getItemTwo(position);
    }

    public static Fragment getItemTwo(int position) {
        return LogFragment.newInstance(position + 1);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        // Generate title based on item position
        return tabTitles[position];
    }
}