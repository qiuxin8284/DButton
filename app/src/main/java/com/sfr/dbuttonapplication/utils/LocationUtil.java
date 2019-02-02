package com.sfr.dbuttonapplication.utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

import java.util.List;

/**
 * Created by ASUS on 2018/9/10.
 */

public class LocationUtil {

    private static String judgeProvider(Context context, LocationManager locationManager) {
        List<String> prodiverlist = locationManager.getProviders(true);
        if (prodiverlist.contains(LocationManager.NETWORK_PROVIDER)) {
            return LocationManager.NETWORK_PROVIDER;//网络定位
        } else if (prodiverlist.contains(LocationManager.GPS_PROVIDER)) {
            return LocationManager.GPS_PROVIDER;//GPS定位
        } else {
            Toast.makeText(context, "没有可用的位置提供器", Toast.LENGTH_SHORT).show();
        }
        return null;
    }

    public static Location beginLocatioon(Context context, LocationManager locationManager) {
        String provider = judgeProvider(context, locationManager);
        //有位置提供器的情况
        if (provider != null) {
            //为了压制getLastKnownLocation方法的警告
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                return null;
            }
            return locationManager.getLastKnownLocation(provider);
        } else {
            //不存在位置提供器的情况
            Toast.makeText(context, "不存在位置提供器的情况", Toast.LENGTH_SHORT).show();
        }
        return null;
    }
}

