package com.jordan.httplibrary.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.safari.core.protocol.RequestMessage;
import com.safari.core.protocol.ResponseMessage;

import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by icean on 2016/9/1.
 */
public final class CommonUtils {

    private static RequestMessage.Biz sBiz;

    public static RequestMessage.Biz createBiz(Context ctx, String user_token){

        if (null == sBiz){
            TelephonyManager tm = (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE);
            String device_id = tm.getDeviceId();

            RequestMessage.Biz.Builder biz_build = RequestMessage.Biz.newBuilder();
            biz_build.setDeviceId((TextUtils.isEmpty(device_id) ? "0" : device_id));
            biz_build.setDeviceName(Build.MANUFACTURER);
            biz_build.setDeviceOs(String.valueOf(Build.VERSION.SDK_INT));
            biz_build.setDeviceVersion(Build.VERSION.RELEASE);
            biz_build.setDeviceType(HttpUtilsConfig.DEVICE_TYPE);
            biz_build.setDeviceToken("11111111");
            biz_build.setNetType(getNetType(ctx));
            biz_build.setVersion(getAppVersion(ctx));
            biz_build.setToken(user_token);
            biz_build.setLang(getCurrentLan());
            sBiz = biz_build.build();
        } else {//If current BIZ is not NULL, we must check if it's language field is changed
            String current_language = sBiz.getLang();
            String now_language = getCurrentLan();
            RequestMessage.Biz.Builder biz_builder = RequestMessage.Biz.newBuilder(sBiz);
            if (TextUtils.isEmpty(current_language)
                    || !current_language.equals(now_language)) {
                biz_builder.setLang(now_language);
            }
            TelephonyManager tm = (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE);
            String device_id = tm.getDeviceId();
            biz_builder.setDeviceId((TextUtils.isEmpty(device_id) ? "0" : device_id));
            if (!TextUtils.isEmpty(user_token)) {
                biz_builder.setToken(user_token);
            }
            sBiz = biz_builder.build();
        }
        return sBiz;
    }

    public static RequestMessage.Biz createBizByDefault(Context ctx, String user_token){

        if (null == sBiz){
            RequestMessage.Biz.Builder biz_build = RequestMessage.Biz.newBuilder();
            biz_build.setDeviceId("-1");
            biz_build.setDeviceName(Build.MANUFACTURER);
            biz_build.setDeviceOs(String.valueOf(Build.VERSION.SDK_INT));
            biz_build.setDeviceVersion(Build.VERSION.RELEASE);
            biz_build.setDeviceType(HttpUtilsConfig.DEVICE_TYPE);
            biz_build.setDeviceToken("11111111");
            biz_build.setNetType(getNetType(ctx));
            biz_build.setVersion(getAppVersion(ctx));
            biz_build.setToken(user_token);
            biz_build.setLang(getCurrentLan());
            sBiz = biz_build.build();
        } else {//If current BIZ is not NULL, we must check if it's language field is changed
            String current_language = sBiz.getLang();
            String now_language = getCurrentLan();
            RequestMessage.Biz.Builder biz_builder = RequestMessage.Biz.newBuilder(sBiz);
            if (TextUtils.isEmpty(current_language)
                    || !current_language.equals(now_language)) {
                biz_builder.setLang(now_language);
            }
            TelephonyManager tm = (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE);
            String device_id = tm.getDeviceId();
            biz_builder.setDeviceId((TextUtils.isEmpty(device_id) ? "0" : device_id));
            if (!TextUtils.isEmpty(user_token)) {
                biz_builder.setToken(user_token);
            }
            sBiz = biz_builder.build();
//            String current_language = sBiz.getLang();
//            String now_language = getCurrentLan();
//            RequestMessage.Biz.Builder biz_builder = RequestMessage.Biz.newBuilder(sBiz);
//            if (TextUtils.isEmpty(current_language)
//                    || !current_language.equals(now_language)) {
//                biz_builder.setLang(now_language);
//            }
//            if (!TextUtils.isEmpty(user_token)) {
//                biz_builder.setToken(user_token);
//            }
//            sBiz = biz_builder.build();
//            Log.i("token","createBizByDefault setToken biz!=null:"+user_token);
        }
        return sBiz;
    }

    public static RequestMessage.Request createRequest(Context ctx, String main_json, String user_token, boolean is_granted){
        RequestMessage.Request.Builder all_builder = RequestMessage.Request.newBuilder();
        String base64_str = DES3Util.encode(main_json);
        all_builder.setMain(base64_str);
        //all_builder.setBiz(is_granted ? createBiz(ctx, user_token) : createBizByDefault(ctx, user_token));
        all_builder.setBiz(is_granted ? createBiz(ctx, user_token) : createBizByDefault(ctx, user_token));
        RequestMessage.Request request_body = all_builder.build();
        return request_body;
    }

