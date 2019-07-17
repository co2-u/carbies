package com.example.carbonfootprinttracker.models;

import com.parse.ParseClassName;
import com.parse.ParseFile;
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

    public int getScore() { return Integer.parseInt(KEY_SCORE); }

    public int getDistance() { return Integer.parseInt(KEY_DISTANCE); }

    public int getRiders() { return Integer.parseInt(KEY_RIDERS); }

    public String getTransportation() { return KEY_TRANSPORTATION; }

    public String getTitle() { return KEY_TITLE; }

    public String getStartLocation() { return KEY_START_LOCATION; }

    public String getEndLocation() { return KEY_END_LOCATION; }

    public String getKeyCreatedAt() { return KEY_CREATED_AT; }

    public void setScore(int score) {
        //TODO make actual equation
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
