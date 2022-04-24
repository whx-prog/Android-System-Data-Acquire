package com.example.infomationcollector;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipboardManager;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PermissionInfo;
import android.content.pm.ServiceInfo;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.CallLog;
import android.text.format.DateFormat;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.Manifest;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CallLog;
import android.provider.Settings;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class Get_history extends AppCompatActivity {
    private TextView Display_text;
    private String TAG  = "MainActivity: ";
    private Uri callUri = CallLog.Calls.CONTENT_URI;
    private String[] columns = {CallLog.Calls.CACHED_NAME// 通话记录的联系人
            , CallLog.Calls.NUMBER// 通话记录的电话号码
            , CallLog.Calls.DATE// 通话记录的日期
            , CallLog.Calls.DURATION// 通话时长
            , CallLog.Calls.TYPE};// 通话类型}
    private DateFormat DateFormatTools;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_history);

        Display_text = findViewById(R.id.History_text);
        Display_text.setText("");
       Display_text.setMovementMethod(ScrollingMovementMethod.getInstance());
     //   addCallLOg();
      //  IntentFilter intentFilter = new IntentFilter();
      //  intentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
     //  intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
     //   intentFilter.addAction(LocationManager.PROVIDERS_CHANGED_ACTION);
        Intent intent = getIntent();
        String type = intent.getStringExtra("type");

        //setInfo();
      // getContentCallLog();getAllAppNames()
        switch (type)
        {
            case "通话记录":
                getContentCallLog();
                break;
            case "短信记录":
                Toast.makeText(this, "进入短信", Toast.LENGTH_SHORT).show();
                get_SMS();
                break;
            case "获取包名":
                getAllAppNames();
                break;
            case "社交软件":
                获取社交软件信息();
                break;
        }

    }


    private void addCallLOg() {  //添加通话记录
        Log.i(TAG,"添加通话 alog");
        ContentValues values = new ContentValues();
        values.clear();
        values.put(CallLog.Calls.CACHED_NAME, "lum");
        values.put(CallLog.Calls.NUMBER, 123456789);
        values.put(CallLog.Calls.TYPE, "1");
/*        values.put(CallLog.Calls.DATE, calllog.getmCallLogDate());
        values.put(CallLog.Calls.DURATION, calllog.getmCallLogDuration());*/
        values.put(CallLog.Calls.NEW, "0");// 0已看1未看 ,由于没有获取默认全为已读
        getContentResolver().insert(CallLog.Calls.CONTENT_URI, values);

    }

    private  void get_SMS()
    {
        String lastTime = "1534228493681"; // 时间

        Log.i("SMSUtil", "开始获取短信");

        Cursor cursor = null;

       /* String uri = "content://sms";
        Uri mUri = Uri.parse(uri);
        Set<String> params = mUri.getQueryParameterNames();
        Toast.makeText(this, "循环前", Toast.LENGTH_SHORT).show();
        if(params.size()==0)
            Toast.makeText(this, "大小为0", Toast.LENGTH_SHORT).show();
        for (Iterator iterator = params.iterator(); iterator.hasNext();) {
            String name = (String) iterator.next();
            String value = mUri.getQueryParameter(name);
        appendInfo("\n"+name+"::"+value);
        }


        for(String param: params) {
            Toast.makeText(this, "进入循环", Toast.LENGTH_SHORT).show();
            if(null==param)
                Toast.makeText(this, "string is null", Toast.LENGTH_SHORT).show();
            appendInfo(param+"\n");
        }*/



// 添加异常捕捉

        try {

//第一种， 查询所有短信


                cursor = Get_history.this.getContentResolver().query(

                Uri.parse("content://sms"),

                new String[]{"_id", "address", "body", "date", "person", "type"},

                null, null, "date desc");

//第二种， 通过查询条件， 例如：date > lastTime， 过滤数据

/*cursor = App.mContext.getContentResolver().query(

Uri.parse("content://sms"),

new String[]{"_id", "address", "body", "date", "person", "type"},

"date > ?", new String[]{lastTime}, "date desc");*/

            if (cursor != null) {

              //  ArrayList<HashMap> smsList = new ArrayList<HashMap>();

                while (cursor.moveToNext()) {

                    String body = cursor.getString(cursor.getColumnIndex("body"));// 在这里获取短信信息

                    String person = cursor.getString(cursor.getColumnIndex("person")); // 陌生人为null

                    String address = cursor.getString(cursor.getColumnIndex("address"));

                    String _id = cursor.getString(cursor.getColumnIndex("_id"));

                  //  String date = cursor.getString(cursor.getColumnIndex("date"));
                    long dateLong = cursor.getLong(cursor.getColumnIndex("date")); //获取通话日期
                    String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(dateLong));

                    String type = cursor.getString(cursor.getColumnIndex("type"));
                 //  String  readed=cursor.getString(cursor.getColumnIndex("read"));

                   // HashMap smsMap = new HashMap<>();

                  /*  smsMap.put("body", body);

                    smsMap.put("person", person);

                    smsMap.put("address", address);

                    smsMap.put("_id", _id);

                    smsMap.put("date", date);*/

                    String sms_type="";
                    if("1"==type)
                        sms_type="已发送";
                    else
                        sms_type="接收";


                    if(null==person)
                        person="陌生人";

                 //   if("1"==readed)
                   //     readed="已读";
                 //   else
                  //      readed="未读";


                    //smsList.add(smsMap);
                    appendInfo(  "来源 = " + person+"  号码 = " + address

                           + "  日期 = " + date + "  类型 = " + sms_type+"\n"+"内容："+"\n" + body+"\n" );




                }

// 返回所有的短信

            //    return smsList;

            }

        } catch (Exception e) {

            e.printStackTrace();

            Log.i("test_sms", "e = " + e.getMessage());

        } finally {

            if (cursor != null) {

                cursor.close();

            }

        }

        return ;

    }



    //获取通话记录
    private void getContentCallLog() {
        InfoHelper infoHelper = new InfoHelper(Get_history.this);
        // clear
        Display_text.setText("");
        Cursor cursor = getContentResolver().query(callUri, // 查询通话记录的URI
                columns
                , null, null, CallLog.Calls.DEFAULT_SORT_ORDER// 按照时间逆序排列，最近打的最先显示
        );
        Log.i(TAG, "cursor count:" + cursor.getCount());
        while (cursor.moveToNext()) {
            String name = cursor.getString(cursor.getColumnIndex(CallLog.Calls.CACHED_NAME));  //姓名
            String number = cursor.getString(cursor.getColumnIndex(CallLog.Calls.NUMBER));  //号码
            long dateLong = cursor.getLong(cursor.getColumnIndex(CallLog.Calls.DATE)); //获取通话日期
            String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(dateLong));
            String time = new SimpleDateFormat("HH:mm").format(new Date(dateLong));
            int duration = cursor.getInt(cursor.getColumnIndex(CallLog.Calls.DURATION));//获取通话时长，值为多少秒
            int type = cursor.getInt(cursor.getColumnIndex(CallLog.Calls.TYPE)); //获取通话类型：传入(1),传出(2)和未接(3),4(语音邮件),5(已拒绝)和6(已拒绝列表)
            String dayCurrent = new SimpleDateFormat("dd").format(new Date());
            String dayRecord = new SimpleDateFormat("dd").format(new Date(dateLong));

            String call_type="";
            switch (type)
            {
                case 1:
                    call_type="呼入";
                    break;
                case 2:
                    call_type="呼出";
                    break;
                case 3:
                    call_type="未接";
                    break;
                case 4:
                    call_type="语言右键";
                    break;
                case 5:
                    call_type="拒绝接听";
                    break;
                case 6:
                    call_type="已经拒绝列表";
                    break;

            }

            if(null==name)
                name="陌生人";

            System.out.println(" "+name+" "+number);
            appendInfo( "来源：" +name+" 号码："+number+"\n"+"通话日期："+date+"\n"+"持续时间："+duration+" 通话类型: "+call_type+"\n");

            Log.i(TAG, "Call log: " + "\n"
                    + "name: " + name + "\n"
                    + "phone number: " + number + "\n"


            );

        }
    }

      /*  public static String getCallHistoryStr(String noall){
            String callHistoryJson = "";

            callHistoryJson= GetHostCommlog(getActivity());

            return callHistoryJson;
        }*/

    public  void 获取社交软件信息()
    {
        PackageManager pm=getPackageManager();
        List<PackageInfo> list = pm.getInstalledPackages(PackageManager.GET_PERMISSIONS);
        List<PackageInfo> list_服务 = pm.getInstalledPackages(PackageManager.GET_SERVICES);
        List<PackageInfo> list_模块 = pm.getInstalledPackages(PackageManager.GET_ACTIVITIES);
            System.out.println("AAAAAAAAAAA");
        for(int i=0;i<list.size();i++)
        {
            ApplicationInfo applicationInfo = list.get(i).applicationInfo;
            String packageName=list.get(i).packageName;
            String appName=list.get(i).applicationInfo.loadLabel(getPackageManager()).toString();

            if (!appName.contains("微信")&&!appName.contains("QQ")&&!appName.contains("Soul")&&!appName.contains("探探")&&!appName.contains("抖音")&&!appName.contains("陌陌")
                    &&!appName.contains("哔哩哔哩")&&!appName.contains("知乎")) {
                continue;
            }
            //得到手机上已经安装的应用的名字,即在AndriodMainfest.xml中的app_name。

            //得到手机上已经安装的应用的图标,即在AndriodMainfest.xml中的icon。
            Drawable drawable =list.get(i).applicationInfo.loadIcon(getPackageManager());
            //得到应用所在包的名字,即在AndriodMainfest.xml中的package的值。
            String package_versionName=list.get(i).versionName;
            int version_code=list.get(i).versionCode;
            long dateLong=list.get(i).lastUpdateTime;
            String 上一次更新时间=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(dateLong));
          //  ActivityInfo[] 所有的功能激活模块信息=list.get(i).activities;

            appendInfo("应用名字："+appName+"  包名: "+packageName+"\n"+"  版本名："+package_versionName+"  版本号: "+version_code+"\n"
                    +"  上一次更新时间： "+上一次更新时间+"\n"+"权限信息： ");
            if(null!=list.get(i).permissions)
                for(PermissionInfo p:list.get(i).permissions)
                {
                    appendInfo(p.name+"  组：");
                }
            else
                appendInfo("无\n");

          Display_text.append("服务器信息：\n");
            if(null!=list_服务.get(i).services)
                for(ServiceInfo S:list_服务.get(i).services)
                {
                    appendInfo(S.name);
                }
            else
                appendInfo("无\n");




            Display_text.append("\n");
        }

        /*for (PackageInfo packageInfo : list) {
            ApplicationInfo applicationInfo = packageInfo.applicationInfo;
            String packageName=packageInfo.packageName;
            String appName=packageInfo.applicationInfo.loadLabel(getPackageManager()).toString();

            if (!appName.contains("微信")&&!appName.contains("QQ")&&!appName.contains("Soul")&&!appName.contains("探探")&&!appName.contains("抖音")&&!appName.contains("陌陌")
                    &&!appName.contains("哔哩哔哩")&&!appName.contains("知乎")) {
                continue;
            }
            //得到手机上已经安装的应用的名字,即在AndriodMainfest.xml中的app_name。

            //得到手机上已经安装的应用的图标,即在AndriodMainfest.xml中的icon。
            Drawable drawable = packageInfo.applicationInfo.loadIcon(getPackageManager());
            //得到应用所在包的名字,即在AndriodMainfest.xml中的package的值。
            String package_versionName=packageInfo.versionName;
            int version_code=packageInfo.versionCode;
            long dateLong=packageInfo.lastUpdateTime;
            String 上一次更新时间=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(dateLong));
            ActivityInfo[] 所有的功能激活模块信息=packageInfo.activities;

            appendInfo("应用名字："+appName+"  包名: "+packageName+"\n"+"  版本名："+package_versionName+"  版本号: "+version_code+"\n"
                    +"  上一次更新时间： "+上一次更新时间+"\n"+"权限信息： ");
            if(null!=packageInfo.permissions)
                for(PermissionInfo p:packageInfo.permissions)
                {
                    appendInfo(p.name+"  组："+p.group);
                }
            else
                appendInfo("无\n");


            Display_text.append("\n");

        }*/
    }



    public void getAllAppNames(){
        PackageManager pm=getPackageManager();
        ////获取到所有安装了的应用程序的信息，包括那些卸载了的，但没有清除数据的应用程序
      //  List<PackageInfo> list2=pm.getInstalledPackages(PackageManager.GET_UNINSTALLED_PACKAGES);
        List<PackageInfo> list = pm.getInstalledPackages(PackageManager.GET_PERMISSIONS);
        int j=0;

        for (PackageInfo packageInfo : list) {
            ApplicationInfo applicationInfo = packageInfo.applicationInfo;
            String packageName=packageInfo.packageName;
            if (isSystemApp(applicationInfo)) {
                continue;
            }
            //得到手机上已经安装的应用的名字,即在AndriodMainfest.xml中的app_name。
            String appName=packageInfo.applicationInfo.loadLabel(getPackageManager()).toString();
            //得到手机上已经安装的应用的图标,即在AndriodMainfest.xml中的icon。
            Drawable drawable = packageInfo.applicationInfo.loadIcon(getPackageManager());
            //得到应用所在包的名字,即在AndriodMainfest.xml中的package的值。

            String package_versionName=packageInfo.versionName;
            int version_code=packageInfo.versionCode;
            long dateLong=packageInfo.lastUpdateTime;
            String 上一次更新时间=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(dateLong));
          //  PermissionInfo[] 所有的权限信息=packageInfo.permissions;
            ServiceInfo[] 所有的服务信息=packageInfo.services;
            ActivityInfo[] 所有的功能激活模块信息=packageInfo.activities;



            appendInfo("应用名字："+appName+"  包名: "+packageName+"\n"+"  版本名："+package_versionName+"  版本号: "+version_code+"\n"
            +"  上一次更新时间： "+上一次更新时间+"\n"+"权限信息： ");
            if(null!=packageInfo.permissions)
            for(PermissionInfo p:packageInfo.permissions)
            {
                appendInfo(p.name);
            }
            else
                appendInfo("无\n");
            Display_text.append("\n");

            j++;
        }
       // Log.e("========cccccc", "应用的总个数:"+j);
    }

    private boolean isSystemApp(ApplicationInfo appInfo) {

        return (appInfo.flags & appInfo.FLAG_SYSTEM) > 0;

    }



    private void appendInfo(String str) {
        Display_text.append(str + "\n");
    }

    private void setInfo(){
        InfoHelper infoHelper = new InfoHelper(Get_history.this);
        // clear
        Display_text.setText("");

        // synchronous
       /* appendInfo("手机品牌: " + infoHelper.getPhoneBrand());
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
        appendInfo("WIFI连接信息: " + infoHelper.getWIFIConnection());*/
      //  appendInfo("电池电量: " + ("".equals(battery) ? "" : battery)+ "%");




    }
}