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
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.sfr.dbuttonapplication.DButtonApplication;
import com.sfr.dbuttonapplication.R;
import com.sfr.dbuttonapplication.activity.BindDButtonActivity;
import com.sfr.dbuttonapplication.activity.widget.LoadingProgressDialog;
import com.sfr.dbuttonapplication.entity.UserData;
import com.sfr.dbuttonapplication.http.HttpSendJsonManager;
import com.sfr.dbuttonapplication.utils.APPUtils;

public class RegisterSucessActivity extends AppCompatActivity implements View.OnClickListener {

    private Button mBtnRegisterSucess;
    private static final int USER_INFO_SUCCESS = 1;
    private static final int USER_INFO_FALSE = 2;
    private TextView mTvID, mTvSex, mTvAge, mTvBlood, mTvPhoneNumber;
    private ImageView mIvHead;
    private Button mBtnBindDButton;//btn_bind_dbutton
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case USER_INFO_SUCCESS:
                    LoadingProgressDialog.Dissmiss();
                    //刷新界面
                    if(mUserData!=null) {
                        mTvID.setText(mUserData.getName());
                        if(mUserData.getGender().equals("1")) {
                            mTvSex.setText(R.string.sex_man);
                        }else{
                            mTvSex.setText(R.string.sex_woman);
                        }
                        mTvAge.setText(mUserData.getAge());
                        mTvBlood.setText(mUserData.getBlood());
                        mTvPhoneNumber.setText(mUserData.getPhone());
                        //mIvHead url赋值
                        if(!TextUtils.isEmpty(mUserData.getImg())){
                            ImageLoader.getInstance().displayImage(mUserData.getImg(),mIvHead);
                        }
                    }
                    break;
                case USER_INFO_FALSE:
                    //重新获取
                    mUserInfoTask = new UserInfoTask();
                    mUserInfoTask.execute("");
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        APPUtils.setTranslucent(this);
        setContentView(R.layout.activity_register_sucess);
        initTitle();
        setView();
        setListener();
        LoadingProgressDialog.show(RegisterSucessActivity.this, false, true, 30000);
        mUserInfoTask = new UserInfoTask();
        mUserInfoTask.execute("");
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
        mBtnRegisterSucess = (Button) findViewById(R.id.btn_register_sucess);
        mTvID = (TextView) findViewById(R.id.tv_user_data_id);
        mTvSex = (TextView) findViewById(R.id.tv_user_data_sex);
        mTvAge = (TextView) findViewById(R.id.tv_user_data_age);
        mTvBlood = (TextView) findViewById(R.id.tv_user_data_blood);
        mTvPhoneNumber = (TextView) findViewById(R.id.tv_user_data_phone_number);
        mIvHead = (ImageView) findViewById(R.id.iv_input_your_head);
        mBtnBindDButton = (Button) findViewById(R.id.btn_bind_dbutton);
    }

    private void setListener() {
        mBtnRegisterSucess.setOnClickListener(this);
        mBtnBindDButton.setOnClickListener(this);
    }

    private TextView mActivityTitle, mTitleExtra, mTitleBack;

    private void initTitle() {
        mActivityTitle = (TextView) findViewById(R.id.title_info);
        mTitleExtra = (TextView) findViewById(R.id.title_extra);
        mTitleBack = (TextView) findViewById(R.id.title_back);
        mActivityTitle.setText(getResources().getString(R.string.new_user_register));
        mTitleExtra.setVisibility(View.GONE);
        mTitleBack.setVisibility(View.GONE);
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
            case R.id.btn_register_sucess:
                finish();
                break;
            case R.id.btn_bind_dbutton:
                Intent intent = new Intent(RegisterSucessActivity.this, BindDButtonActivity.class);
                startActivity(intent);
                break;
        }
    }

    private UserInfoTask mUserInfoTask;
    private UserData mUserData;

    private class UserInfoTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {
            mUserData = new UserData();
            mUserData = HttpSendJsonManager.userInfo(RegisterSucessActivity.this);
            DButtonApplication.mUserData = mUserData;
            if (mUserData.isOK()) {
                mHandler.sendEmptyMessage(USER_INFO_SUCCESS);
            } else {
                mHandler.sendEmptyMessageDelayed(USER_INFO_FALSE, 10000);
            }
            return null;
        }
    }
}
