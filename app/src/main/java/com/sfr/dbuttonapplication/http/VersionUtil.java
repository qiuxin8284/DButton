package com.sfr.dbuttonapplication.http;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

public class VersionUtil {
    public static String getVersionName(Context context) {

        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packInfo;
            packInfo = packageManager.getPackageInfo(context.getPackageName(),
                    0);
            String version = packInfo.versionName;
            return version;
        } catch (NameNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return "99.99";
        }

    }

    public static boolean compareVersion(String minVer, String nowVer) {
        int min = Integer.parseInt(minVer.replace(".", ""));
        int now = Integer.parseInt(nowVer.replace(".", ""));
        if (now > min) {
            return false;
        }
        return true;
    }
}
