package com.example.infomationcollector;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.telephony.CellLocation;
import android.telephony.TelephonyManager;
import android.telephony.cdma.CdmaCellLocation;
import android.telephony.gsm.GsmCellLocation;
import android.text.TextUtils;
import android.Manifest;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

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
            //获得Ip地址
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

    public Location getGPSLocation() {
        Location location = null;
        LocationManager manager = this.getLocationManager(this.context);
        //权限检查
        if (ActivityCompat.checkSelfPermission(this.context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return null;
        }
        if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {//是否支持GPS定位
            location = manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        }
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
                return "中国移动";
            case "46001":
                return "中国联通";
            case "46003":
                return "中国电信";
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
        // 是否开启
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
        // 是否开启
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
        // 是否开启
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

}
