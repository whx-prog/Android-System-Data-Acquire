package com.example.infomationcollector;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.telephony.CellLocation;
import android.telephony.TelephonyManager;
import android.telephony.cdma.CdmaCellLocation;
import android.telephony.gsm.GsmCellLocation;
import android.text.TextUtils;
import android.Manifest;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.example.infomationcollector.wifilocation.AP;
import com.example.infomationcollector.wifilocation.Point;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class InfoHelper {
    private Context context;

    public InfoHelper(Context context) {
        this.context = context;
    }

    public boolean isOpenGPS() {
        LocationManager locationManager
                = (LocationManager) this.context.getSystemService(Context.LOCATION_SERVICE);
        return locationManager != null && locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    public String getLinuxKernalInfoEx() {
        StringBuilder result = new StringBuilder();
        String line;
        String[] cmd = new String[]{"/system/bin/cat", "/proc/version"};
        String workdirectory = "/system/bin/";
        try {
            ProcessBuilder bulider = new ProcessBuilder(cmd);
            bulider.directory(new File(workdirectory));
            bulider.redirectErrorStream(true);
            Process process = bulider.start();
            InputStream in = process.getInputStream();
            InputStreamReader isrout = new InputStreamReader(in);
            BufferedReader brout = new BufferedReader(isrout, 8 * 1024);

            while ((line = brout.readLine()) != null) {
                result.append(line);
            }
            in.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return result.toString();
    }

    public InetAddress getLocalInetAddress() {
        InetAddress ip = null;
        try {
            Enumeration<NetworkInterface> en_netInterface = NetworkInterface.getNetworkInterfaces();
            while (en_netInterface.hasMoreElements()) {
                NetworkInterface ni = en_netInterface.nextElement();
                Enumeration<InetAddress> en_ip = ni.getInetAddresses();
                while (en_ip.hasMoreElements()) {
                    ip = en_ip.nextElement();
                    if (!ip.isLoopbackAddress() && !ip.getHostAddress().contains(":"))
                        break;
                    else
                        ip = null;
                }

                if (ip != null) {
                    break;
                }
            }
        } catch (SocketException e) {

            e.printStackTrace();
        }
        return ip;
    }

    public String getMacAddress() {
        String strMacAddr = null;
        try {
            //??????Ip??????
            InetAddress ip = this.getLocalInetAddress();
            byte[] b = NetworkInterface.getByInetAddress(ip).getHardwareAddress();
            StringBuilder buffer = new StringBuilder();
            for (int i = 0; i < b.length; i++) {
                if (i != 0) {
                    buffer.append(':');
                }
                String str = Integer.toHexString(b[i] & 0xFF);
                buffer.append(str.length() == 1 ? 0 + str : str);
            }
            strMacAddr = buffer.toString().toUpperCase();
        } catch (Exception ignored) {

        }

        return strMacAddr;
    }

    @SuppressLint("HardwareIds")
    public String getAndroidId() {
        return Settings.Secure.getString(this.context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    @SuppressLint({"HardwareIds", "MissingPermission"})
    public String getMCC() {
        TelephonyManager tm =
                (TelephonyManager) this.context.getSystemService(Context.TELEPHONY_SERVICE);
        if (tm == null) {
            return null;
        }
        String networkOperator = tm.getNetworkOperator();
        if (!TextUtils.isEmpty(networkOperator)) {
            return networkOperator.substring(3);
        }
        return null;
    }

    @SuppressLint({"HardwareIds", "MissingPermission"})
    public String getMNC() {
        TelephonyManager tm =
                (TelephonyManager) this.context.getSystemService(Context.TELEPHONY_SERVICE);
        if (tm == null) {
            return null;
        }
        String networkOperator = tm.getNetworkOperator();
        if (!TextUtils.isEmpty(networkOperator)) {
            return networkOperator.substring(0, 3);
        }
        return null;
    }

    public boolean isPhone() {
        TelephonyManager tm =
                (TelephonyManager) this.context.getSystemService(Context.TELEPHONY_SERVICE);
        return tm != null && tm.getPhoneType() != TelephonyManager.PHONE_TYPE_NONE;
    }

    public Location get_Gps_Location_2(Context context) {
        Location location = null;
        String locationProvider="";
        LocationManager locationManager = (LocationManager) context.getSystemService(context.LOCATION_SERVICE);
        List<String> providers = locationManager.getProviders(true);
        if (providers.contains(LocationManager.GPS_PROVIDER)) {
            //?????????GPS
            locationProvider = LocationManager.GPS_PROVIDER;
            //   Log.d(TAG, "onCreate: gps=" + locationProvider);
        } else if (providers.contains(LocationManager.NETWORK_PROVIDER)) {
            //?????????Network
            locationProvider = LocationManager.NETWORK_PROVIDER;
            //   Log.d(TAG, "onCreate: network=" + locationProvider);
        } else {
            //   Log.d(TAG, "onCreate: ??????????????????????????????");
            Toast.makeText(context, "??????????????????????????????", Toast.LENGTH_SHORT).show();
        }
        //??????Location?????????????????????????????????locationManager.getBestProvider(criteria, true);

        location = locationManager.getLastKnownLocation(locationProvider);
        return  location;
    }


    public Location getGPSLocation(Context context) {
        Location location = null;

        LocationManager manager = this.getLocationManager(this.context);
        //????????????
       String res="not enter if box";
        if (ActivityCompat.checkSelfPermission(this.context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(context, "enter 1", Toast.LENGTH_SHORT).show();
            return null;
        }

        if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {//????????????GPS??????
           // Toast.makeText(context, "enter 2", Toast.LENGTH_SHORT).show();
            res="enter 2";
            location = manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        }

        if (null==location)
            System.out.println("location is null");
        Toast.makeText(context, res, Toast.LENGTH_SHORT).show();
        return location;
    }

    private LocationManager getLocationManager(@NonNull Context context) {
        return (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    }

    public String getSimOperatorByMnc() {
        TelephonyManager tm =
                (TelephonyManager) this.context.getSystemService(Context.TELEPHONY_SERVICE);
        String operator = tm != null ? tm.getSimOperator() : null;
        if (operator == null) return null;
        switch (operator) {
            case "46000":
            case "46002":
            case "46007":
                return "????????????";
            case "46001":
                return "????????????";
            case "46003":
                return "????????????";
            default:
                return operator;
        }
    }

    public String getSimOperatorName() {
        TelephonyManager tm =
                (TelephonyManager) this.context.getSystemService(Context.TELEPHONY_SERVICE);
        return tm != null ? tm.getSimOperatorName() : null;
    }

    public boolean isSimCardReady() {
        TelephonyManager tm =
                (TelephonyManager) this.context.getSystemService(Context.TELEPHONY_SERVICE);
        return tm != null && tm.getSimState() == TelephonyManager.SIM_STATE_READY;
    }

    @SuppressLint("MissingPermission")
    public String getSimNumber() {
        TelephonyManager tm =
                (TelephonyManager) this.context.getSystemService(Context.TELEPHONY_SERVICE);
        return tm != null ? tm.getVoiceMailNumber() : null;
    }

    @SuppressLint("MissingPermission")
    public int getWIFIAddress() {
        WifiManager wifiManager = (WifiManager) this.context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        // ????????????
        if (wifiManager == null) {
            return 0;
        }
        if (!wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(true);
        }
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        return wifiInfo != null ? wifiInfo.getIpAddress() : 0;
    }

    @SuppressLint("MissingPermission")
    public List<String> getWIFIList() {

        if (Build.VERSION.SDK_INT >= 23 && !this.isOpenGPS()) {
            try {
                //noinspection deprecation
                Settings.Secure.putInt(this.context.getContentResolver(), Settings.Secure.LOCATION_MODE, 1);
            }catch (SecurityException e){

            }
        }

        List<ScanResult> wifiList;
        WifiManager wifiManager = (WifiManager) this.context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (wifiManager == null) {
            return new ArrayList<>();
        }
        // ????????????
        if (!wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(true);
        }
        wifiList = wifiManager.getScanResults();

        List<String> res = new ArrayList<>();
        for (ScanResult sr : wifiList) {
            res.add(sr.SSID);
        }
        return wifiList != null ? res : new ArrayList<String>();
    }

    @SuppressLint("MissingPermission")
    public Point getPoint() {

        List<ScanResult> wifiList;
        WifiManager wifiManager = (WifiManager) this.context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (wifiManager == null) {
            return null;
        }
        // ????????????
        if (!wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(true);
        }
        // get wifi ap list first
        wifiList = wifiManager.getScanResults();
        Point res = new Point();
        for (ScanResult sr : wifiList) {
            AP ap = new AP(sr.SSID, sr.BSSID, sr.level);
            res.addAP(ap);
        }
        // get GPS info second
        Location loc = this.getGPSLocation(this.context);
        res.setLocation(loc.getLatitude(), loc.getLongitude());
        res.setTypeToGPS();
        Log.d("InfoHelper", "getPoint: " + res.loc);
        return wifiList != null ? res : null;
    }

    @SuppressLint("MissingPermission")
    public WifiInfo getWIFIConnection() {

        if (Build.VERSION.SDK_INT >= 23 && !this.isOpenGPS()) {
            try {
                //noinspection deprecation
                Settings.Secure.putInt(this.context.getContentResolver(), Settings.Secure.LOCATION_MODE, 1);
            }catch (SecurityException e){

            }
        }

        WifiManager wifiManager = (WifiManager) this.context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (wifiManager == null) {
            return null;
        }
        // ????????????
        if (!wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(true);
        }
        WifiInfo wifiList = wifiManager.getConnectionInfo();
        return wifiList != null ? wifiList : null;
    }

    public String getPhoneModel() {
        return Build.MODEL;
    }

    public String getSDKVersion() {
        return Build.VERSION.SDK;
    }

    public String getSystemVersion() {
        return Build.VERSION.RELEASE;
    }

    public String getPhoneBrand() {
        return Build.BRAND;
    }

    public String getCPUName() {
        return Build.HARDWARE;
    }

    // ???????????????APP????????????






}
