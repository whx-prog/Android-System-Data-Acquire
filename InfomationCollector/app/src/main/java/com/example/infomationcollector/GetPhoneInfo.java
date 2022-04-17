package com.example.infomationcollector;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;

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

        // synchronous
        appendInfo("手机品牌: " + infoHelper.getPhoneBrand());
        appendInfo("处理器: " + infoHelper.getCPUName());
        appendInfo("安卓版本: " + infoHelper.getSystemVersion());
        appendInfo("SDK版本: " + infoHelper.getSDKVersion());
        appendInfo("手机型号: " + infoHelper.getPhoneModel());
        appendInfo("是否已经打开GPS: " + infoHelper.isOpenGPS());
        appendInfo("Linux内核版本: " + infoHelper.getLinuxKernalInfoEx());
        appendInfo("IP地址: " + infoHelper.getLocalInetAddress());
        appendInfo("MAC地址: " + infoHelper.getMacAddress());
        appendInfo("安卓ID: " + infoHelper.getAndroidId());
        appendInfo("MNC: " + infoHelper.getMNC());
        appendInfo("MCC: " + infoHelper.getMCC());
        appendInfo("是否是手机: " + infoHelper.isPhone());
        appendInfo("GPS位置(Latitude): " + infoHelper.getGPSLocation().getLatitude());
        appendInfo("GPS位置(Longitude): " + infoHelper.getGPSLocation().getLongitude());
        appendInfo("移动网络运营商: " + infoHelper.getSimOperatorByMnc());
        appendInfo("SIM卡运营商: " + infoHelper.getSimOperatorName());
        appendInfo("SIM卡是否准备好: " + infoHelper.isSimCardReady());
        appendInfo("SIM卡号码: " + infoHelper.getSimNumber());
        appendInfo("WIFI地址: " + infoHelper.getWIFIAddress());
        appendInfo("WIFI列表: " + infoHelper.getWIFIList());
        appendInfo("WIFI连接信息: " + infoHelper.getWIFIConnection());
        appendInfo("电池电量: " + ("".equals(battery) ? "" : battery)+ "%");

        // asynchronous
        Handler handler = new Handler();
        handler.postDelayed(new Runnable(){
            @Override
            public void run() {
                appendInfo("剪切板内容为: " + getClipboardContent());
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
                    // 获取复制、剪切的文本内容
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
            // 获取剪贴板的剪贴数据集
            ClipData clipData = mClipboardManager.getPrimaryClip();
            if (null != clipData && clipData.getItemCount() > 0) {
                // 从数据集中获取（粘贴）第一条文本数据
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