package com.sfr.dbuttonapplication.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sfr.dbuttonapplication.DButtonApplication;
import com.sfr.dbuttonapplication.R;
import com.sfr.dbuttonapplication.activity.widget.LoadingProgressDialog;
import com.sfr.dbuttonapplication.entity.LayerListData;
import com.sfr.dbuttonapplication.http.HttpAnalyJsonManager;
import com.sfr.dbuttonapplication.http.HttpSendJsonManager;
import com.sfr.dbuttonapplication.utils.APPUtils;
import com.sfr.dbuttonapplication.utils.LogUtil;
import com.sfr.dbuttonapplication.utils.SettingSharedPerferencesUtil;
import com.sfr.dbuttonapplication.utils.ToastUtils;

public class MyDButtonActivity extends AppCompatActivity implements View.OnClickListener {

    private RelativeLayout mRLBindDButton, mRLBuyDButton;//rl_dbutton_bind
    private RelativeLayout mRLNoBind, mRLHasBind, mRLNoConnect;
    private RelativeLayout mRLUnbindOne,mRLUnbindTwo;
    private Button mBtnExplain,mBtnConnect; //btn_explain
    private static final int LAYER_LIST_SUCCESS = 1;
    private static final int LAYER_LIST_FALSE = 2;
    private static final int LAYER_DEL_SUCCESS = 3;
    private static final int LAYER_DEL_FALSE = 4;
    private TextView mTvDButtonIDConnect, mTvDButtonIDNoConnect,mTvBattery;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case LAYER_LIST_SUCCESS:
                    LoadingProgressDialog.Dissmiss();
                    //区分是否显示界面，如果size>0显示有绑定，size=0显示未绑定
                    //再刷一次
                    DButtonApplication.mAddDbutton = false;
                    if (mLayerListData.getList().size() == 0) {
                        mRLNoBind.setVisibility(View.VISIBLE);
                        mRLHasBind.setVisibility(View.GONE);
                        mRLNoConnect.setVisibility(View.GONE);
                        DButtonApplication.mNowMac = "";
                        mIDS = "";
                        SettingSharedPerferencesUtil.SetBindDbuttonIDValue(
                                MyDButtonActivity.this,DButtonApplication.mUserData.getPhone(),mIDS);
                        SettingSharedPerferencesUtil.SetBindDbuttonMACValue(
                                MyDButtonActivity.this,DButtonApplication.mUserData.getPhone(),DButtonApplication.mNowMac);
                    } else {
                        DButtonApplication.mNowMac = mLayerListData.getList().get(0).getMac();
                        mIDS = mLayerListData.getList().get(0).getId();
                        SettingSharedPerferencesUtil.SetBindDbuttonIDValue(
                                MyDButtonActivity.this,DButtonApplication.mUserData.getPhone(),mIDS);
                        SettingSharedPerferencesUtil.SetBindDbuttonMACValue(
                                MyDButtonActivity.this,DButtonApplication.mUserData.getPhone(),DButtonApplication.mNowMac);
                        //判断是否连接
                        if(DButtonApplication.isConnectDevice){
                            DButtonApplication.mInstance.readBatteryShow();
                            mRLNoBind.setVisibility(View.GONE);
                            mRLHasBind.setVisibility(View.VISIBLE);
                            mRLNoConnect.setVisibility(View.GONE);
                        }else {
                            mRLNoBind.setVisibility(View.GONE);
                            mRLHasBind.setVisibility(View.GONE);
                            mRLNoConnect.setVisibility(View.VISIBLE);
                        }
                        mTvDButtonIDConnect.setText("宝珠ID：" + DButtonApplication.mNowMac);
                        mTvDButtonIDNoConnect.setText("宝珠ID：" + DButtonApplication.mNowMac);
//                        mLayerListTask = new LayerListTask();
//                        mLayerListTask.execute("");
                    }
                    break;
                case LAYER_LIST_FALSE:
                    LoadingProgressDialog.Dissmiss();
                    ToastUtils.shortToast(MyDButtonActivity.this, HttpAnalyJsonManager.lastError);
                    break;
                case LAYER_DEL_SUCCESS:
                    LoadingProgressDialog.Dissmiss();
                    mRLNoBind.setVisibility(View.VISIBLE);
                    mRLHasBind.setVisibility(View.GONE);
                    mRLNoConnect.setVisibility(View.GONE);
                    DButtonApplication.mNowMac = "";
                    mIDS = "";
                    SettingSharedPerferencesUtil.SetBindDbuttonIDValue(
                            MyDButtonActivity.this,DButtonApplication.mUserData.getPhone(),mIDS);
                    SettingSharedPerferencesUtil.SetBindDbuttonMACValue(
                            MyDButtonActivity.this,DButtonApplication.mUserData.getPhone(),DButtonApplication.mNowMac);
                    DButtonApplication.mInstance.disconnect();
                    break;
                case LAYER_DEL_FALSE:
                    LoadingProgressDialog.Dissmiss();
                    ToastUtils.shortToast(MyDButtonActivity.this, HttpAnalyJsonManager.lastError);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        APPUtils.setTranslucent(this);
        setContentView(R.layout.activity_my_dbutton);
        initTitle();
        registerReceiver();
        setView();
        setListener();

    }

    private void setListener() {
        mRLBindDButton.setOnClickListener(this);
        mRLBuyDButton.setOnClickListener(this);
        mBtnExplain.setOnClickListener(this);
        mRLUnbindOne.setOnClickListener(this);
        mRLUnbindTwo.setOnClickListener(this);
        mBtnConnect.setOnClickListener(this);
    }

    private void setView() {
        mRLUnbindOne = (RelativeLayout) findViewById(R.id.rl_dbutton_unbind);
        mRLUnbindTwo = (RelativeLayout) findViewById(R.id.rl_dbutton_unbind_two);

        mRLBindDButton = (RelativeLayout) findViewById(R.id.rl_dbutton_bind);
        mRLBuyDButton = (RelativeLayout) findViewById(R.id.rl_dbutton_buy);
        mRLNoBind = (RelativeLayout) findViewById(R.id.rl_no_dbutton);
        mRLHasBind = (RelativeLayout) findViewById(R.id.rl_has_dbutton);
        mRLNoConnect = (RelativeLayout) findViewById(R.id.rl_has_dbutton_no_connect);
        mTvDButtonIDConnect = (TextView) findViewById(R.id.my_dbutton_id_connect);
        mTvDButtonIDNoConnect = (TextView) findViewById(R.id.my_dbutton_id_no_connect);
        mTvBattery = (TextView) findViewById(R.id.tv_battrty);
        mBtnExplain = (Button) findViewById(R.id.btn_explain);
        mBtnConnect = (Button) findViewById(R.id.btn_connect);

        DButtonApplication.mNowMac = SettingSharedPerferencesUtil.GetBindDbuttonMACValue(
                MyDButtonActivity.this, DButtonApplication.mUserData.getPhone());
        mIDS = SettingSharedPerferencesUtil.GetBindDbuttonIDValue(
                MyDButtonActivity.this,DButtonApplication.mUserData.getPhone());
        if (TextUtils.isEmpty(DButtonApplication.mNowMac)) {
            mRLNoBind.setVisibility(View.VISIBLE);
            mRLHasBind.setVisibility(View.GONE);
            mRLNoConnect.setVisibility(View.GONE);
        } else {
            //判断是否连接
            if(DButtonApplication.isConnectDevice){
                DButtonApplication.mInstance.readBatteryShow();
                mRLNoBind.setVisibility(View.GONE);
                mRLHasBind.setVisibility(View.VISIBLE);
                mRLNoConnect.setVisibility(View.GONE);
            }else {
                mRLNoBind.setVisibility(View.GONE);
                mRLHasBind.setVisibility(View.GONE);
                mRLNoConnect.setVisibility(View.VISIBLE);
            }
            mTvDButtonIDConnect.setText("宝珠ID：" + DButtonApplication.mNowMac);
            mTvDButtonIDNoConnect.setText("宝珠ID：" + DButtonApplication.mNowMac);
        }
        LoadingProgressDialog.show(MyDButtonActivity.this, false, true, 30000);
        mLayerListTask = new LayerListTask();
        mLayerListTask.execute("");
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (DButtonApplication.mAddDbutton) {
            DButtonApplication.mNowMac = SettingSharedPerferencesUtil.GetBindDbuttonMACValue(
                    MyDButtonActivity.this, DButtonApplication.mUserData.getPhone());
            if (TextUtils.isEmpty(DButtonApplication.mNowMac)) {
                mRLNoBind.setVisibility(View.VISIBLE);
                mRLHasBind.setVisibility(View.GONE);
                mRLNoConnect.setVisibility(View.GONE);
            } else {
                //判断是否连接
                if(DButtonApplication.isConnectDevice){
                    DButtonApplication.mInstance.readBatteryShow();
                    mRLNoBind.setVisibility(View.GONE);
                    mRLHasBind.setVisibility(View.VISIBLE);
                    mRLNoConnect.setVisibility(View.GONE);
                }else {
                    mRLNoBind.setVisibility(View.GONE);
                    mRLHasBind.setVisibility(View.GONE);
                    mRLNoConnect.setVisibility(View.VISIBLE);
                }
                mTvDButtonIDConnect.setText("宝珠ID：" + DButtonApplication.mNowMac);
                mTvDButtonIDNoConnect.setText("宝珠ID：" + DButtonApplication.mNowMac);
                LoadingProgressDialog.show(MyDButtonActivity.this, false, true, 30000);
                mLayerListTask = new LayerListTask();
                mLayerListTask.execute("");
            }
        }
    }

    private TextView mActivityTitle, mTitleExtra, mTitleBack;

    private void initTitle() {
        mActivityTitle = (TextView) findViewById(R.id.title_info);
        mTitleExtra = (TextView) findViewById(R.id.title_extra);
        mTitleBack = (TextView) findViewById(R.id.title_back);
        mActivityTitle.setText(getResources().getString(R.string.my_dbutton));
        mTitleExtra.setVisibility(View.GONE);
        mTitleBack.setVisibility(View.VISIBLE);
        mTitleBack.setText(getResources().getString(R.string.go_up));
        mTitleBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_dbutton_bind:
                Intent intent = new Intent(MyDButtonActivity.this, BindDButtonActivity.class);
                startActivity(intent);
                break;
            case R.id.rl_dbutton_buy:
                intent = new Intent(MyDButtonActivity.this, WebViewActivity.class);
                intent.putExtra("url", "http://www.baidu.com");
                startActivity(intent);
                break;
            case R.id.btn_explain:
                intent = new Intent(MyDButtonActivity.this, DButtonExplainActivity.class);
                startActivity(intent);
                break;
            case R.id.rl_dbutton_unbind:
                DButtonApplication.canConnect = false;
                LoadingProgressDialog.show(MyDButtonActivity.this, false, true, 30000);
                mLayerDelTask = new LayerDelTask();
                mLayerDelTask.execute("");
                break;
            case R.id.rl_dbutton_unbind_two:
                DButtonApplication.canConnect = false;
                LoadingProgressDialog.show(MyDButtonActivity.this, false, true, 30000);
                mLayerDelTask = new LayerDelTask();
                mLayerDelTask.execute("");
                break;
            case R.id.btn_connect:
                //mBleManager.connectToDevice(true, DButtonApplication.mNowMac);
                DButtonApplication.mInstance.connectToDevice();
                break;
        }
    }

    private LayerListTask mLayerListTask;
    private LayerListData mLayerListData;

    private class LayerListTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {
            mLayerListData = HttpSendJsonManager.layerList(MyDButtonActivity.this);
            if (mLayerListData.isOK()) {
                mHandler.sendEmptyMessage(LAYER_LIST_SUCCESS);
            } else {
                mHandler.sendEmptyMessage(LAYER_LIST_FALSE);
            }
            return null;
        }
    }

    private String mIDS = "";//调用赋值
    private LayerDelTask mLayerDelTask;

    private class LayerDelTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {
            if (HttpSendJsonManager.delLayer(MyDButtonActivity.this, mIDS)) {
                mHandler.sendEmptyMessage(LAYER_DEL_SUCCESS);
            } else {
                mHandler.sendEmptyMessage(LAYER_DEL_FALSE);
            }
            return null;
        }
    }

