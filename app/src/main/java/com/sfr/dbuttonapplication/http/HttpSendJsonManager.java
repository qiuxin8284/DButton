package com.sfr.dbuttonapplication.http;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.jordan.httplibrary.utils.Base64;
import com.jordan.httplibrary.utils.CommonUtils;
import com.safari.core.protocol.RequestMessage;
import com.sfr.dbuttonapplication.DButtonApplication;
import com.sfr.dbuttonapplication.R;
import com.sfr.dbuttonapplication.entity.AlarmData;
import com.sfr.dbuttonapplication.entity.AlarmIDData;
import com.sfr.dbuttonapplication.entity.AlarmListData;
import com.sfr.dbuttonapplication.entity.ContactData;
import com.sfr.dbuttonapplication.entity.LayerListData;
import com.sfr.dbuttonapplication.entity.RegisterData;
import com.sfr.dbuttonapplication.entity.RenewData;
import com.sfr.dbuttonapplication.entity.UploadData;
import com.sfr.dbuttonapplication.entity.UserData;
import com.sfr.dbuttonapplication.utils.LogUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.UUID;


public class HttpSendJsonManager {

    public static final String SEX_MAN = "1";
    public static final String SEX_WOMAN = "2";

    public static final String BLOOD_A = "a";
    public static final String BLOOD_B = "b";
    public static final String BLOOD_AB = "ab";
    public static final String BLOOD_O = "o";
    public static final String BLOOD_OTHER = "other";

    public static final int ACCOUNT_USERNAME = 1;
    public static final int ACCOUNT_PHONE_NUMBER = 2;
    public static final int ACCOUNT_EMAIL = 3;
    public static final int ACCOUNT_WEIBO = 4;
    public static final int ACCOUNT_WEIXIN = 5;

    public static final int SEND_MSG_REGISTER = 1;
    public static final int SEND_MSG_LOGIN = 2;
    public static final int SEND_MSG_FORGET_PASSWORD = 3;

    public static final int UPLOAD_PIC = 1;
    public static final int UPLOAD_TEXT = 2;
    public static final int UPLOAD_VOICE = 3;
    public static final int UPLOAD_SPEAK_VOICE = 4;


    public static final String GENDER_MAN = "1";
    public static final String GENDER_WOMAN = "2";

    public static final String PAY_TYPE_JOIN = "1";
    public static final String PAY_TYPE_BUY = "2";

    public final static String RENEW_TYPE_OTA = "1";//"Android Phone";
    public final static String RENEW_TYPE_IOS = "2";//"Android Phone";
    public final static String RENEW_TYPE_ANDROID = "3";//"Android Phone";

    /**
     * 获取版本信息
     *
     * @param context
     * @return
     */
    public static RenewData renew(Context context, String type) {
        RenewData renewData = new RenewData();
        renewData.setOK(false);
        String url = "v0/ver/renew.htm";
        try {
            JSONObject sendJSONObject = new JSONObject();
            JSONObject mainJSONObject = new JSONObject();

            mainJSONObject.put("type", type);
//            sendJSONObject.put("main", mainJSONObject);
//            sendJSONObject.put("biz", getBiz());
            RequestMessage.Request request_proto = CommonUtils.createRequest(context, mainJSONObject.toString(), DButtonApplication.USER_TOKEN, false);
            sendJSONObject.put("data", Base64.encode(request_proto.toByteArray()));

            String json = sendJSONObject.toString();
            LogUtil.println("renew" + json);
            String synchronousResult = DButtonApplication.httpManager.SyncHttpCommunicate(url, json);
            return HttpAnalyJsonManager.renew(synchronousResult, context);
        } catch (Exception e) {
            HttpAnalyJsonManager.lastError = context.getResources().getString(R.string.network_connection_failed);
            e.printStackTrace();
            return renewData;
        }
    }

    /**
     * 上传反馈内容
     *
     * @param context
     * @param content
     * @return
     */
    public static boolean feedUpload(Context context, String content) {
        String url = "v0/feed/upload.htm";
        try {
            JSONObject sendJSONObject = new JSONObject();
            JSONObject mainJSONObject = new JSONObject();

            mainJSONObject.put("content", content);
//            sendJSONObject.put("main", mainJSONObject);
//            sendJSONObject.put("biz", getBiz());
            RequestMessage.Request request_proto = CommonUtils.createRequest(context, mainJSONObject.toString(), DButtonApplication.USER_TOKEN, false);
            sendJSONObject.put("data", Base64.encode(request_proto.toByteArray()));

            String json = sendJSONObject.toString();
            LogUtil.println("FeedUpload" + json);
            String synchronousResult = DButtonApplication.httpManager.SyncHttpCommunicate(url, json);
            return HttpAnalyJsonManager.feedUpload(synchronousResult, context);
        } catch (Exception e) {
            HttpAnalyJsonManager.lastError = context.getResources().getString(R.string.network_connection_failed);
            e.printStackTrace();
            return false;
        }
    }

    public static final String SEND_SMS_TYPE_REGISTER = "1";
    public static final String SEND_SMS_TYPE_LOGIN = "2";
    public static final String SEND_SMS_TYPE_FORGETPASSWORD = "3";
    public static final String SEND_SMS_TYPE_BIND_ACCOUNT = "4";
    public static final String SEND_SMS_TYPE_UNBIND_ACCOUNT = "5";
    public static final String SEND_SMS_TYPE_DIED_ACCOUNT = "6";

    /**
     * 发送验证码
     *
     * @param phone
     * @param type
     * @return
     */
    public static boolean sendSMS(Context context,
                                  String phone, String type) {
        String url = "v0/sms/send.htm";
        try {
            JSONObject sendJSONObject = new JSONObject();
            JSONObject mainJSONObject = new JSONObject();

            mainJSONObject.put("phone", phone);
            mainJSONObject.put("type", type);
//            sendJSONObject.put("main", mainJSONObject);
//            sendJSONObject.put("biz", getBiz());
            RequestMessage.Request request_proto = CommonUtils.createRequest(context, mainJSONObject.toString(), DButtonApplication.USER_TOKEN, false);
            sendJSONObject.put("data", Base64.encode(request_proto.toByteArray()));

            String json = sendJSONObject.toString();

            LogUtil.println("sendSMS" + json);
            String synchronousResult = DButtonApplication.httpManager.SyncHttpCommunicate(url, json);
//            String result_json = HttpUtils.sendHttpRequest(CommunicateConfig.GetHttpClientAdress()+url, request_proto);
            LogUtil.println("sendSMS synchronousResult1" + synchronousResult);
            return HttpAnalyJsonManager.sendSMS(synchronousResult, context);
        } catch (Exception e) {
            HttpAnalyJsonManager.lastError = context.getResources().getString(R.string.network_connection_failed);
            e.printStackTrace();
            return false;
        }
    }

    public static final int UPLOAD_TPYE_HEAD = 1;
    public static final int UPLOAD_TPYE_PIC = 2;
    public static final int UPLOAD_TPYE_MUSIC = 3;
    public static final int UPLOAD_TPYE_VOICE = 4;
    public static final int UPLOAD_TPYE_FILE = 5;

    /**
     * 上传媒体文件
     *
     * @param context
     * @param type
     * @param name
     * @param file
     * @return
     */
    public static UploadData upload(Context context, int type, String name, String file, String time) {
        UploadData uploadData = new UploadData();
        uploadData.setOK(false);
        String url = "v0/media/upload.htm";
        try {
            JSONObject sendJSONObject = new JSONObject();
            JSONObject mainJSONObject = new JSONObject();

            mainJSONObject.put("type", type);
            mainJSONObject.put("name", name);
            mainJSONObject.put("file", file);
            mainJSONObject.put("time", time);
//            sendJSONObject.put("main", mainJSONObject);
//            sendJSONObject.put("biz", getBiz());
            RequestMessage.Request request_proto = CommonUtils.createRequest(context, mainJSONObject.toString(), DButtonApplication.USER_TOKEN, false);
            sendJSONObject.put("data", Base64.encode(request_proto.toByteArray()));


            String json = sendJSONObject.toString();
            LogUtil.println("upload" + json);

            Log.e("upload", "upload()|前" + DButtonApplication.datenamesdf.format(new Date(System.currentTimeMillis())));
            String stringbase64 = Base64.encode(request_proto.toByteArray());
            byte[] bytebase64 = Base64.decode(stringbase64);
            JSONObject resultJson = new JSONObject(json);
            Log.e("upload", "upload()|后" + DButtonApplication.datenamesdf.format(new Date(System.currentTimeMillis())));


            String synchronousResult = DButtonApplication.httpManager.SyncHttpCommunicate(url, json);
            LogUtil.println(":" + synchronousResult);
            return HttpAnalyJsonManager.mediaUpload(synchronousResult, context);
        } catch (Exception e) {
            HttpAnalyJsonManager.lastError = context.getResources().getString(R.string.network_connection_failed);
            e.printStackTrace();
            return uploadData;
        }
    }


    private static final String CHARSET = "utf-8"; //编码格式

