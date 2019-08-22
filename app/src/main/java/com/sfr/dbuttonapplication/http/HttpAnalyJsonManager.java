package com.sfr.dbuttonapplication.http;


import android.content.Context;
import android.util.Log;

import com.sfr.dbuttonapplication.DButtonApplication;
import com.sfr.dbuttonapplication.R;
import com.sfr.dbuttonapplication.entity.AlarmCallData;
import com.sfr.dbuttonapplication.entity.AlarmContactData;
import com.sfr.dbuttonapplication.entity.AlarmData;
import com.sfr.dbuttonapplication.entity.AlarmIDData;
import com.sfr.dbuttonapplication.entity.AlarmListData;
import com.sfr.dbuttonapplication.entity.AlarmResultData;
import com.sfr.dbuttonapplication.entity.ContactData;
import com.sfr.dbuttonapplication.entity.LayerData;
import com.sfr.dbuttonapplication.entity.LayerListData;
import com.sfr.dbuttonapplication.entity.RegisterData;
import com.sfr.dbuttonapplication.entity.RenewData;
import com.sfr.dbuttonapplication.entity.UploadData;
import com.sfr.dbuttonapplication.entity.UserData;
import com.sfr.dbuttonapplication.utils.LogUtil;
import com.sfr.dbuttonapplication.utils.SettingSharedPerferencesUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class HttpAnalyJsonManager {
    public static String lastError;

    public static void lastErrorDefaultValue(Context context, String error) {
        HttpAnalyJsonManager.lastError = error;
//		if(error.equals("服务器忙，请稍后再操作")){
//			HttpAnalyJsonManager.lastError = context.getResources().getString(R.string.safari_error_code_1000);
//		}else if(error.equals("请求参数有误")){
//			HttpAnalyJsonManager.lastError = context.getResources().getString(R.string.safari_error_code_1001);
//		}else if(error.equals("验证异常")){
//			HttpAnalyJsonManager.lastError = context.getResources().getString(R.string.safari_error_code_1002);
//		}else if(error.equals("请求参数格式有误")){
//			HttpAnalyJsonManager.lastError = context.getResources().getString(R.string.safari_error_code_1003);
//		}else if(error.equals("短信发送频繁")){
//			HttpAnalyJsonManager.lastError = context.getResources().getString(R.string.safari_error_code_1004);
//		}else if(error.equals("验证失败或验证码已失效")){
//			HttpAnalyJsonManager.lastError = context.getResources().getString(R.string.safari_error_code_1005);
//		}else if(error.equals("非法请求")){
//			HttpAnalyJsonManager.lastError = context.getResources().getString(R.string.safari_error_code_1006);
//		}else if(error.equals("已存在")){
//			HttpAnalyJsonManager.lastError = context.getResources().getString(R.string.safari_error_code_1007);
//		}else if(error.equals("数据为空")){
//			HttpAnalyJsonManager.lastError = context.getResources().getString(R.string.safari_error_code_1008);
//		}else if(error.equals("注册失败")){
//			HttpAnalyJsonManager.lastError = context.getResources().getString(R.string.safari_error_code_1009);
//		}else if(error.equals("会话过期")){
//			HttpAnalyJsonManager.lastError = context.getResources().getString(R.string.safari_error_code_1010);
//		}else if(error.equals("用户未注册")){
//			HttpAnalyJsonManager.lastError = context.getResources().getString(R.string.safari_error_code_1011);
//		}else if(error.equals("短信发送失败")){
//			HttpAnalyJsonManager.lastError = context.getResources().getString(R.string.safari_error_code_1012);
//		}else if(error.equals("已绑定手机号")){
//			HttpAnalyJsonManager.lastError = context.getResources().getString(R.string.safari_error_code_1013);
//		}else if(error.equals("已绑定手机号")){
//			HttpAnalyJsonManager.lastError = context.getResources().getString(R.string.safari_error_code_1014);
//		}else if(error.equals("对象为空")){
//			HttpAnalyJsonManager.lastError = context.getResources().getString(R.string.safari_error_code_1015);
//		}else if(error.equals("支付中或支付失败")){
//			HttpAnalyJsonManager.lastError = context.getResources().getString(R.string.safari_error_code_1016);
//		}else if(error.equals("该账号已注册")){
//			HttpAnalyJsonManager.lastError = context.getResources().getString(R.string.safari_error_code_1017);
//		}else if(error.equals("原密码不对")){
//			HttpAnalyJsonManager.lastError = context.getResources().getString(R.string.safari_error_code_1018);
//		}else if(error.equals("手机号输入不正确")){
//			HttpAnalyJsonManager.lastError = context.getResources().getString(R.string.safari_error_code_1019);
//		}else if(error.equals("邮箱输入不正确")){
//			HttpAnalyJsonManager.lastError = context.getResources().getString(R.string.safari_error_code_1020);
//		}else if(error.equals("该邮箱已被占用")){
//			HttpAnalyJsonManager.lastError = context.getResources().getString(R.string.safari_error_code_1021);
//		}else if(error.equals("账号或密码输入错误")){
//			HttpAnalyJsonManager.lastError = context.getResources().getString(R.string.safari_error_code_1022);
//		}else if(error.equals("未知异常")){
//			HttpAnalyJsonManager.lastError = context.getResources().getString(R.string.safari_error_code_9999);
//		}

//		else if(error.equals("支付中")){
//			HttpAnalyJsonManager.lastError = context.getResources().getString(R.string.safari_error_code_1016);
//		}else if(error.equals("支付失败")){
//			HttpAnalyJsonManager.lastError = context.getResources().getString(R.string.safari_error_code_1016);
//		}
    }
//	public static boolean onlyResult(String json,Context context) throws JSONException{
//		if(!lastError.equals("")) return false;
//		JSONObject resultJson= new JSONObject(json);
//		if(resultJson.getString("result").equals("0"))
//		{
//
//			return true;
//		}
//		else
//		{
//			return false;
//		}
//	}

    /**
     * 获取版本信息
     *
     * @param json
     * @param context
     * @return
     * @throws JSONException
     */
    public static RenewData renew(String json, Context context) throws JSONException {
        RenewData renewData = new RenewData();
        renewData.setOK(false);
        if (!lastError.equals("")) {
            lastError = context.getResources().getString(R.string.acquisition_failure);
            return renewData;
        }
        JSONObject dataJson = new JSONObject(json);
        String lowestVer = dataJson.getString("lowestVer");
        String newVer = dataJson.getString("newVer");
        String link = dataJson.getString("link");
        renewData.setLink(link);
        renewData.setLowestVer(lowestVer);
        renewData.setNewVer(newVer);
        renewData.setOK(true);
        return renewData;
    }

    /**
     * 上传反馈内容
     *
     * @param json
     * @param context
     * @return
     * @throws JSONException
     */
    public static boolean feedUpload(String json, Context context) throws JSONException {
        if (!lastError.equals("")) {
            lastError = context.getResources().getString(R.string.upload_failed);
            return false;
        }
        JSONObject resultJson = new JSONObject(json);
        if (resultJson.getString("result").equals("0")) {

            return true;
        } else {
            return false;
        }
    }

    //sendSMS
    public static boolean sendSMS(String json, Context context) throws JSONException {
        if (!lastError.equals("")) return false;

        return true;
    }

    /**
     * 上传媒体文件
     *
     * @param json
     * @param context
     * @return
     * @throws JSONException
     */
    public static UploadData mediaUpload(String json, Context context) throws JSONException {
        UploadData uploadData = new UploadData();
        uploadData.setOK(false);
        if (!lastError.equals("")) {
            lastError = context.getResources().getString(R.string.upload_failed);
            return uploadData;
        }
        JSONObject resultJson = new JSONObject(json);
        String id = resultJson.getString("id");
        String name = resultJson.getString("name");
        String url = resultJson.getString("url");
        uploadData.setId(id);
        uploadData.setName(name);
        uploadData.setUrl(url);
        uploadData.setOK(true);
        android.util.Log.e("uploadData", "uploadData:"+uploadData.toString());
        return uploadData;
    }

    public static UploadData uploadMedia(String json, Context context) throws JSONException {
        UploadData uploadData = new UploadData();
        uploadData.setOK(false);
        JSONObject resultJson = new JSONObject(json);
        String result = resultJson.getString("result");
        if(result.equals("0")){
            lastError = context.getResources().getString(R.string.upload_failed);
            return uploadData;
        }
//        String id = resultJson.getString("id");
//        String name = resultJson.getString("name");
        String url = resultJson.getString("path");
//        uploadData.setId(id);
//        uploadData.setName(name);
        uploadData.setUrl(url);
        uploadData.setOK(true);
        android.util.Log.e("uploadData", "uploadData:"+uploadData.toString());
        return uploadData;
    }

    //registerAccount
    public static RegisterData registerAccount(String json, Context context) throws JSONException {
        RegisterData registerData = new RegisterData();
        registerData.setOK(false);
        if (!lastError.equals("")) return registerData;
        JSONObject dataJson = new JSONObject(json);
        String token = dataJson.getString("token");
        DButtonApplication.USER_TOKEN = token;
        registerData.setToken(token);
        registerData.setOK(true);
        return registerData;
    }

    public static UserData login(String json, Context context) throws JSONException {
        UserData userData = new UserData();
        userData.setOK(false);
        if (!lastError.equals("")) return userData;
        JSONObject dataJson = new JSONObject(json);
        userData.setToken(dataJson.getString("token"));
        DButtonApplication.USER_TOKEN = userData.getToken();
        SettingSharedPerferencesUtil.SetLoginTokenValue(context,userData.getToken());

        JSONObject infoJson = dataJson.getJSONObject("info");
        userData.setUsername(infoJson.getString("username"));
        userData.setName(infoJson.getString("name"));
        userData.setPhone(infoJson.getString("phone"));
        userData.setGender(infoJson.getString("gender"));
        userData.setAge(infoJson.getString("age"));
        userData.setBlood(infoJson.getString("blood"));
        userData.setBirth(infoJson.getLong("birth"));
        userData.setImg(infoJson.getString("img"));
        userData.setOK(true);
        return userData;
    }

    public static UserData userInfo(String json, Context context) throws JSONException {
        UserData userData = new UserData();
        userData.setOK(false);
        if (!lastError.equals("")) return userData;
        JSONObject dataJson = new JSONObject(json);
        userData.setUsername(dataJson.getString("username"));
        userData.setName(dataJson.getString("name"));
        userData.setPhone(dataJson.getString("phone"));
        userData.setGender(dataJson.getString("gender"));
        userData.setBirth(dataJson.getLong("birth"));
        userData.setAge(dataJson.getString("age"));
        userData.setBlood(dataJson.getString("blood"));
        userData.setImg(dataJson.getString("img"));
        userData.setOK(true);
        return userData;
    }

    //setUserInfo
    public static RegisterData setUserInfo(String json, Context context) throws JSONException {
        RegisterData registerData = new RegisterData();
        registerData.setOK(false);
        if (!lastError.equals("")) return registerData;
//        JSONObject dataJson = new JSONObject(json);
//        String token = dataJson.getString("token");
//        DButtonApplication.USER_TOKEN = token;
//        registerData.setToken(token);
        registerData.setOK(true);
        return registerData;
    }

    //增加联系人
    public static boolean addContact(String json, Context context) throws JSONException {
        if (!lastError.equals("")) return false;

        return true;
    }


    //修改联系人
    public static boolean upContact(String json, Context context) throws JSONException {
        if (!lastError.equals("")) return false;

        return true;
    }

    //设置紧急联系人
    public static boolean contactSet(String json, Context context) throws JSONException {
        if (!lastError.equals("")) return false;

        return true;
    }

    //联系人列表
    public static ContactData contactList(String json, Context context) throws JSONException {
        ContactData contactData = new ContactData();
        contactData.setOK(false);
        if (!lastError.equals("")) return contactData;
        JSONObject dataJson = new JSONObject(json);

        JSONArray contactListArray = dataJson.getJSONArray("contacts");
        ArrayList<UserData> userDataList = new ArrayList<UserData>();
        for (int i = 0; i < contactListArray.length(); i++) {
            JSONObject contactList = contactListArray.getJSONObject(i);
            UserData userData = new UserData();
            userData.setId(contactList.getString("id"));
            userData.setName(contactList.getString("name"));
            userData.setPhone(contactList.getString("phone"));
            userData.setGender(contactList.getString("gender"));
            //userData.setAge(contactList.getString("age"));
            userData.setBlood(contactList.getString("blood"));
            userData.setImg(contactList.getString("img"));
            userData.setRelation(contactList.getString("relation"));
            userData.setIsUrgent(contactList.getString("isUrgent"));
            LogUtil.i("contactList", userData.toString());
            userDataList.add(userData);
        }
        contactData.setList(userDataList);
        contactData.setOK(true);
        return contactData;
    }

    //删除联系人
    public static boolean contactDelete(String json, Context context) throws JSONException {
        if (!lastError.equals("")) return false;

        return true;
    }

    //搜索联系人
    public static UserData searchContact(String json, Context context) throws JSONException {
        UserData userData = new UserData();
        userData.setOK(false);
        if (!lastError.equals("")) return userData;
        JSONObject dataJson = new JSONObject(json);
        userData.setId(dataJson.getString("id"));
        userData.setUsername(dataJson.getString("username"));
        userData.setPhone(dataJson.getString("phone"));
        userData.setName(dataJson.getString("name"));
        userData.setGender(dataJson.getString("gender"));
        userData.setAge(dataJson.getString("age"));
        userData.setBlood(dataJson.getString("blood"));
        userData.setImg(dataJson.getString("img"));
        userData.setOK(true);
        return userData;
    }

    //添加固件
    public static boolean addLayer(String json, Context context) throws JSONException {
        if (!lastError.equals("")) return false;

        return true;
    }

    //修改固件
    public static boolean upLayer(String json, Context context) throws JSONException {
        if (!lastError.equals("")) return false;

        return true;
    }

    //设置默认固件
    public static boolean setLayer(String json, Context context) throws JSONException {
        if (!lastError.equals("")) return false;

        return true;
    }

    //固件列表
    public static LayerListData layerList(String json, Context context) throws JSONException {
        LayerListData layerListData = new LayerListData();
        layerListData.setOK(false);
        if (!lastError.equals("")) return layerListData;
        JSONObject dataJson = new JSONObject(json);

        JSONArray layerDataListArray = dataJson.getJSONArray("layers");
        ArrayList<LayerData> layerDataList = new ArrayList<LayerData>();
        for (int i = 0; i < layerDataListArray.length(); i++) {
            JSONObject layerDataJSON = layerDataListArray.getJSONObject(i);
            LayerData layerData = new LayerData();
            layerData.setId(layerDataJSON.getString("id"));
            layerData.setIsDefalut(layerDataJSON.getString("isDefault"));//isDefalut
            layerData.setMac(layerDataJSON.getString("mac"));
            layerData.setName(layerDataJSON.getString("name"));
            LogUtil.i("contactList", layerData.toString());
            layerDataList.add(layerData);
        }
        layerListData.setList(layerDataList);
        layerListData.setOK(true);
        return layerListData;
    }

    //删除固件
    public static boolean delLayer(String json, Context context) throws JSONException {
        if (!lastError.equals("")) return false;

        return true;
    }

    //上传警报
    public static AlarmIDData alarmUp(String json, Context context) throws JSONException {
        AlarmIDData alarmIDData = new AlarmIDData();
        alarmIDData.setOK(false);
        if (!lastError.equals("")) return alarmIDData;

//        JSONObject dataJson = new JSONObject(json);
//        String id = dataJson.getString("id");
        String id = json.substring(1,json.length()-1);
        Log.e("alarmUp", "onReceive() alarmUp++++++++++++++++++++++id:"+id);
        alarmIDData.setId(id);
        alarmIDData.setOK(true);
        return alarmIDData;
    }
    //删除固件
    public static boolean alarmUpdate(String json, Context context) throws JSONException {
        if (!lastError.equals("")) return false;

        return true;
    }


    //我收到的警报---这块设计有问题
    public static AlarmListData myGetAlarmList(String json, Context context) throws JSONException {
        AlarmListData alarmListData = new AlarmListData();
        alarmListData.setOK(false);
        if (!lastError.equals("")) return alarmListData;
        JSONObject dataJson = new JSONObject(json);
        JSONArray alarmDataListArray = dataJson.getJSONArray("results");
        ArrayList<AlarmResultData> alarmDataList = new ArrayList<AlarmResultData>();
        for (int i = 0; i < alarmDataListArray.length(); i++) {
            try {
                AlarmResultData alarmResultData = new AlarmResultData();
                AlarmData alarmData = new AlarmData();
                UserData userData = new UserData();
                JSONObject alarmResultJSON = alarmDataListArray.getJSONObject(i);
                alarmResultData.setId(alarmResultJSON.getString("id"));
                alarmResultData.setAlarmId(alarmResultJSON.getString("alarmId"));
                JSONObject alarmDataJSON = alarmResultJSON.getJSONObject("alarm");
                alarmData.setId(alarmDataJSON.getString("id"));
                alarmData.setPoint(alarmDataJSON.getString("point"));
                alarmData.setBeginTime(alarmDataJSON.getString("beginTime"));
                alarmData.setEndTime(alarmDataJSON.getString("endTime"));
                alarmData.setAddress(alarmDataJSON.getString("address"));
                alarmData.setRecord(alarmDataJSON.getString("record"));
                alarmData.setDuration(alarmDataJSON.getString("duration"));
                alarmData.setVipName(alarmDataJSON.getString("vipName"));
                alarmData.setVipImg(alarmDataJSON.getString("vipImg"));
                alarmData.setVipPhone(alarmDataJSON.getString("vipPhone"));
                alarmResultData.setAlarmData(alarmData);
                JSONObject contactDataJSON = alarmResultJSON.getJSONObject("contact");
                userData.setId(alarmResultJSON.getString("id"));
                userData.setCreateTime(contactDataJSON.getString("createdTime"));
                userData.setDelFlag(contactDataJSON.getString("delFlag"));
                userData.setVipId(contactDataJSON.getString("vipId"));
                userData.setName(contactDataJSON.getString("name"));
                userData.setPhone(contactDataJSON.getString("phone"));
                userData.setGender(contactDataJSON.getString("gender"));
                userData.setBlood(contactDataJSON.getString("blood"));
                userData.setImg(contactDataJSON.getString("img"));
                userData.setRelation(contactDataJSON.getString("relation"));
                userData.setIsUrgent(contactDataJSON.getString("isUrgent"));
                alarmResultData.setUserData(userData);
                alarmDataList.add(alarmResultData);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        alarmListData.setCount(dataJson.getString("count"));
        alarmListData.setIsNext(dataJson.getString("isNext"));
        alarmListData.setAlarmDataArrayList(alarmDataList);
        alarmListData.setOK(true);
        return alarmListData;
    }

    //我发出的警报
    public static AlarmListData mySendAlarmList(String json, Context context) throws JSONException {
        AlarmListData alarmListData = new AlarmListData();
        alarmListData.setOK(false);
        if (!lastError.equals("")) return alarmListData;
        JSONObject dataJson = new JSONObject(json);
        JSONArray alarmDataListArray = dataJson.getJSONArray("results");
        ArrayList<AlarmResultData> alarmDataList = new ArrayList<AlarmResultData>();
        for (int i = 0; i < alarmDataListArray.length(); i++) {
            try {
                AlarmResultData alarmResultData = new AlarmResultData();
                AlarmData alarmData = new AlarmData();
                JSONObject alarmResultJSON = alarmDataListArray.getJSONObject(i);
                alarmData.setId(alarmResultJSON.getString("id"));
                alarmData.setVipImg(alarmResultJSON.getString("vipImg"));
                alarmData.setVipName(alarmResultJSON.getString("vipName"));
                alarmData.setPoint(alarmResultJSON.getString("point"));
                alarmData.setBeginTime(alarmResultJSON.getString("beginTime"));
                alarmData.setEndTime(alarmResultJSON.getString("endTime"));
                alarmData.setAddress(alarmResultJSON.getString("address"));
                alarmData.setRecord(alarmResultJSON.getString("record"));
                alarmData.setDuration(alarmResultJSON.getString("duration"));
                alarmResultData.setAlarmData(alarmData);
                alarmDataList.add(alarmResultData);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        alarmListData.setCount(dataJson.getString("count"));
        alarmListData.setIsNext(dataJson.getString("isNext"));
        alarmListData.setAlarmDataArrayList(alarmDataList);
        alarmListData.setOK(true);
        return alarmListData;
    }

    //警报详情
    public static AlarmData alarmDetail(String json, Context context) throws JSONException {
        AlarmData alarmData = new AlarmData();
        alarmData.setOK(false);
        if (!lastError.equals("")) return alarmData;
        JSONObject dataJson = new JSONObject(json);
        //JSONObject callJson = dataJson.getJSONObject("callAlarms");
        //JSONObject contactJson = callJson.getJSONObject("contact");

        AlarmCallData alarmCallData = new AlarmCallData();
        AlarmContactData alarmContactData = new AlarmContactData();
//        alarmContactData.setId(contactJson.getString("id"));
//        alarmContactData.setPhone(contactJson.getString("phone"));
//        alarmContactData.setName(contactJson.getString("name"));
//        alarmContactData.setRelation(contactJson.getString("relation"));
//        alarmContactData.setImg(contactJson.getString("img"));
//        alarmCallData.setAlarmContactData(alarmContactData);
//
//        alarmCallData.setId(callJson.getString("id"));
//        alarmCallData.setAlarmId(callJson.getString("alarmId"));
//        alarmCallData.setVipId(callJson.getString("vipId"));
//        alarmCallData.setContactId(callJson.getString("contactId"));
        alarmData.setAlarmCallData(alarmCallData);

        alarmData.setId(dataJson.getString("id"));
        alarmData.setType(dataJson.getString("type"));
        alarmData.setPoint(dataJson.getString("point"));
        alarmData.setBeginTime(dataJson.getString("beginTime"));
        alarmData.setEndTime(dataJson.getString("endTime"));
        alarmData.setAddress(dataJson.getString("address"));
        alarmData.setRecord(dataJson.getString("record"));
        alarmData.setDuration(dataJson.getString("duration"));
        alarmData.setSession(dataJson.getString("session"));
        //多了两个参数,"vipId":"ff808081670fe9610167112476450017","vipName":"ww"暂时用不到就是
        alarmData.setOK(true);
        return alarmData;
    }

    //删除警报
    public static boolean delAlarm(String json, Context context) throws JSONException {
        if (!lastError.equals("")) return false;

        return true;
    }


    //附近的警报
    public static AlarmListData getNearAlarmList(String json, Context context) throws JSONException {
        AlarmListData alarmListData = new AlarmListData();
        alarmListData.setOK(false);
        if (!lastError.equals("")) return alarmListData;
        JSONObject dataJson = new JSONObject(json);
        JSONArray alarmDataListArray = dataJson.getJSONArray("alarms");
        ArrayList<AlarmResultData> alarmDataList = new ArrayList<AlarmResultData>();
        for (int i = 0; i < alarmDataListArray.length(); i++) {
            try {
                AlarmResultData alarmResultData = new AlarmResultData();
                AlarmData alarmData = new AlarmData();
                JSONObject alarmResultJSON = alarmDataListArray.getJSONObject(i);
                //alarmResultData.setId(alarmResultJSON.getString("id"));
                //alarmResultData.setAlarmId(alarmResultJSON.getString("alarmId"));
                alarmData.setId(alarmResultJSON.getString("id"));
                alarmData.setVipImg(alarmResultJSON.getString("vipImg"));
                alarmData.setVipName(alarmResultJSON.getString("vipName"));
                alarmData.setPoint(alarmResultJSON.getString("point"));
                alarmData.setBeginTime(alarmResultJSON.getString("beginTime"));
                alarmData.setEndTime(alarmResultJSON.getString("endTime"));
                alarmData.setAddress(alarmResultJSON.getString("address"));
                alarmData.setRecord(alarmResultJSON.getString("record"));
                alarmData.setDuration(alarmResultJSON.getString("duration"));
                alarmData.setType(alarmResultJSON.getString("type"));
                alarmData.setSource(alarmResultJSON.getString("source"));
                alarmData.setLongitude(alarmResultJSON.getString("longitude"));
                alarmData.setLatitude(alarmResultJSON.getString("latitude"));
                alarmResultData.setAlarmData(alarmData);
                alarmDataList.add(alarmResultData);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        alarmListData.setAlarmDataArrayList(alarmDataList);
        alarmListData.setOK(true);
        return alarmListData;
    }

//	/**
//	 * 查看媒体文件
//	 * @param json
//	 * @param context
//	 * @return
//	 * @throws JSONException
//	 */
//	public static UploadData view(String json,Context context) throws JSONException{
//		UploadData uploadData = new UploadData();
//		uploadData.setOK(false);
//		if(!lastError.equals("")){
//			lastError=context.getResources().getString(R.string.acquisition_failure);
//			return uploadData;
//		}
//		JSONObject resultJson= new JSONObject(json);
//		if(resultJson.getString("result").equals("0"))
//		{
//			JSONObject dataJson = resultJson.getJSONObject("data");
//			String id = dataJson.getString("id");
//			String name = dataJson.getString("name");
//			String url = dataJson.getString("url");
//			uploadData.setId(id);
//			uploadData.setName(name);
//			uploadData.setUrl(url);
//			uploadData.setOK(true);
//			return uploadData;
//		}
//		else
//		{
//			return uploadData;
//		}
//	}
//
//	/**
//	 * 上传异常信息
//	 * @param json
//	 * @param context
//	 * @return
//	 * @throws JSONException
//	 */
//	public static boolean logException(String json,Context context) throws JSONException{
//		if(!lastError.equals("")){
//			lastError=context.getResources().getString(R.string.upload_failed);
//			return false;
//		}
//		JSONObject resultJson= new JSONObject(json);
//		if(resultJson.getString("result").equals("0"))
//		{
//
//			return true;
//		}
//		else
//		{
//			return false;
//		}
//	}
//	/**
//	 * 修改用户所有信息
//	 * @param json
//	 * @param context
//	 * @return
//	 * @throws JSONException
//	 */
//	public static boolean setAllInfo(String json,Context context) throws JSONException{
//		if(!lastError.equals("")){
//			lastError=context.getResources().getString(R.string.modify_failed);
//			return false;
//		}
//		JSONObject resultJson= new JSONObject(json);
//		if(resultJson.getString("result").equals("0"))
//		{
//
//			return true;
//		}
//		else
//		{
//			return false;
//		}
//	}
//	/**
//	 * 修改用户单挑信息
//	 * @param json
//	 * @param context
//	 * @return
//	 * @throws JSONException
//	 */
//	public static boolean setSingleInfo(String json,Context context) throws JSONException{
//		if(!lastError.equals("")){
//			lastError=context.getResources().getString(R.string.modify_failed);
//			return false;
//		}
//		JSONObject resultJson= new JSONObject(json);
//		if(resultJson.getString("result").equals("0"))
//		{
//
//			return true;
//		}
//		else
//		{
//			return false;
//		}
//	}
//
//
//	//loginAccount
//	public static boolean loginAccount(String json,Context context) throws JSONException{
//		if(!lastError.equals("")) return false;
//		JSONObject resultJson= new JSONObject(json);
//		if(resultJson.getString("result").equals("0"))
//		{
//
//			CinetworkApplication.adsList=new ArrayList<AdsData>();
//			JSONObject dataJson = resultJson.getJSONObject("data");
//			CinetworkApplication.mDeviceUtil.setToken(dataJson.getString("token"));
//			JSONArray adsArray = dataJson.getJSONArray("ads");
//			for(int i=0;i<adsArray.length();i++){
//				JSONObject ads = adsArray.getJSONObject(i);
//				AdsData adsData = new AdsData();
//				adsData.setPicUrl(ads.getString("picUrl"));
//				adsData.setLink(ads.getString("link"));
//				LogUtil.i("login", adsData.toString());
//				CinetworkApplication.adsList.add(adsData);
//			}
//			CinetworkApplication.mInfoData = new InfoData();
//
//			CinetworkApplication.mInfoData.setJoinFee(dataJson.getString("joinFee"));
//			CinetworkApplication.mInfoData.setDiscountId(dataJson.getString("discountId"));
//			CinetworkApplication.mInfoData.setSale(dataJson.getString("sale"));
//			JSONObject infoJson = dataJson.getJSONObject("info");
//			CinetworkApplication.mInfoData.setGender(infoJson.getString("gender"));
//			CinetworkApplication.mInfoData.setImgUrl(infoJson.getString("imgUrl"));
//			CinetworkApplication.mInfoData.setJoinId(infoJson.getString("joinId"));
//			CinetworkApplication.mInfoData.setMobile(infoJson.getString("mobile"));
//			CinetworkApplication.mInfoData.setName(infoJson.getString("name"));
//			CinetworkApplication.mInfoData.setNick(infoJson.getString("nick"));
//			CinetworkApplication.mInfoData.setStat(infoJson.getString("stat"));
//			CinetworkApplication.mInfoData.setTel(infoJson.getString("tel"));
//			LogUtil.i("login", "CinetworkApplication.mInfoData:"+CinetworkApplication.mInfoData.toString());
//			return true;
//		}
//		else
//		{
//			return false;
//		}
//	}
//	//forgetPassword
//	public static boolean forgetPassword(String json,Context context) throws JSONException{
//		if(!lastError.equals("")) return false;
//		JSONObject resultJson= new JSONObject(json);
//		if(resultJson.getString("result").equals("0"))
//		{
//
//			return true;
//		}
//		else
//		{
//			return false;
//		}
//	}
//	public static boolean bind(String json,Context context) throws JSONException{
//		if(!lastError.equals("")) return false;
//		JSONObject resultJson= new JSONObject(json);
//		if(resultJson.getString("result").equals("0"))
//		{
//
//			return true;
//		}
//		else
//		{
//			return false;
//		}
//	}
//	public static boolean proList(String json,Context context,String type,String ltype) throws JSONException{
//		if(!lastError.equals("")) return false;
//		JSONObject resultJson= new JSONObject(json);
//		if(resultJson.getString("result").equals("0"))
//		{
//			//先查询TYPE有的
//			HashMap<String,String> map=DatabaseService.findAllMarketProductMapByType(Integer.parseInt(type));
//			//存储数据库
//			JSONObject dataJson = resultJson.getJSONObject("data");
//			JSONArray productsArray = dataJson.getJSONArray("products");
//			for(int i=0;i<productsArray.length();i++){
//				JSONObject products = productsArray.getJSONObject(i);
//				String id=products.getString("id");
//				if(map.containsKey(id)){
//					map.remove(id);
//				}
//				String name=products.getString("name");
//				String price=products.getString("price");
//				String briefDesp=products.getString("briefDesp");
//				String detailDesp=products.getString("detailDesp");
//				String coverUrl=products.getString("coverUrl");
//				DatabaseService.createMarketProduct(coverUrl,name, price, briefDesp, detailDesp, "", "", Integer.parseInt(type),id,ltype);
//			}
//			//DatabaseService.createMarketProduct("picture","产品13", "369.00", "柠檬健康好生活", "能够健身", "产品信息1", "产品特点1",Integer.parseInt(type),10);
//
//			//遍历删除
//			for(String key:map.keySet()){
//				DatabaseService.deleteMarketProductBySID(key);
//			}
//
//
//			return true;
//		}
//		else
//		{
//			return false;
//		}
//	}
//	public static boolean proDetail(String json,Context context) throws JSONException{
//		if(!lastError.equals("")) return false;
//		JSONObject resultJson= new JSONObject(json);
//		if(resultJson.getString("result").equals("0"))
//		{
//			JSONObject dataJson = resultJson.getJSONObject("data");
//			JSONArray picsArray = dataJson.getJSONArray("pics");
//			String id=dataJson.getString("id");
//			String name=dataJson.getString("name");
//			String price=dataJson.getString("price");
//			String briefDesp=dataJson.getString("briefDesp");
//			String detailDesp=dataJson.getString("detailDesp");
//			String picUrl="";
//			String detailUrl="";
//			if(picsArray.length()>0){
//				JSONObject pics = picsArray.getJSONObject(0);
//				picUrl=pics.getString("picUrl");
//			}
//			if(picsArray.length()>1){
//				JSONObject pics = picsArray.getJSONObject(1);
//				detailUrl=pics.getString("picUrl");
//			}
//			DatabaseService.updateMarketProductBigUrl(name, price, briefDesp, detailDesp,picUrl,detailUrl, id);
//			return true;
//		}
//		else
//		{
//			return false;
//		}
//	}
//	public static boolean orderDetail(String json,Context context) throws JSONException{
//		if(!lastError.equals("")) return false;
//		JSONObject resultJson= new JSONObject(json);
//		if(resultJson.getString("result").equals("0"))
//		{
//			JSONObject dataJson = resultJson.getJSONObject("data");
//			JSONObject orderJson = dataJson.getJSONObject("order");
//			CinetworkApplication.orderMoney=orderJson.getString("saleAmount");
//			JSONObject recInfoJson = orderJson.getJSONObject("recInfo");
//			CinetworkApplication.orderContact=recInfoJson.getString("contact");
//			CinetworkApplication.orderPhone=recInfoJson.getString("mobile");
//			CinetworkApplication.orderAddress=recInfoJson.getString("address");
//			return true;
//		}
//		else
//		{
//			return false;
//		}
//	}
//	public static boolean addRec(String json,Context context) throws JSONException{
//		if(!lastError.equals("")) return false;
//		JSONObject resultJson= new JSONObject(json);
//		if(resultJson.getString("result").equals("0"))
//		{
//
//			return true;
//		}
//		else
//		{
//			return false;
//		}
//	}
//	public static RecData recList(String json,Context context) throws JSONException{
//		RecData recData = new RecData();
//		recData.setOK(false);
//		if(!lastError.equals("")) return recData;
//		JSONObject resultJson= new JSONObject(json);
//		if(resultJson.getString("result").equals("0"))
//		{
//
//			JSONObject dataJson = resultJson.getJSONObject("data");
//			JSONArray recivesArray = dataJson.getJSONArray("recives");
//			if(recivesArray.length()>0){
//				JSONObject recives = recivesArray.getJSONObject(recivesArray.length()-1);
//				if(recives.has("address")){
//					recData.setAddress(recives.getString("address"));
//				}else{
//					recData.setAddress("");
//				}
//				if(recives.has("contact")){
//					recData.setName(recives.getString("contact"));
//				}else{
//					recData.setName("");
//				}
//				if(recives.has("mobile")){
//					recData.setPhone(recives.getString("mobile"));
//				}else{
//					recData.setPhone("");
//				}
//				if(recives.has("id")){
//					recData.setRecId(recives.getString("id"));
//				}else{
//					recData.setRecId("");
//				}
//			}
//			recData.setOK(true);
//			return recData;
//		}
//		else
//		{
//			return recData;
//		}
//	}
//	public static boolean recUpdate(String json,Context context) throws JSONException{
//		if(!lastError.equals("")) return false;
//		JSONObject resultJson= new JSONObject(json);
//		if(resultJson.getString("result").equals("0"))
//		{
//
//			return true;
//		}
//		else
//		{
//			return false;
//		}
//	}
//	public static boolean recModify(String json,Context context) throws JSONException{
//		if(!lastError.equals("")) return false;
//		JSONObject resultJson= new JSONObject(json);
//		if(resultJson.getString("result").equals("0"))
//		{
//
//			return true;
//		}
//		else
//		{
//			return false;
//		}
//	}
//	public static boolean delModify(String json,Context context) throws JSONException{
//		if(!lastError.equals("")) return false;
//		JSONObject resultJson= new JSONObject(json);
//		if(resultJson.getString("result").equals("0"))
//		{
//
//			return true;
//		}
//		else
//		{
//			return false;
//		}
//	}
//	public static boolean joinAdd(String json,Context context) throws JSONException{
//		CinetworkApplication.joinId="";
//		if(!lastError.equals("")) return false;
//		JSONObject resultJson= new JSONObject(json);
//		if(resultJson.getString("result").equals("0"))
//		{
//
//			JSONObject dataJson = resultJson.getJSONObject("data");
//			CinetworkApplication.joinId=dataJson.getString("id");
//			return true;
//		}
//		else
//		{
//			return false;
//		}
//	}
//	public static JoinInfoData joinInfo(String json,Context context) throws JSONException{
//		JoinInfoData joinInfoData = new JoinInfoData();
//		joinInfoData.setOK(false);
//		if(!lastError.equals("")) return joinInfoData;
//		JSONObject resultJson= new JSONObject(json);
//		if(resultJson.getString("result").equals("0"))
//		{
//			JSONObject dataJson = resultJson.getJSONObject("data");
//			joinInfoData.setAddress(dataJson.getString("address"));
//			joinInfoData.setEmail(dataJson.getString("email"));
//			joinInfoData.setGender(dataJson.getString("gender"));
//			joinInfoData.setGrUrl(dataJson.getString("qrUrl"));
//			joinInfoData.setId(dataJson.getString("id"));
//			joinInfoData.setImgUrl(dataJson.getString("imgUrl"));
//			joinInfoData.setMobile(dataJson.getString("mobile"));
//			joinInfoData.setName(dataJson.getString("name"));
//			joinInfoData.setNick(dataJson.getString("nick"));
//			joinInfoData.setStat(dataJson.getString("stat"));
//			joinInfoData.setPhone(dataJson.getString("phone"));
//			//做解析
//			joinInfoData.setOK(true);
//			return joinInfoData;
//		}
//		else
//		{
//			return joinInfoData;
//		}
//	}
//	public static boolean addCart(String json,Context context) throws JSONException{
//		if(!lastError.equals("")) return false;
//		JSONObject resultJson= new JSONObject(json);
//		if(resultJson.getString("result").equals("0"))
//		{
//
//			return true;
//		}
//		else
//		{
//			return false;
//		}
//	}
//	public static boolean updateCart(String json,Context context) throws JSONException{
//		if(!lastError.equals("")) return false;
//		JSONObject resultJson= new JSONObject(json);
//		if(resultJson.getString("result").equals("0"))
//		{
//
//			return true;
//		}
//		else
//		{
//			return false;
//		}
//	}
//	public static boolean delCart(String json,Context context) throws JSONException{
//		if(!lastError.equals("")) return false;
//		JSONObject resultJson= new JSONObject(json);
//		if(resultJson.getString("result").equals("0"))
//		{
//
//			return true;
//		}
//		else
//		{
//			return false;
//		}
//	}
//	public static boolean addOrder(String json,Context context) throws JSONException{
//		if(!lastError.equals("")) return false;
//		JSONObject resultJson= new JSONObject(json);
//		if(resultJson.getString("result").equals("0"))
//		{
//
//			JSONObject dataJson = resultJson.getJSONObject("data");
//			JSONObject orderJson = dataJson.getJSONObject("order");
//			CinetworkApplication.orderId=orderJson.getString("id");
//			CinetworkApplication.orderNo=orderJson.getString("no");
//			return true;
//		}
//		else
//		{
//			return false;
//		}
//	}
//	public static boolean cartList(String json,Context context,String langType) throws JSONException{
//		if(!lastError.equals("")) return false;
//		JSONObject resultJson= new JSONObject(json);
//		if(resultJson.getString("result").equals("0"))
//		{
//
//			//存储数据库
//			JSONObject dataJson = resultJson.getJSONObject("data");
//			JSONArray cartsArray = dataJson.getJSONArray("carts");
//			for(int i=0;i<cartsArray.length();i++){
//				JSONObject carts = cartsArray.getJSONObject(i);
//				String id=carts.getString("id");
//				String prdId=carts.getString("prdId");
//				String type=carts.getString("type");
//				String name=carts.getString("name");
//				String price=carts.getString("price");
//				String briefDesp=carts.getString("briefDesp");
//				String coverUrl=carts.getString("coverUrl");
//				String count=carts.getString("count");
//				DatabaseService.createShoppingCart(coverUrl,name, price, briefDesp, count, CinetworkApplication.openID,prdId,id,langType);
//			}
//			return true;
//		}
//		else
//		{
//			return false;
//		}
//	}
//	public static ArrayList<OrderData> orderList(String json,Context context) throws JSONException{
//		ArrayList<OrderData> orderDatas = new ArrayList<OrderData>();
//		if(!lastError.equals("")) return orderDatas;
//		JSONObject resultJson= new JSONObject(json);
//		if(resultJson.getString("result").equals("0"))
//		{
//
//			//存储数据库
//			JSONObject dataJson = resultJson.getJSONObject("data");
//			JSONArray ordersArray = dataJson.getJSONArray("orders");
//			for(int i=0;i<ordersArray.length();i++){
//				JSONObject orders = ordersArray.getJSONObject(i);
//				OrderData orderData = new OrderData();
//				orderData.setAmount(orders.getString("amount"));
//				orderData.setCreateTime(orders.getString("createTime"));
//				orderData.setId(orders.getString("id"));
//				orderData.setLang(orders.getString("lang"));
//				if(orders.has("no")){
//					orderData.setNo(orders.getString("no"));
//				}else{
//					orderData.setNo("暂无订单号");
//				}
//				orderData.setPayType(orders.getString("payType"));
//				orderData.setSale(orders.getString("sale"));
//				orderData.setSaleAmount(orders.getString("saleAmount"));
//				orderData.setStat(orders.getString("stat"));
//				orderDatas.add(orderData);
//			}
//			return orderDatas;
//		}
//		else
//		{
//			return orderDatas;
//		}
//	}
//	public static boolean checkoutPay(String json,Context context) throws JSONException{
//		CinetworkApplication.payNo="";
//		CinetworkApplication.payFree="";
//		CinetworkApplication.sign="";
//		CinetworkApplication.prePayId="";
//		CinetworkApplication.noncestr="";
//		CinetworkApplication.timestamp="";
//		if(!lastError.equals("")) return false;
//		JSONObject resultJson= new JSONObject(json);
//		if(resultJson.getString("result").equals("0"))
//		{
//
//			JSONObject dataJson = resultJson.getJSONObject("data");
//			CinetworkApplication.payNo=dataJson.getString("no");
//			CinetworkApplication.payFree=dataJson.getString("fee");
//			CinetworkApplication.sign=dataJson.getString("sign");
//			CinetworkApplication.prePayId=dataJson.getString("prePayId");
//			CinetworkApplication.noncestr=dataJson.getString("noncestr");
//			CinetworkApplication.timestamp=dataJson.getString("timestamp");
//			return true;
//		}
//		else
//		{
//			return false;
//		}
//	}
//

}
