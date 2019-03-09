package com.sfr.dbuttonapplication.activity.login;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.sfr.dbuttonapplication.DButtonApplication;
import com.sfr.dbuttonapplication.R;
import com.sfr.dbuttonapplication.activity.MainActivity;
import com.sfr.dbuttonapplication.activity.WebViewActivity;
import com.sfr.dbuttonapplication.activity.widget.LoadingProgressDialog;
import com.sfr.dbuttonapplication.entity.UserData;
import com.sfr.dbuttonapplication.http.HttpAnalyJsonManager;
import com.sfr.dbuttonapplication.http.HttpSendJsonManager;
import com.sfr.dbuttonapplication.utils.APPUtils;
import com.sfr.dbuttonapplication.utils.LogUtil;
import com.sfr.dbuttonapplication.utils.SettingSharedPerferencesUtil;
import com.sfr.dbuttonapplication.utils.ToastUtils;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private Button mBtnLogin;
    private static final int SEND_SMS_SUCCESS = 1;
    private static final int SEND_SMS_FALSE = 2;
    private static final int LOGIN_SUCCESS = 3;
    private static final int LOGIN_FALSE = 4;
    private static final int SEND_SMS_TIME = 5;
    private String mPhone, mSmsCode;
    private EditText mEtPhone, mEtSmsCode;
    private TextView mBtnSendSms,mTvRegister;
    private int time = 60;
    private TextView mTvMoreDetai;
    private View mPhoneNumLine,mSmsCodeLine;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SEND_SMS_SUCCESS:
                    //mBtnSendSms.setBackgroundResource(R.mipmap.btn_sms_code_no);
                    mBtnSendSms.setClickable(false);
                    mBtnSendSms.setTextColor(0XFFAEAEAE);
                    time = 60;
                    mBtnSendSms.setText("验证码已发送..."+time+"s");
                    sendEmptyMessageDelayed(SEND_SMS_TIME,1000);
                    break;
                case SEND_SMS_FALSE:
                    ToastUtils.shortToast(LoginActivity.this, HttpAnalyJsonManager.lastError);
                    break;
                case LOGIN_SUCCESS:
                    LoadingProgressDialog.Dissmiss();
                    DButtonApplication.mNowMac = SettingSharedPerferencesUtil.GetBindDbuttonMACValue(
                            LoginActivity.this, DButtonApplication.mUserData.getPhone());
                    LogUtil.println("DButtonApplication::Login::mNowMac= " + DButtonApplication.mNowMac);
                    if(!TextUtils.isEmpty(DButtonApplication.mNowMac)) {
                        DButtonApplication.mInstance.startScanDevice();
                    }
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                    break;
                case LOGIN_FALSE:
                    LoadingProgressDialog.Dissmiss();
                    ToastUtils.shortToast(LoginActivity.this, HttpAnalyJsonManager.lastError);
