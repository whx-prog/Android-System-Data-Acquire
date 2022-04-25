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
                Toast t = Toast.makeText(context,"已手动记录",Toast.LENGTH_LONG);
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
                Toast t = Toast.makeText(context,"已自动记录",Toast.LENGTH_LONG);
                t.show();
            }
        });

        buttonLoc = findViewById(R.id.button_location);
        buttonLoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 模拟GPS未开启或没有定位权限。
                InfoHelper infoHelper = new InfoHelper(context);
                Toast t;
                Point p = server.getNearestPoint(infoHelper.getPoint());
                if(p != null){
                    t = Toast.makeText(context,"WiFi指纹定位成功",Toast.LENGTH_LONG);
                    p.setTypeToWiFi();
                    displayIndo(p);
                    displayPoint(p);
                }else{
                    t = Toast.makeText(context,"WiFi指纹定位失败，找不到相关点",Toast.LENGTH_LONG);
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
                    t = Toast.makeText(context,"GPS定位成功",Toast.LENGTH_LONG);
                    p.setTypeToGPS();
                    displayIndo(p);
                }else{
                    t = Toast.makeText(context,"GPS定位失败",Toast.LENGTH_LONG);
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

        appendInfo("是否已经打开GPS: " + infoHelper.isOpenGPS());
        appendInfo("IP地址: " + infoHelper.getLocalInetAddress());
        appendInfo("MAC地址: " + infoHelper.getMacAddress());
        if(p.type.equals(PointLocationType.GPS)){
            appendInfo("GPS定位成功，采用GPS位置");
            appendInfo("GPS位置(Longitude): " + p.loc.longitude);
            appendInfo("GPS位置(Latitude): " + p.loc.latitude);
        }else{
            appendInfo("GPS定位失败，采用WiFi指纹定位");
            Location loc = server.getLocation(infoHelper.getPoint());
            appendInfo("WiFi指纹定位(Latitude): " + loc.latitude);
            appendInfo("WiFi指纹定位(Longitude): " + loc.longitude);
        }
    }

    private void displayPoint(Point p){
        // clear
        tvw.setText("");

        appendInfo("当前点信息");
        appendInfo("定位方法: " + p.type.toString());
        appendInfo("经度(Longitude): " + p.loc.longitude);
        appendInfo("维度(Latitude): " + p.loc.latitude);
        appendInfo("Ap信息列表: ");
        for(AP ap: p.aps){
            appendInfo("BSSID: " + ap.BSSID + "   level: " + ap.level + "   SSID: " + ap.SSID);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}