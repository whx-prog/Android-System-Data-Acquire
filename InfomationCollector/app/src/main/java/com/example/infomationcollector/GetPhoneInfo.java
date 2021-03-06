package com.example.infomationcollector;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;
import android.widget.Toast;

public class GetPhoneInfo extends AppCompatActivity implements MyReceiver.MyListener{
    // resources
    private TextView tv1;
    private MyReceiver myReceiver;

    private String battery = "";
    private String clipboardContent = "";

    private ClipboardManager mClipboardManager;
    ClipboardManager.OnPrimaryClipChangedListener mOnPrimaryClipChangedListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_phone_info);
        tv1 = findViewById(R.id.tv1);
        tv1.setText("");
        tv1.setMovementMethod(ScrollingMovementMethod.getInstance());
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        intentFilter.addAction(LocationManager.PROVIDERS_CHANGED_ACTION);
        myReceiver = new MyReceiver();
        registerReceiver(myReceiver, intentFilter);
        myReceiver.setMyListener(this);
        registerClipEvents();
        setInfo();
    }

    @Override
    public void onListener(String level, boolean network) {
        if (!network) {
            this.battery = level;
        }
        setInfo();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setInfo();
    }

    private void appendInfo(String str) {
        tv1.append(str + "\n");
    }

    private void setInfo(){
        InfoHelper infoHelper = new InfoHelper(GetPhoneInfo.this);
        // clear
        tv1.setText("");
        //System.out.println("AAAAAAAAAA");
        // synchronous
        appendInfo("????????????: " + infoHelper.getPhoneBrand());
        appendInfo("?????????: " + infoHelper.getCPUName());
        appendInfo("????????????: " + infoHelper.getSystemVersion());
        appendInfo("SDK??????: " + infoHelper.getSDKVersion());
        appendInfo("????????????: " + infoHelper.getPhoneModel());
        appendInfo("??????????????????GPS: " + infoHelper.isOpenGPS());
        appendInfo("Linux????????????: " + infoHelper.getLinuxKernalInfoEx());
        appendInfo("IP??????: " + infoHelper.getLocalInetAddress());
        appendInfo("MAC??????: " + infoHelper.getMacAddress());
        appendInfo("??????ID: " + infoHelper.getAndroidId());
        appendInfo("MNC: " + infoHelper.getMNC());
        appendInfo("MCC: " + infoHelper.getMCC());
        appendInfo("???????????????: " + infoHelper.isPhone());
        if(null!=infoHelper.getGPSLocation(GetPhoneInfo.this))
        {
            appendInfo("GPS??????(Latitude): " + infoHelper.getGPSLocation(GetPhoneInfo.this).getLatitude());
            appendInfo("GPS??????(Longitude): " + infoHelper.getGPSLocation(GetPhoneInfo.this).getLongitude());
        }else if(null!=infoHelper.get_Gps_Location_2(GetPhoneInfo.this))
        {
            appendInfo("GPS??????(Latitude): " + infoHelper.get_Gps_Location_2(GetPhoneInfo.this).getLatitude());
            appendInfo("GPS??????(Longitude): " + infoHelper.get_Gps_Location_2(GetPhoneInfo.this).getLongitude());
          //  infoHelper.get_Gps_Location_2(GetPhoneInfo.this);
        }//else
            //Toast.makeText(GetPhoneInfo.this, "location ??????", Toast.LENGTH_SHORT).show();
     //   appendInfo("GPS??????(Latitude): " + infoHelper.getGPSLocation().getLatitude());
     //   appendInfo("GPS??????(Longitude): " + infoHelper.getGPSLocation().getLongitude());
        appendInfo("?????????????????????: " + infoHelper.getSimOperatorByMnc());
        appendInfo("SIM????????????: " + infoHelper.getSimOperatorName());
        appendInfo("SIM??????????????????: " + infoHelper.isSimCardReady());
        appendInfo("SIM?????????: " + infoHelper.getSimNumber());
        appendInfo("WIFI??????: " + infoHelper.getWIFIAddress());
        appendInfo("WIFI??????: " + infoHelper.getWIFIList());
        appendInfo("WIFI????????????: " + infoHelper.getWIFIConnection());
        appendInfo("????????????: " + ("".equals(battery) ? "" : battery)+ "%");

        // asynchronous
        Handler handler = new Handler();
        handler.postDelayed(new Runnable(){
            @Override
            public void run() {
                appendInfo("??????????????????: " + getClipboardContent());
            }
        },10);


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(myReceiver);
        if (mClipboardManager != null && mOnPrimaryClipChangedListener != null) {
            mClipboardManager.removePrimaryClipChangedListener(mOnPrimaryClipChangedListener);
        }
    }

    private void registerClipEvents() {
        mClipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        mOnPrimaryClipChangedListener = new ClipboardManager.OnPrimaryClipChangedListener() {
            @Override
            public void onPrimaryClipChanged() {
                if (mClipboardManager.hasPrimaryClip()
                        && mClipboardManager.getPrimaryClip().getItemCount() > 0) {
                    // ????????????????????????????????????
                    CharSequence content =
                            mClipboardManager.getPrimaryClip().getItemAt(0).getText();

                    clipboardContent = content.toString();

                    setInfo();
                }
            }
        };
        mClipboardManager.addPrimaryClipChangedListener(mOnPrimaryClipChangedListener);
    }

    private String getClipboardContent() {
        if (null != mClipboardManager) {
            // ?????????????????????????????????
            ClipData clipData = mClipboardManager.getPrimaryClip();
            if (null != clipData && clipData.getItemCount() > 0) {
                // ??????????????????????????????????????????????????????
                ClipData.Item item = clipData.getItemAt(0);
                if (null != item) {
                    String content = item.getText().toString();
                    clipboardContent = content;
                    return content;
                }
            }
        }
        return "";
    }
}