//
//    @Override
//    public void onInitialManager(boolean is_success) {
//        LogUtil.println("MyDButtonActivity::onInitialManager::result= " + is_success);
//        if (is_success) {
//            Toast.makeText(MyDButtonActivity.this, "initial manager success", Toast.LENGTH_LONG).show();
//            mBleManager.setManagerCallback(this);
//            if (mBleManager.isLeEnabled()) {
//                //mBleManager.connectToDevice(true, DButtonApplication.mNowMac);
//            } else {
//
//            }
//
//            mBleManager.startScanDevice();
//            if (!mBleManager.isSupportLE()){
//                Toast.makeText(this, "Current device not support le", Toast.LENGTH_LONG).show();
//                finish();
//            }
//        } else {
//            Toast.makeText(MyDButtonActivity.this, "initial manager false", Toast.LENGTH_LONG).show();
//            mBleManager.setManagerCallback(null);
//        }
//    }
//
//    @Override
//    public void onLESwitch(int current_state){
//        LogUtil.println("MyDButtonActivity::onLESwitch::current_state= " +current_state);
//        if (BleLibsConfig.BLE_SWITCH_CLOSING == current_state || BleLibsConfig.BLE_SWITCH_OPENING == current_state) {
//            Toast.makeText(this, "Bluetooth is operating now", Toast.LENGTH_LONG).show();
//        } else if (BleLibsConfig.BLE_SWITCH_ON == current_state) {
//
//        } else if (BleLibsConfig.BLE_SWITCH_OFF == current_state) {
//
//        } else if (BleLibsConfig.BLE_SWITCH_ERROR == current_state){
//            if (mBleManager.isLeEnabled()){
//
//            } else {
//
//            }
//        }
//    }
//
//    private HashMap<String,DButtonData> mDButtonMap = new HashMap<String,DButtonData>();
//    @Override
//    public void onLEScan(int scan_process, String device_name, String device_class, String device_mac,
//                         int device_rssi, byte[] broadcast_content) {
//        LogUtil.println("BindDButtonActivity::onLEScan::process= " + scan_process +
//                " name= " + device_name + " mac= " + device_mac +
//                " class= " + device_class +  " RSSI= " + device_rssi +
//                " content= " + Arrays.toString(broadcast_content));
//
//        if(!mDButtonMap.containsKey(device_mac)&& !TextUtils.isEmpty(device_mac)) {
//            //添加到List-后续加名称过滤
//            DButtonData dButtonData = new DButtonData();
//            dButtonData.setBroadcast_content(broadcast_content);
//            dButtonData.setDevice_class(device_class);
//            dButtonData.setDevice_mac(device_mac);
//            dButtonData.setDevice_name(device_name);
//            dButtonData.setDevice_rssi(device_rssi);
//            mDButtonMap.put(device_mac,dButtonData);
//        }
//        if (BleLibsConfig.LE_SCAN_PROCESS_BEGIN == scan_process){//指示扫描开始
//            Toast.makeText(this, "LE Scan begin", Toast.LENGTH_LONG).show();
//        } else if (BleLibsConfig.LE_SCAN_PROCESS_DOING == scan_process) {//扫描进行中
//
//        } else if (BleLibsConfig.LE_SCAN_PROCESS_END == scan_process){//扫描结束
//            Toast.makeText(this, "Scan finished", Toast.LENGTH_LONG).show();
//            //判断是否包含DButtonApplication.mNowMac
//            if(mDButtonMap.containsKey(DButtonApplication.mNowMac)){
//                //尝试连接
//                mBleManager.connectToDevice(true, DButtonApplication.mNowMac);
//            }else{
//                //提示用户
//                Toast.makeText(this, "绑定DBUTTON不在身边", Toast.LENGTH_LONG).show();
//            }
//        } else {//扫描发生异常
//            Toast.makeText(this, "Scan exception. finish this process", Toast.LENGTH_LONG).show();
//        }
//    }
//
//    @Override
//    public void onConnectDevice(boolean is_success, String device_name, String device_mac) {
//        super.onConnectDevice(is_success, device_name, device_mac);
//        LogUtil.println("BindDButtonActivity::onLEScan::is_success= " + is_success +
//                " device_name= " + device_name + " device_mac= " + device_mac);
//        if (is_success){
//            mRLNoBind.setVisibility(View.GONE);
//            mRLHasBind.setVisibility(View.VISIBLE);
//            mRLNoConnect.setVisibility(View.GONE);
//        }
//    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mDButtonReceiver);
    }

    private void registerReceiver() {
        mDButtonReceiver = new DButtonReceiver();//广播接受者实例
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(DButtonApplication.ACTION_DBUTTON_CONNECT);
        intentFilter.addAction(DButtonApplication.ACTION_DBUTTON_BATTERY);
        registerReceiver(mDButtonReceiver, intentFilter);
    }

    private DButtonReceiver mDButtonReceiver;

    public class DButtonReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            LogUtil.println("DButtonApplication::DButtonReceiver::DButtonReceiver ");
            String action = intent.getAction();
            //尝试Main界面底部三个按钮分别触发不同广播，开始模拟测试
            if (action.equals(DButtonApplication.ACTION_DBUTTON_CONNECT)) {
                boolean is_success = intent.getBooleanExtra("is_success", false);
                LogUtil.println("DButtonApplication::DButtonReceiver::is_success= " + is_success);
                if(is_success){
                    mRLNoBind.setVisibility(View.GONE);
                    mRLHasBind.setVisibility(View.VISIBLE);
                    mRLNoConnect.setVisibility(View.GONE);
                }
            }else if(action.equals(DButtonApplication.ACTION_DBUTTON_BATTERY)) {
                String battery = intent.getStringExtra("battery");
                mTvBattery.setText("宝珠电量："+battery+"%");
            }
        }
    }
}
