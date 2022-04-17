package com.example.infomationcollector;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.net.ConnectivityManager;

public class MyReceiver extends BroadcastReceiver {
    public MyListener myListener;
    public interface MyListener {
        public void onListener(String level, boolean network);
    }
    @Override
    public void onReceive(Context context, Intent intent) {
        if(ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {
            // 网络状态发生变化 需要刷新
            myListener.onListener("", true);
        }else if (LocationManager.PROVIDERS_CHANGED_ACTION.equals(intent.getAction())) {
            // 位置信息发生变化 需要更新
            myListener.onListener("", true);
        } else {
            //获取当前电量
            int level = intent.getIntExtra("level", 0);
            //电量的总刻度
            myListener.onListener(level+"", false);
        }
    }
    public void setMyListener(MyListener myListener) {
        this.myListener = myListener;
    }
}
