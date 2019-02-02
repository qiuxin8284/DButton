package com.jordan.httplibrary.utils;

import android.Manifest;

/**
 * Created by SAFARI LMT CO. on 2016/9/1.
 */
public final class HttpUtilsConfig {
    public static final String DEVICE_TYPE = "1";
    public static final String HARD_WARE_TYPE = "0";
    public static final String  DEFAULT_APP_VERSION = "99.99";

    public static final String PATTERN_EMAIL = "^([a-zA-Z0-9]*[-_]?[a-zA-Z0-9]+)*@([a-zA-Z0-9]*[-_]?[a-zA-Z0-9]+)+[\\.][A-Za-z]{2,3}([\\.][A-Za-z]{2})?$";;
    public static final String PATTERN_CELLPHONE = "^((13[0-9])|(147)|(15[^4,\\D])|(18[0-9])|(17[0-9]))\\d{8}$";

    public static final int OVER_TIME = 5000;
    public static final String DEFAULT_CHART = "utf-8";
    public static final int HTTP_SUCCCESS = 200;

    public static final String NETWORK_CLASS_NO = "-1";
    public static final String NETWORK_CLASS_UNKNOWN = "0";
    public static final String NETWORK_CLASS_WIFI = "1";
    public static final String NETWORK_CLASS_2_G = "2";
    public static final String NETWORK_CLASS_3_G = "3";
    public static final String NETWORK_CLASS_4_G = "4";
    public static final String NETWORK_CLASS_5_G = "5";

    public static final String LANGUAGE_CHINESE = "zh";
    public static final String LANGUAGE_ENGLISH = "en";
    public static final String COUNTRY_CHINESE = "CN";
    public static final String COUNTRY_TIPEI = "TW";
    public static final String COUNTRY_HONGKONG = "HK";

    public static final String RESULT_CODE_SUCCESS = "0";

    public static final String KEY_ROOT = "infoJson";
    public static final String KEY_ROOT_DATA = "data";
    public static final String KEY_BODY_ROOT = "main";
    public static final String KEY_BIZ_ROOT = "biz";
    public static final String KEY_RESP_ROOT_RESULT = "result";
    public static final String KEY_RESP_ROOT_DATA = "data";
    public static final String KEY_RESP_DATA_INFO = "info";
    public static final String KEY_RESP_CODE = "code";
    public static final String KEY_RESP_MESSAGE = "msg";

//    public static final String BIZ_KEY_DEVICE_ID = "deviceId";
//    public static final String BIZ_KEY_DEVICE_NAME = "deviceName";
//    public static final String BIZ_KEY_DEVICE_OS = "deviceOs";
//    public static final String BIZ_KEY_DEVICE_TOKEN = "deviceToken";
//    public static final String BIZ_KEY_DEVICE_TYPE = "deviceType";
//    public static final String BIZ_KEY_DEVICE_VERSION = "deviceVersion";
//    public static final String BIZ_KEY_NET_TYPE = "netType";
//    public static final String BIZ_KEY_VERSION = "version";
//    public static final String BIZ_KEY_LANGUAGE = "lang";
//    public static final String BIZ_KEY_TOKEN = "version";
    public static final String JSON_NULL = "null";
}
