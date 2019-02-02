package com.sfr.dbuttonapplication.activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.icen.blelibrary.activity.BleBaseActivity;
import com.icen.blelibrary.config.BleLibsConfig;
import com.sfr.dbuttonapplication.DButtonApplication;
import com.sfr.dbuttonapplication.R;
import com.sfr.dbuttonapplication.activity.widget.DeleteConfirmDialog;
import com.sfr.dbuttonapplication.activity.widget.LoadingProgressDialog;
import com.sfr.dbuttonapplication.entity.DButtonData;
import com.sfr.dbuttonapplication.entity.RenewData;
import com.sfr.dbuttonapplication.http.HttpAnalyJsonManager;
import com.sfr.dbuttonapplication.http.HttpSendJsonManager;
import com.sfr.dbuttonapplication.ota.OTAUtils;
import com.sfr.dbuttonapplication.utils.APPUtils;
import com.sfr.dbuttonapplication.utils.LogUtil;
import com.sfr.dbuttonapplication.utils.SettingSharedPerferencesUtil;
import com.sfr.dbuttonapplication.utils.ToastUtils;

import java.util.Arrays;
import java.util.HashMap;

public class OTAUpdateActivity extends BleBaseActivity implements View.OnClickListener {

    private static final int OTA_VERSION_SUCCESS = 1;
    private static final int OTA_VERSION_FALSE = 2;
    private static final int UPDATE_PROGRESS = 3;
    private static int progress = 0;
    private TextView mTvNowVer, mTvUpdateVer;
    private Button mBtnUpdate;
    private TextView mTvSeekBar;//tv_seekbar
    private SeekBar mSeekBar;//seekbar
    private String mNowMac = "";
    private OTAUtils mOTAUtils;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case OTA_VERSION_SUCCESS:
                    LoadingProgressDialog.Dissmiss();
                    if (mRenewData != null) {
                        mTvNowVer.setText("现有版本：Ver " + mRenewData.getLowestVer());
                        mTvUpdateVer.setText("可用版本：Ver " + mRenewData.getNewVer());
                        mOTAUtils = new OTAUtils(DButtonApplication.mInstance,"file_path");
                    }
                    break;
                case OTA_VERSION_FALSE:
                    LoadingProgressDialog.Dissmiss();
                    ToastUtils.shortToast(OTAUpdateActivity.this, HttpAnalyJsonManager.lastError);
                    break;
                case UPDATE_PROGRESS:
                    if(progress<100){
                        progress = progress + 1;
                        mTvSeekBar.setText(progress + "%");
                        mSeekBar.setProgress(progress);
                        mHandler.sendEmptyMessageDelayed(UPDATE_PROGRESS,1000);
                    }else{
                        mTvSeekBar.setVisibility(View.GONE);
                        mSeekBar.setVisibility(View.GONE);
                        mBtnUpdate.setText(R.string.update);
                        mBtnUpdate.setClickable(true);
                    }
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        APPUtils.setTranslucent(this);
        setContentView(R.layout.activity_otaupdate);
        initTitle();
        setView();
        setListener();
        LoadingProgressDialog.show(OTAUpdateActivity.this, false, true, 30000);
        mRenewTask = new RenewTask();
        mRenewTask.execute("");
    }

    private void setListener() {
        mBtnUpdate.setOnClickListener(this);
    }

    private void setView() {
        mTvNowVer = (TextView) findViewById(R.id.tv_now_ver);
        mTvUpdateVer = (TextView) findViewById(R.id.tv_update_ver);
        mBtnUpdate = (Button) findViewById(R.id.btn_update);
        mTvSeekBar = (TextView) findViewById(R.id.tv_seekbar);
        mSeekBar = (SeekBar) findViewById(R.id.seekbar);
    }

    private TextView mActivityTitle, mTitleExtra, mTitleBack;

    private void initTitle() {
        mActivityTitle = (TextView) findViewById(R.id.title_info);
        mTitleExtra = (TextView) findViewById(R.id.title_extra);
        mTitleBack = (TextView) findViewById(R.id.title_back);
        mActivityTitle.setText(getResources().getString(R.string.ota_update));
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
        switch (v.getId()){
            case R.id.btn_update:
                showDeleteConfirmDialog();
                break;
        }

    }

    private RenewTask mRenewTask;
    private RenewData mRenewData;

    private class RenewTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {
            mRenewData = HttpSendJsonManager.renew(OTAUpdateActivity.this, HttpSendJsonManager.RENEW_TYPE_OTA);
            if (mRenewData.isOK()) {
                mHandler.sendEmptyMessage(OTA_VERSION_SUCCESS);
            } else {
                mHandler.sendEmptyMessage(OTA_VERSION_FALSE);
            }
            return null;
        }
    }


    private DeleteConfirmDialog mDeleteConfirmDialog;

    private TextView mTVDeleteText;
    private TextView mTVDeleteHint;
    private TextView mTVCancel;
    private TextView mTVOK;

    public void showDeleteConfirmDialog() {
        mDeleteConfirmDialog = new DeleteConfirmDialog(OTAUpdateActivity.this,
                R.style.share_dialog);
        mDeleteConfirmDialog.show();
        Window window = mDeleteConfirmDialog.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.alpha = 1f;
        window.setAttributes(lp);
        mTVDeleteText = (TextView) window.findViewById(R.id.dialog_delete_confirm_text);
        mTVDeleteText.setText(R.string.ota_update_dialog_text);
        mTVDeleteHint = (TextView) window.findViewById(R.id.dialog_delete_confirm_textview);
        mTVDeleteHint.setText(R.string.ota_update_dialog_hint);
        mTVCancel = (TextView) window.findViewById(R.id.delete_confirm_cancel);
        mTVCancel.setText(R.string.common_cancel);
        mTVCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDeleteConfirmDialog.dismiss();
            }
        });
        mTVOK = (TextView) window.findViewById(R.id.delete_confirm_ok);
        mTVOK.setText(R.string.common_confirm);
        mTVOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDeleteConfirmDialog.dismiss();
                //执行OTA进度条
                mBtnUpdate.setText(R.string.now_update);
                //mBtnUpdate.setBackgroundResource(R.mipmap.btn_sms_code_no);
                mBtnUpdate.setClickable(false);
                mTvSeekBar.setVisibility(View.VISIBLE);
                mSeekBar.setVisibility(View.VISIBLE);
                int state = mOTAUtils.processOTA();//执行OTA升级
                if(state==0){//OTA_STEP_PRE_PAIRE成功
                    state = mOTAUtils.processOTA();//执行OTA升级
                    if(state==0){//OTA_STEP_START成功
                        state = mOTAUtils.processOTA();//执行OTA升级
                        if(state==1){//OTA_STEP_WRITE成功
                            state = mOTAUtils.processOTA();//执行OTA升级
                            if(state==1){//OTA_STEP_LAST成功
                                state = mOTAUtils.processOTA();//执行OTA升级
                                if(state==2){//OTA_STEP_END成功
                                    state = mOTAUtils.processOTA();//执行OTA升级
                                }else{
                                    //异常提醒
                                }
                            }else{
                                //异常提醒
                            }
                        }else{
                            //异常提醒
                        }
                    }else{
                        //异常提醒
                    }
                }else{
                    //异常提醒
                }
                //mHandler.sendEmptyMessage(UPDATE_PROGRESS);  OTAUtils缺少进度管控，可以先OTA包测试通过后，再尝试进行开发进度
            }
        });
        mNowMac = SettingSharedPerferencesUtil.GetBindDbuttonMACValue(
                OTAUpdateActivity.this, DButtonApplication.mUserData.getPhone());
    }


    @Override
    public void onInitialManager(boolean is_success) {
        LogUtil.println("MyDButtonActivity::onInitialManager::result= " + is_success);
        if (is_success) {
            Toast.makeText(OTAUpdateActivity.this, "initial manager success", Toast.LENGTH_LONG).show();
            mBleManager.setManagerCallback(this);
            if (mBleManager.isLeEnabled()) {
                //mBleManager.connectToDevice(true, mNowMac);
            } else {

            }

            mBleManager.startScanDevice();
            if (!mBleManager.isSupportLE()){
                Toast.makeText(this, "Current device not support le", Toast.LENGTH_LONG).show();
                finish();
            }
        } else {
            Toast.makeText(OTAUpdateActivity.this, "initial manager false", Toast.LENGTH_LONG).show();
            mBleManager.setManagerCallback(null);
        }
    }

    @Override
    public void onLESwitch(int current_state){
        LogUtil.println("MyDButtonActivity::onLESwitch::current_state= " +current_state);
        if (BleLibsConfig.BLE_SWITCH_CLOSING == current_state || BleLibsConfig.BLE_SWITCH_OPENING == current_state) {
            Toast.makeText(this, "Bluetooth is operating now", Toast.LENGTH_LONG).show();
        } else if (BleLibsConfig.BLE_SWITCH_ON == current_state) {

        } else if (BleLibsConfig.BLE_SWITCH_OFF == current_state) {

        } else if (BleLibsConfig.BLE_SWITCH_ERROR == current_state){
            if (mBleManager.isLeEnabled()){

            } else {

            }
        }
    }

    private HashMap<String,DButtonData> mDButtonMap = new HashMap<String,DButtonData>();
    @Override
    public void onLEScan(int scan_process, String device_name, String device_class, String device_mac,
                         int device_rssi, byte[] broadcast_content) {
        LogUtil.println("BindDButtonActivity::onLEScan::process= " + scan_process +
                " name= " + device_name + " mac= " + device_mac +
                " class= " + device_class +  " RSSI= " + device_rssi +
                " content= " + Arrays.toString(broadcast_content));

        if(!mDButtonMap.containsKey(device_mac)&& !TextUtils.isEmpty(device_mac)) {
            //添加到List-后续加名称过滤
            DButtonData dButtonData = new DButtonData();
            dButtonData.setBroadcast_content(broadcast_content);
            dButtonData.setDevice_class(device_class);
            dButtonData.setDevice_mac(device_mac);
            dButtonData.setDevice_name(device_name);
            dButtonData.setDevice_rssi(device_rssi);
            mDButtonMap.put(device_mac,dButtonData);
        }
        if (BleLibsConfig.LE_SCAN_PROCESS_BEGIN == scan_process){//指示扫描开始
            Toast.makeText(this, "LE Scan begin", Toast.LENGTH_LONG).show();
        } else if (BleLibsConfig.LE_SCAN_PROCESS_DOING == scan_process) {//扫描进行中

        } else if (BleLibsConfig.LE_SCAN_PROCESS_END == scan_process){//扫描结束
            Toast.makeText(this, "Scan finished", Toast.LENGTH_LONG).show();
            //判断是否包含mNowMac
            if(mDButtonMap.containsKey(mNowMac)){
                //尝试连接
                mBleManager.connectToDevice(true, mNowMac);
            }else{
                //提示用户
                Toast.makeText(this, "绑定DBUTTON不在身边", Toast.LENGTH_LONG).show();
            }
        } else {//扫描发生异常
            Toast.makeText(this, "Scan exception. finish this process", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onConnectDevice(boolean is_success, String device_name, String device_mac) {
        super.onConnectDevice(is_success, device_name, device_mac);
        LogUtil.println("BindDButtonActivity::onLEScan::is_success= " + is_success +
                " device_name= " + device_name + " device_mac= " + device_mac);
        if (is_success){
        }
    }
}
