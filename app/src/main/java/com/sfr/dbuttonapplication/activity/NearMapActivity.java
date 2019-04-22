package com.sfr.dbuttonapplication.activity;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapView;
import com.sfr.dbuttonapplication.DButtonApplication;
import com.sfr.dbuttonapplication.R;
import com.sfr.dbuttonapplication.activity.alarm.AlarmDetailActivity;
import com.sfr.dbuttonapplication.activity.widget.LoadingProgressDialog;
import com.sfr.dbuttonapplication.entity.AlarmListData;
import com.sfr.dbuttonapplication.entity.ContactData;
import com.sfr.dbuttonapplication.entity.UserData;
import com.sfr.dbuttonapplication.http.HttpAnalyJsonManager;
import com.sfr.dbuttonapplication.http.HttpSendJsonManager;
import com.sfr.dbuttonapplication.utils.APPUtils;
import com.sfr.dbuttonapplication.utils.BaiduLocationUtils;
import com.sfr.dbuttonapplication.utils.MapViewUtil;
import com.sfr.dbuttonapplication.utils.ToastUtils;

import java.util.HashMap;

public class NearMapActivity extends AppCompatActivity {

    private MapView mMapView;
    private BaiduMap mBaiduMap;
    private static final int ALARM_LIST_SUCCESS = 1;
    private static final int ALARM_LIST_FALSE = 2;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case ALARM_LIST_SUCCESS:
                    LoadingProgressDialog.Dissmiss();
                    //刷新地图显示的点
                    break;
                case ALARM_LIST_FALSE:
                    LoadingProgressDialog.Dissmiss();
                    ToastUtils.shortToast(NearMapActivity.this, HttpAnalyJsonManager.lastError);
                    break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        APPUtils.setTranslucent(this);
        setContentView(R.layout.activity_near_map);
        initTitle();
        setView();
    }

    private void setView() {
        mMapView = (MapView) findViewById(R.id.near_map_view);
        mBaiduMap = mMapView.getMap();
        //普通地图
        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
        mBaiduMap.setMyLocationEnabled(true);
        mMapView.showZoomControls(false);
        MapViewUtil.goneMap(mMapView);
        //如果已经有权限了直接初始化，否则再请求一次权限，并且成功的时候触发此方法
        BaiduLocationUtils.initLocation(NearMapActivity.this);
    }

    private TextView mActivityTitle, mTitleExtra;
    private ImageView mTitleBack, mIvUpdate;

    private void initTitle() {
        mActivityTitle = (TextView) findViewById(R.id.title_info);
        mTitleExtra = (TextView) findViewById(R.id.title_extra);
        mTitleBack = (ImageView) findViewById(R.id.title_back_btn);
        mIvUpdate = (ImageView) findViewById(R.id.title_right_btn);
        mIvUpdate.setVisibility(View.VISIBLE);
        mIvUpdate.setBackgroundResource(R.drawable.update_btn_selector);
        mActivityTitle.setText(getResources().getString(R.string.help_map));
        mTitleExtra.setVisibility(View.GONE);
        mTitleBack.setVisibility(View.VISIBLE);
        mTitleBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mIvUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //刷新界面update
                LoadingProgressDialog.show(NearMapActivity.this, false, true, 30000);
                mAlarmListTask = new AlarmListTask();
                mAlarmListTask.execute("");

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        //刷新界面update
        LoadingProgressDialog.show(NearMapActivity.this, false, true, 30000);
        mAlarmListTask = new AlarmListTask();
        mAlarmListTask.execute("");
    }

    private AlarmListTask mAlarmListTask;
    private AlarmListData mAlarmListData;

    private class AlarmListTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {
            mAlarmListData = HttpSendJsonManager.getNearAlarmList(NearMapActivity.this,"100","100");
            if (mAlarmListData.isOK()) {
                DButtonApplication.mAddContact = false;
                mHandler.sendEmptyMessage(ALARM_LIST_SUCCESS);
            } else {
                mHandler.sendEmptyMessage(ALARM_LIST_FALSE);
            }
            return null;
        }
    }
}
