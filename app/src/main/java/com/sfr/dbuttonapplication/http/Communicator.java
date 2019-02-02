package com.sfr.dbuttonapplication.http;

import com.sfr.dbuttonapplication.utils.LogUtil;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.List;


public class Communicator {

    private String sessionID = null;
    public String lastError = "";
    public int thisTimeOverTime = OverTime;
    public final static int OverTime = 4750;
    //socket
    private Socket socket;
    private ServerSocket ss;

    public String HttpPostSend(String path, String json) throws Exception {
          /*
		  问题在这里，返回的结果是NULL
		   */
        LogUtil.i("HttpPostSend", "infoJson:" + json);
        String infoJson = json;
        List<NameValuePair> paramPair = new ArrayList<NameValuePair>();
        paramPair.add(new BasicNameValuePair("infoJson", infoJson));
        UrlEncodedFormEntity entity = new UrlEncodedFormEntity(paramPair, "utf-8");
        HttpPost post = new HttpPost(path);
        post.setEntity(entity);
        if (null != this.sessionID) {
            post.setHeader("Cookie", "JSESSIONID=" + sessionID);
            LogUtil.println("set|sessionID" + sessionID);
        }
        DefaultHttpClient client = getNewHttpClient();
        client.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, thisTimeOverTime);
        if (thisTimeOverTime != OverTime)
            thisTimeOverTime = OverTime;
        HttpResponse res = client.execute(post);
        LogUtil.println("444444444444444444444444444444444444444444444res:"+res.getEntity().toString());
        String result = null;
        LogUtil.println("5555555555555555555555555555555555555555res.getStatusLine().getStatusCode():"+res.getStatusLine().getStatusCode());
        if (res.getStatusLine().getStatusCode() == 200) {
//            if (this.sessionID == null | true) {
//                CookieStore mCookieStore = (CookieStore) client.getCookieStore();
//                List<HttpCookie> cookies = mCookieStore.getCookies();
//                for (int i = 0; i < cookies.size(); i++) {
//                    LogUtil.println(cookies.get(i).getName());
//                    if ("JSESSIONID".equals(cookies.get(i).getName())) {
//                        this.sessionID = cookies.get(i).getValue();
//                        LogUtil.println("get|sessionID" + sessionID);
//                    }
//                }
//            }
            HttpEntity httpEntity = res.getEntity();
            result = EntityUtils.toString(httpEntity, "utf-8");
            client.clearResponseInterceptors();
            return result;
        } else {
            LogUtil.println("**********CODED1111111111****************");
            return result;
        }
    }

    public static DefaultHttpClient getNewHttpClient() {
        try {
            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            trustStore.load(null, null);

            SSLSocketFactory sf = new SSLSocketFactoryEx(trustStore);
            sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

            HttpParams params = new BasicHttpParams();
            HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
            HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);

            SchemeRegistry registry = new SchemeRegistry();
            registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
            registry.register(new Scheme("https", sf, 443));
            ClientConnectionManager ccm = new ThreadSafeClientConnManager(params, registry);

            DefaultHttpClient defaultHttpClient = new DefaultHttpClient(ccm, params);
            return defaultHttpClient;
        } catch (Exception e) {
            return new DefaultHttpClient();
        }
    }


    public void listen() throws IOException {
        ss = new ServerSocket(7777);
        while (true) {
            socket = ss.accept();
            BufferedReader br = new BufferedReader(new InputStreamReader(socket
                    .getInputStream()));
            LogUtil.println("you input is : " + br.readLine());
        }

    }


}