package com.example.infomationcollector.wifilocation;

import java.util.ArrayList;

enum PointLocationType {
    GPS, WiFi
}

public class Point {
    PointLocationType type;
    public Location loc;
    ArrayList<AP> aps = new ArrayList<AP>();

    public void addAP(AP ap) {
        this.aps.add(ap);
    }

    public void setTypeToGPS() {
        this.type = PointLocationType.GPS;
    }

    public void setTypeToWiFi() {
        this.type = PointLocationType.WiFi;
    }

    public void setLocation(double latitude, double longitude){
        if (this.loc == null){
            this.loc = new Location(latitude,longitude);
        }else {
        this.loc.longitude = longitude;
        this.loc.latitude = latitude;
        }
    }
}

