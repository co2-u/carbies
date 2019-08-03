package com.example.carbonfootprinttracker;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.ShareActionProvider;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.carbonfootprinttracker.alarm.MyAlarmReceiver;
import com.example.carbonfootprinttracker.fragments.CommunityFragment;
import com.example.carbonfootprinttracker.fragments.ComposeFragment;
import com.example.carbonfootprinttracker.fragments.CurrentScoreFragment;
import com.example.carbonfootprinttracker.fragments.LogFragment;
import com.example.carbonfootprinttracker.fragments.SettingsFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseSession;
import com.parse.ParseUser;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    @BindView(R.id.bottomNavigation) BottomNavigationView bottomNavigationView;
    @BindView(R.id.toolbar) Toolbar toolbar;

    private ShareActionProvider shareActionProvider;
    private FragmentManager fragmentManager;
    public static int score;
    private Menu menu;
    private AlarmManager alarmMgr;
    private PendingIntent alarmIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        score = 0;
        isValidUserLoggedIn();

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        fragmentManager = getSupportFragmentManager();
        final Fragment currentScoreFragment = new CurrentScoreFragment();
        final Fragment composeFragment = new ComposeFragment();
        final Fragment logFragment = new LogFragment();
        final Fragment settingsFragment = new SettingsFragment();
        final Fragment communityFragment = new CommunityFragment();

        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        Fragment fragment;
                        String fragTag = "";
                        switch (item.getItemId()) {
                            case R.id.currentScoreTab:
                                fragment = currentScoreFragment;
                                fragTag = "CurrentScoreFragment";
                                break;
                            case R.id.composeTab:
                                fragment = composeFragment;
                                fragTag = "ComposeFragment";
                                break;
                            case R.id.dailyLogTab:
                                fragment = logFragment;
                                fragTag = "DailyLogFragment";
                                break;
                            case R.id.communityTab:
                                fragment = communityFragment;
                                fragTag = "CommunityFragment";
                                break;
                            case R.id.moreTab:
                                fragment = settingsFragment;
                                fragTag = "SettingsFragment";
                                break;
                            default:
                                fragment = currentScoreFragment;
                                fragTag = "CurrentScoreFragment";
                                break;
                        }
                        fragmentManager.beginTransaction().replace(R.id.fragmentPlaceholder, fragment, fragTag).commit();
                        return true;
                    }
                });

        fragmentManager.beginTransaction().replace(R.id.fragmentPlaceholder, currentScoreFragment, "CurrentScoreFragment").commit();
        scheduleAlarm();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public void setSettingsTabVisibility(boolean status) {
        if (menu == null) {
            return;
        } else {
            menu.findItem(R.id.settingsTab).setVisible(status);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.settingsTab:
                Fragment settingsFragment = new SettingsFragment();
                fragmentManager
                        .beginTransaction()
                        .replace(R.id.fragmentPlaceholder, settingsFragment)
                        .addToBackStack("InfoFragment")
                        .commit();
                break;
            case android.R.id.home:
                if (fragmentManager.getBackStackEntryCount() > 0) {
                    Log.d(TAG, "popping backstack");
                    fragmentManager.popBackStack();
                } else {
                    Log.d(TAG, "nothing on backstack, calling super");
                }
                break;
            }
        return super.onOptionsItemSelected(item);
    }

    // Checks for changes in user's credentials from another device, ie changed password
    private void isValidUserLoggedIn() {
        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser == null) {
            return;
        } else {
            ParseSession.getCurrentSessionInBackground(new GetCallback<ParseSession>() {
                @Override
                public void done(ParseSession object, ParseException e) {
                    boolean isValid = object != null && object.getSessionToken() != null && !object.getSessionToken().isEmpty();
                    if (!isValid) {
                        Log.d(TAG, "Invalid user");
                        ParseUser.logOut();
                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Log.d(TAG, "Valid user");
                        return;
                    }
                }
            });
        }
    }

    public void scheduleAlarm() {
        alarmMgr = (AlarmManager)this.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, MyAlarmReceiver.class);
        alarmIntent = PendingIntent.getBroadcast(this, MyAlarmReceiver.REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        // Set the alarm to start at approximately end of day.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 50);
        // Repeat alarm every day
//        alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, alarmIntent);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmMgr.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), alarmIntent);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            alarmMgr.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), alarmIntent);
        } else {
            alarmMgr.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), alarmIntent);
        }
    }

    public void cancelAlarm() {
        Intent intent = new Intent(getApplicationContext(), MyAlarmReceiver.class);
        final PendingIntent pIntent = PendingIntent.getBroadcast(this, MyAlarmReceiver.REQUEST_CODE,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarm = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        alarm.cancel(pIntent);
    }
}
