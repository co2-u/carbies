package com.example.carbonfootprinttracker.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.Date;

@ParseClassName("Carbie")
public class Carbie extends ParseObject {
    public static final String KEY_USER = "user";
    public static final String KEY_SCORE = "score";
    public static final String KEY_DISTANCE = "distance";
    public static final String KEY_RIDERS = "riders";
    public static final String KEY_TRANSPORTATION = "modeOfTransport";
    public static final String KEY_TITLE = "title";
    public static final String KEY_CREATED_AT = "createdAt";
    public static final String KEY_START_LOCATION = "startLocation";
    public static final String KEY_END_LOCATION = "endLocation";

    public ParseUser getUser() { return getParseUser(KEY_USER); }

    public void setUser() { put(KEY_USER, ParseUser.getCurrentUser()); }

    public Integer getScore() {
        Integer i = new Integer(5);
        try {
            i = getInt(KEY_SCORE);
            return i;
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return i;
    }
    public Double getDistance() { return getDouble(KEY_DISTANCE); }

    public Integer getRiders() { return getInt(KEY_RIDERS); }

    public String getTransportation() { return getString(KEY_TRANSPORTATION); }

    public String getTitle() { return getString(KEY_TITLE); }

    public String getStartLocation() { return getString(KEY_START_LOCATION); }

    public String getEndLocation() { return getString(KEY_END_LOCATION); }

    public String getKeyCreatedAt() { return getString(KEY_CREATED_AT); }

    public void setScore() {
        int footprint = 0;
        switch (getString(KEY_TRANSPORTATION)) {
            case "SmallCar":
                footprint = 400;
                break;
            case "MediumCar":
                footprint = 400;
                break;
            case "LargeCar":
                footprint = 400;
                break;
            case "Hybrid":
                footprint = 196;
                break;
            case "Electric":
                footprint = 129;
                break;
            case "Bus":
                footprint = 290;
                break;
            case "LightRail":
                footprint = 163;
                break;
            case "Bike":
                footprint = 25;
                break;
            case "HeavyRail":
                footprint = 100;
                break;
            case "Walk":
                footprint = 10;
                break;
            case "Rideshare":
                footprint = 400 / getRiders();
                break;
        }
        int score = (int)(footprint * getDistance());
        put(KEY_SCORE, score);
    }

    public void setDistance(int distance) { put(KEY_DISTANCE, distance); }

    public void setRiders(int riders) { put(KEY_RIDERS, riders); }

    public void setTransportation(String transportation) { put(KEY_TRANSPORTATION, transportation); }

    public void setTitle(String title) { put(KEY_TITLE, title); }

    public void setStartLocation(String startLocation) { put(KEY_START_LOCATION, startLocation); }

    public void setEndLocation(String endLocation) { put(KEY_END_LOCATION, endLocation); }

    public void setCreatedAt(Date createdAt) { put(KEY_CREATED_AT, createdAt); }
}
