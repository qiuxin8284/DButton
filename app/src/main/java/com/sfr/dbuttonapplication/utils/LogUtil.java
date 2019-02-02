package com.sfr.dbuttonapplication.utils;

import android.util.Log;

public class LogUtil {

    public static void LogCRF(String m) {
        Log.println(1, "CRF", m);
    }

    public static void i(String a, String b) {
        Log.i(a, b);
    }

    public static void println(String a) {
        if (a != null) {
            System.out.println(a);
        }
    }

    public static void print(String a) {
        if (a != null) {
            System.out.print(a);
        }
    }
}
