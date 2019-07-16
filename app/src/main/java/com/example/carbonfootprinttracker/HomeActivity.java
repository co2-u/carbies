package com.example.carbonfootprinttracker;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.example.carbonfootprinttracker.fragments.ComposeFragment;
import com.example.carbonfootprinttracker.fragments.CurrentScoreFragment;
import com.example.carbonfootprinttracker.fragments.DailyLogFragment;
import com.example.carbonfootprinttracker.fragments.InfoFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HomeActivity extends AppCompatActivity {

    private static final String TAG = "HomeActivity";

    @BindView(R.id.bottomNavigation) BottomNavigationView bottomNavigationView;

    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);

        fragmentManager = getSupportFragmentManager();
        final Fragment currentScoreFragment = new CurrentScoreFragment();
        final Fragment composeFragment = new ComposeFragment();
        final Fragment dailyLogFragment = new DailyLogFragment();
        final Fragment infoFragment = new InfoFragment();

        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        Fragment fragment;
                        switch (item.getItemId()) {
                            case R.id.currentScoreTab:
                                fragment = currentScoreFragment;
                                break;
                            case R.id.composeTab:
                                fragment = composeFragment;
                                break;
                            case R.id.dailyLogTab:
                                fragment = dailyLogFragment;
                                break;
                            case R.id.infoTab:
                                fragment = infoFragment;
                                break;
                            default:
                                fragment = currentScoreFragment;
                                break;
                        }
                        fragmentManager.beginTransaction().replace(R.id.fragmentPlaceholder, fragment).commit();
                        return true;
                    }
                });

        fragmentManager.beginTransaction().replace(R.id.fragmentPlaceholder, currentScoreFragment).commit();
    }
}
