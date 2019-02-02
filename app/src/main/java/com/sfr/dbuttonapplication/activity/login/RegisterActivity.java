package com.sfr.dbuttonapplication.activity.login;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.sfr.dbuttonapplication.R;
import com.sfr.dbuttonapplication.http.HttpAnalyJsonManager;
import com.sfr.dbuttonapplication.http.HttpSendJsonManager;
import com.sfr.dbuttonapplication.utils.APPUtils;
import com.sfr.dbuttonapplication.utils.ToastUtils;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private String mPhone, mSmsCode;
    private EditText mEtPhone, mEtSmsCode;
    private Button mBtnSendSms;
    private static final int SEND_SMS_SUCCESS = 1;
    private static final int SEND_SMS_FALSE = 2;
    private static final int SEND_SMS_TIME = 3;
    private int time = 60;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SEND_SMS_SUCCESS:
                    mBtnSendSms.setBackgroundResource(R.mipmap.btn_sms_code_no);
                    mBtnSendSms.setClickable(false);
                    time = 60;
                    mBtnSendSms.setText("再次获取"+time+"s");
                    sendEmptyMessageDelayed(SEND_SMS_TIME,1000);
                    break;
                case SEND_SMS_FALSE:
                    ToastUtils.shortToast(RegisterActivity.this, HttpAnalyJsonManager.lastError);
                    break;
                case SEND_SMS_TIME:
                    time = time - 1;
                    if(time == 0){
                        mBtnSendSms.setText(R.string.get_sms_code);
                        mBtnSendSms.setBackgroundResource(R.drawable.send_sms_btn_selector);
                        mBtnSendSms.setClickable(true);
                    }else{
                        mBtnSendSms.setText("再次获取"+time+"s");
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
        setContentView(R.layout.activity_register);
        initTitle();
        setView();
        setListener();
    }

    private void initTitle() {
        mActivityTitle = (TextView) findViewById(R.id.title_info);
        mTitleExtra = (TextView) findViewById(R.id.title_extra);
        mTitleBack = (TextView) findViewById(R.id.title_back);
        mActivityTitle.setText(getResources().getString(R.string.new_user_register));
        mTitleExtra.setVisibility(View.VISIBLE);
        mTitleBack.setVisibility(View.VISIBLE);
        mTitleExtra.setText(getResources().getString(R.string.go_next));
        mTitleBack.setText(getResources().getString(R.string.go_up));
        mTitleExtra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPhone = mEtPhone.getText().toString();
                mSmsCode = mEtSmsCode.getText().toString();
                if (TextUtils.isEmpty(mPhone) || TextUtils.isEmpty(mPhone)) {
                    Toast.makeText(RegisterActivity.this, R.string.phone_empty, Toast.LENGTH_LONG).show();
                    return;
                } else if (mPhone.length() != 11) {
                    Toast.makeText(RegisterActivity.this, R.string.phone_no_right, Toast.LENGTH_LONG).show();
                    return;
                } else if (TextUtils.isEmpty(mSmsCode) || TextUtils.isEmpty(mSmsCode)) {
                    Toast.makeText(RegisterActivity.this, R.string.code_empty, Toast.LENGTH_LONG).show();
                    return;
                } else if (mPhone.length() != 11) {
                    Toast.makeText(RegisterActivity.this, R.string.code_no_right, Toast.LENGTH_LONG).show();
                    return;
                } else {
                    Intent intent = new Intent(RegisterActivity.this, RegisterDataActivity.class);
                    intent.putExtra("phone",mPhone);
                    intent.putExtra("code",mSmsCode);
                    startActivity(intent);
                    finish();
                }
            }
        });
        mTitleBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void setView() {
        mEtPhone = (EditText) findViewById(R.id.et_account_number);
        mEtSmsCode = (EditText) findViewById(R.id.et_account_sms_code);
        mBtnSendSms = (Button) findViewById(R.id.btn_send_sms);
    }

    private void setListener() {
        mBtnSendSms.setOnClickListener(this);
    }

    private TextView mActivityTitle, mTitleExtra, mTitleBack;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
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
        }
    }

    private SendSMSTask mSendSMSTask;

    private class SendSMSTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {
            if (HttpSendJsonManager.sendSMS(RegisterActivity.this, mPhone, HttpSendJsonManager.SEND_SMS_TYPE_REGISTER)) {
                mHandler.sendEmptyMessage(SEND_SMS_SUCCESS);
            } else {
                mHandler.sendEmptyMessage(SEND_SMS_FALSE);
            }
            return null;
        }
    }
}
