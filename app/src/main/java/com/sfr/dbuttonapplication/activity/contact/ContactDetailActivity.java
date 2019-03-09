package com.sfr.dbuttonapplication.activity.contact;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.sfr.dbuttonapplication.DButtonApplication;
import com.sfr.dbuttonapplication.R;
import com.sfr.dbuttonapplication.activity.widget.ZQImageViewRoundOval;
import com.sfr.dbuttonapplication.entity.UserData;
import com.sfr.dbuttonapplication.http.HttpAnalyJsonManager;
import com.sfr.dbuttonapplication.http.HttpSendJsonManager;
import com.sfr.dbuttonapplication.utils.APPUtils;
import com.sfr.dbuttonapplication.utils.ToastUtils;

public class ContactDetailActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int CONTACT_SET_SUCCESS = 1;
    private static final int CONTACT_SET_FALSE = 2;
    private static final int CONTACT_SEARCH_SUCCESS = 3;
    private static final int CONTACT_SEARCH_FALSE = 4;
    private ZQImageViewRoundOval mIvHead;
    private TextView mTvName, mTvPhone, mTvGender, mTvAge, mTvBlood, mTvID;
    private LinearLayout mLLGender, mLLAge, mLLBlood;
    private LinearLayout mLlSetPhoneContact;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case CONTACT_SET_SUCCESS:
                    DButtonApplication.mAddContact = true;
                    mLlSetPhoneContact.setVisibility(View.GONE);
                    break;
                case CONTACT_SET_FALSE:
                    ToastUtils.shortToast(ContactDetailActivity.this, HttpAnalyJsonManager.lastError);
                    break;
                case CONTACT_SEARCH_SUCCESS:
                    //{"id":"ff808081670fe9610167112476450017","username":null,"name":"ww","phone":"15989380185","gender":"1","age":"0","blood":"b","img":null}
                    mTvName.setText(mUserData.getName());
                    mTvPhone.setText(mUserData.getPhone());
                    mLLGender.setVisibility(View.VISIBLE);
                    mLLAge.setVisibility(View.VISIBLE);
                    mLLBlood.setVisibility(View.VISIBLE);
                    if (mUserData.getGender().equals("1")) {
                        mTvGender.setText(R.string.sex_man);
                    } else if (mUserData.getGender().equals("2")) {
                        mTvGender.setText(R.string.sex_woman);
                    }
                    mTvAge.setText(mUserData.getAge());
                    mTvBlood.setText(mUserData.getBlood());
                    mTvID.setText(mUserData.getId());
                    if(!TextUtils.isEmpty(mUserData.getImg())){
                        ImageLoader.getInstance().displayImage(mUserData.getImg(),mIvHead);
                    }
                    break;
                case CONTACT_SEARCH_FALSE:
                    if (HttpAnalyJsonManager.lastError.equals("不存在")||HttpAnalyJsonManager.lastError.equals("手机号输入格式有误")) {
                        mTvName.setText(mName);
                        mTvPhone.setText(mPhone);
                        mLLGender.setVisibility(View.GONE);
                        mLLAge.setVisibility(View.GONE);
                        mLLBlood.setVisibility(View.GONE);
                    } else {
                        ToastUtils.shortToast(ContactDetailActivity.this, HttpAnalyJsonManager.lastError);
                    }
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        APPUtils.setTranslucent(this);
        setContentView(R.layout.activity_contact_detail);
        initTitle();
        setView();
        setListener();
    }

    private String mName = "";
    private String mIsUrgent = "";

    private void setView() {
        mID = getIntent().getStringExtra("id");
        mName = getIntent().getStringExtra("name");
        mPhone = getIntent().getStringExtra("phone");
        mIsUrgent = getIntent().getStringExtra("isUrgent");
        mContactSearchTask = new ContactSearchTask();
        mContactSearchTask.execute("");
        mIvHead = (ZQImageViewRoundOval) findViewById(R.id.iv_input_your_head);
        mIvHead.setType(ZQImageViewRoundOval.TYPE_ROUND);
        mIvHead.setRoundRadius(20);//矩形凹行大小
        mTvName = (TextView) findViewById(R.id.tv_input_your_username);
        mTvPhone = (TextView) findViewById(R.id.tv_user_data_phone_number);
        mTvGender = (TextView) findViewById(R.id.tv_user_data_sex);
        mTvAge = (TextView) findViewById(R.id.tv_user_data_age);
        mTvBlood = (TextView) findViewById(R.id.tv_user_data_blood);
        mTvID = (TextView) findViewById(R.id.tv_user_data_id);
        mLLGender = (LinearLayout) findViewById(R.id.ll_user_data_sex);
        mLLAge = (LinearLayout) findViewById(R.id.ll_user_data_age);
        mLLBlood = (LinearLayout) findViewById(R.id.ll_user_data_blood);
        mLlSetPhoneContact = (LinearLayout) findViewById(R.id.btn_contact_detail_sucess);
        if(mIsUrgent.equals("1")){
            mLlSetPhoneContact.setVisibility(View.GONE);
        }else{
            mLlSetPhoneContact.setVisibility(View.VISIBLE);
        }
    }

    private void setListener() {
        mLlSetPhoneContact.setOnClickListener(this);
    }

    private TextView mActivityTitle, mTitleExtra;
    private ImageView mTitleBack;

    private void initTitle() {
        mActivityTitle = (TextView) findViewById(R.id.title_info);
        mTitleExtra = (TextView) findViewById(R.id.title_extra);
        mTitleBack = (ImageView) findViewById(R.id.title_back_btn);
        mActivityTitle.setText(getResources().getString(R.string.contact_data));
        mTitleExtra.setVisibility(View.GONE);
        mTitleBack.setVisibility(View.VISIBLE);
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
            case R.id.btn_contact_detail_sucess:
                mContactSetTask = new ContactSetTask();
                mContactSetTask.execute("");
                break;
        }
    }

    private String mID = "";
    private ContactSetTask mContactSetTask;

    private class ContactSetTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {
            if (HttpSendJsonManager.contactSet(ContactDetailActivity.this, mID)) {
                mHandler.sendEmptyMessage(CONTACT_SET_SUCCESS);
            } else {
                mHandler.sendEmptyMessage(CONTACT_SET_FALSE);
            }
            return null;
        }
    }

    private String mPhone = "";
    private ContactSearchTask mContactSearchTask;
    private UserData mUserData;

    private class ContactSearchTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {
            mUserData = HttpSendJsonManager.searchContact(ContactDetailActivity.this, mPhone);
            if (mUserData.isOK()) {
                mHandler.sendEmptyMessage(CONTACT_SEARCH_SUCCESS);
            } else {
                mHandler.sendEmptyMessage(CONTACT_SEARCH_FALSE);
            }
            return null;
        }
    }
}
