package com.example.infomationcollector.wifilocation;

import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.infomationcollector.InfoHelper;
import com.example.infomationcollector.R;

public class WifiLocation extends AppCompatActivity {
    // resources
    private static final String TAG = "WiFiLocation";
    private TextView tvw;
    private ClipboardManager mClipboardManager;
    ClipboardManager.OnPrimaryClipChangedListener mOnPrimaryClipChangedListener;

    private Button buttonRec;
    private Button buttonRecAuto;
    private Button buttonLoc;
    private Button buttonLocGPS;
    private EditText editTextX;
    private EditText editTextY;
    private ServerSimulator server = new ServerSimulator();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wifi_location);
        tvw = findViewById(R.id.tvw);
        editTextX = findViewById(R.id.editx);
        editTextY = findViewById(R.id.edity);
        tvw.setText("");
        tvw.setMovementMethod(ScrollingMovementMethod.getInstance());
        final Context context = this;

        buttonRec = (Button)findViewById(R.id.button_record);
        buttonRec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InfoHelper infoHelper = new InfoHelper(WifiLocation.this);
                Point p = infoHelper.getPoint();
                double longitude = Double.parseDouble(editTextX.getText().toString());
                double latitude = Double.parseDouble(editTextY.getText().toString());
                p.setLocation(latitude,longitude);
                Log.d(TAG, "get tempPoint going to be located"+p.aps.size());
                server.addPoint(p);
                displayPoint(p);
                Toast t = Toast.makeText(context,"???????????????",Toast.LENGTH_LONG);
                t.show();

            }
        });

        buttonRecAuto = (Button)findViewById(R.id.button_record_auto);
        buttonRecAuto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InfoHelper infoHelper = new InfoHelper(WifiLocation.this);
                Point p = infoHelper.getPoint();
                server.addPoint(p);
                displayPoint(p);
                Toast t = Toast.makeText(context,"???????????????",Toast.LENGTH_LONG);
                t.show();
            }
        });

        buttonLoc = findViewById(R.id.button_location);
        buttonLoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // ??????GPS?????????????????????????????????
                InfoHelper infoHelper = new InfoHelper(context);
                Toast t;
                Point p = server.getNearestPoint(infoHelper.getPoint());
                if(p != null){
                    t = Toast.makeText(context,"WiFi??????????????????",Toast.LENGTH_LONG);
                    p.setTypeToWiFi();
                    displayIndo(p);
                    displayPoint(p);
                }else{
                    t = Toast.makeText(context,"WiFi???????????????????????????????????????",Toast.LENGTH_LONG);
                }
                t.show();
            }
        });

        buttonLocGPS = findViewById(R.id.button_location_gps);
        buttonLocGPS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InfoHelper infoHelper = new InfoHelper(context);
                Toast t;
                Point p = infoHelper.getPoint();
                if(p != null){
                    t = Toast.makeText(context,"GPS????????????",Toast.LENGTH_LONG);
                    p.setTypeToGPS();
                    displayIndo(p);
                }else{
                    t = Toast.makeText(context,"GPS????????????",Toast.LENGTH_LONG);
                }
                t.show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void appendInfo(String str) {
        tvw.append(str + "\n");
    }

    private void displayIndo(Point p){
        InfoHelper infoHelper = new InfoHelper(WifiLocation.this);
        // clear
        tvw.setText("");

        appendInfo("??????????????????GPS: " + infoHelper.isOpenGPS());
        appendInfo("IP??????: " + infoHelper.getLocalInetAddress());
        appendInfo("MAC??????: " + infoHelper.getMacAddress());
        if(p.type.equals(PointLocationType.GPS)){
            appendInfo("GPS?????????????????????GPS??????");
            appendInfo("GPS??????(Longitude): " + p.loc.longitude);
            appendInfo("GPS??????(Latitude): " + p.loc.latitude);
        }else{
            appendInfo("GPS?????????????????????WiFi????????????");
            Location loc = server.getLocation(infoHelper.getPoint());
            appendInfo("WiFi????????????(Latitude): " + loc.latitude);
            appendInfo("WiFi????????????(Longitude): " + loc.longitude);
        }
    }

    private void displayPoint(Point p){
        // clear
        tvw.setText("");

        appendInfo("???????????????");
        appendInfo("????????????: " + p.type.toString());
        appendInfo("??????(Longitude): " + p.loc.longitude);
        appendInfo("??????(Latitude): " + p.loc.latitude);
        appendInfo("Ap????????????: ");
        for(AP ap: p.aps){
            appendInfo("BSSID: " + ap.BSSID + "   level: " + ap.level + "   SSID: " + ap.SSID);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}