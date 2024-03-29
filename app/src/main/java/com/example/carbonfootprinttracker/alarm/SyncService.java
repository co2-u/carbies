package com.example.carbonfootprinttracker.alarm;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.example.carbonfootprinttracker.models.Carbie;
import com.example.carbonfootprinttracker.models.DailySummary;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class SyncService extends IntentService {
    private static final String TAG = "SyncService";
    private static final Integer MAX_CARBON_SCORE = 8000;

    public SyncService() {
        super("SyncService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d("SyncService", "Service running");
        DailySummary dailySummary = new DailySummary();
        dailySummary.setUser();
        queryCarbies(dailySummary);
    }

    protected void queryCarbies(DailySummary dailySummary) {
        Date date = new Date();
        Calendar calendarA = Calendar.getInstance();
        calendarA.setTime(date);
        calendarA.set(Calendar.HOUR_OF_DAY, 0);
        Calendar calendarB = Calendar.getInstance();
        calendarB.setTime(date);
        calendarB.set(Calendar.HOUR_OF_DAY, 23);
        calendarB.set(Calendar.MINUTE, 59);

        ParseQuery<Carbie> query = ParseQuery.getQuery(Carbie.class);
        query.include(Carbie.KEY_USER);
        query.whereEqualTo(Carbie.KEY_USER, ParseUser.getCurrentUser());
        query.whereEqualTo(Carbie.KEY_IS_FAVORITED, false);
        query.whereGreaterThanOrEqualTo(Carbie.KEY_CREATED_AT, calendarA.getTime());
        query.whereLessThan(Carbie.KEY_CREATED_AT, calendarB.getTime());
        query.findInBackground(new FindCallback<Carbie>() {
            @Override
            public void done(List<Carbie> carbies, ParseException e) {
                if (e != null) {
                    Log.d(TAG, "Failed to query carbies in SyncService");
                    e.printStackTrace();
                    return;
                } else {
                    Log.d(TAG, "Queried carbies in SyncService");
                    saveDailySummary(carbies, dailySummary);
                }
            }
        });
    }

    public void saveDailySummary(List<Carbie> carbies, DailySummary dailySummary) {
        double currentScore = 0;
        double milesWalked = 0;
        double milesBiked = 0;
        double milesGasDriven = 0;
        double milesEDriven = 0;
        double milesPublicTransport = 0;
        double milesCarpooled = 0;

        for (int i = 0; i < carbies.size(); i++) {
            Carbie carbie = carbies.get(i);
            currentScore += carbie.getScore();

            switch (carbie.getTransportation()) {
                case "SmallCar":
                    milesGasDriven += carbie.getDistance();
                    break;
                case "MediumCar":
                    milesGasDriven += carbie.getDistance();
                    break;
                case "LargeCar":
                    milesGasDriven += carbie.getDistance();
                    break;
                case "Bike":
                    milesBiked += carbie.getDistance();
                    break;
                case "Hybrid":
                    milesEDriven += carbie.getDistance();
                    break;
                case "Electric":
                    milesEDriven += carbie.getDistance();
                    break;
                case "Bus":
                    milesPublicTransport += carbie.getDistance();
                    break;
                case "Rail":
                    milesPublicTransport += carbie.getDistance();
                    break;
                case "Walk":
                    milesWalked += carbie.getDistance();
                    break;
                case "Rideshare":
                    milesCarpooled += carbie.getDistance();
                    break;
            }
            dailySummary.setScore(currentScore);
            dailySummary.setMilesBiked(milesBiked);
            dailySummary.setMilesCarpooled(milesCarpooled);
            dailySummary.setMilesEDriven(milesEDriven);
            dailySummary.setMilesGasDriven(milesGasDriven);
            dailySummary.setMilesPublicTransport(milesPublicTransport);
            dailySummary.setMilesWalked(milesWalked);

            String recommendation = "";
            if (currentScore < MAX_CARBON_SCORE) {
                recommendation = "Good job on keeping to your goals.";
            } else {
                recommendation = "Room for improvement.";
            }
            dailySummary.setRecommendation(recommendation);

            dailySummary.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        Log.d(TAG, "Successfully saved DailySummary in SyncService");
                    } else {
                        Log.d(TAG, "Failed to save DailySummary in SyncService");
                        e.printStackTrace();
                    }
                }
            });
        }
    }
}
