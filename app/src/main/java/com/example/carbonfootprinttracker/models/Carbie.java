package com.example.carbonfootprinttracker.models;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("Carbie")
public class Carbie extends ParseObject {
    private static final String TAG = "Carbie";

    public static final String KEY_USER = "user";
    public static final String KEY_SCORE = "score";
    public static final String KEY_DISTANCE = "distance";
    public static final String KEY_RIDERS = "riders";
    public static final String KEY_TRANSPORTATION = "modeOfTransport";
    public static final String KEY_TITLE = "title";
    public static final String KEY_CREATED_AT = "createdAt";
    public static final String KEY_START_LOCATION = "startLocation";
    public static final String KEY_END_LOCATION = "endLocation";
    public static final String KEY_IS_FAVORITED = "isFavorited";
    public static final String KEY_MAP_SHOT = "mapShot";
    public static final String KEY_IS_DELETED = "isDeleted";
    public static final String KEY_TRIP_LENGTH = "tripLength";

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

    public Boolean getIsDeleted() {  return getBoolean(KEY_IS_DELETED);  }

    public Boolean getIsFavorited() { return getBoolean(KEY_IS_FAVORITED); }

    public Double getTripLength() { return getDouble(KEY_TRIP_LENGTH); }

    public ParseFile getMapShot() { return getParseFile(KEY_MAP_SHOT); }

    public void setMapShot(ParseFile image) { put(KEY_MAP_SHOT, image); }

    public void setIsFavorited(Boolean status) { put(KEY_IS_FAVORITED, status); }

    public void setScore() {
        int footprint = 0;
        switch (getString(KEY_TRANSPORTATION)) {
            case "SmallCar":
                footprint = 390;
                break;
            case "MediumCar":
                footprint = 430;
                break;
            case "LargeCar":
                footprint = 600;
                break;
            case "Hybrid":
                footprint = 196;
                break;
            case "FossilFuel":
                footprint = 129;
                break;
            case "Renewable":
                footprint = 129;
                break;
            case "Bus":
                footprint = 290;
                break;
            case "Rail":
                footprint = 130;
                break;
            case "Bike":
                footprint = 25;
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

    public void setScore(int score) {
        put(KEY_SCORE, score);
    }

    public void setDistance(double distance) { put(KEY_DISTANCE, distance); }

    public void setRiders(int riders) { put(KEY_RIDERS, riders); }

    public void setTransportation(String transportation) { put(KEY_TRANSPORTATION, transportation); }

    public void setTitle(String title) { put(KEY_TITLE, title); }

    public void setTripLength(Double tripLength) { put(KEY_TRIP_LENGTH, tripLength); }

    public void setStartLocation(String startLocation) { put(KEY_START_LOCATION, startLocation); }

    public void setEndLocation(String endLocation) { put(KEY_END_LOCATION, endLocation); }

    public void setIsDeleted(Boolean isDeleted) { put(KEY_IS_DELETED, isDeleted); }

    public Carbie copy() {
        Carbie copied = new Carbie();
        copied.setTitle(this.getTitle());
        copied.setUser();
        copied.setDistance(this.getDistance());
        copied.setRiders(this.getRiders());
        copied.setTransportation(this.getTransportation());
        copied.setStartLocation(this.getStartLocation());
        copied.setEndLocation(this.getEndLocation());
        copied.setScore(this.getScore());
        copied.setIsFavorited(this.getIsFavorited());
        copied.setIsDeleted(false);
        return copied;
    }
}