    public static UploadData uploadMedia(Context context, int type, String name, String file, String time) {
        String filePath = Environment.getExternalStorageDirectory() + "/" + name;
        UploadData uploadData = new UploadData();
        uploadData.setOK(false);
        String urlPath = "media";


        //边界标识 随机生成，这个作为boundary的主体内容
        String BOUNDARY = UUID.randomUUID().toString();
        String PREFIX = "--";
        //回车换行，用于调整协议头的格式
        String LINE_END = "\r\n";
        //格式的内容信息
        String CONTENT_TYPE = "multipart/form-data";
        try {
            URL url = new URL(CommunicateConfig.GetHttpClientAdress() + urlPath);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url
                    .openConnection();
            httpURLConnection.setDoInput(true);
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setUseCaches(false);
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setRequestProperty("Connection", "Keep-Alive");
            httpURLConnection.setRequestProperty("Charset", "UTF-8");
            //这里设置请求方式以及boundary的内容，即上面生成的随机字符串
            httpURLConnection.setRequestProperty("Content-Type", CONTENT_TYPE + ";boundary="
                    + BOUNDARY);

            DataOutputStream dos = new DataOutputStream(httpURLConnection.getOutputStream());
            //这里的StringBuffer 用来拼接我们的协议头
            StringBuffer sb = new StringBuffer();
            sb.append(PREFIX);
            sb.append(BOUNDARY);
            sb.append(LINE_END);
            sb.append("Content-Disposition: form-data; name=\"type\""
                    + LINE_END + LINE_END);
            sb.append("3" + LINE_END);
            sb.append(PREFIX);
            sb.append(BOUNDARY);
            sb.append(LINE_END);
            /**
             * 这里重点注意：
             * name里面的值为服务器端需要key 只有这个key 才可以得到对应的文件
             * filename是文件的名字，包含后缀名的 比如:abc.png
             */
            sb.append("Content-Disposition: form-data;name=\"postKey\";filename=\"" + name + "\"" + LINE_END);
            //这里Content-Type 传给后台一个mime类型的编码字段，用于识别扩展名
            sb.append("Content-Type: mp3; charset=" + CHARSET + LINE_END);
            sb.append(LINE_END);
            dos.write(sb.toString().getBytes());

            //将SD 文件通过输入流读到Java代码中-++++++++++++++++++++++++++++++`````````````````````````
            FileInputStream fis = new FileInputStream(filePath);
            byte[] buffer = new byte[8192]; // 8k
            int count = 0;
            while ((count = fis.read(buffer)) != -1) {
                dos.write(buffer, 0, count);

            }
            fis.close();
            System.out.println("file send to server............");
            dos.writeBytes(LINE_END);
            dos.writeBytes(PREFIX + BOUNDARY + PREFIX + LINE_END);
            dos.flush();

            //读取服务器返回结果
            InputStream is = httpURLConnection.getInputStream();
            InputStreamReader isr = new InputStreamReader(is, "utf-8");
            BufferedReader br = new BufferedReader(isr);
            String result = br.readLine();

            dos.close();
            is.close();
            LogUtil.println("upload result:" + result);
            return HttpAnalyJsonManager.uploadMedia(result, context);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return uploadData;
    }

    /**
     * 注册账号
     *
     * @param context
     * @param phone
     * @param code
     * @param name
     * @param gender
     * @param birth
     * @param blood
     * @param img
     * @return
     */
    public static RegisterData registerAccount(Context context, String phone,
                                               String code, String name, String gender,
                                               String birth, String blood, String img) {
        RegisterData registerData = new RegisterData();
        registerData.setOK(false);
        String url = "v0/vip/register.htm";
        try {
            JSONObject sendJSONObject = new JSONObject();
            JSONObject mainJSONObject = new JSONObject();

            mainJSONObject.put("phone", phone);
            mainJSONObject.put("code", code);
            mainJSONObject.put("name", name);
            mainJSONObject.put("gender", gender);
            mainJSONObject.put("birth", birth);
            mainJSONObject.put("blood", blood);
            mainJSONObject.put("img", img);
//            sendJSONObject.put("main", mainJSONObject);
//            sendJSONObject.put("biz", getBiz());
            RequestMessage.Request request_proto = CommonUtils.createRequest(context, mainJSONObject.toString(), DButtonApplication.USER_TOKEN, false);
            sendJSONObject.put("data", Base64.encode(request_proto.toByteArray()));

            String json = sendJSONObject.toString();
            LogUtil.println("registerAccount" + json);
            String synchronousResult = DButtonApplication.httpManager.SyncHttpCommunicate(url, json);
            return HttpAnalyJsonManager.registerAccount(synchronousResult, context);
        } catch (Exception e) {
            HttpAnalyJsonManager.lastError = context.getResources().getString(R.string.network_connection_failed);
            e.printStackTrace();
            return registerData;
        }
    }

    /**
     * 登陆
     *
     * @param context
     * @param phone
     * @param code
     * @return
     */
    public static UserData login(Context context, String phone,
                                 String code) {
        UserData userData = new UserData();
        userData.setOK(false);
        String url = "v0/vip/login.htm";
        try {
            JSONObject sendJSONObject = new JSONObject();
            JSONObject mainJSONObject = new JSONObject();

            mainJSONObject.put("phone", phone);
            mainJSONObject.put("code", code);
//            sendJSONObject.put("main", mainJSONObject);
//            sendJSONObject.put("biz", getBiz());
            RequestMessage.Request request_proto = CommonUtils.createRequest(context, mainJSONObject.toString(), DButtonApplication.USER_TOKEN, false);
            sendJSONObject.put("data", Base64.encode(request_proto.toByteArray()));

            String json = sendJSONObject.toString();
            LogUtil.println("login" + json);
            String synchronousResult = DButtonApplication.httpManager.SyncHttpCommunicate(url, json);
            return HttpAnalyJsonManager.login(synchronousResult, context);
        } catch (Exception e) {
            HttpAnalyJsonManager.lastError = context.getResources().getString(R.string.network_connection_failed);
            e.printStackTrace();
            return userData;
        }
    }

    public static UserData selfLogin(Context context) {
        UserData userData = new UserData();
        userData.setOK(false);
        String url = "v0/vip/self.htm";
        try {
            JSONObject sendJSONObject = new JSONObject();
            JSONObject mainJSONObject = new JSONObject();

//            sendJSONObject.put("main", mainJSONObject);
//            sendJSONObject.put("biz", getBiz());
            RequestMessage.Request request_proto = CommonUtils.createRequest(context, mainJSONObject.toString(), DButtonApplication.USER_TOKEN, false);
            sendJSONObject.put("data", Base64.encode(request_proto.toByteArray()));

            String json = sendJSONObject.toString();
            LogUtil.println("selfLogin" + json);
            String synchronousResult = DButtonApplication.httpManager.SyncHttpCommunicate(url, json);
            return HttpAnalyJsonManager.login(synchronousResult, context);
        } catch (Exception e) {
            HttpAnalyJsonManager.lastError = context.getResources().getString(R.string.network_connection_failed);
            e.printStackTrace();
            return userData;
        }
    }

    /**
     * 获取版本信息
     *
     * @param context
     * @return
     */
    public static UserData userInfo(Context context) {
        UserData userData = new UserData();
        userData.setOK(false);
        String url = "v0/vip/info.htm";
        try {
            JSONObject sendJSONObject = new JSONObject();
            JSONObject mainJSONObject = new JSONObject();

//            sendJSONObject.put("main", mainJSONObject);
//            sendJSONObject.put("biz", getBiz());
            RequestMessage.Request request_proto = CommonUtils.createRequest(context, mainJSONObject.toString(), DButtonApplication.USER_TOKEN, false);
            sendJSONObject.put("data", Base64.encode(request_proto.toByteArray()));

            String json = sendJSONObject.toString();
            LogUtil.println("userInfo" + json);
            String synchronousResult = DButtonApplication.httpManager.SyncHttpCommunicate(url, json);
            return HttpAnalyJsonManager.userInfo(synchronousResult, context);
        } catch (Exception e) {
            HttpAnalyJsonManager.lastError = context.getResources().getString(R.string.network_connection_failed);
            e.printStackTrace();
            return userData;
        }
    }

    public static RegisterData setUserInfo(Context context, String name,
                                           String gender, String birth, String blood, String img) {
        RegisterData registerData = new RegisterData();
        registerData.setOK(false);
        String url = "v0/vip/setInfo.htm";
        try {
            JSONObject sendJSONObject = new JSONObject();
            JSONObject mainJSONObject = new JSONObject();

            mainJSONObject.put("name", name);
            mainJSONObject.put("gender", gender);
            mainJSONObject.put("birth", birth);
            mainJSONObject.put("blood", blood);
            mainJSONObject.put("img", img);
//            sendJSONObject.put("main", mainJSONObject);
//            sendJSONObject.put("biz", getBiz());
            LogUtil.println("userInfo mainJSONObject.toString()" + mainJSONObject.toString());
            RequestMessage.Request request_proto = CommonUtils.createRequest(context, mainJSONObject.toString(), DButtonApplication.USER_TOKEN, false);
            sendJSONObject.put("data", Base64.encode(request_proto.toByteArray()));

            String json = sendJSONObject.toString();
            LogUtil.println("userInfo" + json);
            String synchronousResult = DButtonApplication.httpManager.SyncHttpCommunicate(url, json);
            return HttpAnalyJsonManager.setUserInfo(synchronousResult, context);
        } catch (Exception e) {
            HttpAnalyJsonManager.lastError = context.getResources().getString(R.string.network_connection_failed);
            e.printStackTrace();
            return registerData;
        }
    }

    //增加联系人---有问题。应该简单的参数即可搜索
    public static boolean addContact(Context context,
                                     String phone, String name, String gender,
                                     String relation, String blood, String img) {
        String url = "v0/contact/add.htm";
        try {
            JSONObject sendJSONObject = new JSONObject();
            JSONObject mainJSONObject = new JSONObject();

            mainJSONObject.put("phone", phone);
            mainJSONObject.put("name", name);
            mainJSONObject.put("gender", gender);
            mainJSONObject.put("relation", relation);
            mainJSONObject.put("blood", blood);
            mainJSONObject.put("img", img);
//            sendJSONObject.put("main", mainJSONObject);
//            sendJSONObject.put("biz", getBiz());
            RequestMessage.Request request_proto = CommonUtils.createRequest(context, mainJSONObject.toString(), DButtonApplication.USER_TOKEN, false);
            sendJSONObject.put("data", Base64.encode(request_proto.toByteArray()));

            String json = sendJSONObject.toString();

            LogUtil.println("addContact" + json);
            String synchronousResult = DButtonApplication.httpManager.SyncHttpCommunicate(url, json);
//            String result_json = HttpUtils.sendHttpRequest(CommunicateConfig.GetHttpClientAdress()+url, request_proto);
            LogUtil.println("sendSMS synchronousResult1" + synchronousResult);
            return HttpAnalyJsonManager.addContact(synchronousResult, context);
        } catch (Exception e) {
            HttpAnalyJsonManager.lastError = context.getResources().getString(R.string.network_connection_failed);
            e.printStackTrace();
            return false;
        }
    }

    public static final String IS_URGENT_YES = "1";
    public static final String IS_URGENT_NO = "0";

    //增加联系人---有问题。应该简单的参数即可搜索
    public static boolean addContact(Context context,
                                     String phone, String name, String isUrgent, String img) {
        String url = "v0/contact/add.htm";
        try {
            JSONObject sendJSONObject = new JSONObject();
            JSONObject mainJSONObject = new JSONObject();

            mainJSONObject.put("phone", phone);
            mainJSONObject.put("name", name);
            mainJSONObject.put("isUrgent", isUrgent);
            mainJSONObject.put("img", img);
//            sendJSONObject.put("main", mainJSONObject);
//            sendJSONObject.put("biz", getBiz());
            RequestMessage.Request request_proto = CommonUtils.createRequest(context, mainJSONObject.toString(), DButtonApplication.USER_TOKEN, false);
            sendJSONObject.put("data", Base64.encode(request_proto.toByteArray()));

            String json = sendJSONObject.toString();

            LogUtil.println("addContact" + json);
            String synchronousResult = DButtonApplication.httpManager.SyncHttpCommunicate(url, json);
//            String result_json = HttpUtils.sendHttpRequest(CommunicateConfig.GetHttpClientAdress()+url, request_proto);
            LogUtil.println("sendSMS synchronousResult1" + synchronousResult);
            return HttpAnalyJsonManager.addContact(synchronousResult, context);
        } catch (Exception e) {
            HttpAnalyJsonManager.lastError = context.getResources().getString(R.string.network_connection_failed);
            e.printStackTrace();
            return false;
        }
    }

    //修改联系人备注信息-暂时不允许吧
    public static boolean upContact(Context context,
                                    String id, String phone, String name, String gender,
                                    String relation, String blood, String img) {
        String url = "v0/contact/up.htm";
        try {
            JSONObject sendJSONObject = new JSONObject();
            JSONObject mainJSONObject = new JSONObject();

            mainJSONObject.put("id", id);
            mainJSONObject.put("phone", phone);
            mainJSONObject.put("name", name);
            mainJSONObject.put("gender", gender);
            mainJSONObject.put("relation", relation);
            mainJSONObject.put("blood", blood);
            mainJSONObject.put("img", img);
//            sendJSONObject.put("main", mainJSONObject);
//            sendJSONObject.put("biz", getBiz());
            RequestMessage.Request request_proto = CommonUtils.createRequest(context, mainJSONObject.toString(), DButtonApplication.USER_TOKEN, false);
            sendJSONObject.put("data", Base64.encode(request_proto.toByteArray()));

            String json = sendJSONObject.toString();

            LogUtil.println("upContact" + json);
            String synchronousResult = DButtonApplication.httpManager.SyncHttpCommunicate(url, json);
//            String result_json = HttpUtils.sendHttpRequest(CommunicateConfig.GetHttpClientAdress()+url, request_proto);
            LogUtil.println("sendSMS upContact" + synchronousResult);
            return HttpAnalyJsonManager.addContact(synchronousResult, context);
        } catch (Exception e) {
            HttpAnalyJsonManager.lastError = context.getResources().getString(R.string.network_connection_failed);
            e.printStackTrace();
            return false;
        }
    }

    //设置紧急联系人
    public static boolean contactSet(Context context,
                                     String id) {
        String url = "v0/contact/set.htm";
        try {
            JSONObject sendJSONObject = new JSONObject();
            JSONObject mainJSONObject = new JSONObject();

            mainJSONObject.put("id", id);
//            sendJSONObject.put("main", mainJSONObject);
//            sendJSONObject.put("biz", getBiz());
            RequestMessage.Request request_proto = CommonUtils.createRequest(context, mainJSONObject.toString(), DButtonApplication.USER_TOKEN, false);
            sendJSONObject.put("data", Base64.encode(request_proto.toByteArray()));

            String json = sendJSONObject.toString();

            LogUtil.println("contactSet" + json);
            String synchronousResult = DButtonApplication.httpManager.SyncHttpCommunicate(url, json);
//            String result_json = HttpUtils.sendHttpRequest(CommunicateConfig.GetHttpClientAdress()+url, request_proto);
            LogUtil.println("sendSMS synchronousResult1" + synchronousResult);
            return HttpAnalyJsonManager.contactSet(synchronousResult, context);
        } catch (Exception e) {
            HttpAnalyJsonManager.lastError = context.getResources().getString(R.string.network_connection_failed);
            e.printStackTrace();
            return false;
        }
    }

    //联系人列表
    public static ContactData contactList(Context context) {
        ContactData userData = new ContactData();
        userData.setOK(false);
        String url = "v0/contact/list.htm";
        try {
            JSONObject sendJSONObject = new JSONObject();
            JSONObject mainJSONObject = new JSONObject();

//            sendJSONObject.put("main", mainJSONObject);
//            sendJSONObject.put("biz", getBiz());
            RequestMessage.Request request_proto = CommonUtils.createRequest(context, mainJSONObject.toString(), DButtonApplication.USER_TOKEN, false);
            sendJSONObject.put("data", Base64.encode(request_proto.toByteArray()));

            String json = sendJSONObject.toString();
            LogUtil.println("contactList" + json);
            String synchronousResult = DButtonApplication.httpManager.SyncHttpCommunicate(url, json);
            LogUtil.println("contactList synchronousResult1" + synchronousResult);
            return HttpAnalyJsonManager.contactList(synchronousResult, context);
        } catch (Exception e) {
            HttpAnalyJsonManager.lastError = context.getResources().getString(R.string.network_connection_failed);
            e.printStackTrace();
            return userData;
        }
    }

    //删除联系人
    public static boolean contactDelete(Context context,
                                        String ids) {
        String url = "v0/contact/del.htm";
        try {
            JSONObject sendJSONObject = new JSONObject();
            JSONObject mainJSONObject = new JSONObject();

            mainJSONObject.put("ids", ids);
//            sendJSONObject.put("main", mainJSONObject);
//            sendJSONObject.put("biz", getBiz());
            RequestMessage.Request request_proto = CommonUtils.createRequest(context, mainJSONObject.toString(), DButtonApplication.USER_TOKEN, false);
            sendJSONObject.put("data", Base64.encode(request_proto.toByteArray()));

            String json = sendJSONObject.toString();

            LogUtil.println("contactDelete" + json);
            String synchronousResult = DButtonApplication.httpManager.SyncHttpCommunicate(url, json);
//            String result_json = HttpUtils.sendHttpRequest(CommunicateConfig.GetHttpClientAdress()+url, request_proto);
            LogUtil.println("sendSMS synchronousResult1" + synchronousResult);
            return HttpAnalyJsonManager.contactDelete(synchronousResult, context);
        } catch (Exception e) {
            HttpAnalyJsonManager.lastError = context.getResources().getString(R.string.network_connection_failed);
            e.printStackTrace();
            return false;
        }
    }

    //搜索联系人
    public static UserData searchContact(Context context,
                                         String phone) {
        UserData userData = new UserData();
        userData.setOK(false);
        String url = "v0/vip/search.htm";
        try {
            JSONObject sendJSONObject = new JSONObject();
            JSONObject mainJSONObject = new JSONObject();

            mainJSONObject.put("phone", phone);
//            sendJSONObject.put("main", mainJSONObject);
//            sendJSONObject.put("biz", getBiz());
            RequestMessage.Request request_proto = CommonUtils.createRequest(context, mainJSONObject.toString(), DButtonApplication.USER_TOKEN, false);
            sendJSONObject.put("data", Base64.encode(request_proto.toByteArray()));

            String json = sendJSONObject.toString();

            LogUtil.println("searchContact" + json);
            String synchronousResult = DButtonApplication.httpManager.SyncHttpCommunicate(url, json);
//            String result_json = HttpUtils.sendHttpRequest(CommunicateConfig.GetHttpClientAdress()+url, request_proto);
            LogUtil.println("sendSMS synchronousResult1" + synchronousResult);
            return HttpAnalyJsonManager.searchContact(synchronousResult, context);
        } catch (Exception e) {
            HttpAnalyJsonManager.lastError = context.getResources().getString(R.string.network_connection_failed);
            e.printStackTrace();
            return userData;
        }
    }

    //添加固件
    public static boolean addLayer(Context context,
                                   String mac, String name) {
        String url = "v0/layer/add.htm";
        try {
            JSONObject sendJSONObject = new JSONObject();
            JSONObject mainJSONObject = new JSONObject();

            mainJSONObject.put("mac", mac);
            mainJSONObject.put("name", name);
//            sendJSONObject.put("main", mainJSONObject);
//            sendJSONObject.put("biz", getBiz());
            LogUtil.println("addLayer main" + mainJSONObject.toString());
            RequestMessage.Request request_proto = CommonUtils.createRequest(context, mainJSONObject.toString(), DButtonApplication.USER_TOKEN, false);
            sendJSONObject.put("data", Base64.encode(request_proto.toByteArray()));

            String json = sendJSONObject.toString();

            LogUtil.println("addLayer" + json);
            String synchronousResult = DButtonApplication.httpManager.SyncHttpCommunicate(url, json);
//            String result_json = HttpUtils.sendHttpRequest(CommunicateConfig.GetHttpClientAdress()+url, request_proto);
            LogUtil.println("sendSMS synchronousResult1" + synchronousResult);
            return HttpAnalyJsonManager.addLayer(synchronousResult, context);
        } catch (Exception e) {
            HttpAnalyJsonManager.lastError = context.getResources().getString(R.string.network_connection_failed);
            e.printStackTrace();
            return false;
        }
    }

    //修改固件----这个修改固件的名字暂时用不到
    public static boolean upLayer(Context context,
                                  String id, String name) {
        String url = "v0/layer/up.htm";
        try {
            JSONObject sendJSONObject = new JSONObject();
            JSONObject mainJSONObject = new JSONObject();

            mainJSONObject.put("id", id);
            mainJSONObject.put("name", name);
//            sendJSONObject.put("main", mainJSONObject);
//            sendJSONObject.put("biz", getBiz());
            RequestMessage.Request request_proto = CommonUtils.createRequest(context, mainJSONObject.toString(), DButtonApplication.USER_TOKEN, false);
            sendJSONObject.put("data", Base64.encode(request_proto.toByteArray()));

            String json = sendJSONObject.toString();

            LogUtil.println("upLayer" + json);
            String synchronousResult = DButtonApplication.httpManager.SyncHttpCommunicate(url, json);
//            String result_json = HttpUtils.sendHttpRequest(CommunicateConfig.GetHttpClientAdress()+url, request_proto);
            LogUtil.println("sendSMS synchronousResult1" + synchronousResult);
            return HttpAnalyJsonManager.upLayer(synchronousResult, context);
        } catch (Exception e) {
            HttpAnalyJsonManager.lastError = context.getResources().getString(R.string.network_connection_failed);
            e.printStackTrace();
            return false;
        }
    }

    //设置默认固件
    public static boolean setLayer(Context context,
                                   String id) {
        String url = "v0/layer/set.htm";
        try {
            JSONObject sendJSONObject = new JSONObject();
            JSONObject mainJSONObject = new JSONObject();

            mainJSONObject.put("id", id);
//            sendJSONObject.put("main", mainJSONObject);
//            sendJSONObject.put("biz", getBiz());
            RequestMessage.Request request_proto = CommonUtils.createRequest(context, mainJSONObject.toString(), DButtonApplication.USER_TOKEN, false);
            sendJSONObject.put("data", Base64.encode(request_proto.toByteArray()));

            String json = sendJSONObject.toString();

            LogUtil.println("setLayer" + json);
            String synchronousResult = DButtonApplication.httpManager.SyncHttpCommunicate(url, json);
//            String result_json = HttpUtils.sendHttpRequest(CommunicateConfig.GetHttpClientAdress()+url, request_proto);
            LogUtil.println("sendSMS synchronousResult1" + synchronousResult);
            return HttpAnalyJsonManager.setLayer(synchronousResult, context);
        } catch (Exception e) {
            HttpAnalyJsonManager.lastError = context.getResources().getString(R.string.network_connection_failed);
            e.printStackTrace();
            return false;
        }
    }

    //固件列表
    public static LayerListData layerList(Context context) {
        LayerListData layerListData = new LayerListData();
        layerListData.setOK(false);
        String url = "v0/layer/list.htm";
        try {
            JSONObject sendJSONObject = new JSONObject();
            JSONObject mainJSONObject = new JSONObject();

//            sendJSONObject.put("main", mainJSONObject);
//            sendJSONObject.put("biz", getBiz());
            RequestMessage.Request request_proto = CommonUtils.createRequest(context, mainJSONObject.toString(), DButtonApplication.USER_TOKEN, false);
            sendJSONObject.put("data", Base64.encode(request_proto.toByteArray()));

            String json = sendJSONObject.toString();

            LogUtil.println("layerList" + json);
            String synchronousResult = DButtonApplication.httpManager.SyncHttpCommunicate(url, json);
//            String result_json = HttpUtils.sendHttpRequest(CommunicateConfig.GetHttpClientAdress()+url, request_proto);
            LogUtil.println("sendSMS synchronousResult1" + synchronousResult);
            return HttpAnalyJsonManager.layerList(synchronousResult, context);
        } catch (Exception e) {
            HttpAnalyJsonManager.lastError = context.getResources().getString(R.string.network_connection_failed);
            e.printStackTrace();
            return layerListData;
        }
    }

    //删除固件
    public static boolean delLayer(Context context,
                                   String ids) {
        String url = "v0/layer/del.htm";
        try {
            JSONObject sendJSONObject = new JSONObject();
            JSONObject mainJSONObject = new JSONObject();

            mainJSONObject.put("ids", ids);
//            sendJSONObject.put("main", mainJSONObject);
//            sendJSONObject.put("biz", getBiz());
            RequestMessage.Request request_proto = CommonUtils.createRequest(context, mainJSONObject.toString(), DButtonApplication.USER_TOKEN, false);
            sendJSONObject.put("data", Base64.encode(request_proto.toByteArray()));

            String json = sendJSONObject.toString();

            LogUtil.println("delLayer" + json);
            String synchronousResult = DButtonApplication.httpManager.SyncHttpCommunicate(url, json);
//            String result_json = HttpUtils.sendHttpRequest(CommunicateConfig.GetHttpClientAdress()+url, request_proto);
            LogUtil.println("sendSMS synchronousResult1" + synchronousResult);
            return HttpAnalyJsonManager.delLayer(synchronousResult, context);
        } catch (Exception e) {
            HttpAnalyJsonManager.lastError = context.getResources().getString(R.string.network_connection_failed);
            e.printStackTrace();
            return false;
        }
    }

    //上传警报---少了发送给特定的人
    public static AlarmIDData alarmUp(Context context,
                                      String contactIds, String point, String beginTime, String endTime,
                                      String address, String record, String duration) {
        AlarmIDData alarmIDData = new AlarmIDData();
        alarmIDData.setOK(false);
        String url = "v0/alarm/add.htm";
        try {
            JSONObject sendJSONObject = new JSONObject();
            JSONObject mainJSONObject = new JSONObject();

            mainJSONObject.put("type", "1");
            mainJSONObject.put("contactIds", contactIds);
//            mainJSONObject.put("longitude", longitude);
//            mainJSONObject.put("latitude", latitude);
            mainJSONObject.put("point", point);
            mainJSONObject.put("beginTime", beginTime);
            mainJSONObject.put("endTime", "");
            //mainJSONObject.put("endTime", endTime);
            mainJSONObject.put("address", address);
            mainJSONObject.put("record", record);
            mainJSONObject.put("source", DButtonApplication.ALARM_TYPE_SOURCE);
            mainJSONObject.put("duration", duration);
//            sendJSONObject.put("main", mainJSONObject);
//            sendJSONObject.put("biz", getBiz());
            LogUtil.println("alarmUpdate alarmUp json" + mainJSONObject.toString());
            LogUtil.println("alarmUpdate AlarmUpTask record = " + record);
            RequestMessage.Request request_proto = CommonUtils.createRequest(context, mainJSONObject.toString(), DButtonApplication.USER_TOKEN, false);
            sendJSONObject.put("data", Base64.encode(request_proto.toByteArray()));

            String json = sendJSONObject.toString();

            LogUtil.println("alarmUp" + json);
            String synchronousResult = DButtonApplication.httpManager.SyncHttpCommunicate(url, json);
//            String result_json = HttpUtils.sendHttpRequest(CommunicateConfig.GetHttpClientAdress()+url, request_proto);
            LogUtil.println("alarmUp synchronousResult1" + synchronousResult);
            return HttpAnalyJsonManager.alarmUp(synchronousResult, context);
        } catch (Exception e) {
            HttpAnalyJsonManager.lastError = context.getResources().getString(R.string.network_connection_failed);
            e.printStackTrace();
            return alarmIDData;
        }
    }

    public static boolean alarmUpdate(Context context,
                                      String id, String type, String point, String session) {
        String url = "v0/alarm/up.htm";
        try {
            JSONObject sendJSONObject = new JSONObject();
            JSONObject mainJSONObject = new JSONObject();

            mainJSONObject.put("id", id);
            mainJSONObject.put("type", "1");//类型(1:进行中 2:解除警报)
            mainJSONObject.put("point", point);
            mainJSONObject.put("session", session);

            LogUtil.println("alarmUpdate json" + mainJSONObject.toString());
            RequestMessage.Request request_proto = CommonUtils.createRequest(context, mainJSONObject.toString(), DButtonApplication.USER_TOKEN, false);
            sendJSONObject.put("data", Base64.encode(request_proto.toByteArray()));

            String json = sendJSONObject.toString();

            LogUtil.println("alarmUpdate" + json);
            String synchronousResult = DButtonApplication.httpManager.SyncHttpCommunicate(url, json);
//            String result_json = HttpUtils.sendHttpRequest(CommunicateConfig.GetHttpClientAdress()+url, request_proto);
            LogUtil.println("alarmUpdate synchronousResult1" + synchronousResult);
            return HttpAnalyJsonManager.alarmUpdate(synchronousResult, context);
        } catch (Exception e) {
            HttpAnalyJsonManager.lastError = context.getResources().getString(R.string.network_connection_failed);
            e.printStackTrace();
            return false;
        }
    }


    //我收到的警报---这块设计有问题
    public static AlarmListData myGetAlarmList(Context context,
                                               String pageNo, String pageSize) {
        AlarmListData alarmListData = new AlarmListData();
        alarmListData.setOK(false);
        String url = "v0/alarm/aList.htm";
        try {
            JSONObject sendJSONObject = new JSONObject();
            JSONObject mainJSONObject = new JSONObject();

            mainJSONObject.put("pageNo", pageNo);
            mainJSONObject.put("pageSize", pageSize);
            mainJSONObject.put("source", DButtonApplication.ALARM_TYPE_SOURCE);
//            sendJSONObject.put("main", mainJSONObject);
//            sendJSONObject.put("biz", getBiz());
            RequestMessage.Request request_proto = CommonUtils.createRequest(context, mainJSONObject.toString(), DButtonApplication.USER_TOKEN, false);
            sendJSONObject.put("data", Base64.encode(request_proto.toByteArray()));

            String json = sendJSONObject.toString();

            LogUtil.println("myAlarmList" + json);
            String synchronousResult = DButtonApplication.httpManager.SyncHttpCommunicate(url, json);
//            String result_json = HttpUtils.sendHttpRequest(CommunicateConfig.GetHttpClientAdress()+url, request_proto);
            LogUtil.println("myAlarmList synchronousResult1" + synchronousResult);
            return HttpAnalyJsonManager.myGetAlarmList(synchronousResult, context);
        } catch (Exception e) {
            HttpAnalyJsonManager.lastError = context.getResources().getString(R.string.network_connection_failed);
            e.printStackTrace();
            return alarmListData;
        }
    }

    //我发出的警报---这块设计有问题
    public static AlarmListData mySendAlarmList(Context context,
                                                String pageNo, String pageSize) {
        AlarmListData alarmListData = new AlarmListData();
        alarmListData.setOK(false);
        String url = "v0/alarm/list.htm";
        try {
            JSONObject sendJSONObject = new JSONObject();
            JSONObject mainJSONObject = new JSONObject();

            mainJSONObject.put("pageNo", pageNo);
            mainJSONObject.put("pageSize", pageSize);
            mainJSONObject.put("source", DButtonApplication.ALARM_TYPE_SOURCE);
//            sendJSONObject.put("main", mainJSONObject);
//            sendJSONObject.put("biz", getBiz());
            RequestMessage.Request request_proto = CommonUtils.createRequest(context, mainJSONObject.toString(), DButtonApplication.USER_TOKEN, false);
            sendJSONObject.put("data", Base64.encode(request_proto.toByteArray()));

            String json = sendJSONObject.toString();

            LogUtil.println("myAlarmList" + json);
            String synchronousResult = DButtonApplication.httpManager.SyncHttpCommunicate(url, json);
//            String result_json = HttpUtils.sendHttpRequest(CommunicateConfig.GetHttpClientAdress()+url, request_proto);
            LogUtil.println("sendSMS synchronousResult1" + synchronousResult);
            return HttpAnalyJsonManager.mySendAlarmList(synchronousResult, context);
        } catch (Exception e) {
            HttpAnalyJsonManager.lastError = context.getResources().getString(R.string.network_connection_failed);
            e.printStackTrace();
            return alarmListData;
        }
    }

    //警报详情
    public static AlarmData alarmDetail(Context context,
                                        String id) {
        AlarmData alarmData = new AlarmData();
        alarmData.setOK(false);
        String url = "v0/alarm/detail.htm";
        try {
            JSONObject sendJSONObject = new JSONObject();
            JSONObject mainJSONObject = new JSONObject();

            mainJSONObject.put("id", id);
//            sendJSONObject.put("main", mainJSONObject);
//            sendJSONObject.put("biz", getBiz());
            RequestMessage.Request request_proto = CommonUtils.createRequest(context, mainJSONObject.toString(), DButtonApplication.USER_TOKEN, false);
            sendJSONObject.put("data", Base64.encode(request_proto.toByteArray()));

            String json = sendJSONObject.toString();

            LogUtil.println("alarmDetail" + json);
            String synchronousResult = DButtonApplication.httpManager.SyncHttpCommunicate(url, json);
//            String result_json = HttpUtils.sendHttpRequest(CommunicateConfig.GetHttpClientAdress()+url, request_proto);
            LogUtil.println("sendSMS synchronousResult1" + synchronousResult);
            return HttpAnalyJsonManager.alarmDetail(synchronousResult, context);
        } catch (Exception e) {
            HttpAnalyJsonManager.lastError = context.getResources().getString(R.string.network_connection_failed);
            e.printStackTrace();
            return alarmData;
        }
    }

    //删除警报
    public static boolean delAlarm(Context context,
                                   String ids, String classes) {
        String url = "v0/alarm/del.htm";
        try {
            JSONObject sendJSONObject = new JSONObject();
            JSONObject mainJSONObject = new JSONObject();

            mainJSONObject.put("classes", classes);
            mainJSONObject.put("ids", ids);
//            sendJSONObject.put("main", mainJSONObject);
//            sendJSONObject.put("biz", getBiz());
            LogUtil.println("delAlarm mainJSONObject.toString():" + mainJSONObject.toString());
            RequestMessage.Request request_proto = CommonUtils.createRequest(context, mainJSONObject.toString(), DButtonApplication.USER_TOKEN, false);
            sendJSONObject.put("data", Base64.encode(request_proto.toByteArray()));

            String json = sendJSONObject.toString();

            LogUtil.println("delAlarm" + json);
            String synchronousResult = DButtonApplication.httpManager.SyncHttpCommunicate(url, json);
//            String result_json = HttpUtils.sendHttpRequest(CommunicateConfig.GetHttpClientAdress()+url, request_proto);
            LogUtil.println("sendSMS synchronousResult1" + synchronousResult);
            return HttpAnalyJsonManager.delAlarm(synchronousResult, context);
        } catch (Exception e) {
            HttpAnalyJsonManager.lastError = context.getResources().getString(R.string.network_connection_failed);
            e.printStackTrace();
            return false;
        }
    }

    public static AlarmListData getNearAlarmList(Context context,
                                                String longitude, String latitude) {
        AlarmListData alarmListData = new AlarmListData();
        alarmListData.setOK(false);
        String url = "v0/alarm/near.htm";
        try {
            JSONObject sendJSONObject = new JSONObject();
            JSONObject mainJSONObject = new JSONObject();

            mainJSONObject.put("longitude", longitude);
            mainJSONObject.put("latitude", latitude);
//            sendJSONObject.put("main", mainJSONObject);
//            sendJSONObject.put("biz", getBiz());
            LogUtil.println("getNearAlarmList longitude" + longitude);
            LogUtil.println("getNearAlarmList latitude" + latitude);
            LogUtil.println("getNearAlarmList json" + mainJSONObject.toString());
            RequestMessage.Request request_proto = CommonUtils.createRequest(context, mainJSONObject.toString(), DButtonApplication.USER_TOKEN, false);
            sendJSONObject.put("data", Base64.encode(request_proto.toByteArray()));

            String json = sendJSONObject.toString();

            LogUtil.println("getNearAlarmList" + json);
            String synchronousResult = DButtonApplication.httpManager.SyncHttpCommunicate(url, json);
//            String result_json = HttpUtils.sendHttpRequest(CommunicateConfig.GetHttpClientAdress()+url, request_proto);
            LogUtil.println("getNearAlarmList synchronousResult1" + synchronousResult);
            return HttpAnalyJsonManager.getNearAlarmList(synchronousResult, context);
        } catch (Exception e) {
            HttpAnalyJsonManager.lastError = context.getResources().getString(R.string.network_connection_failed);
            e.printStackTrace();
            return alarmListData;
        }
    }
//
//	/**
//	 * 查看媒体文件
//	 * @param context
//	 * @param id
//	 * @return
//	 */
//	public static UploadData view(Context context,int id) {
//		UploadData uploadData = new UploadData();
//		uploadData.setOK(false);
//		String url="media/view.htm";
//		try {
//			JSONObject sendJSONObject = new JSONObject();
//			JSONObject mainJSONObject = new JSONObject();
//
//			mainJSONObject.put("id", id);
//			sendJSONObject.put("main", mainJSONObject);
//
//			sendJSONObject.put("biz", getBiz());
//
//			String json = sendJSONObject.toString();
//			LogUtil.println("view" + json);
//			String synchronousResult = CinetworkApplication.httpManager.SyncHttpCommunicate(url,json);
//			return HttpAnalyJsonManager.view(synchronousResult,context);
//		} catch (Exception e) {
//			HttpAnalyJsonManager.lastError = context.getResources().getString(R.string.network_connection_failed);
//			e.printStackTrace();
//			return uploadData;
//		}
//	}

//
//	/**
//	 * 上传异常信息
//	 * @param context
//	 * @param exClass
//	 * @param exMethod
//	 * @param exDesc
//	 * @param exData
//	 * @return
//	 */
//	public static boolean logException(Context context, String exClass,
//			String exMethod, String exDesc,String exData) {
//		String url="log/exception.htm";
//		try {
//			JSONObject sendJSONObject = new JSONObject();
//			JSONObject mainJSONObject = new JSONObject();
//
//			mainJSONObject.put("exClass", exClass);
//			mainJSONObject.put("exMethod", exMethod);
//			mainJSONObject.put("exDesc", exDesc);
//			mainJSONObject.put("exData", exData);
//			sendJSONObject.put("main", mainJSONObject);
//
//			sendJSONObject.put("biz", getBiz());
//
//			String json = sendJSONObject.toString();
//			LogUtil.println("logException" + json);
//			String synchronousResult = CinetworkApplication.httpManager.SyncHttpCommunicate(url,json);
//			return HttpAnalyJsonManager.logException(synchronousResult,context);
//		} catch (Exception e) {
//			HttpAnalyJsonManager.lastError = context.getResources().getString(R.string.network_connection_failed);
//			e.printStackTrace();
//			return false;
//		}
//	}
//
//	/**
//	 * 账号登陆
//	 * @param context
//	 * @param username
//	 * @param password
//	 * @return
//	 */
//	public static boolean loginAccount(Context context, String username,
//								String password) {
//		String url="vip/normalLogin.htm";
//		try {
//			JSONObject sendJSONObject = new JSONObject();
//			JSONObject mainJSONObject = new JSONObject();
//
//			mainJSONObject.put("username", username);
//			mainJSONObject.put("password", password);
//			sendJSONObject.put("main", mainJSONObject);
//
//			sendJSONObject.put("biz", getBiz());
//
//			String json = sendJSONObject.toString();
//			LogUtil.println("loginAccount" + json);
//			String synchronousResult = CinetworkApplication.httpManager.SyncHttpCommunicate(url,json);
//			return HttpAnalyJsonManager.loginAccount(synchronousResult,context);
//		} catch (Exception e) {
//			HttpAnalyJsonManager.lastError = context.getResources().getString(R.string.network_connection_failed);
//			e.printStackTrace();
//			return false;
//		}
//	}
//	/**
//	 * 忘记密码
//	 * @param context
//	 * @param password
//	 * @param email
//	 * @param code
//	 * @return
//	 */
//	public static boolean forgetPassword(Context context,
//										  String password,String email,String code) {
//		String url="vip/forgetpwd.htm";
//		try {
//			JSONObject sendJSONObject = new JSONObject();
//			JSONObject mainJSONObject = new JSONObject();
//
//			mainJSONObject.put("password", password);
//			mainJSONObject.put("email", email);
//			mainJSONObject.put("code", code);
//			sendJSONObject.put("main", mainJSONObject);
//
//			sendJSONObject.put("biz", getBiz());
//
//			String json = sendJSONObject.toString();
//			LogUtil.println("forgetPassword" + json);
//			String synchronousResult = CinetworkApplication.httpManager.SyncHttpCommunicate(url,json);
//			return HttpAnalyJsonManager.forgetPassword(synchronousResult,context);
//		} catch (Exception e) {
//			HttpAnalyJsonManager.lastError = context.getResources().getString(R.string.network_connection_failed);
//			e.printStackTrace();
//			return false;
//		}
//	}

    //	/**
//	 * 绑定手机号
//	 * @param context
//	 * @param mobile
//	 * @return
//	 */
//	public static boolean bind(Context context, String mobile) {
//		String url="vip/bind.htm";
//		try {
//			JSONObject sendJSONObject = new JSONObject();
//			JSONObject mainJSONObject = new JSONObject();
//
//			mainJSONObject.put("mobile", mobile);
//			sendJSONObject.put("main", mainJSONObject);
//
//			sendJSONObject.put("biz", getBiz());
//
//			String json = sendJSONObject.toString();
//			LogUtil.println("bind" + json);
//			String synchronousResult = CinetworkApplication.httpManager.SyncHttpCommunicate(url,json);
//			return HttpAnalyJsonManager.bind(synchronousResult,context);
//		} catch (Exception e) {
//			HttpAnalyJsonManager.lastError = context.getResources().getString(R.string.network_connection_failed);
//			e.printStackTrace();
//			return false;
//		}
//	}
//
//	/**
//	 * 修改用户信息（单条）
//	 * @param context
//	 * @param key
//	 * @param value
//	 * @return
//	 */
//	public static boolean setSingleInfo(Context context, String key,
//			String value) {
//		String url="vip/setSingleInfo.htm";
//		try {
//			JSONObject sendJSONObject = new JSONObject();
//			JSONObject mainJSONObject = new JSONObject();
//
//			mainJSONObject.put("key", key);
//			mainJSONObject.put("value", value);
//			sendJSONObject.put("main", mainJSONObject);
//
//			sendJSONObject.put("biz", getBiz());
//
//			String json = sendJSONObject.toString();
//			LogUtil.println("setSingleInfo" + json);
//			String synchronousResult = CinetworkApplication.httpManager.SyncHttpCommunicate(url,json);
//			return HttpAnalyJsonManager.setSingleInfo(synchronousResult,context);
//		} catch (Exception e) {
//			HttpAnalyJsonManager.lastError = context.getResources().getString(R.string.network_connection_failed);
//			e.printStackTrace();
//			return false;
//		}
//	}
//	/**
//	 * 修改用户信息(所有)
//	 * @param context
//	 * @param name
//	 * @param tel
//	 * @param nick
//	 * @param gender
//	 * @param img
//	 * @return
//	 */
//	public static boolean setAllInfo(Context context, String name,
//			String tel,String nick,String gender,String img) {
//		String url="vip/setAllInfo.htm";
//		try {
//			JSONObject sendJSONObject = new JSONObject();
//			JSONObject mainJSONObject = new JSONObject();
//
//			mainJSONObject.put("name", name);
//			mainJSONObject.put("tel", tel);
//			mainJSONObject.put("nick", nick);
//			mainJSONObject.put("gender", gender);
//			mainJSONObject.put("img", img);
//			sendJSONObject.put("main", mainJSONObject);
//
//			sendJSONObject.put("biz", getBiz());
//
//			String json = sendJSONObject.toString();
//			LogUtil.println("setAllInfo" + json);
//			String synchronousResult = CinetworkApplication.httpManager.SyncHttpCommunicate(url,json);
//			return HttpAnalyJsonManager.setAllInfo(synchronousResult,context);
//		} catch (Exception e) {
//			HttpAnalyJsonManager.lastError = context.getResources().getString(R.string.network_connection_failed);
//			e.printStackTrace();
//			return false;
//		}
//	}
//
//	/**
//	 * 产品列表
//	 * @param context
//	 * @param type
//	 * @return
//	 */
//	public static boolean proList(Context context, String type) {
//		String url="pro/list.htm";
//		try {
//			JSONObject sendJSONObject = new JSONObject();
//			JSONObject mainJSONObject = new JSONObject();
//
//			mainJSONObject.put("type", type);
//			sendJSONObject.put("main", mainJSONObject);
//
//			sendJSONObject.put("biz", getBiz());
//
//			String json = sendJSONObject.toString();
//			LogUtil.println("proList" + json);
//			String synchronousResult = CinetworkApplication.httpManager.SyncHttpCommunicate(url,json);
//			return HttpAnalyJsonManager.proList(synchronousResult,context,type,CinetworkApplication.mDeviceUtil.getDeviceData().getLang());
//		} catch (Exception e) {
//			HttpAnalyJsonManager.lastError = context.getResources().getString(R.string.network_connection_failed);
//			e.printStackTrace();
//			return false;
//		}
//	}
//
//	/**
//	 * 产品详情
//	 * @param context
//	 * @param id
//	 * @return
//	 */
//	public static boolean proDetail(Context context, String id) {
//		String url="pro/detail.htm";
//		try {
//			JSONObject sendJSONObject = new JSONObject();
//			JSONObject mainJSONObject = new JSONObject();
//
//			mainJSONObject.put("id", id);
//			sendJSONObject.put("main", mainJSONObject);
//
//			sendJSONObject.put("biz", getBiz());
//
//			String json = sendJSONObject.toString();
//			LogUtil.println("proDetail" + json);
//			String synchronousResult = CinetworkApplication.httpManager.SyncHttpCommunicate(url,json);
//			return HttpAnalyJsonManager.proDetail(synchronousResult,context);
//		} catch (Exception e) {
//			HttpAnalyJsonManager.lastError = context.getResources().getString(R.string.network_connection_failed);
//			e.printStackTrace();
//			return false;
//		}
//	}
//	/**
//	 * 订单详情
//	 * @param context
//	 * @param id
//	 * @return
//	 */
//	public static boolean orderDetail(Context context, String id) {
//		String url="order/detail.htm";
//		try {
//			JSONObject sendJSONObject = new JSONObject();
//			JSONObject mainJSONObject = new JSONObject();
//
//			mainJSONObject.put("id", id);
//			sendJSONObject.put("main", mainJSONObject);
//
//			sendJSONObject.put("biz", getBiz());
//
//			String json = sendJSONObject.toString();
//			LogUtil.println("orderDetail" + json);
//			String synchronousResult = CinetworkApplication.httpManager.SyncHttpCommunicate(url,json);
//			return HttpAnalyJsonManager.orderDetail(synchronousResult,context);
//		} catch (Exception e) {
//			HttpAnalyJsonManager.lastError = context.getResources().getString(R.string.network_connection_failed);
//			e.printStackTrace();
//			return false;
//		}
//	}
//	/**
//	 * 添加收获地址
//	 * @param context
//	 * @param contact
//	 * @param mobile
//	 * @param address
//	 * @param de
//	 * @return
//	 */
//	public static boolean addRec(Context context, String contact,
//			String mobile,String address,String de) {
//		String url="rec/add.htm";
//		try {
//			JSONObject sendJSONObject = new JSONObject();
//			JSONObject mainJSONObject = new JSONObject();
//
//			mainJSONObject.put("contact", contact);
//			mainJSONObject.put("mobile", mobile);
//			mainJSONObject.put("address", address);
//			mainJSONObject.put("default", de);
//			sendJSONObject.put("main", mainJSONObject);
//
//			sendJSONObject.put("biz", getBiz());
//
//			String json = sendJSONObject.toString();
//			LogUtil.println("addRec" + json);
//			String synchronousResult = CinetworkApplication.httpManager.SyncHttpCommunicate(url,json);
//			return HttpAnalyJsonManager.addRec(synchronousResult,context);
//		} catch (Exception e) {
//			HttpAnalyJsonManager.lastError = context.getResources().getString(R.string.network_connection_failed);
//			e.printStackTrace();
//			return false;
//		}
//	}
//	/**
//	 * 收货列表
//	 * @param context
//	 * @param type
//	 * @return
//	 */
//	public static RecData recList(Context context) {
//		String url="rec/list.htm";
//		try {
//			JSONObject sendJSONObject = new JSONObject();
//			JSONObject mainJSONObject = new JSONObject();
//
//			sendJSONObject.put("main", mainJSONObject);
//
//			sendJSONObject.put("biz", getBiz());
//
//			String json = sendJSONObject.toString();
//			LogUtil.println("recList" + json);
//			String synchronousResult = CinetworkApplication.httpManager.SyncHttpCommunicate(url,json);
//			return HttpAnalyJsonManager.recList(synchronousResult,context);
//		} catch (Exception e) {
//			HttpAnalyJsonManager.lastError = context.getResources().getString(R.string.network_connection_failed);
//			e.printStackTrace();
//			RecData recData = new RecData();
//			recData.setOK(false);
//			return recData;
//		}
//	}
//
//	/**
//	 * 修改收获地址
//	 * @param context
//	 * @param type
//	 * @return
//	 */
//	public static boolean recUpdate(Context context, String contact,
//			String mobile,String address,String id) {
//		String url="rec/update.htm";
//		try {
//			JSONObject sendJSONObject = new JSONObject();
//			JSONObject mainJSONObject = new JSONObject();
//
//			mainJSONObject.put("id", id);
//			mainJSONObject.put("address", address);
//			mainJSONObject.put("contact", contact);
//			mainJSONObject.put("mobile", mobile);
//			sendJSONObject.put("main", mainJSONObject);
//
//			sendJSONObject.put("biz", getBiz());
//
//			String json = sendJSONObject.toString();
//			LogUtil.println("recUpdate" + json);
//			String synchronousResult = CinetworkApplication.httpManager.SyncHttpCommunicate(url,json);
//			return HttpAnalyJsonManager.recUpdate(synchronousResult,context);
//		} catch (Exception e) {
//			HttpAnalyJsonManager.lastError = context.getResources().getString(R.string.network_connection_failed);
//			e.printStackTrace();
//			return false;
//		}
//	}
//
//	/**
//	 * 修改默认收货地址
//	 * @param context
//	 * @param id
//	 * @return
//	 */
//	public static boolean recModify(Context context,String id) {
//		String url="rec/modify.htm";
//		try {
//			JSONObject sendJSONObject = new JSONObject();
//			JSONObject mainJSONObject = new JSONObject();
//
//			mainJSONObject.put("id", id);
//			sendJSONObject.put("main", mainJSONObject);
//
//			sendJSONObject.put("biz", getBiz());
//
//			String json = sendJSONObject.toString();
//			LogUtil.println("recModify" + json);
//			String synchronousResult = CinetworkApplication.httpManager.SyncHttpCommunicate(url,json);
//			return HttpAnalyJsonManager.recModify(synchronousResult,context);
//		} catch (Exception e) {
//			HttpAnalyJsonManager.lastError = context.getResources().getString(R.string.network_connection_failed);
//			e.printStackTrace();
//			return false;
//		}
//	}
//
//	/**
//	 * 删除收获地址
//	 * @param context
//	 * @param ids
//	 * @return
//	 */
//	public static boolean delModify(Context context,String ids) {
//		String url="rec/del.htm";
//		try {
//			JSONObject sendJSONObject = new JSONObject();
//			JSONObject mainJSONObject = new JSONObject();
//
//			mainJSONObject.put("ids", ids);
//			sendJSONObject.put("main", mainJSONObject);
//
//			sendJSONObject.put("biz", getBiz());
//
//			String json = sendJSONObject.toString();
//			LogUtil.println("delModify" + json);
//			String synchronousResult = CinetworkApplication.httpManager.SyncHttpCommunicate(url,json);
//			return HttpAnalyJsonManager.delModify(synchronousResult,context);
//		} catch (Exception e) {
//			HttpAnalyJsonManager.lastError = context.getResources().getString(R.string.network_connection_failed);
//			e.printStackTrace();
//			return false;
//		}
//	}
//
//	/**
//	 * 加盟
//	 * @param context
//	 * @param refer
//	 * @param address
//	 * @return
//	 */
//	public static boolean joinAdd(Context context,String refer,String name,
//			String phone,String gender,String email,String address) {
//		String url="vip/joinAdd.htm";
//		try {
//			JSONObject sendJSONObject = new JSONObject();
//			JSONObject mainJSONObject = new JSONObject();
//
//			mainJSONObject.put("refer", refer);
//			mainJSONObject.put("name", name);
//			mainJSONObject.put("phone", phone);
//			mainJSONObject.put("gender", gender);
//			mainJSONObject.put("email", email);
//			mainJSONObject.put("address", address);
//			sendJSONObject.put("main", mainJSONObject);
//
//			sendJSONObject.put("biz", getBiz());
//
//			String json = sendJSONObject.toString();
//			LogUtil.println("joinAdd" + json);
//			String synchronousResult = CinetworkApplication.httpManager.SyncHttpCommunicate(url,json);
//			return HttpAnalyJsonManager.joinAdd(synchronousResult,context);
//		} catch (Exception e) {
//			HttpAnalyJsonManager.lastError = context.getResources().getString(R.string.network_connection_failed);
//			e.printStackTrace();
//			return false;
//		}
//	}
//	/**
//	 * 加盟商信息
//	 * @param context
//	 * @param id
//	 * @return
//	 */
//	public static JoinInfoData joinInfo(Context context,String id) {
//		String url="vip/joinInfo.htm";
//		try {
//			JSONObject sendJSONObject = new JSONObject();
//			JSONObject mainJSONObject = new JSONObject();
//
//			mainJSONObject.put("id", id);
//			sendJSONObject.put("main", mainJSONObject);
//
//			sendJSONObject.put("biz", getBiz());
//
//			String json = sendJSONObject.toString();
//			LogUtil.println("joinInfo" + json);
//			String synchronousResult = CinetworkApplication.httpManager.SyncHttpCommunicate(url,json);
//			return HttpAnalyJsonManager.joinInfo(synchronousResult,context);
//		} catch (Exception e) {
//			HttpAnalyJsonManager.lastError = context.getResources().getString(R.string.network_connection_failed);
//			e.printStackTrace();
//			JoinInfoData joinInfoData = new JoinInfoData();
//			joinInfoData.setOK(false);
//			return joinInfoData;
//		}
//	}
//	/**
//	 * 添加购物车
//	 * @param context
//	 * @param id
//	 * @return
//	 */
//	public static boolean addCart(Context context,String prdId,String count) {
//		String url="cart/add.htm";
//		try {
//			JSONObject sendJSONObject = new JSONObject();
//			JSONObject mainJSONObject = new JSONObject();
//
//			mainJSONObject.put("prdId", prdId);
//			mainJSONObject.put("count", count);
//			sendJSONObject.put("main", mainJSONObject);
//
//			sendJSONObject.put("biz", getBiz());
//
//			String json = sendJSONObject.toString();
//			LogUtil.println("addCart" + json);
//			String synchronousResult = CinetworkApplication.httpManager.SyncHttpCommunicate(url,json);
//			return HttpAnalyJsonManager.addCart(synchronousResult,context);
//		} catch (Exception e) {
//			HttpAnalyJsonManager.lastError = context.getResources().getString(R.string.network_connection_failed);
//			e.printStackTrace();
//			return false;
//		}
//	}
//	/**
//	 * 修改购物车
//	 * @param context
//	 * @param id
//	 * @param count
//	 * @return
//	 */
//	public static boolean updateCart(Context context,String id,String count) {
//		String url="cart/update.htm";
//		try {
//			JSONObject sendJSONObject = new JSONObject();
//			JSONObject mainJSONObject = new JSONObject();
//
//			mainJSONObject.put("id", id);
//			mainJSONObject.put("count", count);
//			sendJSONObject.put("main", mainJSONObject);
//
//			sendJSONObject.put("biz", getBiz());
//
//			String json = sendJSONObject.toString();
//			LogUtil.println("updateCart" + json);
//			String synchronousResult = CinetworkApplication.httpManager.SyncHttpCommunicate(url,json);
//			return HttpAnalyJsonManager.updateCart(synchronousResult,context);
//		} catch (Exception e) {
//			HttpAnalyJsonManager.lastError = context.getResources().getString(R.string.network_connection_failed);
//			e.printStackTrace();
//			return false;
//		}
//	}
//	/**
//	 * 删除购物车
//	 * @param context
//	 * @param ids
//	 * @return
//	 */
//	public static boolean delCart(Context context,String ids) {
//		String url="cart/del.htm";
//		try {
//			JSONObject sendJSONObject = new JSONObject();
//			JSONObject mainJSONObject = new JSONObject();
//
//			mainJSONObject.put("ids", ids);
//			sendJSONObject.put("main", mainJSONObject);
//
//			sendJSONObject.put("biz", getBiz());
//
//			String json = sendJSONObject.toString();
//			LogUtil.println("delCart" + json);
//			String synchronousResult = CinetworkApplication.httpManager.SyncHttpCommunicate(url,json);
//			return HttpAnalyJsonManager.delCart(synchronousResult,context);
//		} catch (Exception e) {
//			HttpAnalyJsonManager.lastError = context.getResources().getString(R.string.network_connection_failed);
//			e.printStackTrace();
//			return false;
//		}
//	}
//	/**
//	 * 购物车列表
//	 * @param context
//	 * @param pageNo
//	 * @param pageSize
//	 * @return
//	 */
//	public static boolean cartList(Context context,String pageNo,String pageSize) {
//		String url="cart/list.htm";
//		try {
//			JSONObject sendJSONObject = new JSONObject();
//			JSONObject mainJSONObject = new JSONObject();
//
//			mainJSONObject.put("pageNo", pageNo);
//			mainJSONObject.put("pageSize", pageSize);
//			sendJSONObject.put("main", mainJSONObject);
//
//			sendJSONObject.put("biz", getBiz());
//
//			String json = sendJSONObject.toString();
//			LogUtil.println("cartList" + json);
//			String synchronousResult = CinetworkApplication.httpManager.SyncHttpCommunicate(url,json);
//			return HttpAnalyJsonManager.cartList(synchronousResult,context,CinetworkApplication.mDeviceUtil.getDeviceData().getLang());
//		} catch (Exception e) {
//			HttpAnalyJsonManager.lastError = context.getResources().getString(R.string.network_connection_failed);
//			e.printStackTrace();
//			return false;
//		}
//	}
//	public static ArrayList<OrderData> orderList(Context context,String pageNo,String pageSize) {
//		String url="order/list.htm";
//		try {
//			JSONObject sendJSONObject = new JSONObject();
//			JSONObject mainJSONObject = new JSONObject();
//
//			mainJSONObject.put("pageNo", pageNo);
//			mainJSONObject.put("pageSize", pageSize);
//			sendJSONObject.put("main", mainJSONObject);
//
//			sendJSONObject.put("biz", getBiz());
//
//			String json = sendJSONObject.toString();
//			LogUtil.println("orderList" + json);
//			String synchronousResult = CinetworkApplication.httpManager.SyncHttpCommunicate(url,json);
//			return HttpAnalyJsonManager.orderList(synchronousResult,context);
//		} catch (Exception e) {
//			HttpAnalyJsonManager.lastError = context.getResources().getString(R.string.network_connection_failed);
//			e.printStackTrace();
//			return new ArrayList<OrderData>();
//		}
//	}
//
//	public static boolean addOrder(Context context,ArrayList<BuyData> cartsDatas,String id,String contact,String mobile,String address) {
//		String url="order/add.htm";
//		try {
//			JSONObject sendJSONObject = new JSONObject();
//			JSONObject mainJSONObject = new JSONObject();
//
//
//			JSONArray cartsJsonArray = new JSONArray();
//			for(int i=0;i<cartsDatas.size()-2;i++){
//				BuyData buyData = cartsDatas.get(i);
//				JSONObject cartsJSONObject = new JSONObject();
//				cartsJSONObject.put("cartId", buyData.getCid());
//				cartsJSONObject.put("count", buyData.getNumber());
//				cartsJsonArray.put(cartsJSONObject);
//			}
//			mainJSONObject.put("carts", cartsJsonArray);
//
//			JSONObject receiveInfoOrderJSONObject = new JSONObject();
//			receiveInfoOrderJSONObject.put("id", id);
//			receiveInfoOrderJSONObject.put("contact", contact);
//			receiveInfoOrderJSONObject.put("mobile", mobile);
//			receiveInfoOrderJSONObject.put("address", address);
//			mainJSONObject.put("reciveInfoOrder", receiveInfoOrderJSONObject);
//			sendJSONObject.put("main", mainJSONObject);
//
//			sendJSONObject.put("biz", getBiz());
//
//			String json = sendJSONObject.toString();
//			LogUtil.println("addOrder" + json);
//			String synchronousResult = CinetworkApplication.httpManager.SyncHttpCommunicate(url,json);
//			return HttpAnalyJsonManager.addOrder(synchronousResult,context);
//		} catch (Exception e) {
//			HttpAnalyJsonManager.lastError = context.getResources().getString(R.string.network_connection_failed);
//			e.printStackTrace();
//			return false;
//		}
//	}
//
//	public static boolean checkoutPay(Context context,String type,String joinId,
//			String orderId,String seller,String payType) {
//		if(payType.equals("5")){
//			payType="3";
//		}
//		String url="checkout/pay.htm";
//		try {
//			JSONObject sendJSONObject = new JSONObject();
//			JSONObject mainJSONObject = new JSONObject();
//
//
//			mainJSONObject.put("type", type);
//			mainJSONObject.put("joinId", joinId);
//			mainJSONObject.put("orderId", orderId);
//			mainJSONObject.put("seller", seller);
////			mainJSONObject.put("fee", fee);
////			mainJSONObject.put("saleFee", saleFee);
//			mainJSONObject.put("payType", payType);
////			mainJSONObject.put("paySn", paySn);
////			mainJSONObject.put("payAccount", payAccount);
////			mainJSONObject.put("payStat", payStat);
////			mainJSONObject.put("payTime", payTime);
////			mainJSONObject.put("payCode", payCode);
////			mainJSONObject.put("payDesc", payDesc);
//			sendJSONObject.put("main", mainJSONObject);
//
//			sendJSONObject.put("biz", getBiz());
//
//			String json = sendJSONObject.toString();
//			LogUtil.println("checkoutPay" + json);
//			String synchronousResult = CinetworkApplication.httpManager.SyncHttpCommunicate(url,json);
//			return HttpAnalyJsonManager.checkoutPay(synchronousResult,context);
//		} catch (Exception e) {
//			HttpAnalyJsonManager.lastError = context.getResources().getString(R.string.network_connection_failed);
//			e.printStackTrace();
//			return false;
//		}
//	}
    private static JSONObject getBiz() throws JSONException {
        JSONObject bizJSONObject = new JSONObject();
        DeviceData deviceData = DButtonApplication.mDeviceUtil.getDeviceData();
        bizJSONObject.put("deviceId", deviceData.getDeviceId());
        bizJSONObject.put("deviceName", deviceData.getDeviceName());
        bizJSONObject.put("deviceOs", deviceData.getDeviceOs());
        bizJSONObject.put("deviceToken", deviceData.getDeviceToken());
        bizJSONObject.put("deviceType", deviceData.getDeviceType());
        bizJSONObject.put("deviceVersion", deviceData.getDeviceVersion());
        bizJSONObject.put("netType", deviceData.getNetType());
        bizJSONObject.put("version", deviceData.getVersion());
        bizJSONObject.put("token", deviceData.getToken());
        bizJSONObject.put("lang", deviceData.getLang());
        return bizJSONObject;
    }

}