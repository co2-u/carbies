package com.example.carbonfootprinttracker.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("DailySummary")
public class DailySummary extends ParseObject {
    private static final String TAG = "DailySummary";

    public static final String KEY_USER = "user";
    public static final String KEY_SCORE = "score";
    public static final String KEY_RECOMMENDATION = "recommendation";
    public static final String KEY_MILES_WALKED = "milesWalked";
    public static final String KEY_MILES_BIKED = "milesBiked";
    public static final String KEY_MILES_GAS_DRIVEN = "milesGasDriven";
    public static final String KEY_MILES_E_DRIVEN = "milesEDriven";
    public static final String KEY_MILES_CARPOOLED= "milesCarpooled";
    public static final String KEY_MILES_PUBLIC_TRANSPORT= "milesPublicTransport";

    public ParseUser getUser() { return getParseUser(KEY_USER); }

    public void setUser() { put(KEY_USER, ParseUser.getCurrentUser()); }

    public Integer getScore() { return getInt(KEY_SCORE); }

    public void setScore(Integer score) { put(KEY_SCORE, score); }

    public String getRecommendation() { return getString(KEY_RECOMMENDATION); }

    public void setRecommendation(String recommendation) { put(KEY_RECOMMENDATION, recommendation); }

    public Integer getMilesWalked() { return getInt(KEY_MILES_WALKED); }

    public void setMilesWalked(Integer miles) { put(KEY_MILES_WALKED, miles); }

    public Integer getMilesBiked() { return getInt(KEY_MILES_BIKED); }

    public void setMilesBiked(Integer miles) { put(KEY_MILES_BIKED, miles); }

    public Integer getMilesGasDriven() { return getInt(KEY_MILES_GAS_DRIVEN); }

    public void setMilesGasDriven(Integer miles) { put(KEY_MILES_GAS_DRIVEN, miles); }

    public Integer getMilesEDriven() { return getInt(KEY_MILES_E_DRIVEN); }

    public void setMilesEDriven(Integer miles) { put(KEY_MILES_E_DRIVEN, miles); }

    public Integer getMilesCarpooled() { return getInt(KEY_MILES_CARPOOLED); }

    public void setMilesCarpooled(Integer miles) { put(KEY_MILES_CARPOOLED, miles); }

    public Integer getMilesPublicTransport() { return getInt(KEY_MILES_PUBLIC_TRANSPORT); }

    public void setMilesPublicTransport(Integer miles) { put(KEY_MILES_PUBLIC_TRANSPORT, miles); }

}
