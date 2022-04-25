package com.example.infomationcollector.wifilocation;

import android.util.Log;

import java.util.ArrayList;

// Simulate the server workflow
public class ServerSimulator {
    ArrayList<Point> LocationDB = new ArrayList<Point>();
    private static final String TAG = "ServerSimulator";
    public void addPoint(Point p) {
        this.LocationDB.add(p);
    }

    public Location getLocation(Point p) {
        Point res = getNearestPoint(p);
        return res != null? res.loc : null;
    }

    // It's a funny location calculation.
    public double distance(Point p1,Point p2){
        double dis = -1 ;
        boolean flag = false;

        for(AP ap1 : p1.aps){
            for(AP ap2: p2.aps){
                if (ap1.BSSID.equals(ap2.BSSID)){
                Log.d(TAG, ap1.BSSID+" "+ap1.level+" "+ap2.BSSID+" "+ap2.level);
                flag = true;
                    dis += Math.pow((ap1.level - ap2.level), 2);
                }
            }
        }
        if(flag && dis==-1){
            dis = 0;
        }
        Log.d(TAG, "distance: " + dis);
        return dis;
    }

    public Point getNearestPoint(Point req_point){
        double dis = Double.MAX_VALUE;
        double tempDis = 0;
        Point res = null;
        Log.d(TAG, "size of DB : "+ this.LocationDB.size());
        for (Point p : this.LocationDB) {
            tempDis = distance(req_point,p);
            if(dis>tempDis){
                dis = tempDis;
                res = p;
            }
        }
        if(dis > -1 && res != null) {
            Log.d(TAG, "nearestPoint x : "+ res.loc.latitude);
            return res;
        }else{
            return null;
        }
    }


}
