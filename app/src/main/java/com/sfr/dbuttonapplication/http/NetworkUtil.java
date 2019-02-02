package com.sfr.dbuttonapplication.http;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

public class NetworkUtil {
	private static final int NET_WIFI=1;
	private static final int NET_2G=2;
	private static final int NET_3G=3;
	private static final int NET_4G=4;
	private static final int NET_5G=5;
	
    public static int getNowNetworkState(Context context) {
        int network = 0;
        ConnectivityManager connectMgr = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connectMgr.getActiveNetworkInfo();
        if (info != null && info.isConnected()) {
            if (info.getType() == ConnectivityManager.TYPE_WIFI) {
                network = NET_WIFI;
            } else if (info.getType() == ConnectivityManager.TYPE_MOBILE) {
                if (isFastMobileNetwork(context)) {
                    network = NET_3G;
                } else {
                    network = NET_2G;
                }
            }
        }
        return network;
    }

    private static boolean isFastMobileNetwork(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        switch (telephonyManager.getNetworkType()) {
        case TelephonyManager.NETWORK_TYPE_1xRTT:
            return false; // ~ 50-100 kbps
        case TelephonyManager.NETWORK_TYPE_CDMA:
            return false; // ~ 14-64 kbps
        case TelephonyManager.NETWORK_TYPE_EDGE:
            return false; // ~ 50-100 kbps
        case TelephonyManager.NETWORK_TYPE_EVDO_0:
            return true; // ~ 400-1000 kbps
        case TelephonyManager.NETWORK_TYPE_EVDO_A:
            return true; // ~ 600-1400 kbps
        case TelephonyManager.NETWORK_TYPE_GPRS:
            return false; // ~ 100 kbps
        case TelephonyManager.NETWORK_TYPE_HSDPA:
            return true; // ~ 2-14 Mbps
        case TelephonyManager.NETWORK_TYPE_HSPA:
            return true; // ~ 700-1700 kbps
        case TelephonyManager.NETWORK_TYPE_HSUPA:
            return true; // ~ 1-23 Mbps
        case TelephonyManager.NETWORK_TYPE_UMTS:
            return true; // ~ 400-7000 kbps
        case TelephonyManager.NETWORK_TYPE_EHRPD:
            return true; // ~ 1-2 Mbps
        case TelephonyManager.NETWORK_TYPE_EVDO_B:
            return true; // ~ 5 Mbps
        case TelephonyManager.NETWORK_TYPE_HSPAP:
            return true; // ~ 10-20 Mbps
        case TelephonyManager.NETWORK_TYPE_IDEN:
            return false; // ~25 kbps
        case TelephonyManager.NETWORK_TYPE_LTE:
            return true; // ~ 10+ Mbps
        case TelephonyManager.NETWORK_TYPE_UNKNOWN:
            return false;
        default:
            return false;
        }
    }

}
