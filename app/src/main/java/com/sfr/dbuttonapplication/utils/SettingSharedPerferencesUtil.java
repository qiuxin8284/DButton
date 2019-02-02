package com.sfr.dbuttonapplication.utils;

import android.content.Context;

public class SettingSharedPerferencesUtil {

    public static final String CLICK_ITEM_CHOICE_POSITION_PATH = "filepath_click_item_choice_position_";
    private static final String CLICK_ITEM_CHOICE_POSITION_CONFIG = "config_click_item_choice_position_";

    public static boolean SetClickItemChoicePositionValue(Context context, String clickItem , String choicePosition) {
        return PrefsHelper.save(context, CLICK_ITEM_CHOICE_POSITION_CONFIG+clickItem, choicePosition, CLICK_ITEM_CHOICE_POSITION_PATH+clickItem);
    }

    public static String GetClickItemChoicePositionValue(Context context, String clickItem) {
        String choicePosition = "0";
        try {
            choicePosition = PrefsHelper.read(context, CLICK_ITEM_CHOICE_POSITION_CONFIG+clickItem, CLICK_ITEM_CHOICE_POSITION_PATH+clickItem);
            if(choicePosition==null||choicePosition.equals("")){
                choicePosition = "0";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return choicePosition;
    }


    public static final String CHOICE_POSITION_CONTAINS_CONTENT_PATH = "filepath_choice_position_contains_content_";
    private static final String CHOICE_POSITION_CONTAINS_CONTENT_CONFIG = "config_choice_position_contains_content_";

    public static boolean SetChoicePositionContainsContentValue(Context context, String clickItem, String choicePosition , String content) {
        return PrefsHelper.save(context, CLICK_ITEM_CHOICE_POSITION_CONFIG+clickItem+choicePosition, content,
                CLICK_ITEM_CHOICE_POSITION_PATH+clickItem+choicePosition);
    }

    public static String GetChoicePositionContainsContentValue(Context context, String clickItem, String choicePosition) {
        String content = "";
        try {
            content = PrefsHelper.read(context, CLICK_ITEM_CHOICE_POSITION_CONFIG+clickItem+choicePosition,
                    CLICK_ITEM_CHOICE_POSITION_PATH+clickItem+choicePosition);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return content;
    }


    public static final String BIND_DBUTTON_ID_PATH = "filepath_bind_dbutton_id_";
    private static final String BIND_DBUTTON_ID_CONFIG = "config_bind_dbutton_id_";

    public static boolean SetBindDbuttonIDValue(Context context, String phoneNumber, String dbuttonID) {
        return PrefsHelper.save(context, BIND_DBUTTON_ID_CONFIG+phoneNumber, dbuttonID,
                BIND_DBUTTON_ID_PATH+phoneNumber);
    }

    public static String GetBindDbuttonIDValue(Context context, String phoneNumber) {
        String dbuttonID = "";
        try {
            dbuttonID = PrefsHelper.read(context, BIND_DBUTTON_ID_CONFIG+phoneNumber,
                    BIND_DBUTTON_ID_PATH+phoneNumber);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dbuttonID;
    }

    public static final String BIND_DBUTTON_MAC_PATH = "filepath_bind_dbutton_mac_";
    private static final String BIND_DBUTTON_MAC_CONFIG = "config_bind_dbutton_mac_";

    public static boolean SetBindDbuttonMACValue(Context context, String phoneNumber, String mac) {
        return PrefsHelper.save(context, BIND_DBUTTON_MAC_CONFIG+phoneNumber, mac,
                BIND_DBUTTON_MAC_PATH+phoneNumber);
    }

    public static String GetBindDbuttonMACValue(Context context, String phoneNumber) {
        String mac = "";
        try {
            mac = PrefsHelper.read(context, BIND_DBUTTON_MAC_CONFIG+phoneNumber,
                    BIND_DBUTTON_MAC_PATH+phoneNumber);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mac;
    }

    public static final String LOGIN_TOKEN_PATH = "filepath_login_token_";
    private static final String LOGIN_TOKEN_CONFIG = "config_login_token_";

    public static boolean SetLoginTokenValue(Context context, String token) {
        return PrefsHelper.save(context, LOGIN_TOKEN_CONFIG, token,
                LOGIN_TOKEN_PATH);
    }

    public static String GetLoginTokenValue(Context context) {
        String token = "";
        try {
            token = PrefsHelper.read(context, LOGIN_TOKEN_CONFIG,
                    LOGIN_TOKEN_PATH);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return token;
    }
}
