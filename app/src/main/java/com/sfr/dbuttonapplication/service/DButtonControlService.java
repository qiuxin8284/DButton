package com.sfr.dbuttonapplication.service;

import android.Manifest;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.jordan.httplibrary.HttpUtils;
import com.jordan.httplibrary.utils.CommonUtils;
import com.sfr.dbuttonapplication.DButtonApplication;
import com.sfr.dbuttonapplication.R;
import com.sfr.dbuttonapplication.entity.AlarmIDData;
import com.sfr.dbuttonapplication.entity.PointData;
import com.sfr.dbuttonapplication.entity.UploadData;
import com.sfr.dbuttonapplication.http.HttpAnalyJsonManager;
import com.sfr.dbuttonapplication.http.HttpSendJsonManager;
import com.sfr.dbuttonapplication.utils.ToastUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DButtonControlService extends Service {
    public String TAG = "DButtonControlService";
    private String nowTime = "";//后续删除掉
    private String endTime = "";//后续删除掉
    private long nowTimeLong;
    private long endTimeLong;
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");// HH:mm:ss
    private LocationManager locationManager;
    private boolean isAlarmUp = false;
    private ArrayList<PointData> mPointDataList = new ArrayList<PointData>();

    @Override
    public void onCreate() {
        super.onCreate();

        mDButtonControlReceiver = new DButtonControlReceiver();//广播接受者实例
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(DButtonApplication.ACTION_ONE_CLICK);
        intentFilter.addAction(DButtonApplication.ACTION_DOUBLE_CLICK);
        intentFilter.addAction(DButtonApplication.ACTION_LONG_CLICK);
        registerReceiver(mDButtonControlReceiver, intentFilter);


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mDButtonControlReceiver);
        Log.e(TAG, "onDestroy() ++++++++++++++++++++++++++++++++++++++++++++");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG, "onStartCommand() ++++++++++++++++++++++++++++++++++++++++|Date获取当前日期时间" + nowTime);

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public LocationListener locationListener = new LocationListener() {
        // Provider的状态在可用、暂时不可用和无服务三个状态直接切换时触发此函数
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.e(TAG, "locationListener onStatusChanged() ++++++++++++++++++++++++++++++++++++++++++++");

        }

        // Provider被enable时触发此函数，比如GPS被打开
        @Override
        public void onProviderEnabled(String provider) {
            Log.e(TAG, "locationListener onProviderEnabled() ++++++++++++++++++++++++++++++++++++++++++++");

        }

        // Provider被disable时触发此函数，比如GPS被关闭
        @Override
        public void onProviderDisabled(String provider) {
            Log.e(TAG, "locationListener onProviderDisabled() ++++++++++++++++++++++++++++++++++++++++++++");

        }

        //当坐标改变时触发此函数，如果Provider传进相同的坐标，它就不会被触发
        @Override
        public void onLocationChanged(Location location) {
            if (hasStart) {
                PointData pointData = new PointData();
                Date date = new Date(System.currentTimeMillis());
                String pointTime = simpleDateFormat.format(date);
                pointData.setPointTime(date.getTime());
                pointData.setLocation(location);
                //定义log方法在后台打印
                Log.e(TAG, "locationListener onLocationChanged() ++++++++++++++++++++++++++++++++++++++++++++"
                        + simpleDateFormat.format(new Date(System.currentTimeMillis())) + "|location:" + location.toString() + "|Date获取当前日期时间:" + pointTime);
                //存个DATA 存到List里面
                mPointDataList.add(pointData);
                if (isAlarmUp) {
                    //触发定时：记录轨迹，修改接口轨迹上传
                    //然后isAlarmUp=true定时上传轨迹/周一志旺提供接口
                    //初始化的时候赋值List
                    mPointDataList = new ArrayList<PointData>();
                }
            }
        }
    };


    private DButtonControlReceiver mDButtonControlReceiver;
    public static final int MESSAGE_BEGIN_FILE = 0;
    public static final int MESSAGE_RECORD_FILE = MESSAGE_BEGIN_FILE + 1;
    public static final int MESSAGE_END_FILE = MESSAGE_BEGIN_FILE + 2;
    public static final String FILE_CONTENT = "file_content";
    private Handler mFileHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MESSAGE_BEGIN_FILE:
                    break;
                case MESSAGE_RECORD_FILE:
                    byte[] content = msg.getData().getByteArray(FILE_CONTENT);

                    break;
                case MESSAGE_END_FILE:
                    break;
            }
        }
    };

    public class DButtonControlReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(DButtonApplication.ACTION_DOUBLE_CLICK)) {
                if (!hasStart) {
                    hasStart = true;
                    mPointDataList = new ArrayList<PointData>();
                    startRemark();//记录开始时间，记录轨迹点，记录录音
                } else {

                }
            } else if (action.equals(DButtonApplication.ACTION_LONG_CLICK)) {
                //---------------------长按--------------------------
                if (!hasStart) {
                    hasStart = true;
                    //初始化的时候赋值List
                    mPointDataList = new ArrayList<PointData>();
                    startRemark();//记录开始时间，记录轨迹点，记录录音
                    //获取结束时间
                    endTimeLong = System.currentTimeMillis();
                    Date date = new Date(System.currentTimeMillis());
                    endTime = simpleDateFormat.format(date);
                    mAlarmUpTask = new AlarmUpTask();
                    mAlarmUpTask.execute("");
                } else {
                    // 结束录音
                    stop();
                    //获取结束时间
                    endTimeLong = System.currentTimeMillis();
                    Date date = new Date(System.currentTimeMillis());
                    endTime = simpleDateFormat.format(date);
                    //上传录音文件
                    mUploadTask = new UploadTask();
                    mUploadTask.execute("");
                }

            } else if (action.equals(DButtonApplication.ACTION_ONE_CLICK)) {
                if (hasStart) {
                    hasStart = false;
                    isAlarmUp = false;
                    stop();
                    mUploadTask = new UploadTask();
                    mUploadTask.execute("");
                } else {
                    Log.e(TAG, "onReceive() ++++++++++++++++++++++++++++++++++++++++++无启动状态下无法结束");
                }
            } else if (DButtonApplication.ACTION_CONTENT.equals(action)) {//发送的是内容
                //调用接口翻译数据写文件
                byte[] content = intent.getByteArrayExtra(DButtonApplication.KEY_CONTENT);
                Message msg = mFileHandler.obtainMessage(MESSAGE_RECORD_FILE);
                Bundle content_bl = new Bundle();
                content_bl.putByteArray(FILE_CONTENT, content);
                msg.setData(content_bl);
            }
        }
    }

    private boolean hasStart = false;
    private boolean isOver = true;

    private void startRemark() {
        //获取当前时间
        nowTimeLong = System.currentTimeMillis();
        Date date = new Date(System.currentTimeMillis());
        nowTime = simpleDateFormat.format(date);
        //定义log方法在后台打印
        Log.e(TAG, "onCreate() ++++++++++++++++++++++++++++++++++++++++++" + Thread.currentThread().getName() + "|Date获取当前日期时间" + nowTime);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        //获取所有可用的位置提供器
        List<String> providers = locationManager.getProviders(true);
        String locationProvider = null;
        if (providers.contains(LocationManager.GPS_PROVIDER)) {
            //如果是GPS
            locationProvider = LocationManager.GPS_PROVIDER;
        } else if (providers.contains(LocationManager.NETWORK_PROVIDER)) {
            //如果是Network
            locationProvider = LocationManager.NETWORK_PROVIDER;
        } else {
            Intent i = new Intent();
            i.setAction(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(i);
        }
        //获取Location
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Location location = locationManager.getLastKnownLocation(locationProvider);
        //监视地理位置变化
        locationManager.requestLocationUpdates(locationProvider, 10000, 0, locationListener);
        //开始录音
        start();
    }

    private MediaRecorder mediaRecorder;
    private boolean isRecording;
    String fileName = "DButton" + ".m4a";
    ;

    /**
     * 开始录音
     */
    protected void start() {
        try {
            //fileName = DateFormat.format("yyyyMMdd_HHmmss", Calendar.getInstance(Locale.CHINA)) + ".m4a";
            File file = new File(Environment.getExternalStorageDirectory() + "/" + fileName);
            if (file.exists()) {
                // 如果文件存在，删除它，演示代码保证设备上只有一个录音文件
                file.delete();
            }
            file.createNewFile();
            mediaRecorder = new MediaRecorder();
            // 设置音频录入源
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            // 设置录制音频的输出格式
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            // 设置音频的编码格式
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            // 设置录制音频文件输出文件路径
            mediaRecorder.setOutputFile(file.getAbsolutePath());

            mediaRecorder.setOnErrorListener(new MediaRecorder.OnErrorListener() {

                @Override
                public void onError(MediaRecorder mr, int what, int extra) {
                    // 发生错误，停止录制
                    mediaRecorder.stop();
                    mediaRecorder.release();
                    mediaRecorder = null;
                    isRecording = false;
                    Toast.makeText(DButtonControlService.this, "录音发生错误", Toast.LENGTH_SHORT).show();
                }
            });

            // 准备、开始
            mediaRecorder.prepare();
            mediaRecorder.start();

            mStartDuration = System.currentTimeMillis();
            isRecording = true;
            Toast.makeText(DButtonControlService.this, "开始录音", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 录音结束
     */
    protected void stop() {
        if (isRecording) {
            // 如果正在录音，停止并释放资源
            mediaRecorder.stop();
            mediaRecorder.release();
            mediaRecorder = null;
            isRecording = false;
            mDuration = System.currentTimeMillis() - mStartDuration;
            Toast.makeText(DButtonControlService.this, "录音结束", Toast.LENGTH_SHORT).show();
        }
    }

    private static final int ALARM_UP_SUCCESS = 1;
    private static final int ALARM_UP_FALSE = 2;
    private static final int UPLOAD_SUCCESS = 3;
    private static final int UPLOAD_FALSE = 4;
    private static final int ALARM_UPDATE_SUCCESS = 5;
    private static final int ALARM_UPDATE_FALSE = 6;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case ALARM_UP_SUCCESS:
                    //只有收到别人推送的才会刷新ALarmList
                    DButtonApplication.mAddAlarm = true;
                    //上传警报记录成功后，记录ID
                    isAlarmUp = true;
                    mHandler.sendEmptyMessageDelayed(ALARM_UPDATE_SUCCESS,30000);
                    break;
                case ALARM_UP_FALSE:
                    ToastUtils.shortToast(DButtonControlService.this, HttpAnalyJsonManager.lastError);
                    break;
                case UPLOAD_SUCCESS:
                    android.util.Log.e("uploadData", "mUploadData:" + mUploadData.toString());
                    mRecord = mUploadData.getUrl();
                    break;
                case UPLOAD_FALSE:
                    ToastUtils.shortToast(DButtonControlService.this, getResources().getString(R.string.upload_photo_false));
                    break;
                case ALARM_UPDATE_SUCCESS:
                    mAlarmUpdateTask = new AlarmUpdateTask();
                    mAlarmUpdateTask.execute("");
                    break;
                case ALARM_UPDATE_FALSE:
                    //ToastUtils.shortToast(DButtonControlService.this, getResources().getString(R.string.upload_photo_false));
                    break;
            }
        }
    };
    private String mRecord = "";
    private long mDuration = 0;
    private String contactIds = "";
    //private JSONArray pointJsonArray = new JSONArray();
    String point = "";
    private AlarmUpTask mAlarmUpTask;

    private class AlarmUpTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {
            if (DButtonApplication.mContactList.size() > 0) {
                String contactIds = "";
                for (int i = 0; i < DButtonApplication.mContactList.size(); i++) {
                    if (i == 0) {
                        contactIds = DButtonApplication.mContactList.get(i).getId();
                    } else {
                        contactIds = contactIds + "," + DButtonApplication.mContactList.get(i).getId();
                    }
                }
                Location lastLocation = null;
//                //上传当前轨迹记录 -- 得到ArrayList轨迹点
//                JSONArray pointJsonArray = new JSONArray();
//                for (int i = 0; i < mPointDataList.size(); i++) {
//                    try {
//                        PointData pointData = mPointDataList.get(i);
//                        lastLocation = pointData.getLocation();
//                        JSONObject pointDataObject = new JSONObject();
//                        pointDataObject.put("time", String.valueOf(pointData.getPointTime()));
//                        pointDataObject.put("latitude", String.valueOf(pointData.getLocation().getLatitude()));
//                        pointDataObject.put("longitude", String.valueOf(pointData.getLocation().getLongitude()));
//                        pointJsonArray.put(pointDataObject);
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                }

                for (int i = 0; i < mPointDataList.size(); i++) {
//                    try {
                    String pointdata = "";
                    //JSONObject pointJsonObject = new JSONObject();
                    PointData pointData = mPointDataList.get(i);
                    lastLocation = pointData.getLocation();
                    //JSONObject pointDataObject = new JSONObject();
                    //pointDataObject.put("time", String.valueOf(pointData.getPointTime()));
                    //pointDataObject.put("latitude", String.valueOf(pointData.getLocation().getLatitude()));
                    //pointDataObject.put("longitude", String.valueOf(pointData.getLocation().getLongitude()));
                    //pointJsonObject.put("point", pointDataObject);
                    pointdata = String.valueOf(pointData.getLocation().getLongitude())+"|"+ String.valueOf(pointData.
                            getLocation().getLatitude()) +"|"+ String.valueOf(pointData.getPointTime());
                    if(TextUtils.isEmpty(point)){
                        point = pointdata;
                    }else{
                        point = point + "," + pointdata;
                    }

//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
                }


                if (lastLocation != null) getLocation(lastLocation);
            }
            return null;
        }
    }


    private AlarmUpdateTask mAlarmUpdateTask;

    private class AlarmUpdateTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {
            boolean isOk = HttpSendJsonManager.alarmUpdate(DButtonControlService.this, alarmIDData.getId()
                    ,"1", point, "");
            //用JSON的形式保存轨迹Point值
            if (isOk) {
                mHandler.sendEmptyMessage(ALARM_UPDATE_SUCCESS);
            } else {
                mHandler.sendEmptyMessage(ALARM_UPDATE_FALSE);
            }
            return null;
        }
    }

    /**
     * 得到当前经纬度并开启线程去反向地理编码
     */
    public void getLocation(Location location) {
        String latitude = location.getLatitude() + "";
        String longitude = location.getLongitude() + "";
        String url = "http://api.map.baidu.com/geocoder/v2/?ak=KGdMNn9tb2GivRZ4SP2BuWy9fkfyLKB6" +
                "&callback=renderReverse&location=" + latitude + "," + longitude + "&output=json&pois=0";
        new LocationAsyncTask(url).execute();
    }

    AlarmIDData alarmIDData;
    class LocationAsyncTask extends AsyncTask<Void, Void, Void> {
        String url = null;//要请求的网址
        String str = null;//服务器返回的数据
        String address = null;

        public LocationAsyncTask(String url) {
            this.url = url;
        }

        @Override
        protected Void doInBackground(Void... params) {
            str = HttpUtils.getData(url);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            try {
                str = str.replace("renderReverse&&renderReverse", "");
                str = str.replace("(", "");
                str = str.replace(")", "");
                JSONObject jsonObject = new JSONObject(str);
                JSONObject address = jsonObject.getJSONObject("result");
                JSONObject location = jsonObject.getJSONObject("location");
                String city = address.getString("formatted_address");
                String district = address.getString("sematic_description");

                alarmIDData = HttpSendJsonManager.alarmUp(DButtonControlService.this, contactIds, point,
                        String.valueOf(nowTimeLong), String.valueOf(endTimeLong),
                        district, mRecord, String.valueOf(mDuration));
                //用JSON的形式保存轨迹Point值
                if (alarmIDData.isOK()) {
                    mHandler.sendEmptyMessage(ALARM_UP_SUCCESS);
                } else {
                    mHandler.sendEmptyMessage(ALARM_UP_FALSE);
                }

                if (DButtonApplication.mContactList.size() > 0) {
                    for (int i = 0; i < DButtonApplication.mContactList.size(); i++) {
                        String phoneNumber = DButtonApplication.mContactList.get(i).getPhone();
                        String message = "我在" + city + "出事了,出事时间是" + simpleDateFormat.format(
                                new Date(nowTimeLong)) + "-" + simpleDateFormat.format(new Date(endTimeLong));
                        sendSMS(phoneNumber, message);
                        if (DButtonApplication.mContactList.get(i).getIsUrgent().equals("1")) {
                            //拨打电话
                            callPhone(phoneNumber);//同时背景播放音乐
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            super.onPostExecute(aVoid);
        }
    }

    public void sendSMS(String phoneNumber, String message) {
        // 获取短信管理器
        android.telephony.SmsManager smsManager = android.telephony.SmsManager
                .getDefault();
        // 拆分短信内容（手机短信长度限制）
        List<String> divideContents = smsManager.divideMessage(message);
        for (String text : divideContents) {
            smsManager.sendTextMessage(phoneNumber, null, text, null,
                    null);
        }
    }


    /**
     * 拨打电话（直接拨打电话）
     *
     * @param phoneNum 电话号码
     */
    public void callPhone(String phoneNum) {
        Intent intent = new Intent(Intent.ACTION_CALL);
        Uri data = Uri.parse("tel:" + phoneNum);
        intent.setData(data);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        startActivity(intent);
    }


    private UploadTask mUploadTask;
    private UploadData mUploadData;
    private String mFile;
    private long mStartDuration = 0;

    private class UploadTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {
            mFile = CommonUtils.encodeToBase64(Environment.getExternalStorageDirectory() + "/" + fileName);
            mUploadData = new UploadData();
            mUploadData = HttpSendJsonManager.upload(DButtonControlService.this,
                    HttpSendJsonManager.UPLOAD_TPYE_HEAD, fileName, mFile, String.valueOf(mDuration));
            if (mUploadData.isOK()) {
                if (hasStart) {
                    //执行长按的上传方法
                    //此处需要判断录音文件是否上传成功
                    mAlarmUpTask = new AlarmUpTask();
                    mAlarmUpTask.execute("");
                    //继续开始录音
                    start();
                } else {
                    //执行单击的修改方法
                }
                mHandler.sendEmptyMessage(UPLOAD_SUCCESS);
            } else {
                mHandler.sendEmptyMessage(UPLOAD_FALSE);
            }
            return null;
        }
    }
}
