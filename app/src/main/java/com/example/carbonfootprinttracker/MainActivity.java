package com.example.carbonfootprinttracker;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ShareActionProvider;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

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

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    @BindView(R.id.bottomNavigation) BottomNavigationView bottomNavigationView;
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.ivShare)
    ImageView ivShare;

    private ShareActionProvider shareActionProvider;
    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

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
                intent.putExtra(Intent.EXTRA_TEXT, String.valueOf("score!!"));
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

        fragmentManager.beginTransaction().replace(R.id.fragmentPlaceholder, currentScoreFragment, "CurrentScoreFragment").commit();
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
}
