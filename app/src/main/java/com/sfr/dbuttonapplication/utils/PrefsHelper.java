package com.sfr.dbuttonapplication.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class PrefsHelper {

    public static boolean save(Context context, String key, String value,
                               String fileName) {
        SharedPreferences sp = context.getSharedPreferences(fileName,
                Context.MODE_PRIVATE);
        Editor editor = sp.edit();
        editor.putString(key, value);
        return editor.commit();
    }

    public static String read(Context context, String key, String fileName) {
        SharedPreferences sp = context.getSharedPreferences(fileName,
                Context.MODE_PRIVATE);
        return sp.getString(key, "");
    }

    public static boolean saveBoolean(Context context, String key,
                                      boolean value, String fileName) {
        SharedPreferences sp = context.getSharedPreferences(fileName,
                Context.MODE_PRIVATE);
        Editor editor = sp.edit();
        editor.putBoolean(key, value);
        return editor.commit();
    }

    public static boolean readboolean(Context context, String key,
                                      String fileName) {
        SharedPreferences sp = context.getSharedPreferences(fileName,
                Context.MODE_PRIVATE);
        return sp.getBoolean(key, false);
    }
}
