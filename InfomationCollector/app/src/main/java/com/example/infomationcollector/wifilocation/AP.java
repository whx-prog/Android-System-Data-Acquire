package com.example.infomationcollector.wifilocation;

public class AP {
    String SSID;
    String BSSID;
    int level;

    public AP(String SSID, String BSSID, int level){
        this.SSID = SSID;
        this.BSSID = BSSID;
        this.level = level;
    }
}
