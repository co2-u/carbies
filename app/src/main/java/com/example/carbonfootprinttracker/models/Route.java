package com.example.carbonfootprinttracker.models;

import com.google.android.gms.maps.model.Polyline;
import com.google.maps.model.DirectionsLeg;
import com.google.maps.model.Distance;
import com.google.maps.model.Duration;

public class Route {
    private Polyline polyline;
    private DirectionsLeg leg;

    public Route(Polyline polyline, DirectionsLeg leg) {
        this.polyline = polyline;
        this.leg = leg;
    }

    public Polyline getPolyline() {
        return polyline;
    }

    public DirectionsLeg getLeg() {
        return leg;
    }

    public Duration getDuration() {
        return leg.duration;
    }

    public Distance getDistance() {
        return  leg.distance;
    }

    public String getStartAddress() { return leg.startAddress; }

    public String getEndAddress() { return leg.endAddress; }
}
