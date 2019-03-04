package com.sfr.dbuttonapplication.activity.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.sfr.dbuttonapplication.DButtonApplication;
import com.sfr.dbuttonapplication.R;
import com.sfr.dbuttonapplication.activity.EditUserDataActivity;
import com.sfr.dbuttonapplication.activity.MyDButtonActivity;
import com.sfr.dbuttonapplication.activity.VersionUpdateActivity;
import com.sfr.dbuttonapplication.activity.alarm.MyAlarmListActivity;
import com.sfr.dbuttonapplication.activity.login.LoginActivity;
import com.sfr.dbuttonapplication.activity.widget.XCRoundRectImageView;
import com.sfr.dbuttonapplication.activity.widget.ZQImageViewRoundOval;
import com.sfr.dbuttonapplication.utils.SettingSharedPerferencesUtil;


public class MyFragment extends Fragment implements View.OnClickListener{
    private LinearLayout mBtnEditData;
    private RelativeLayout mRLMyAlarmList,mRLOTAUpdate,mRLDButton,mRLCallUs;
    private TextView mTvID, mTvSex, mTvAge, mTvBlood, mTvPhoneNumber;
    private ZQImageViewRoundOval mIvHead;
    private RelativeLayout mRLLogoutAccount;
    private LinearLayout mLlContactUs;
    //DButtonApplication.mUserData = mUserData;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.my_fragment, null);
        initTitle(view);
        setView(view);
        setListener();
        return view;
    }

    private void setListener() {
        mBtnEditData.setOnClickListener(this);
        mRLMyAlarmList.setOnClickListener(this);
        mRLOTAUpdate.setOnClickListener(this);
        mRLDButton.setOnClickListener(this);
        mRLLogoutAccount.setOnClickListener(this);
        mLlContactUs.setOnClickListener(this);
        mRLCallUs.setOnClickListener(this);
    }

    private TextView mActivityTitle,mTitleExtra;
    private void initTitle(View view) {
        mActivityTitle = (TextView) view.findViewById(R.id.title_info);
        mTitleExtra = (TextView) view.findViewById(R.id.title_extra);
        mActivityTitle.setText(getResources().getString(R.string.person_center));
        mTitleExtra.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_edit_user_data:
                Intent intent = new Intent(getActivity(), EditUserDataActivity.class);
                startActivity(intent);
                break;
            case R.id.rl_my_alarm_list:
                intent = new Intent(getActivity(), MyAlarmListActivity.class);
                startActivity(intent);
                break;
            case R.id.rl_ota_update:
                intent = new Intent(getActivity(), VersionUpdateActivity.class);
                startActivity(intent);
                break;
            case R.id.rl_my_dbutton:
//                intent = new Intent(getActivity(), BindDButtonActivity.class);
//                startActivity(intent);
                intent = new Intent(getActivity(), MyDButtonActivity.class);
                startActivity(intent);
                break;
            case R.id.rl_logout_account:
                SettingSharedPerferencesUtil.SetLoginTokenValue(getActivity(),"");
                intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
                getActivity().finish();
                break;
            case R.id.ll_contact_us:
                callPhone("0755-12345678");
                break;
            case R.id.rl_about_us:
                callPhone("0755-12345678");
                break;
        }

    }

    public void setView(View view) {
        mBtnEditData = (LinearLayout)view.findViewById(R.id.btn_edit_user_data);
        mRLMyAlarmList = (RelativeLayout)view.findViewById(R.id.rl_my_alarm_list);
        mRLOTAUpdate = (RelativeLayout)view.findViewById(R.id.rl_ota_update);
        mRLDButton = (RelativeLayout)view.findViewById(R.id.rl_my_dbutton);
        mRLLogoutAccount = (RelativeLayout)view.findViewById(R.id.rl_logout_account);
        mRLCallUs = (RelativeLayout)view.findViewById(R.id.rl_about_us);
        mLlContactUs = (LinearLayout) view.findViewById(R.id.ll_contact_us);

        mTvID = (TextView) view.findViewById(R.id.tv_user_data_id);
        mTvSex = (TextView) view.findViewById(R.id.tv_user_data_sex);
        mTvAge = (TextView) view.findViewById(R.id.tv_user_data_age);
        mTvBlood = (TextView) view.findViewById(R.id.tv_user_data_blood);
        mTvPhoneNumber = (TextView) view.findViewById(R.id.tv_user_data_phone_number);
        mIvHead = (ZQImageViewRoundOval) view.findViewById(R.id.iv_input_your_head);
        mIvHead.setType(ZQImageViewRoundOval.TYPE_ROUND);
        mIvHead.setRoundRadius(20);//矩形凹行大小

        //刷新界面
        if(DButtonApplication.mUserData!=null) {
            mTvID.setText(DButtonApplication.mUserData.getName());
            if(TextUtils.isEmpty(DButtonApplication.mUserData.getGender())){
                mTvSex.setText(R.string.sex_man);
            }else {
                if (DButtonApplication.mUserData.getGender().equals("1")) {
                    mTvSex.setText(R.string.sex_man);
                } else {
                    mTvSex.setText(R.string.sex_woman);
                }
            }
            mTvAge.setText(DButtonApplication.mUserData.getAge());
            mTvBlood.setText(DButtonApplication.mUserData.getBlood());
            mTvPhoneNumber.setText(DButtonApplication.mUserData.getPhone());
            //mIvHead url赋值
            if(!TextUtils.isEmpty(DButtonApplication.mUserData.getImg())){
                ImageLoader.getInstance().displayImage(DButtonApplication.mUserData.getImg(),mIvHead);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        //刷新界面
        if(DButtonApplication.mUserData!=null) {
            mTvID.setText(DButtonApplication.mUserData.getName());
            if(TextUtils.isEmpty(DButtonApplication.mUserData.getGender())){
                mTvSex.setText(R.string.sex_man);
            }else {
                if (DButtonApplication.mUserData.getGender().equals("1")) {
                    mTvSex.setText(R.string.sex_man);
                } else {
                    mTvSex.setText(R.string.sex_woman);
                }
            }
            mTvAge.setText(DButtonApplication.mUserData.getAge());
            mTvBlood.setText(DButtonApplication.mUserData.getBlood());
            mTvPhoneNumber.setText(DButtonApplication.mUserData.getPhone());
            //mIvHead url赋值
            if(!TextUtils.isEmpty(DButtonApplication.mUserData.getImg())){
                ImageLoader.getInstance().displayImage(DButtonApplication.mUserData.getImg(),mIvHead);
            }
        }
    }
    public void callPhone(String phoneNum) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        Uri data = Uri.parse("tel:" + phoneNum);
        intent.setData(data);
        startActivity(intent);
    }
}
