package com.sfr.dbuttonapplication;
/**
 * Created by ASUS on 2018/8/24.
 */

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGattCharacteristic;
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
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.CoordinateConverter;
import com.icen.blelibrary.BleBaseApplication;
import com.icen.blelibrary.config.BleLibsConfig;
//import com.iflytek.cloud.SpeechConstant;
//import com.iflytek.cloud.SpeechUtility;
import com.jordan.httplibrary.HttpUtils;
import com.jordan.httplibrary.utils.CommonUtils;
import com.linkingdigital.ble.BeadsAudio;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.sfr.dbuttonapplication.entity.AlarmIDData;
import com.sfr.dbuttonapplication.entity.DButtonData;
import com.sfr.dbuttonapplication.entity.Music;
import com.sfr.dbuttonapplication.entity.PointData;
import com.sfr.dbuttonapplication.entity.UploadData;
import com.sfr.dbuttonapplication.entity.UserData;
import com.sfr.dbuttonapplication.http.Communicator;
import com.sfr.dbuttonapplication.http.DeviceUtil;
import com.sfr.dbuttonapplication.http.HttpManager;
import com.sfr.dbuttonapplication.http.HttpSendJsonManager;
import com.sfr.dbuttonapplication.ota.FileUtils;
import com.sfr.dbuttonapplication.utils.LogUtil;
import com.sfr.dbuttonapplication.utils.ToastUtils;
import com.umeng.commonsdk.UMConfigure;
import com.umeng.socialize.PlatformConfig;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class DButtonApplication extends BleBaseApplication {

    List<Music> musicList;
    public static Communicator communicator;
    public static HttpManager httpManager;
    public static String USER_TOKEN = "";
    public static DeviceUtil mDeviceUtil;
    private static Context context;
    public static UserData mUserData;
    public static final String ACTION_ONE_CLICK = "com.sfr.dbutton.action.one";
    public static final String ACTION_DOUBLE_CLICK = "com.sfr.dbutton.action.double";
    public static final String ACTION_LONG_CLICK = "com.sfr.dbutton.action.long";
    public static final String ACTION_CONTENT = "com.sfr.dbutton.action.content";
    public static final String ACTION_DBUTTON_CONNECT = "com.sfr.dbutton.action.connect";
    public static final String ACTION_DBUTTON_SCAN = "com.sfr.dbutton.action.scan";
    public static final String ACTION_ALARM_LIST_UPDATE = "com.sfr.dbutton.action.alarm.list.update";
    public static final String ACTION_DBUTTON_BATTERY = "com.sfr.dbutton.action.dbutton.battery";
    public static final String KEY_CONTENT = "key_content";

    public static final String UUID_SERVICE_BATTERY = "180F";
    public static final String UUID_CHAR_LEVEL = "2A19";
    public static final String UUID_SERVICE_OTA = "00008072-0000-544c-8267-4c4442454144";
    public static final String UUID_CHAR_OTA = "00007201-0000-544c-8267-4c4442454144";
    public static final String UUID_SERVICE_BEAD = "00008071-0000-544c-8267-4c4442454144";
    public static final String UUID_CHAR_DATA = "00007101-0000-544c-8267-4c4442454144";
    public static final String UUIDCHAR_CTRL = "00007102-0000-544c-8267-4c4442454144";

    private static final String SPEECH_UTILITY_ID = "5b854e27";
    public static SimpleDateFormat datenamesdf = new SimpleDateFormat("yyyyMMddHHmmss");
    public static boolean mAddContact = false;
    public static boolean mAddAlarm = false;
    public static boolean mAddDbutton = false;
    public static boolean mIsEmpty = false;
    public static ArrayList<UserData> mContactList;
    public static HashMap<String, UserData> mContactMap;
    private static LocationClient client;
    public static BDLocation bdLocation;
    public static double nowLatitude = 0;
    public static double nowLongitude = 0;
    public static HashMap<String, DButtonData> mDButtonMap = new HashMap<String, DButtonData>();
    public static String mNowMac = "";
    private FileUtils mFileUtils;
    private boolean mFilterContent = false;
    /**
     * 全局Context，原理是因为Application类是应用最先运行的，所以在我们的代码调用时，该值已经被赋值过了
     */
    public static DButtonApplication mInstance;
    public static final String ALARM_TYPE_SOURCE = "1";//1.小D，2.老D

    @Override
    public void onCreate() {
        super.onCreate();

        mInstance = this;
        context = getApplicationContext();
        createSpeech();
        communicator = new Communicator();
        mDeviceUtil = new DeviceUtil(this);
        httpManager = HttpManager.getInstance(this);
        mContactList = new ArrayList<UserData>();
        mContactMap = new HashMap<String, UserData>();
        initImageLoader(this);
        //在使用SDK各组件之前初始化context信息，传入ApplicationContext
        SDKInitializer.initialize(this);
        client = new LocationClient(this);
        mFileUtils = new FileUtils(this);
        BeadsAudio.mp3_init(4000);
        //client.registerLocationListener( myListener );    //注册监听函数
        initLocation();
        client.start();

        UMConfigure.init(this, "5a12384aa40fa3551f0001d1"
                , "umeng", UMConfigure.DEVICE_TYPE_PHONE, "");//58edcfeb270c93091c000be2 5965ee00734be40b580001a0
        initUmeng();

        mDButtonControlReceiver = new DButtonControlReceiver();//广播接受者实例
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(DButtonApplication.ACTION_ONE_CLICK);
        intentFilter.addAction(DButtonApplication.ACTION_DOUBLE_CLICK);
        intentFilter.addAction(DButtonApplication.ACTION_LONG_CLICK);
        intentFilter.addAction(DButtonApplication.ACTION_CONTENT);
        registerReceiver(mDButtonControlReceiver, intentFilter);

    }

    private void initUmeng() {
        PlatformConfig.setWeixin("wxdc1e388c3822c80b", "3baf1193c85774b3fd9d18447d76cab0");
//        2.        //豆瓣RENREN平台目前只能在服务器端配置
//        3.        PlatformConfig.setSinaWeibo("3921700954", "04b48b094faeb16683c32669824ebdad","http://sns.whalecloud.com");
//        4.        PlatformConfig.setYixin("yxc0614e80c9304c11b0391514d09f13bf");
//        5.        PlatformConfig.setQQZone("100424468", "c7394704798a158208a74ab60104f0ba");
//        6.        PlatformConfig.setTwitter("3aIN7fuF685MuZ7jtXkQxalyi", "MK6FEYG63eWcpDFgRYw4w9puJhzDl0tyuqWjZ3M7XJuuG7mMbO");
//        7.        PlatformConfig.setAlipay("2015111700822536");
//        8.        PlatformConfig.setLaiwang("laiwangd497e70d4", "d497e70d4c3e4efeab1381476bac4c5e");
//        9.        PlatformConfig.setPinterest("1439206");
//        10.        PlatformConfig.setKakao("e4f60e065048eb027e235c806b27c70f");
//        11.        PlatformConfig.setDing("dingoalmlnohc0wggfedpk");
//        12.        PlatformConfig.setVKontakte("5764965","5My6SNliAaLxEm3Lyd9J");
//        13.        PlatformConfig.setDropbox("oz8v5apet3arcdy","h7p2pjbzkkxt02a");
//        14.        PlatformConfig.setYnote("9c82bf470cba7bd2f1819b0ee26f86c6ce670e9b");

    }

    private void initImageLoader(Context context) {
        ImageLoaderConfiguration.Builder config = new ImageLoaderConfiguration.Builder(context);
        config.threadPriority(Thread.NORM_PRIORITY - 2);
        config.denyCacheImageMultipleSizesInMemory();
        config.diskCacheFileNameGenerator(new Md5FileNameGenerator());
        config.diskCacheSize(50 * 1024 * 1024); // 50 MiB
        config.tasksProcessingOrder(QueueProcessingType.LIFO);
        config.writeDebugLogs(); // Remove for release app

        // Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(config.build());
    }

    private void createSpeech() {
        // 将“12345678”替换成您申请的APPID，申请地址：http://www.xfyun.cn
        // 请勿在“=”与appid之间添加任何空字符或者转义符
        //SpeechUtility.createUtility(context, SpeechConstant.APPID + "=" + SPEECH_UTILITY_ID);
    }

    public static Context getContext() {
        return context;
    }

    /**
     * 获取储存权限
     *
     * @param activity
     * @return
     */
    public static boolean isGrantExternalRW(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && activity.checkSelfPermission(
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            activity.requestPermissions(new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, 1);

            return false;
        }

        return true;
    }

    public List<Music> getMusicList() {
        return musicList;
    }

    public void setMusicList(List<Music> musicList) {
        this.musicList = musicList;
    }


    public BDAbstractLocationListener mBDListener = new MyLocationListenner();

    private void initLocation() {
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系
        option.setPriority(LocationClientOption.GpsFirst);
        //int span = 1000;
        //option.setScanSpan(span);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
        option.setOpenGps(true);//可选，默认false,设置是否使用gps
        option.setLocationNotify(true);//可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
        option.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        option.setIsNeedLocationPoiList(true);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        option.setIgnoreKillProcess(false);//可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
        option.SetIgnoreCacheException(false);//可选，默认false，设置是否收集CRASH信息，默认收集
        option.setEnableSimulateGps(false);//可选，默认false，设置是否需要过滤gps仿真结果，默认需要
        client.setLocOption(option);
        client.registerLocationListener(mBDListener);
    }

    /**
     * 获取百度的Client
     */
    public static LocationClient getBDClient() {
        return client;
    }

    public class MyLocationListenner extends BDAbstractLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            //mAddress = mAddress + "\n" + "地址= "+location.getAddress().address+"，坐标：" + location.getLongitude()+"."+location.getLatitude();
            android.util.Log.e("SlashInfo", "JordanApplication::onReceiveLocation::info= " + location.getAddress().address + "，坐标：" + location.getLongitude() + "." + location.getLatitude());
            if (location == null)
                return;
            bdLocation = location;
            if (bdLocation != null) {
                client.stop();
            }
        }

    }


    @Override
    public void onInitialManager(boolean is_success) {
        getManager().saveAutoReconnect(true);
        LogUtil.println("DButtonApplication::onInitialManager::result= " + is_success);
        if (is_success) {
            //Toast.makeText(this, "initial manager success", Toast.LENGTH_LONG).show();
            getManager().setManagerCallback(this);
            LogUtil.println("DButtonApplication::onInitialManager::getManager().isLeEnabled()= " + getManager().isLeEnabled());
            if (getManager().isLeEnabled()) {

            } else {
                Toast.makeText(this, R.string.please_open_ble_permission, Toast.LENGTH_LONG).show();
            }
            LogUtil.println("DButtonApplication::onInitialManager::getManager().isSupportLE()= " + getManager().isSupportLE());
            if (!getManager().isSupportLE()) {
                //Toast.makeText(this, "Current device not support le", Toast.LENGTH_LONG).show();
            }
        } else {
            //Toast.makeText(this, "initial manager false", Toast.LENGTH_LONG).show();
            getManager().setManagerCallback(null);
        }
    }

    @Override
    public void onLESwitch(int current_state) {
        LogUtil.println("DButtonApplication::onLESwitch::current_state= " + current_state);
        if (BleLibsConfig.BLE_SWITCH_CLOSING == current_state || BleLibsConfig.BLE_SWITCH_OPENING == current_state) {
            Toast.makeText(this, "Bluetooth is operating now", Toast.LENGTH_LONG).show();
        } else if (BleLibsConfig.BLE_SWITCH_ON == current_state) {

        } else if (BleLibsConfig.BLE_SWITCH_OFF == current_state) {

        } else if (BleLibsConfig.BLE_SWITCH_ERROR == current_state) {
            if (getManager().isLeEnabled()) {

            } else {
                Toast.makeText(this, R.string.please_open_ble_permission, Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onLEScan(int scan_process, String device_name, String device_class, String device_mac,
                         int device_rssi, byte[] broadcast_content) {
        LogUtil.println("DButtonApplication::onLEScan::process= " + scan_process +
                " name= " + device_name + " mac= " + device_mac +
                " class= " + device_class + " RSSI= " + device_rssi +
                " content= " + Arrays.toString(broadcast_content));
        com.sfr.dbuttonapplication.utils.FileUtils.writeLog(TAG,  "name= " + device_name + " mac= " + device_mac);

        if (!mDButtonMap.containsKey(device_mac) && !TextUtils.isEmpty(device_mac)) {
            if ("BEADS-1".equals(device_name)) {
                //添加到List-后续加名称过滤
                DButtonData dButtonData = new DButtonData();
                dButtonData.setBroadcast_content(broadcast_content);
                dButtonData.setDevice_class(device_class);
                dButtonData.setDevice_mac(device_mac);
                dButtonData.setDevice_name(device_name);
                dButtonData.setDevice_rssi(device_rssi);
                mDButtonMap.put(device_mac, dButtonData);
                LogUtil.println("DButtonApplication::onLEScan::mDButtonMap= " + dButtonData.toString());
            }
        }
        LogUtil.println("DButtonApplication::onLEScan::mDButtonMap.size= " + mDButtonMap.size());
        //-------------------发广播----------------
        if (BleLibsConfig.LE_SCAN_PROCESS_BEGIN == scan_process) {//指示扫描开始
            //Toast.makeText(this, "扫描开始", Toast.LENGTH_LONG).show();
            Intent intent = new Intent();
            intent.setAction(DButtonApplication.ACTION_DBUTTON_SCAN);
            intent.putExtra("scan_process", scan_process);
            sendBroadcast(intent);
        } else if (BleLibsConfig.LE_SCAN_PROCESS_DOING == scan_process) {//扫描进行中
            Intent intent = new Intent();
            intent.setAction(DButtonApplication.ACTION_DBUTTON_SCAN);
            intent.putExtra("scan_process", scan_process);
            sendBroadcast(intent);

        } else if (BleLibsConfig.LE_SCAN_PROCESS_END == scan_process) {//扫描结束
            Intent intent = new Intent();
            intent.setAction(DButtonApplication.ACTION_DBUTTON_SCAN);
            intent.putExtra("scan_process", scan_process);
            sendBroadcast(intent);
            //Toast.makeText(this, "扫描结束", Toast.LENGTH_LONG).show();
            LogUtil.println("DButtonApplication::onLEScan::mNowMac= " + mNowMac);
            //判断是否包含mNowMac
            if (mDButtonMap.containsKey(mNowMac)) {
                //尝试连接
                getManager().connectToDevice(true, mNowMac);
            } else {
                //提示用户
                //Toast.makeText(this, "绑定DBUTTON不在身边", Toast.LENGTH_LONG).show();
                intent = new Intent();
                intent.setAction(DButtonApplication.ACTION_DBUTTON_CONNECT);
                intent.putExtra("is_success", false);
                sendBroadcast(intent);
            }
        } else {//扫描发生异常
            //Toast.makeText(this, "Scan exception. finish this process", Toast.LENGTH_LONG).show();
        }
    }


    //}
    public static final String UUID_CHAR_READ = "00007101-0000-544c-8267-4c4442454144";
    public static boolean isConnectDevice = false;
    public static boolean canConnect = false;

    @Override
    public void onConnectDevice(boolean is_success, String device_name, String device_mac) {
        super.onConnectDevice(is_success, device_name, device_mac);
        isConnectDevice = is_success;
        LogUtil.println("DButtonApplication::onConnectDevice::is_success= " + is_success +
                " device_name= " + device_name + " device_mac= " + device_mac);
        LogUtil.println("DButtonApplication::onConnectDevice::is_success= " + is_success);
        if (is_success) {
            canConnect = true;
            if (isFristConnect) {
                Log.e(TAG, "mHandler onConnectDevice 震动触发");
                mHandler.sendEmptyMessageDelayed(10, 2000);
                mFilterContent = true;
                mHandler.sendEmptyMessageDelayed(11, 1000);
            }
        } else {
            //当如果是蓝牙未支持的情况下则不理会？
            //需要给个间隔时间，例如5S
//            if (canConnect)
//                mHandler.sendEmptyMessageDelayed(DISCONNECT_TO_CONNECT_DEVICE, 5000);
        }
        Intent intent = new Intent();
        intent.setAction(DButtonApplication.ACTION_DBUTTON_CONNECT);
        intent.putExtra("is_success", is_success);
        sendBroadcast(intent);

        getManager().initialNotification(UUID_CHAR_READ);

        if (isFristConnect) {
            Log.e(TAG, "mHandler onConnectDevice 主动连接 is_success:" + is_success);
            isFristConnect = false;
        } else {
            Log.e(TAG, "mHandler onConnectDevice 重新连接 is_success:" + is_success);
        }
    }

    //onReadCh::result= true uuid= 00007101-0000-544c-8267-4c4442454144 ble_value= [1]
    @Override
    public void onChChange(boolean is_success, String ch_uuid, byte[] ble_value) {
        Log.e(TAG, "onChChange() +++is_success：" + is_success + "|ch_uuid：" + ch_uuid);
        Log.e(TAG, "onChChange() +++ble_value：" + ble_value);
        super.onChChange(is_success, ch_uuid, ble_value);
        if (is_success && null != ble_value && ble_value.length > 0) {
            if (ble_value.length == 1) {
                String value = Arrays.toString(ble_value);
                if (mFilterContent) {
                    Log.e(TAG, "onChChange() +++有按键过滤");
                } else {
                    Log.e(TAG, "onChChange() +++无按键过滤");
                    if (value.contains("1")) {//单击
                        //单击无效化处理
                        if (!isAlarmUp) {
                            Log.e(TAG, "mHandler onChChange 单击 震动触发");
                            mHandler.sendEmptyMessage(10);
                            if (hasStart) {
                                Log.e(TAG, "onChChange() +++单击 启动结束");
                                //结束轨迹
                                hasStart = false;
                                //结束录音
                                stop();
                            } else {
                                Log.e(TAG, "onChChange() +++单击 无效处理");
                            }
                        } else {
                            Log.e(TAG, "onChChange() +++单击 恢复录音");
                            getManager().writeCharacteristic(UUIDCHAR_CTRL, 27,
                                    BluetoothGattCharacteristic.FORMAT_UINT8);
                        }
                    } else if (value.contains("2")) {//双击
                        Log.e(TAG, "onChChange() +++双击");
                        Intent intent = new Intent();
                        intent.setAction(DButtonApplication.ACTION_DOUBLE_CLICK);
                        sendBroadcast(intent);
                    } else if (value.contains("3")) {//长按
                        Log.e(TAG, "onChChange() +++长按");
                        Intent intent = new Intent();
                        intent.setAction(DButtonApplication.ACTION_LONG_CLICK);
                        sendBroadcast(intent);
                    }
                }
            } else {
                String value = Arrays.toString(ble_value);
//                Log.e(TAG, "onReceive() ++++++++++++++++++++++++++++++++++++++++++录音 value:" + value);
//                Log.e(TAG, "onReceive() ++++++++++++++++++++++++++++++++++++++++++录音 mFileUtils.isRecording():" + mFileUtils.isRecording());
                if (mFileUtils.isRecording()) {
                    //开始保存流文件录音参照beadsAudio的方法写文件
                    mFileUtils.processWriteFile(ble_value);
                }
//                Intent intent = new Intent(ACTION_CONTENT);
//                Bundle content = new Bundle();
//                content.putByteArray(KEY_CONTENT, ble_value);
//                sendBroadcast(intent);
            }
        }
    }

    public void readBatteryShow() {
        LogUtil.println("DButtonApplication::readBatteryShow:");
        getManager().readBattery();
    }

    /**
     * 更新电量
     *
     * @param is_success
     * @param ble_value
     */
    @Override
    public void onReadBattery(boolean is_success, byte[] ble_value) {
        String value = Arrays.toString(ble_value);
        LogUtil.println("DButtonApplication::onReadBattery::is_success= " + is_success);
        LogUtil.println("DButtonApplication::onReadBattery::value= " + value);
        Intent intent = new Intent();
        intent.setAction(DButtonApplication.ACTION_DBUTTON_BATTERY);
        intent.putExtra("battery", value);
        sendBroadcast(intent);
        super.onReadBattery(is_success, ble_value);
    }

    /**
     * 更新信号强度
     *
     * @param is_success
     * @param current_rssi
     */
    @Override
    public void onReadRSSI(boolean is_success, int current_rssi) {
        super.onReadRSSI(is_success, current_rssi);
    }

    public void startScanDevice() {
        // 获得蓝牙适配器对象
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
            bluetoothAdapter.enable();
        }
        LogUtil.println("DButtonApplication::startScanDevice::");
        mDButtonMap = new HashMap<String, DButtonData>();
        com.sfr.dbuttonapplication.utils.FileUtils.clearLog();
        getManager().startScanDevice();
    }

    private boolean isFristConnect = false;

    public void connectToDevice() {
        isFristConnect = true;
        LogUtil.println("DButtonApplication::connectToDevice::mNowMac= " + mNowMac);
        getManager().connectToDevice(true, mNowMac);
//        mDButtonMap = new HashMap<String, DButtonData>();
//        getManager().startScanDevice();
    }

    public void disconnect() {
        getManager().disconnect();
//        mDButtonMap = new HashMap<String, DButtonData>();
//        getManager().startScanDevice();
    }


    //========================================================Service移植过来代码====================================================

    public String TAG = "DButton";
    private String nowTime = "";//后续删除掉
    private String endTime = "";//后续删除掉
    private long nowTimeLong;
    private long endTimeLong;
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");// HH:mm:ss
    private LocationManager locationManager;
    private boolean isAlarmUp = false;
    private boolean isOverUp = false;
    private ArrayList<PointData> mPointDataList = new ArrayList<PointData>();
    private String fileLogName = "dbutton.log";
    //public static String mAddress = "";
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
                String string = "纬度为：" + location.getLatitude() + ",经度为："
                        + location.getLongitude();
                android.util.Log.e("SlashInfo", string);
                CoordinateConverter converter = new CoordinateConverter();
                converter.from(CoordinateConverter.CoordType.GPS);
                converter.coord(new LatLng(location.getLatitude(), location.getLongitude()));
                LatLng latLng = converter.convert();
                String string1 = "+++纬度为：" + latLng.latitude + ",经度为："
                        + latLng.longitude;
                Log.e(TAG, "locationListener onLocationChanged() ++++++++++++++++++++++++++++++++++++++++++++"
                        + simpleDateFormat.format(new Date(System.currentTimeMillis())) + "|string1:" + string1);

                com.sfr.dbuttonapplication.utils.FileUtils.writeLogName(TAG, string1,fileLogName);
                PointData pointData = new PointData();
                Date date = new Date(System.currentTimeMillis());
                String pointTime = simpleDateFormat.format(date);
                pointData.setPointTime(date.getTime());
                pointData.setLocation(latLng);
                //定义log方法在后台打印
                Log.e(TAG, "locationListener onLocationChanged() ++++++++++++++++++++++++++++++++++++++++++++"
                        + simpleDateFormat.format(new Date(System.currentTimeMillis())) + "|location:" + location.toString() + "|Date获取当前日期时间:" + pointTime);
                //存个DATA 存到List里面
                mPointDataList.add(pointData);
//                if (isAlarmUp) {
//                    //触发定时：记录轨迹，修改接口轨迹上传
//                    //然后isAlarmUp=true定时上传轨迹/周一志旺提供接口
//                    //初始化的时候赋值List
//                    mPointDataList = new ArrayList<PointData>();
//                }
            }
        }
    };


    private DButtonControlReceiver mDButtonControlReceiver;

    public class DButtonControlReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            //尝试Main界面底部三个按钮分别触发不同广播，开始模拟测试
            if (action.equals(DButtonApplication.ACTION_DOUBLE_CLICK)) {
                //---------------------双击--------------------------
                Log.e(TAG, "onReceive() ++++++++++++++++++++++++++++++++++++++++++双击");
                //才开始注册信息---如果双击没点击，那么直接轨迹只有当前的点和无录音文件，此时长按才开始录音，地址初始化
                if (!hasStart) {
                    Log.e(TAG, "onReceive() ++++++++++++++++++++++++++++++++++++++++++尚未启动");
                    fileLogName = "DButton_"+simpleDateFormat.format(new Date());
                    hasStart = true;
                    isOverUp = false;
                    //初始化的时候赋值List
                    mPointDataList = new ArrayList<PointData>();
                    hasCallPhone = false;

                    //Log.e(TAG, "onReceive() ++++++++++++++++++++++++++++++++++++++++++getManager().readBattery();"+getManager().readBattery());
//                    getManager().readCharacteristic(UUIDCHAR_CTRL);

//                    Log.e(TAG, "onReceive() ++++++++++++++++++++++++++++++++++++++++++sleep1");
//                    try {
//                        Thread.sleep(500);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                    Log.e(TAG, "onReceive() ++++++++++++++++++++++++++++++++++++++++++readCharacteristic");
//                    getManager().readCharacteristic(UUID_CHAR_DATA);
//                    Log.e(TAG, "onReceive() ++++++++++++++++++++++++++++++++++++++++++sleep2");
//                    try {
//                        Thread.sleep(3000);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                    Log.e(TAG, "onReceive() ++++++++++++++++++++++++++++++++++++++++++writeCharacteristic");
//                    getManager().writeCharacteristic(UUID_CHAR_DATA, 27,
//                            BluetoothGattCharacteristic.FORMAT_UINT8);
//                    getManager().writeCharacteristic(UUIDCHAR_CTRL, 27,
//                            BluetoothGattCharacteristic.FORMAT_UINT8);
                    fristStartRemark();//记录开始时间，记录轨迹点，记录录音
                } else {
                    Log.e(TAG, "onReceive() ++++++++++++++++++++++++++++++++++++++++++已经启动");
//                    getManager().writeCharacteristic(UUIDCHAR_CTRL, 27,
//                            BluetoothGattCharacteristic.FORMAT_UINT8);
                }
            } else if (action.equals(DButtonApplication.ACTION_LONG_CLICK)) {
                Log.e(TAG, "onReceive() ++++++++++++++++++++++++++++++++++++++++++长按");
                if (isAlarmUp) {
                    Log.e(TAG, "onReceive() ++++++++++++++++++++++++++++++++++++++++++已经触发警报，未解除之前不再做任何操作");
                    getManager().writeCharacteristic(UUIDCHAR_CTRL, 27,
                            BluetoothGattCharacteristic.FORMAT_UINT8);
                } else {
                    Log.e(TAG, "onReceive() ++++++++++++++++++++++++++++++++++++++++++未触发警报");
                    //---------------------长按--------------------------
                    if (!hasStart) {
                        fileLogName = "DButton_"+simpleDateFormat.format(new Date());
                        hasCallPhone = false;
                        Log.e(TAG, "onReceive() ++++++++++++++++++++++++++++++++++++++++++尚未启动-直接长按");
                        hasStart = true;
                        isOverUp = false;
                        //初始化的时候赋值List
                        mPointDataList = new ArrayList<PointData>();
                        fristStartRemark();//记录开始时间，记录轨迹点，记录录音
                        //获取结束时间
                        endTimeLong = System.currentTimeMillis();
                        Date date = new Date(System.currentTimeMillis());
                        endTime = simpleDateFormat.format(date);
                        Log.e(TAG, "onReceive() ++++++++++++++++++++++++++++++++++++++++++" + Thread.currentThread().getName() + "|Date获取当前日期时间" + endTime);
                        mAlarmUpTask = new AlarmUpTask();
                        mAlarmUpTask.execute("");
                    } else {
                        Log.e(TAG, "onReceive() ++++++++++++++++++++++++++++++++++++++++++已经启动-长按结束");
                        // 结束录音
                        stop();
                        //获取结束时间
                        endTimeLong = System.currentTimeMillis();
                        Date date = new Date(System.currentTimeMillis());
                        endTime = simpleDateFormat.format(date);
                        Log.e(TAG, "onReceive() ++++++++++++++++++++++++++++++++++++++++++" + Thread.currentThread().getName() + "|Date获取当前日期时间" + endTime);
                        //上传录音文件
//                        mUploadTask = new UploadTask();
//                        mUploadTask.execute("");
                        if (mUploadTask != null)
                            mUploadTask.cancel(true);
                        mUploadTask = null;
                        mUploadTask = new UploadTask();
                        mUploadTask.execute("");
                    }
                }
            }
            //---------------------单击--------------------------通过ACTION来源判断
            else if (action.equals(DButtonApplication.ACTION_ONE_CLICK)) {
                Log.e(TAG, "onReceive() ++++++++++++++++++++++++++++++++++++++++++单击");
                if (hasStart) {
                    //结束轨迹
                    hasStart = false;
                    //结束录音
                    stop();
                    if (isAlarmUp) {
                        Log.e(TAG, "mHandler DButtonControlReceiver 单击 震动触发");
                        mHandler.sendEmptyMessage(10);
                        Log.e(TAG, "onReceive() ++++++++++++++++++++++++++++++++++++++++++有报警触发下结束");
                        //修改接口轨迹上传、录音上传，通过ID修改-此处需要判断录音文件是否上传成功
                        //无ID直接跳过上传接口
                        isOverUp = true;
                        mUploadTask = new UploadTask();
                        mUploadTask.execute("");
                    } else {
                        Log.e(TAG, "onReceive() ++++++++++++++++++++++++++++++++++++++++++无报警触发下结束");
                    }
                } else {
                    Log.e(TAG, "onReceive() ++++++++++++++++++++++++++++++++++++++++++无启动状态下无法结束");
                }
            } else if (action.equals(DButtonApplication.ACTION_CONTENT)) {
                Log.e(TAG, "onReceive() ++++++++++++++++++++++++++++++++++++++++++录音 mFileUtils.isRecording():" + mFileUtils.isRecording());
                if (mFileUtils.isRecording()) {
                    byte[] ble_value = intent.getByteArrayExtra(KEY_CONTENT);
                    //开始保存流文件录音参照beadsAudio的方法写文件
                    mFileUtils.processWriteFile(ble_value);
                }

            }
        }
    }

    private boolean hasStart = false;
    private boolean isOver = true;

    private void fristStartRemark() {
        //开始录音
        fristStart();
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
        locationManager.requestLocationUpdates(locationProvider, 1000, 0, locationListener);
    }

    private void startRemark() {
        //开始录音
        start();
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
        locationManager.requestLocationUpdates(locationProvider, 1000, 0, locationListener);
    }

    private MediaRecorder mediaRecorder;
    private boolean isRecording;
    String fileName = "dbutton.mp3";


    protected void fristStart() {
        fileName = "dbutton" + datenamesdf.format(new Date().getTime()) + ".mp3";
        String wavFileName = "dbutton" + datenamesdf.format(new Date().getTime()) + ".wav";
        mFileUtils.beginWriteFile(fileName, wavFileName);
        getManager().writeCharacteristic(UUIDCHAR_CTRL, 27,
                BluetoothGattCharacteristic.FORMAT_UINT8);
//        try {
//            //fileName = DateFormat.format("yyyyMMdd_HHmmss", Calendar.getInstance(Locale.CHINA)) + ".m4a";
//            File file = new File(Environment.getExternalStorageDirectory() + "/" + fileName);
//            if (file.exists()) {
//                // 如果文件存在，删除它，演示代码保证设备上只有一个录音文件
//                file.delete();
//            }
//            file.createNewFile();
//            mediaRecorder = new MediaRecorder();
//            // 设置音频录入源
//            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
//            // 设置录制音频的输出格式
//            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
//            // 设置音频的编码格式
//            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
//            // 设置录制音频文件输出文件路径
//            mediaRecorder.setOutputFile(file.getAbsolutePath());
//
//            mediaRecorder.setOnErrorListener(new MediaRecorder.OnErrorListener() {
//
//                @Override
//                public void onError(MediaRecorder mr, int what, int extra) {
//                    // 发生错误，停止录制
//                    mediaRecorder.stop();
//                    mediaRecorder.release();
//                    mediaRecorder = null;
//                    isRecording = false;
//                    Toast.makeText(DButtonApplication.this, "录音发生错误", Toast.LENGTH_SHORT).show();
//                }
//            });
//
//            // 准备、开始
//            mediaRecorder.prepare();
//            mediaRecorder.start();
//
//            mStartDuration = System.currentTimeMillis();
//            isRecording = true;
//            Toast.makeText(DButtonApplication.this, "开始录音", Toast.LENGTH_SHORT).show();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    /**
     * 开始录音
     */
    protected void start() {
        fileName = "dbutton" + datenamesdf.format(new Date().getTime()) + ".mp3";
        String wavFileName = "dbutton" + datenamesdf.format(new Date().getTime()) + ".wav";
        mFileUtils.beginWriteFile(fileName, wavFileName);
        getManager().writeCharacteristic(UUIDCHAR_CTRL, 24,
                BluetoothGattCharacteristic.FORMAT_UINT8);
//        try {
//            //fileName = DateFormat.format("yyyyMMdd_HHmmss", Calendar.getInstance(Locale.CHINA)) + ".m4a";
//            File file = new File(Environment.getExternalStorageDirectory() + "/" + fileName);
//            if (file.exists()) {
//                // 如果文件存在，删除它，演示代码保证设备上只有一个录音文件
//                file.delete();
//            }
//            file.createNewFile();
//            mediaRecorder = new MediaRecorder();
//            // 设置音频录入源
//            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
//            // 设置录制音频的输出格式
//            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
//            // 设置音频的编码格式
//            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
//            // 设置录制音频文件输出文件路径
//            mediaRecorder.setOutputFile(file.getAbsolutePath());
//
//            mediaRecorder.setOnErrorListener(new MediaRecorder.OnErrorListener() {
//
//                @Override
//                public void onError(MediaRecorder mr, int what, int extra) {
//                    // 发生错误，停止录制
//                    mediaRecorder.stop();
//                    mediaRecorder.release();
//                    mediaRecorder = null;
//                    isRecording = false;
//                    Toast.makeText(DButtonApplication.this, "录音发生错误", Toast.LENGTH_SHORT).show();
//                }
//            });
//
//            // 准备、开始
//            mediaRecorder.prepare();
//            mediaRecorder.start();
//
//            mStartDuration = System.currentTimeMillis();
//            isRecording = true;
//            Toast.makeText(DButtonApplication.this, "开始录音", Toast.LENGTH_SHORT).show();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    /**
     * 录音结束
     */
    protected void stop() {
        if (mFileUtils.isRecording()) {
            mFileUtils.endWriteFile();
        }
//        if (isRecording) {
//            // 如果正在录音，停止并释放资源
//            mediaRecorder.stop();
//            mediaRecorder.release();
//            mediaRecorder = null;
//            isRecording = false;
//            mDuration = System.currentTimeMillis() - mStartDuration;
//            Toast.makeText(DButtonApplication.this, "录音结束", Toast.LENGTH_SHORT).show();
//        }
    }

    private static final int ALARM_UP_SUCCESS = 1;
    private static final int ALARM_UP_FALSE = 2;
    private static final int UPLOAD_SUCCESS = 3;
    private static final int UPLOAD_FALSE = 4;
    private static final int ALARM_UPDATE_SUCCESS = 5;
    private static final int ALARM_UPDATE_FALSE = 6;
    private static final int DISCONNECT_TO_CONNECT_DEVICE = 7;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 11:
                    mFilterContent = false;
                    break;
                case 10:
                    Log.e(TAG, "mHandler 震动触发");
                    getManager().writeCharacteristic(UUIDCHAR_CTRL, 2,
                            BluetoothGattCharacteristic.FORMAT_UINT8);
                    break;
                case DISCONNECT_TO_CONNECT_DEVICE:
                    LogUtil.println("DButtonApplication::DISCONNECT_TO_CONNECT_DEVICE::正在重连= ");
                    //重连方法
                    connectToDevice();
                    break;
                case ALARM_UP_SUCCESS:
                    //只有收到别人推送的才会刷新ALarmList
                    DButtonApplication.mAddAlarm = true;
                    //上传警报记录成功后，记录ID
                    isAlarmUp = true;
                    mHandler.sendEmptyMessageDelayed(ALARM_UPDATE_SUCCESS, 30000);

                    Intent intent = new Intent();
                    intent.setAction(DButtonApplication.ACTION_ALARM_LIST_UPDATE);
                    sendBroadcast(intent);
                    break;
                case ALARM_UP_FALSE:
                    mAlarmUpTask = new AlarmUpTask();
                    mAlarmUpTask.execute("");
//                    ToastUtils.shortToast(DButtonApplication.this, HttpAnalyJsonManager.lastError);
                    break;
                case UPLOAD_SUCCESS:
                    android.util.Log.e("uploadData", "mUploadData:" + mUploadData.toString());
                    mRecord = mUploadData.getUrl();
                    LogUtil.println("alarmUpdate UPLOAD_SUCCESS mRecord：" + mRecord);
                    break;
                case UPLOAD_FALSE:
                    //ToastUtils.shortToast(DButtonApplication.this, getResources().getString(R.string.upload_photo_false));
//                    mUploadTask = new UploadTask();
//                    mUploadTask.execute("");
                    break;
                case ALARM_UPDATE_SUCCESS:
                    if (isAlarmUp) {
//                        mAlarmUpdateTask = new AlarmUpdateTask();
//                        mAlarmUpdateTask.execute("");
                        //如果后续考虑录音30S上传
                        //那么则是在这个地方不断的调用uploadTask方法而不是调用修改方法，通过isAlarmUp判断/不是用hasStart，然后分别执行不同的加载方法。
                        //修改接口轨迹上传、录音上传，通过ID修改-此处需要判断录音文件是否上传成功
                        mUploadTask = new UploadTask();
                        mUploadTask.execute("");
                    }
                    break;
                case ALARM_UPDATE_FALSE:
                    //ToastUtils.shortToast(DButtonControlService.this, getResources().getString(R.string.upload_photo_false));
                    if (isAlarmUp) {
                        Log.e(TAG, "onReceive() UploadTask+++++++++++++++++++++++AlarmUpdateTask3");
                        mAlarmUpdateTask = new AlarmUpdateTask();
                        mAlarmUpdateTask.execute("");
                    }
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
    private boolean hasCallPhone = false;

    private class AlarmUpTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {
            Log.e(TAG, "onReceive() AlarmUpTask+++++++++++++++++++++++doInBackground");
            Log.e(TAG, "onReceive() AlarmUpTask+++++++++++++++++++++++DButtonApplication.mContactList.size():" + DButtonApplication.mContactList.size());
            if (DButtonApplication.mContactList.size() > 0) {
                String contactIds = "";
                for (int i = 0; i < DButtonApplication.mContactList.size(); i++) {
                    if (i == 0) {
                        contactIds = DButtonApplication.mContactList.get(i).getId();
                    } else {
                        contactIds = contactIds + "," + DButtonApplication.mContactList.get(i).getId();
                    }
                }
                Log.e(TAG, "onReceive() AlarmUpTask+++++++++++++++++++++++contactIds:" + contactIds);
                //Location lastLocation = null;
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

                point = "";
                Log.e(TAG, "onReceive() AlarmUpTask+++++++++++++++++++++++mPointDataList.size():" + mPointDataList.size());
                for (int i = 0; i < mPointDataList.size(); i++) {
//                    try {
                    String pointdata = "";
                    //JSONObject pointJsonObject = new JSONObject();
                    PointData pointData = mPointDataList.get(i);
                    //lastLocation = pointData.getLocation();
                    //JSONObject pointDataObject = new JSONObject();
                    //pointDataObject.put("time", String.valueOf(pointData.getPointTime()));
                    //pointDataObject.put("latitude", String.valueOf(pointData.getLocation().getLatitude()));
                    //pointDataObject.put("longitude", String.valueOf(pointData.getLocation().getLongitude()));
                    //pointJsonObject.put("point", pointDataObject);
                    pointdata = String.valueOf(pointData.getLocation().longitude) + "|" + String.valueOf(pointData.
                            getLocation().latitude) + "|" + String.valueOf(pointData.getPointTime());
                    if (TextUtils.isEmpty(point)) {
                        point = pointdata;
                    } else {
                        point = point + "," + pointdata;
                    }
                    Log.e(TAG, "onReceive() AlarmUpTask++++++++++++++++++++++point:" + point);

//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
                }

                BDLocation lastLocation = client.getLastKnownLocation();
                if (lastLocation == null) lastLocation = bdLocation;
                if (lastLocation != null) {
                    Log.e(TAG, "onReceive() AlarmUpTask+++++++++++++++++++++++lastLocation != null");
                    if (TextUtils.isEmpty(point)) {
                        Date date = new Date(System.currentTimeMillis());
                        long pointTime = date.getTime();
                        point = String.valueOf(lastLocation.getLongitude()) + "|" + lastLocation.getLatitude()
                                + "|" + String.valueOf(pointTime);
                    }
                    String city = lastLocation.getAddress().city;
                    String district = lastLocation.getAddress().district;
                    Log.e(TAG, "onReceive() AlarmUpTask getLocation++++++++++++++++++++++city:" + city + "|district:" + district);
                    Log.e(TAG, "onReceive() AlarmUpTask getLocation++++++++++++++++++++++street:" + lastLocation.getAddress().street + "|address:" + lastLocation.getAddress().address);

                    LogUtil.println("alarmUpdate AlarmUpTask mRecord = " + mRecord);
                    alarmIDData = HttpSendJsonManager.alarmUp(DButtonApplication.this, contactIds, point,
                            String.valueOf(nowTimeLong), String.valueOf(endTimeLong),
                            lastLocation.getAddress().address, mRecord, String.valueOf(mDuration));
                    Log.e(TAG, "onReceive() AlarmUpTask+++++++++++++++++++++++alarmIDData.isOK():" + alarmIDData.isOK());
                    //用JSON的形式保存轨迹Point值
                    if (alarmIDData.isOK()) {
                        mPointDataList = new ArrayList<PointData>();
                        mRecord = "";
                        LogUtil.println("alarmUpdate UPLOAD_SUCCESS mRecord = 空2");
                        mHandler.sendEmptyMessage(ALARM_UP_SUCCESS);
                    } else {
                        mHandler.sendEmptyMessageDelayed(ALARM_UP_FALSE, 3000);
                    }

                    if (!hasCallPhone) {
                        if (DButtonApplication.mContactList.size() > 0) {
                            hasCallPhone = true;
                            for (int i = 0; i < DButtonApplication.mContactList.size(); i++) {
                                String phoneNumber = DButtonApplication.mContactList.get(i).getPhone();
//                            String message = "我在" + lastLocation.getAddress().address + "出事了,出事时间是" + simpleDateFormat.format(
//                                    new Date(nowTimeLong)) + "-" + simpleDateFormat.format(new Date(endTimeLong));
                                String message = getSmsText(lastLocation.getAddress().address, simpleDateFormat.format(
                                        new Date(nowTimeLong)) + "-" + simpleDateFormat.format(new Date(endTimeLong)));
                                sendSMS(phoneNumber, message);
                                if (DButtonApplication.mContactList.get(i).getIsUrgent().equals("1")) {
                                    //拨打电话
                                    callPhone(phoneNumber);//同时背景播放音乐
                                }
                            }
                        }
                    }
                    //getLocation(lastLocation);
                } else {
                    Log.e(TAG, "onReceive() AlarmUpTask+++++++++++++++++++++++lastLocation == null");
                    if (mPointDataList.size() != 0) {
                        Log.e(TAG, "onReceive() point------------------------1");
                        LatLng location = mPointDataList.get(mPointDataList.size() - 1).getLocation();
                        Log.e(TAG, "onReceive() point11:" + point);
                        if (TextUtils.isEmpty(point)) {
                            Date date = new Date(System.currentTimeMillis());
                            long pointTime = date.getTime();
                            point = String.valueOf(location.longitude) + "|" + location.latitude
                                    + "|" + String.valueOf(pointTime);
                            Log.e(TAG, "onReceive() point12:" + point);
                        }

                        alarmIDData = HttpSendJsonManager.alarmUp(DButtonApplication.this, contactIds, point,
                                String.valueOf(nowTimeLong), String.valueOf(endTimeLong),
                                "经度" + location.longitude + ".纬度" + location.latitude, mRecord, String.valueOf(mDuration));
                        Log.e(TAG, "onReceive() AlarmUpTask+++++++++++++++++++++++alarmIDData.isOK():" + alarmIDData.isOK());
                        //用JSON的形式保存轨迹Point值
                        if (alarmIDData.isOK()) {
                            mPointDataList = new ArrayList<PointData>();
                            mRecord = "";
                            LogUtil.println("alarmUpdate UPLOAD_SUCCESS mRecord = 空3");
                            mHandler.sendEmptyMessage(ALARM_UP_SUCCESS);
                        } else {
                            mHandler.sendEmptyMessageDelayed(ALARM_UP_FALSE, 3000);
                        }

                        if (!hasCallPhone) {
                            if (DButtonApplication.mContactList.size() > 0) {
                                hasCallPhone = true;
                                for (int i = 0; i < DButtonApplication.mContactList.size(); i++) {
                                    String phoneNumber = DButtonApplication.mContactList.get(i).getPhone();
//                                String message = "我在经度" + location.longitude + ".纬度" + location.latitude +
//                                        "出事了,出事时间是" + simpleDateFormat.format(new Date(nowTimeLong)) + "-" +
//                                        simpleDateFormat.format(new Date(endTimeLong));
                                    String message = getSmsText("经度" + location.longitude + ".纬度" + location.latitude
                                            , simpleDateFormat.format(
                                                    new Date(nowTimeLong)) + "-" + simpleDateFormat.format(new Date(endTimeLong)));
                                    sendSMS(phoneNumber, message);
                                    if (DButtonApplication.mContactList.get(i).getIsUrgent().equals("1")) {
                                        //拨打电话
                                        callPhone(phoneNumber);//同时背景播放音乐
                                    }
                                }
                            }
                        }
                    }
                    //需要自动获取当前地址信息，组成一个location进行传输
                    else {
                        Log.e(TAG, "onReceive() point------------------------2");
                        if (ActivityCompat.checkSelfPermission(DButtonApplication.this, Manifest.permission.ACCESS_FINE_LOCATION)
                                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(DButtonApplication.this,
                                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                        } else {
                            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            Log.e(TAG, "onReceive() point21:" + point);
                            if (TextUtils.isEmpty(point)) {
                                Date date = new Date(System.currentTimeMillis());
                                long pointTime = date.getTime();
                                point = String.valueOf(location.getLongitude()) + "|" + location.getLatitude()
                                        + "|" + String.valueOf(pointTime);
                                Log.e(TAG, "onReceive() point22:" + point);
                            }
                            alarmIDData = HttpSendJsonManager.alarmUp(DButtonApplication.this, contactIds, point,
                                    String.valueOf(nowTimeLong), String.valueOf(endTimeLong),
                                    "经度" + location.getLongitude() + ".纬度" + location.getLatitude(), mRecord, String.valueOf(mDuration));
                            Log.e(TAG, "onReceive() AlarmUpTask+++++++++++++++++++++++alarmIDData.isOK():" + alarmIDData.isOK());
                            //用JSON的形式保存轨迹Point值
                            if (alarmIDData.isOK()) {
                                mPointDataList = new ArrayList<PointData>();
                                mRecord = "";
                                LogUtil.println("alarmUpdate UPLOAD_SUCCESS mRecord = 空3");
                                mHandler.sendEmptyMessage(ALARM_UP_SUCCESS);
                            } else {
                                mHandler.sendEmptyMessageDelayed(ALARM_UP_FALSE, 3000);
                            }

                            if (!hasCallPhone) {
                                if (DButtonApplication.mContactList.size() > 0) {
                                    hasCallPhone = true;
                                    for (int i = 0; i < DButtonApplication.mContactList.size(); i++) {
                                        String phoneNumber = DButtonApplication.mContactList.get(i).getPhone();
//                                    String message = "我在经度" + location.getLongitude() + ".纬度" + location.getLatitude() +
//                                            "出事了,出事时间是" + simpleDateFormat.format(new Date(nowTimeLong)) + "-" +
//                                            simpleDateFormat.format(new Date(endTimeLong));
                                        String message = getSmsText("经度" + location.getLongitude() + ".纬度" + location.getLatitude()
                                                , simpleDateFormat.format(
                                                        new Date(nowTimeLong)) + "-" + simpleDateFormat.format(new Date(endTimeLong)));
                                        sendSMS(phoneNumber, message);
                                        if (DButtonApplication.mContactList.get(i).getIsUrgent().equals("1")) {
                                            //拨打电话
                                            callPhone(phoneNumber);//同时背景播放音乐
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }else{
                //mHandler.sendEmptyMessageDelayed(ALARM_UP_FALSE, 3000);
            }
            return null;
        }
    }


    private AlarmUpdateTask mAlarmUpdateTask;

    private class AlarmUpdateTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {
            Log.e(TAG, "onReceive() AlarmUpdateTask+++++++++++++++++++++++doInBackground");
            Log.e(TAG, "onReceive() AlarmUpdateTask+++++++++++++++++++++++mPointDataList.size():" + mPointDataList.size());
            point = "";
            for (int i = 0; i < mPointDataList.size(); i++) {
                String pointdata = "";
                PointData pointData = mPointDataList.get(i);
                pointdata = String.valueOf(pointData.getLocation().longitude) + "|" + String.valueOf(pointData.
                        getLocation().latitude) + "|" + String.valueOf(pointData.getPointTime());
                if (TextUtils.isEmpty(point)) {
                    point = pointdata;
                } else {
                    point = point + "," + pointdata;
                }
            }
            if (TextUtils.isEmpty(point)) {
                BDLocation lastLocation = client.getLastKnownLocation();
                if (lastLocation == null) lastLocation = bdLocation;
                if (lastLocation != null) {
                    Date date = new Date(System.currentTimeMillis());
                    String pointTime = simpleDateFormat.format(date);
                    point = String.valueOf(lastLocation.getLongitude()) + "|" + lastLocation.getLatitude()
                            + "|" + String.valueOf(pointTime);
                }
            }
            boolean isOk;
            LogUtil.println("alarmUpdate AlarmUpdateTask mRecord：" + mRecord);
            if (isOverUp) {
                isOk = HttpSendJsonManager.alarmUpdate(DButtonApplication.this, alarmIDData.getId()
                        , "2", point, mRecord);
            } else {
                isOk = HttpSendJsonManager.alarmUpdate(DButtonApplication.this, alarmIDData.getId()
                        , "1", point, mRecord);
            }
            Log.e(TAG, "onReceive() AlarmUpdateTask+++++++++++++++++++++++isOk:" + isOk);
            //用JSON的形式保存轨迹Point值
            if (isOk) {
                mPointDataList = new ArrayList<PointData>();
                mRecord = "";
                LogUtil.println("alarmUpdate UPLOAD_SUCCESS mRecord = 空1");
                if (isOverUp) {
                    isAlarmUp = false;
                    Log.e(TAG, "onReceive() AlarmUpdateTask+++++++++++++++++++++++彻底结束警报流程");
                } else {
                    mHandler.sendEmptyMessageDelayed(ALARM_UPDATE_SUCCESS, 30000);
                }
            } else {
                mHandler.sendEmptyMessageDelayed(ALARM_UPDATE_FALSE, 30000);
            }
            return null;
        }
    }

    /**
     * 得到当前经纬度并开启线程去反向地理编码
     */
    public void getLocation(Location location) {
        BDLocation bdlocation = client.getLastKnownLocation();
        Log.e(TAG, "onReceive() getLocation++++++++++++++++++++++bdlocation:" + bdlocation.getLatitude()
                + "|" + bdlocation.getLongitude() + "|" + bdlocation.getAddress().address);
        Log.e(TAG, "onReceive() getLocation++++++++++++++++++++++location:" + location.getLatitude()
                + "|" + location.getLongitude());
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
                Log.e(TAG, "onReceive() AlarmUpTask+++++++++++++++++++++++LocationAsyncTask str:" + str);
                str = str.replace("renderReverse&&renderReverse", "");
                str = str.replace("(", "");
                str = str.replace(")", "");
                JSONObject jsonObject = new JSONObject(str);
                JSONObject address = jsonObject.getJSONObject("result");
                JSONObject location = jsonObject.getJSONObject("location");
                String city = address.getString("formatted_address");
                String district = address.getString("sematic_description");

                alarmIDData = HttpSendJsonManager.alarmUp(DButtonApplication.this, contactIds, point,
                        String.valueOf(nowTimeLong), String.valueOf(endTimeLong),
                        district, mRecord, String.valueOf(mDuration));
                Log.e(TAG, "onReceive() AlarmUpTask+++++++++++++++++++++++alarmIDData.isOK():" + alarmIDData.isOK());
                //用JSON的形式保存轨迹Point值
                if (alarmIDData.isOK()) {
                    mHandler.sendEmptyMessage(ALARM_UP_SUCCESS);
                } else {
                    mHandler.sendEmptyMessageDelayed(ALARM_UP_FALSE, 3000);
                }

                if (!hasCallPhone) {
                    if (DButtonApplication.mContactList.size() > 0) {
                        hasCallPhone = true;
                        for (int i = 0; i < DButtonApplication.mContactList.size(); i++) {
                            String phoneNumber = DButtonApplication.mContactList.get(i).getPhone();
//                        String message = "我在" + city + district + "出事了,出事时间是" + simpleDateFormat.format(
//                                new Date(nowTimeLong)) + "-" + simpleDateFormat.format(new Date(endTimeLong));
                            String message = getSmsText(city + district, simpleDateFormat.format(
                                    new Date(nowTimeLong)) + "-" + simpleDateFormat.format(new Date(endTimeLong)));
                            sendSMS(phoneNumber, message);
                            if (DButtonApplication.mContactList.get(i).getIsUrgent().equals("1")) {
                                //拨打电话
                                callPhone(phoneNumber);//同时背景播放音乐
                            }
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
        LogUtil.println("sendSMSSS phoneNumber" + phoneNumber + "|message:" + message);
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
            Log.e(TAG, "onReceive() UploadTask+++++++++++++++++++++++doInBackground");
            //fileName = "dbutton20190276111147.mp3";
            //fileName = "Music/莫斯科.mp3";
            mFile = CommonUtils.encodeToBase64(Environment.getExternalStorageDirectory() + "/" + fileName);
            mUploadData = new UploadData();
            mUploadData = HttpSendJsonManager.uploadMedia(DButtonApplication.this,
                    HttpSendJsonManager.UPLOAD_TPYE_HEAD, fileName, mFile, String.valueOf(mDuration));
            if (mUploadData.isOK()) {
                mHandler.sendEmptyMessage(UPLOAD_SUCCESS);
                Log.e(TAG, "onReceive() UploadTask+++++++++++++++++++++++mUploadData.isOK():" + mUploadData.isOK());
                if (!isAlarmUp) {
                    Log.e(TAG, "onReceive() UploadTask+++++++++++++++++++++++AlarmUpTask");
                    //执行长按的上传方法
                    //此处需要判断录音文件是否上传成功
                    mAlarmUpTask = new AlarmUpTask();
                    mAlarmUpTask.execute("");
                    if (!isOverUp) {
                        //继续开始录音
                        start();
                    }
                } else {
                    stop();
                    Log.e(TAG, "onReceive() UploadTask+++++++++++++++++++++++AlarmUpdateTask1");
                    //执行单击的修改方法
                    mAlarmUpdateTask = new AlarmUpdateTask();
                    mAlarmUpdateTask.execute("");
                    if (!isOverUp) {
                        //继续开始录音
                        start();
                    }
                }
            } else {
                mHandler.sendEmptyMessage(UPLOAD_FALSE);
                if (!isAlarmUp) {
                    Log.e(TAG, "onReceive() UploadTask+++++++++++++++++++++++AlarmUpTask");
                    //执行长按的上传方法
                    //此处需要判断录音文件是否上传成功
                    mAlarmUpTask = new AlarmUpTask();
                    mAlarmUpTask.execute("");
                    if (!isOverUp) {
                        //继续开始录音
                        start();
                    }
                } else {
                    Log.e(TAG, "onReceive() UploadTask+++++++++++++++++++++++AlarmUpdateTask2");
                    //执行单击的修改方法
                    mAlarmUpdateTask = new AlarmUpdateTask();
                    mAlarmUpdateTask.execute("");
                    if (!isOverUp) {
                        //继续开始录音
                        //start();
                    }
                }
            }
            return null;
        }
    }

    private String getSmsText(String address, String time) {
        String name = "我";
        if (DButtonApplication.mUserData != null) name = DButtonApplication.mUserData.getName();
        String smsText = "您好！" + name + "现在遇到了非常紧急的情况需要您的帮助，" +
                "TA现在的位置在" + address + "附近。详情请立即下载小D（APP下载链接），此条为系统发送勿回！";
        return smsText;
    }
}
