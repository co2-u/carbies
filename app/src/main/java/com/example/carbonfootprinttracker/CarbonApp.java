package com.example.carbonfootprinttracker;

import android.app.Application;

import com.parse.Parse;

public class CarbonApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        final Parse.Configuration configuration = new Parse.Configuration.Builder(this)
                .applicationId(getResources().getString(R.string.myAppId))
                .clientKey(getResources().getString(R.string.myClientKey))
                .server(getResources().getString(R.string.myServer))
                .build();

        Parse.initialize(configuration);
    }
}
