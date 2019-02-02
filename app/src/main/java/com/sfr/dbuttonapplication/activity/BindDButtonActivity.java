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
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.icen.blelibrary.config.BleLibsConfig;
import com.sfr.dbuttonapplication.DButtonApplication;
import com.sfr.dbuttonapplication.R;
import com.sfr.dbuttonapplication.activity.adapter.BindDButtonListAdapter;
import com.sfr.dbuttonapplication.activity.widget.LoadingProgressDialog;
import com.sfr.dbuttonapplication.entity.DButtonData;
import com.sfr.dbuttonapplication.http.HttpAnalyJsonManager;
import com.sfr.dbuttonapplication.http.HttpSendJsonManager;
import com.sfr.dbuttonapplication.utils.APPUtils;
import com.sfr.dbuttonapplication.utils.LogUtil;
import com.sfr.dbuttonapplication.utils.SettingSharedPerferencesUtil;
import com.sfr.dbuttonapplication.utils.ToastUtils;

import java.util.ArrayList;
import java.util.HashMap;

public class BindDButtonActivity extends AppCompatActivity implements View.OnClickListener {

    private ListView mLvBindDButtonList;
    private BindDButtonListAdapter mBindDButtonListAdapter;
    private ArrayList<DButtonData> mBindDButtonList;
    private HashMap<String, DButtonData> mDButtonMap = new HashMap<String, DButtonData>();
    private TextView mTvEmptyHint, mTvHintTitle, mTvHintText;
    private static final int ADD_LAY_SUCCESS = 1;
    private static final int ADD_LAY_FALSE = 2;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case ADD_LAY_SUCCESS:
                    LoadingProgressDialog.Dissmiss();
                    DButtonApplication.mAddDbutton = true;
                    ToastUtils.shortToast(BindDButtonActivity.this, R.string.bind_success);
                    SettingSharedPerferencesUtil.SetBindDbuttonMACValue(
                            BindDButtonActivity.this, DButtonApplication.mUserData.getPhone(), mMac);
                    finish();
                    break;
                case ADD_LAY_FALSE:
                    LoadingProgressDialog.Dissmiss();
                    ToastUtils.shortToast(BindDButtonActivity.this, HttpAnalyJsonManager.lastError);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        APPUtils.setTranslucent(this);
        setContentView(R.layout.activity_bind_dbutton);
        initTitle();
        registerReceiver();
        setView();
        setListener();

    }

    private void setListener() {
        mLvBindDButtonList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                DButtonData dButtonData = mBindDButtonList.get(position);
                //添加固件
                mMac = dButtonData.getDevice_mac();
                mName = dButtonData.getDevice_name();
                if (TextUtils.isEmpty(mName)) mName = "noName";
                LoadingProgressDialog.show(BindDButtonActivity.this, false, true, 30000);
                mAddLayerTask = new AddLayerTask();
                mAddLayerTask.execute("");
            }
        });
    }

    private void setView() {
        mBindDButtonList = new ArrayList<DButtonData>();
        mDButtonMap = new HashMap<String, DButtonData>();
        mLvBindDButtonList = (ListView) findViewById(R.id.lv_bind_dbutton_list);
        mBindDButtonListAdapter = new BindDButtonListAdapter(BindDButtonActivity.this, mBindDButtonList);
        mLvBindDButtonList.setAdapter(mBindDButtonListAdapter);
        mTvEmptyHint = (TextView) findViewById(R.id.tv_empty_hint);
//        if (mBindDButtonList.size() == 0) {
//            mTvEmptyHint.setVisibility(View.VISIBLE);
//        } else {
//            mTvEmptyHint.setVisibility(View.GONE);
//        }
        mTvHintTitle = (TextView) findViewById(R.id.tv_bind_search_over_hint_title);
        mTvHintText = (TextView) findViewById(R.id.tv_bind_search_over_hint_text);
        mTvHintTitle.setText(R.string.dbutton_to_bind_search_hint_title);
        mTvHintText.setText(R.string.dbutton_to_bind_search_hint_text);
        DButtonApplication.mInstance.startScanDevice();
    }

    private TextView mActivityTitle, mTitleExtra, mTitleBack;

    private void initTitle() {
        mActivityTitle = (TextView) findViewById(R.id.title_info);
        mTitleExtra = (TextView) findViewById(R.id.title_extra);
        mTitleBack = (TextView) findViewById(R.id.title_back);
        mActivityTitle.setText(getResources().getString(R.string.dbutton_to_bind));
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

    }

    private String mMac = "";
    private String mName = "";
    private AddLayerTask mAddLayerTask;

    private class AddLayerTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {
            if (HttpSendJsonManager.addLayer(BindDButtonActivity.this, mMac, mName)) {
                //需要返回ID
                mHandler.sendEmptyMessage(ADD_LAY_SUCCESS);
            } else {
                mHandler.sendEmptyMessage(ADD_LAY_FALSE);
            }
            return null;
        }
    }


