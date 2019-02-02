package com.jordan.httplibrary;


import android.util.Log;

import com.jordan.httplibrary.utils.Base64;
import com.jordan.httplibrary.utils.HttpUtilsConfig;
import com.safari.core.protocol.RequestMessage;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by icean on 2017/1/26.
 */

public final class HttpUtils {

    public static String sendHttpRequest(String target_address, RequestMessage.Request request_message){
        try {
            Log.i("Photo","sendHttpRequest               1");
            JSONObject all_data = new JSONObject();
            Log.i("Photo","sendHttpRequest               2");
            String data_str = Base64.encode(request_message.toByteArray());
            Log.i("Photo","sendHttpRequest               3");

            all_data.put(HttpUtilsConfig.KEY_ROOT_DATA, data_str);
            Log.i("Photo","sendHttpRequest               4");

            List<NameValuePair> params = new ArrayList<NameValuePair>();
            Log.i("Photo","sendHttpRequest               5");
            params.add(new BasicNameValuePair(HttpUtilsConfig.KEY_ROOT, all_data.toString()));
            Log.i("Photo","sendHttpRequest               6");

            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(params, HttpUtilsConfig.DEFAULT_CHART);
            Log.i("Photo","sendHttpRequest               7");
            HttpPost post = new HttpPost(target_address);
            Log.i("Photo","sendHttpRequest               8");
            post.setEntity(entity);
            Log.i("Photo","sendHttpRequest               9");
            DefaultHttpClient client = new DefaultHttpClient();
            Log.i("Photo","sendHttpRequest               10");
            client.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, HttpUtilsConfig.OVER_TIME);
            Log.i("Photo","sendHttpRequest               11");
            Log.i("Photo","sendHttpRequest11 main_json_str:"+request_message.toString());
            HttpResponse response = client.execute(post);
            Log.i("Photo","sendHttpRequest               12");
            if (response.getStatusLine().getStatusCode() == HttpUtilsConfig.HTTP_SUCCCESS){
                Log.i("Photo","sendHttpRequest               13");
                HttpEntity result_entity = response.getEntity();
                Log.i("Photo","sendHttpRequest               14");
                String response_string = EntityUtils.toString(result_entity);
                Log.i("Photo","sendHttpRequest               15"+response_string);
                return response_string;
            }
            Log.i("Photo","sendHttpRequest               16");
        }catch (Exception e) {
            Log.i("Photo","sendHttpRequest               17 ex");
            e.printStackTrace();
            return "999999";
        }
        return null;
    }
  public static String getData(String urlAddress) {
        try {  
            URL url = new URL(urlAddress);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection(); // 打开和URL之间的联接
  
            // 获取返回数据,使用 utf-8 将流数据进行转码，否则会产生乱码  
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));
            String line = null;  
            StringBuffer sb = new StringBuffer();  
            while ((line = in.readLine()) != null) {  
                sb.append(line);  
            }  
            in.close(); // 关闭流  
            return sb.toString();  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
        return "";  
    }  
}
