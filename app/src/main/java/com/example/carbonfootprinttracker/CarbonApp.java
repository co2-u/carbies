package com.example.carbonfootprinttracker;

import android.app.Application;

import com.example.carbonfootprinttracker.models.Carbie;
import com.parse.Parse;
import com.parse.ParseObject;

public class CarbonApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        ParseObject.registerSubclass(Carbie.class);
        final Parse.Configuration configuration = new Parse.Configuration.Builder(this)
                .applicationId(getResources().getString(R.string.myAppId))
                .clientKey(getResources().getString(R.string.myClientKey))
                .server(getResources().getString(R.string.myServer))
                .build();

        Parse.initialize(configuration);
    }
}