//    @Override
//    public void onInitialManager(boolean is_success) {
//        LogUtil.println("BindDButtonActivity::onInitialManager::result= " + is_success);
//        if (is_success) {
//            Toast.makeText(BindDButtonActivity.this, "initial manager success", Toast.LENGTH_LONG).show();
//            mBleManager.setManagerCallback(this);
//            if (mBleManager.isLeEnabled()) {
//
//            } else {
//
//            }
//
//            mBleManager.startScanDevice();
//            if (!mBleManager.isSupportLE()) {
//                Toast.makeText(this, "Current device not support le", Toast.LENGTH_LONG).show();
//                finish();
//            }
//        } else {
//            Toast.makeText(BindDButtonActivity.this, "initial manager false", Toast.LENGTH_LONG).show();
//            mBleManager.setManagerCallback(null);
//        }
//    }
//
//    @Override
//    public void onLESwitch(int current_state) {
//        LogUtil.println("BindDButtonActivity::onLESwitch::current_state= " + current_state);
//        if (BleLibsConfig.BLE_SWITCH_CLOSING == current_state || BleLibsConfig.BLE_SWITCH_OPENING == current_state) {
//            Toast.makeText(this, "Bluetooth is operating now", Toast.LENGTH_LONG).show();
//        } else if (BleLibsConfig.BLE_SWITCH_ON == current_state) {
//
//        } else if (BleLibsConfig.BLE_SWITCH_OFF == current_state) {
//
//        } else if (BleLibsConfig.BLE_SWITCH_ERROR == current_state) {
//            if (mBleManager.isLeEnabled()) {
//
//            } else {
//
//            }
//        }
//    }
//
//    @Override
//    public void onLEScan(int scan_process, String device_name, String device_class, String device_mac,
//                         int device_rssi, byte[] broadcast_content) {
//        LogUtil.println("BindDButtonActivity::onLEScan::process= " + scan_process +
//                " name= " + device_name + " mac= " + device_mac +
//                " class= " + device_class + " RSSI= " + device_rssi +
//                " content= " + Arrays.toString(broadcast_content));
//
//        if (device_name.equals("BEADS-1")) {
//            if (!mDButtonMap.containsKey(device_mac) && !TextUtils.isEmpty(device_mac)) {
//                //添加到List-后续加名称过滤
//                DButtonData dButtonData = new DButtonData();
//                dButtonData.setBroadcast_content(broadcast_content);
//                dButtonData.setDevice_class(device_class);
//                dButtonData.setDevice_mac(device_mac);
//                dButtonData.setDevice_name(device_name);
//                dButtonData.setDevice_rssi(device_rssi);
//                mBindDButtonList.add(dButtonData);
//                mDButtonMap.put(device_mac, dButtonData);
//            }
//            if (BleLibsConfig.LE_SCAN_PROCESS_BEGIN == scan_process) {//指示扫描开始
//                Toast.makeText(this, "LE Scan begin", Toast.LENGTH_LONG).show();
//                //更改文字说明
//                mTvHintTitle.setText(R.string.dbutton_to_bind_search_hint_title);
//                mTvHintText.setText(R.string.dbutton_to_bind_search_hint_text);
//            } else if (BleLibsConfig.LE_SCAN_PROCESS_DOING == scan_process) {//扫描进行中
//                //do nothing
//                //更改文字说明
//                mTvHintTitle.setText(R.string.dbutton_to_bind_search_hint_title);
//                mTvHintText.setText(R.string.dbutton_to_bind_search_hint_text);
//            } else if (BleLibsConfig.LE_SCAN_PROCESS_END == scan_process) {//扫描结束
//                Toast.makeText(this, "Scan finished", Toast.LENGTH_LONG).show();
//                //更改文字说明
//                mTvHintTitle.setText(R.string.dbutton_to_bind_search_over_hint_title);
//                mTvHintText.setText(R.string.dbutton_to_bind_search_over_hint_text);
//                //刷新list
//                mBindDButtonListAdapter.setServiceList(mBindDButtonList);
//                if (mBindDButtonList.size() == 0) {
//                    mTvEmptyHint.setVisibility(View.VISIBLE);
//                } else {
//                    mTvEmptyHint.setVisibility(View.GONE);
//                }
//            } else {//扫描发生异常
//                Toast.makeText(this, "Scan exception. finish this process", Toast.LENGTH_LONG).show();
//            }
//        }
//    }
//
//    @Override
//    public void onConnectDevice(boolean is_success, String device_name, String device_mac) {
//        super.onConnectDevice(is_success, device_name, device_mac);
//        LogUtil.println("BindDButtonActivity::onLEScan::is_success= " + is_success +
//                " device_name= " + device_name + " device_mac= " + device_mac);
//        if (is_success) {
//
//        }
//    }


    public void onLEScan(int scan_process) {
        if (BleLibsConfig.LE_SCAN_PROCESS_BEGIN == scan_process) {//指示扫描开始
            mBindDButtonList = new ArrayList<DButtonData>();
            Toast.makeText(this, "LE Scan begin", Toast.LENGTH_LONG).show();
            //更改文字说明
            mTvHintTitle.setText(R.string.dbutton_to_bind_search_hint_title);
            mTvHintText.setText(R.string.dbutton_to_bind_search_hint_text);
        } else if (BleLibsConfig.LE_SCAN_PROCESS_DOING == scan_process) {//扫描进行中
            //do nothing
            //更改文字说明
            mTvHintTitle.setText(R.string.dbutton_to_bind_search_hint_title);
            mTvHintText.setText(R.string.dbutton_to_bind_search_hint_text);
        } else if (BleLibsConfig.LE_SCAN_PROCESS_END == scan_process) {//扫描结束
            for (String key : DButtonApplication.mDButtonMap.keySet()) {
                //添加到List-后续加名称过滤
                DButtonData dButtonData = DButtonApplication.mDButtonMap.get(key);
                mBindDButtonList.add(dButtonData);
            }
            Toast.makeText(this, "Scan finished", Toast.LENGTH_LONG).show();
            //更改文字说明
            mTvHintTitle.setText(R.string.dbutton_to_bind_search_over_hint_title);
            mTvHintText.setText(R.string.dbutton_to_bind_search_over_hint_text);
            //刷新list
            mBindDButtonListAdapter.setServiceList(mBindDButtonList);
            if (mBindDButtonList.size() == 0) {
                mTvEmptyHint.setVisibility(View.VISIBLE);
            } else {
                mTvEmptyHint.setVisibility(View.GONE);
            }
        } else {//扫描发生异常
            Toast.makeText(this, "Scan exception. finish this process", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mDButtonReceiver);
    }

    private void registerReceiver() {
        mDButtonReceiver = new DButtonReceiver();//广播接受者实例
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(DButtonApplication.ACTION_DBUTTON_SCAN);
        registerReceiver(mDButtonReceiver, intentFilter);
    }

    private DButtonReceiver mDButtonReceiver;

    public class DButtonReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            LogUtil.println("DButtonApplication::DButtonReceiver::DButtonReceiver ");
            String action = intent.getAction();
            //尝试Main界面底部三个按钮分别触发不同广播，开始模拟测试
            if (action.equals(DButtonApplication.ACTION_DBUTTON_SCAN)) {
                int scan_process = intent.getIntExtra("scan_process", 0);
                LogUtil.println("DButtonApplication::DButtonReceiver::scan_process= " + scan_process);
                onLEScan(scan_process);
            }
        }
    }
}
