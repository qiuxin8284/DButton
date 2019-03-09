package com.sfr.dbuttonapplication.activity.login;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
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

    private TextView mTvCancel,mTvAccpet;
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
        mActivityTitle.setText(getResources().getString(R.string.register_hint_protect));
        mTitleExtra.setVisibility(View.GONE);
        mTitleBack.setVisibility(View.GONE);
    }

    private void setView() {
        mTvAccpet = (TextView)findViewById(R.id.tv_common_accpet);
        mTvCancel = (TextView)findViewById(R.id.tv_common_cancel);
    }

    private void setListener() {
        mTvAccpet.setOnClickListener(this);
        mTvCancel.setOnClickListener(this);
    }

    private TextView mActivityTitle, mTitleExtra, mTitleBack;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_common_accpet:
                Intent intent = new Intent(RegisterActivity.this,RegisterDataActivity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.tv_common_cancel:
                finish();
                break;
        }
    }

}
