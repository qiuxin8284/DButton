package com.sfr.dbuttonapplication.activity.login;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;

import com.sfr.dbuttonapplication.DButtonApplication;
import com.sfr.dbuttonapplication.R;
import com.sfr.dbuttonapplication.activity.MainActivity;
import com.sfr.dbuttonapplication.entity.UserData;
import com.sfr.dbuttonapplication.http.HttpAnalyJsonManager;
import com.sfr.dbuttonapplication.http.HttpSendJsonManager;
import com.sfr.dbuttonapplication.utils.APPUtils;
import com.sfr.dbuttonapplication.utils.LogUtil;
import com.sfr.dbuttonapplication.utils.PermissionHelper;
import com.sfr.dbuttonapplication.utils.PermissionInterface;
import com.sfr.dbuttonapplication.utils.SettingSharedPerferencesUtil;
import com.sfr.dbuttonapplication.utils.ToastUtils;

public class LoadingActivity extends AppCompatActivity implements PermissionInterface {

    private static final int GO_TO_LOGIN = 0;
    private static final int DELAY_TIME = 2000;
    private static final int LOGIN_SUCCESS = 3;
    private static final int LOGIN_FALSE = 4;
    private Handler mLoginHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case GO_TO_LOGIN:
                    login();
                    break;
                case LOGIN_SUCCESS:
                    DButtonApplication.mNowMac = SettingSharedPerferencesUtil.GetBindDbuttonMACValue(
                            LoadingActivity.this, DButtonApplication.mUserData.getPhone());
                    if(!TextUtils.isEmpty(DButtonApplication.mNowMac)) {
                        DButtonApplication.mInstance.startScanDevice();
                    }
                    Intent intent = new Intent(LoadingActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                    break;
                case LOGIN_FALSE:
                    ToastUtils.shortToast(LoadingActivity.this, HttpAnalyJsonManager.lastError);
                    intent = new Intent(LoadingActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //APPUtils.setTranslucentGone(this);
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_loading);

        //得到当前界面的装饰视图
        View decorView = getWindow().getDecorView();
//        SYSTEM_UI_FLAG_FULLSCREEN表示全屏的意思，也就是会将状态栏隐藏
        //设置系统UI元素的可见性
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
        //隐藏标题栏
//        ActionBar actionBar = getSupportActionBar();
//        actionBar.hide();

        //初始化并发起权限申请
        mPermissionHelper = new PermissionHelper(this, this);
        mPermissionHelper.requestPermissions();

    }

    private void login() {
        if(SettingSharedPerferencesUtil.GetFristStartValueConfig(LoadingActivity.this).equals("")){
            //开启引导页流程
            SettingSharedPerferencesUtil.SetFristStartValue(LoadingActivity.this,"true");
            Intent intent = new Intent(LoadingActivity.this, FirstLoadActivity.class);
            startActivity(intent);
            finish();
        }else {
            String token = SettingSharedPerferencesUtil.GetLoginTokenValue(LoadingActivity.this);
            if (TextUtils.isEmpty(token)) {
                Intent intent = new Intent(LoadingActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            } else {
                //尝试自动登陆
                DButtonApplication.USER_TOKEN = token;
                mLoginTask = new LoginTask();
                mLoginTask.execute("");
            }
        }
    }

    private LoginTask mLoginTask;
    private UserData mUserData;

    private class LoginTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {
            mUserData = HttpSendJsonManager.selfLogin(LoadingActivity.this);
            LogUtil.println("login mUserData:" + mUserData.toString());
            DButtonApplication.mUserData = mUserData;
            if (mUserData.isOK()) {
                mLoginHandler.sendEmptyMessage(LOGIN_SUCCESS);
            } else {
                mLoginHandler.sendEmptyMessage(LOGIN_FALSE);
            }
            return null;
        }
    }

    private PermissionHelper mPermissionHelper;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (mPermissionHelper.requestPermissionsResult(requestCode, permissions, grantResults)) {
            //权限请求结果，并已经处理了该回调
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public int getPermissionsRequestCode() {
        //设置权限请求requestCode，只有不跟onRequestPermissionsResult方法中的其他请求码冲突即可。
        return 10000;
    }

    @Override
    public String[] getPermissions() {
        //设置该界面所需的全部权限
//        String[] mPermissionList = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
//                Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.CALL_PHONE,
//                Manifest.permission.READ_LOGS, Manifest.permission.READ_PHONE_STATE,
//                Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.SET_DEBUG_APP,
//                Manifest.permission.SYSTEM_ALERT_WINDOW, Manifest.permission.GET_ACCOUNTS,
//                Manifest.permission.ACCESS_COARSE_LOCATION,
//                Manifest.permission.DISABLE_KEYGUARD,
//                Manifest.permission.RECEIVE_BOOT_COMPLETED,
//                Manifest.permission.READ_CONTACTS,
//                Manifest.permission.WRITE_APN_SETTINGS};
        String[] mPermissionList = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.CALL_PHONE,
                Manifest.permission.READ_PHONE_STATE, Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.SEND_SMS,
                Manifest.permission.READ_CONTACTS, Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO};
        return mPermissionList;
    }

    @Override
    public void requestPermissionsSuccess() {
        //权限请求用户已经全部允许
        initViews();
    }

    @Override
    public void requestPermissionsFail() {
        //权限请求不被用户允许。可以提示并退出或者提示权限的用途并重新发起权限申请。
        finish();
    }

    private void initViews() {
        //已经拥有所需权限，可以放心操作任何东西了
        mLoginHandler.sendEmptyMessageDelayed(GO_TO_LOGIN, DELAY_TIME);
    }
}
