package com.example.carbonfootprinttracker;

import android.app.Application;

import com.parse.Parse;

public class CarbonApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        final Parse.Configuration configuration = new Parse.Configuration.Builder(this)
                .applicationId("myCarbie")
                .clientKey("fbu_class_of_2019")
                .server("https://carbon-footprint-tracker.herokuapp.com/parse")
                .build();

        Parse.initialize(configuration);
    }
}
