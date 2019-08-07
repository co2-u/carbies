package com.example.carbonfootprinttracker;

import android.app.Application;

import com.example.carbonfootprinttracker.models.Carbie;
import com.example.carbonfootprinttracker.models.DailySummary;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.parse.Parse;
import com.parse.ParseObject;

public class CarbonApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        ParseObject.registerSubclass(Carbie.class);
        ParseObject.registerSubclass(DailySummary.class);
        final Parse.Configuration configuration = new Parse.Configuration.Builder(this)
                .applicationId(getResources().getString(R.string.myAppId))
                .clientKey(getResources().getString(R.string.myClientKey))
                .server(getResources().getString(R.string.myServer))
                .build();

        Parse.initialize(configuration);

        Places.initialize(getApplicationContext(), getResources().getString(R.string.google_maps_api_key));
        PlacesClient placesClient = Places.createClient(this);
    }
}
