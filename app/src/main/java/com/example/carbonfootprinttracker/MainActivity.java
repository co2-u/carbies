package com.example.carbonfootprinttracker;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.ShareActionProvider;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.carbonfootprinttracker.alarm.MyAlarmReceiver;
import com.example.carbonfootprinttracker.fragments.ComposeFragment;
import com.example.carbonfootprinttracker.fragments.CurrentScoreFragment;
import com.example.carbonfootprinttracker.fragments.DailyLogFragment;
import com.example.carbonfootprinttracker.fragments.FavoritesFragment;
import com.example.carbonfootprinttracker.fragments.InfoFragment;
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
    @BindView(R.id.ivShare) ImageView ivShare;

    private ShareActionProvider shareActionProvider;
    private FragmentManager fragmentManager;
    public static int score;
    private Menu menu;

    private AlarmManager alarmMgr;
    private PendingIntent alarmIntent;

    //For SyncService
    // The authority for the sync adapter's content provider
    public static final String AUTHORITY = "com.example.android.datasync.provider";
    // An account type, in the form of a domain name
    public static final String ACCOUNT_TYPE = "example.com";
    // The account name
    public static final String ACCOUNT = "dummyaccount";
    // Instance fields
    Account mAccount;
    // Sync interval constants
    public static final long SECONDS_PER_MINUTE = 60L;
    public static final long SYNC_INTERVAL_IN_MINUTES = 1L;
    public static final long SYNC_INTERVAL =
            SYNC_INTERVAL_IN_MINUTES *
                    SECONDS_PER_MINUTE;
    // Global variables
    // A content resolver for accessing the provider
    ContentResolver mResolver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        score = 0;
        isValidUserLoggedIn();

        //onBoarding stuff
        SharedPreferences preferences =
                getSharedPreferences("my_preferences", MODE_PRIVATE);

        if(!preferences.getBoolean("onboarding_complete",false)){

            Intent onBoarding = new Intent(this, OnBoardingActivity.class);
            startActivity(onBoarding);

            // Close the OnboardingActivity
            finish();
            return; }

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        fragmentManager = getSupportFragmentManager();
        final Fragment currentScoreFragment = new CurrentScoreFragment();
        final Fragment composeFragment = new ComposeFragment();
        final Fragment dailyLogFragment = new DailyLogFragment();
        final Fragment infoFragment = new InfoFragment();
        final Fragment favoritesFragment = new FavoritesFragment();

        ivShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, "My CO2&U score is " + score);
                startActivity(Intent.createChooser(intent, "Share your Daily Score!"));
            }
        });


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
                                fragment = dailyLogFragment;
                                fragTag = "DailyLogFragment";
                                break;
                            case R.id.favoritesTab:
                                fragment = favoritesFragment;
                                fragTag = "FavoritesFragment";
                                break;
                            case R.id.moreTab:
                                fragment = infoFragment;
                                fragTag = "InfoFragment";
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

//        // Create the dummy account for SyncService
//        mAccount = CreateSyncAccount(this);
//
//        // Get the content resolver for your app
//        mResolver = getContentResolver();
//        /*
//         * Turn on periodic syncing
//         */
//        ContentResolver.addPeriodicSync(
//                mAccount,
//                AUTHORITY,
//                Bundle.EMPTY,
//                SYNC_INTERVAL);

        fragmentManager.beginTransaction().replace(R.id.fragmentPlaceholder, currentScoreFragment, "CurrentScoreFragment").commit();
        scheduleAlarm();
    }

    // Inflate toolbar with
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
                fragmentManager.beginTransaction().replace(R.id.fragmentPlaceholder, settingsFragment).commit();
            case android.R.id.home:
                if (fragmentManager.getBackStackEntryCount() > 0) {
                    Log.i(TAG, "popping backstack");
                    fragmentManager.popBackStack();
                } else {
                    Log.i(TAG, "nothing on backstack, calling super");
                }
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

    /**
     * Create a new dummy account for the sync adapter
     *
     * @param context The application context
     */
    public static Account CreateSyncAccount(Context context) {
        // Create the account type and default account
        Account newAccount = new Account(
                ACCOUNT, ACCOUNT_TYPE);
        // Get an instance of the Android account manager
        AccountManager accountManager =
                (AccountManager) context.getSystemService(
                        ACCOUNT_SERVICE);
        /*
         * Add the account and account type, no password or user data
         * If successful, return the Account object, otherwise report an error.
         */
        if (accountManager.addAccountExplicitly(newAccount, null, null)) {
            /*
             * If you don't set android:syncable="true" in
             * in your <provider> element in the manifest,
             * then call context.setIsSyncable(account, AUTHORITY, 1)
             * here.
             */
            Log.d(TAG, "Added account successfully");
        } else {
            /*
             * The account exists or some other error occurred. Log this, report it,
             * or handle it internally.
             */
            Log.d(TAG, "Error adding account");
        }
        return newAccount;
    }

    // Setup a recurring alarm every half hour
    public void scheduleAlarm() {
        Log.d(TAG, "scheduledAlarm");
        alarmMgr = (AlarmManager)this.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, MyAlarmReceiver.class);
        alarmIntent = PendingIntent.getBroadcast(this, MyAlarmReceiver.REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Set the alarm to start at approximately 2:00 p.m.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 2);
        calendar.set(Calendar.MINUTE, 4);

        alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, alarmIntent);
    }
}