    public static String getAppVersion(Context ctx) {
        PackageManager pm = ctx.getPackageManager();
        try {
            PackageInfo pInfo = pm.getPackageInfo(ctx.getPackageName(), 0);
            return pInfo.versionName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getNetType(Context ctx) {
        ConnectivityManager cm = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo current_net_info = cm.getActiveNetworkInfo();
        if (null == current_net_info)
            return HttpUtilsConfig.NETWORK_CLASS_NO;
        int network_type = current_net_info.getType();
        if (network_type == ConnectivityManager.TYPE_WIFI)
            return HttpUtilsConfig.NETWORK_CLASS_WIFI;
        else {
            int network_sub_type = current_net_info.getSubtype();
            switch (network_sub_type){
                case TelephonyManager.NETWORK_TYPE_GPRS:
                case TelephonyManager.NETWORK_TYPE_EDGE:
                case TelephonyManager.NETWORK_TYPE_CDMA:
                case TelephonyManager.NETWORK_TYPE_1xRTT:
                case TelephonyManager.NETWORK_TYPE_IDEN:
                    return HttpUtilsConfig.NETWORK_CLASS_2_G;
                case TelephonyManager.NETWORK_TYPE_UMTS:
                case TelephonyManager.NETWORK_TYPE_EVDO_0:
                case TelephonyManager.NETWORK_TYPE_EVDO_A:
                case TelephonyManager.NETWORK_TYPE_HSDPA:
                case TelephonyManager.NETWORK_TYPE_HSUPA:
                case TelephonyManager.NETWORK_TYPE_HSPA:
                case TelephonyManager.NETWORK_TYPE_EVDO_B:
                case TelephonyManager.NETWORK_TYPE_EHRPD:
                case TelephonyManager.NETWORK_TYPE_HSPAP:
                    return HttpUtilsConfig.NETWORK_CLASS_3_G;
                case TelephonyManager.NETWORK_TYPE_LTE:
                    return HttpUtilsConfig.NETWORK_CLASS_4_G;
                default:
                    return HttpUtilsConfig.NETWORK_CLASS_UNKNOWN;
            }
        }
    }

    public static String getCurrentLan(){
        Locale current_locale = Locale.getDefault();
        String language_code = current_locale.getLanguage();
        StringBuffer buffer_launguage = new StringBuffer();
        if (HttpUtilsConfig.LANGUAGE_CHINESE.equals(language_code)) {
            buffer_launguage.append(HttpUtilsConfig.LANGUAGE_CHINESE);
            buffer_launguage.append("_");
            String country_code = current_locale.getCountry();
            if (HttpUtilsConfig.COUNTRY_CHINESE.equals(country_code)){
                buffer_launguage.append(HttpUtilsConfig.COUNTRY_CHINESE);
            } else if (HttpUtilsConfig.COUNTRY_HONGKONG.equals(country_code)) {
                buffer_launguage.append(HttpUtilsConfig.COUNTRY_HONGKONG);
            } else {
                buffer_launguage.append(HttpUtilsConfig.COUNTRY_TIPEI);
            }
        } else {
            buffer_launguage.append(HttpUtilsConfig.LANGUAGE_ENGLISH);
        }
        return buffer_launguage.toString();
    }

    public static boolean isEmail(String email){
        if (TextUtils.isEmpty(email))
            return false;
        else {
            Pattern email_pattern = Pattern.compile(HttpUtilsConfig.PATTERN_EMAIL);
            Matcher email_matcher = email_pattern.matcher(email);
            return email_matcher.matches();
        }
    }

    public static boolean isCellPhone(String cellphone){
        if (TextUtils.isEmpty(cellphone))
            return false;
        else {
            Pattern cellphone_pattern = Pattern.compile(HttpUtilsConfig.PATTERN_CELLPHONE);
            Matcher cellphone_matcher = cellphone_pattern.matcher(cellphone);
            return cellphone_matcher.matches();
        }
    }

    public static String encodeToBase64(String file_path) {
        StringBuilder file_builder = new StringBuilder();
        if (!TextUtils.isEmpty(file_path)) {
            try {
                InputStream file_stream = new FileInputStream(new File(file_path));
                byte[] temp_buffer = new byte[1024];
                int n = -1;
                while (-1 != (n = file_stream.read(temp_buffer))){
                    file_builder.append(Base64.encode(temp_buffer));
                }
                file_stream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (null != file_builder && file_builder.length() > 0) {
            return file_builder.toString();
        } else {
            return null;
        }

    }

    public static boolean isSuccess(String response_data){
        if (TextUtils.isEmpty(response_data))
            return false;
            //throw new RuntimeException("isSuccess::info::Input param is null");

        try {
            JSONObject result_data = new JSONObject(response_data);
            String data_value_temp = result_data.getString(HttpUtilsConfig.KEY_RESP_ROOT_DATA);
            byte[] data_bytes = Base64.decode(data_value_temp);
            ResponseMessage.Response result_buffer = ResponseMessage.Response.parseFrom(data_bytes);
            String result_value = result_buffer.getResult();
            if (HttpUtilsConfig.RESULT_CODE_SUCCESS.equals(result_value)){
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            //throw new RuntimeException(e.getCause());
        }
        return false;
    }


    public static String getDataStrFromResult(String result_json_str) {
        try {
            JSONObject result_data = new JSONObject(result_json_str);
            String data_value_temp = result_data.getString(HttpUtilsConfig.KEY_RESP_ROOT_DATA);
            byte[] result_data_bytes = Base64.decode(data_value_temp);
            ResponseMessage.Response result_data_buffer = ResponseMessage.Response.parseFrom(result_data_bytes);
            String data_str = result_data_buffer.getData();
            if (TextUtils.isEmpty(data_str)) {
                return "";
            } else {
                String data_str_temp = DES3Util.decode(data_str);
                return data_str_temp;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }
    /**
     * 判断网络是否畅通
     * @param context
     * @return
     */
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity == null) {
            return false;
        }
        else
        {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
            {
                for (int i = 0; i < info.length; i++)
                {
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

}