//                    intent = new Intent(LoginActivity.this, MainActivity.class);
//                    startActivity(intent);
//                    finish();
                    break;
                case SEND_SMS_TIME:
                    time = time - 1;
                    if(time == 0){
                        mBtnSendSms.setText(R.string.get_sms_code);
                        if (mEtPhone.getText().toString().length() == 11) {
                            mBtnSendSms.setClickable(true);
                            mBtnSendSms.setTextColor(0XFF2CA349);
                        }else{
                            mBtnSendSms.setClickable(false);
                            mBtnSendSms.setTextColor(0XFFAEAEAE);
                        }
                    }else{
                        mBtnSendSms.setText("验证码已发送..."+time+"s");
                        sendEmptyMessageDelayed(SEND_SMS_TIME,1000);
                    }
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        APPUtils.setTranslucent(this);
        setContentView(R.layout.activity_login);
        initTitle();
        setView();
        setLister();
        initAction();
    }

    private void initAction() {
        View statusBar = findViewById(R.id.statusBarView);
        ViewGroup.LayoutParams layoutParams = statusBar.getLayoutParams();
        layoutParams.height = getStatusBarHeight();
    }
    public int getStatusBarHeight() {
        int result = 0; //获取状态栏高度的资源id
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    private void setView() {
        mBtnLogin = (Button) findViewById(R.id.btn_login);
        mEtPhone = (EditText) findViewById(R.id.et_account_number);
        mEtSmsCode = (EditText) findViewById(R.id.et_account_sms_code);
        mBtnSendSms = (TextView) findViewById(R.id.btn_send_sms);
        mTvMoreDetai = (TextView) findViewById(R.id.learn_detail);
        mTvRegister = (TextView) findViewById(R.id.tv_register);
        mTvRegister.setText(getResources().getString(R.string.new_user));
        mTvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
        mPhoneNumLine = (View) findViewById(R.id.phone_num_line);
        mSmsCodeLine = (View) findViewById(R.id.sms_code_line);
    }

    private void setLister() {
        mBtnLogin.setOnClickListener(this);
        mBtnSendSms.setOnClickListener(this);
        mTvMoreDetai.setOnClickListener(this);
        mEtPhone.setOnFocusChangeListener(new android.view.View.
                OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    // 此处为得到焦点时的处理内容
                    mPhoneNumLine.setBackgroundResource(R.color.line_two);
                } else {
                    // 此处为失去焦点时的处理内容
                    mPhoneNumLine.setBackgroundResource(R.color.line_three);
                }
            }
        });
        mEtSmsCode.setOnFocusChangeListener(new android.view.View.
                OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    // 此处为得到焦点时的处理内容
                    mSmsCodeLine.setBackgroundResource(R.color.line_two);
                } else {
                    // 此处为失去焦点时的处理内容
                    mSmsCodeLine.setBackgroundResource(R.color.line_three);
                }
            }
        });
        mEtPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (mEtPhone.getText().toString().length() == 11) {
                    mBtnSendSms.setClickable(true);
                    mBtnSendSms.setTextColor(0XFF2CA349);
                }else{
                    mBtnSendSms.setClickable(false);
                    mBtnSendSms.setTextColor(0XFFAEAEAE);
                }
                if (mEtPhone.getText().toString().length() == 11&&mEtSmsCode.getText().toString().length() == 6) {
                    mBtnLogin.setClickable(true);
                    mBtnLogin.setBackgroundResource(R.drawable.login_btn_selector);
                }else{
                    mBtnLogin.setClickable(false);
                    mBtnLogin.setBackgroundResource(R.mipmap.btn_login_none);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mEtSmsCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (mEtPhone.getText().toString().length() == 11&&mEtSmsCode.getText().toString().length() == 6) {
                    mBtnLogin.setClickable(true);
                    mBtnLogin.setBackgroundResource(R.drawable.login_btn_selector);
                }else{
                    mBtnLogin.setClickable(false);
                    mBtnLogin.setBackgroundResource(R.mipmap.btn_login_none);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login:
                mPhone = mEtPhone.getText().toString();
                mSmsCode = mEtSmsCode.getText().toString();
                if (TextUtils.isEmpty(mPhone) || TextUtils.isEmpty(mPhone)) {
                    Toast.makeText(LoginActivity.this, R.string.phone_empty, Toast.LENGTH_LONG).show();
                    return;
                } else if (mPhone.length() != 11) {
                    Toast.makeText(LoginActivity.this, R.string.phone_no_right, Toast.LENGTH_LONG).show();
                    return;
                }  else if (TextUtils.isEmpty(mSmsCode) || TextUtils.isEmpty(mSmsCode)) {
                    Toast.makeText(LoginActivity.this, R.string.code_empty, Toast.LENGTH_LONG).show();
                    return;
                } else if (mSmsCode.length() != 6) {
                    Toast.makeText(LoginActivity.this, R.string.code_no_right, Toast.LENGTH_LONG).show();
                    return;
                } else {
                    LoadingProgressDialog.show(LoginActivity.this, false, true, 30000);
                    mLoginTask = new LoginTask();
                    mLoginTask.execute("");
                }
                break;
            case R.id.btn_send_sms:
                mPhone = mEtPhone.getText().toString();
                if (TextUtils.isEmpty(mPhone) || TextUtils.isEmpty(mPhone)) {
                    Toast.makeText(this, R.string.phone_empty, Toast.LENGTH_LONG).show();
                    return;
                } else if (mPhone.length() != 11) {
                    Toast.makeText(this, R.string.phone_no_right, Toast.LENGTH_LONG).show();
                    return;
                } else {
                    //计时置灰色不可点击
                    //除非传递false
                    mSendSMSTask = new SendSMSTask();
                    mSendSMSTask.execute("");
                }
                break;
            case R.id.learn_detail:
                Intent intent = new Intent(LoginActivity.this, WebViewActivity.class);
                intent.putExtra("url","http://www.baidu.com");
                startActivity(intent);
                break;
        }
    }

    private TextView mActivityTitle, mTitleExtra;

    private void initTitle() {
        mActivityTitle = (TextView) findViewById(R.id.title_info);
        mTitleExtra = (TextView) findViewById(R.id.title_extra);
        mActivityTitle.setText(getResources().getString(R.string.login));
        mTitleExtra.setVisibility(View.GONE);
    }
    private SendSMSTask mSendSMSTask;

    private class SendSMSTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {
            if (HttpSendJsonManager.sendSMS(LoginActivity.this, mPhone, HttpSendJsonManager.SEND_SMS_TYPE_LOGIN)) {
                mHandler.sendEmptyMessage(SEND_SMS_SUCCESS);
            } else {
                mHandler.sendEmptyMessage(SEND_SMS_FALSE);
            }
            return null;
        }
    }

    private LoginTask mLoginTask;
    private UserData mUserData;

    private class LoginTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {
            mUserData = HttpSendJsonManager.login(LoginActivity.this, mPhone, mSmsCode);
            LogUtil.println("login mUserData:" + mUserData.toString());
            DButtonApplication.mUserData = mUserData;
            if (mUserData.isOK()) {
                mHandler.sendEmptyMessage(LOGIN_SUCCESS);
            } else {
                mHandler.sendEmptyMessage(LOGIN_FALSE);
            }
            return null;
        }
    }
}
