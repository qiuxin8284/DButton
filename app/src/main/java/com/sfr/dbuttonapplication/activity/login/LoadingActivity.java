package com.sfr.dbuttonapplication.activity.login;

import android.Manifest;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Window;

import com.sfr.dbuttonapplication.DButtonApplication;
import com.sfr.dbuttonapplication.R;
import com.sfr.dbuttonapplication.activity.MainActivity;
import com.sfr.dbuttonapplication.entity.UserData;
import com.sfr.dbuttonapplication.http.HttpAnalyJsonManager;
import com.sfr.dbuttonapplication.http.HttpSendJsonManager;
import com.sfr.dbuttonapplication.utils.APPUtils;
import com.sfr.dbuttonapplication.utils.LogUtil;
import com.sfr.dbuttonapplication.utils.SettingSharedPerferencesUtil;
import com.sfr.dbuttonapplication.utils.ToastUtils;

public class LoadingActivity extends AppCompatActivity {

    private static final int GO_TO_LOGIN = 0;
    private static final int DELAY_TIME = 3000;
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
                    DButtonApplication.mInstance.startScanDevice();
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
        APPUtils.setTranslucent(this);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_loading);
        if (Build.VERSION.SDK_INT >= 23) {
            String[] mPermissionList = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.CALL_PHONE,
                    Manifest.permission.READ_LOGS, Manifest.permission.READ_PHONE_STATE,
                    Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.SET_DEBUG_APP,
                    Manifest.permission.SYSTEM_ALERT_WINDOW, Manifest.permission.GET_ACCOUNTS,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.DISABLE_KEYGUARD,
                    Manifest.permission.RECEIVE_BOOT_COMPLETED,
                    Manifest.permission.READ_CONTACTS,
                    Manifest.permission.WRITE_APN_SETTINGS};
            ActivityCompat.requestPermissions(this, mPermissionList, 123);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        mLoginHandler.sendEmptyMessageDelayed(GO_TO_LOGIN, DELAY_TIME);
    }


    private void login() {
        String token = SettingSharedPerferencesUtil.GetLoginTokenValue(LoadingActivity.this);
        if(TextUtils.isEmpty(token)) {
            Intent intent = new Intent(LoadingActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }else{
            //尝试自动登陆
            DButtonApplication.USER_TOKEN = token;
            mLoginTask = new LoginTask();
            mLoginTask.execute("");
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
}
