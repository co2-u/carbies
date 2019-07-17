package com.example.carbonfootprinttracker;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.example.carbonfootprinttracker.fragments.ComposeFragment;
import com.example.carbonfootprinttracker.fragments.CurrentScoreFragment;
import com.example.carbonfootprinttracker.fragments.DailyLogFragment;
import com.example.carbonfootprinttracker.fragments.InfoFragment;
import com.example.carbonfootprinttracker.fragments.SettingsFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HomeActivity extends AppCompatActivity {

    private static final String TAG = "HomeActivity";

    @BindView(R.id.bottomNavigation) BottomNavigationView bottomNavigationView;
    @BindView(R.id.toolbar) Toolbar toolbar;

    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

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

    // Inflate toolbar with
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.settingsTab:
                Fragment settingsFragment = new SettingsFragment();
                fragmentManager.beginTransaction().replace(R.id.fragmentPlaceholder, settingsFragment).commit();
        }
        return super.onOptionsItemSelected(item);
    }
}
