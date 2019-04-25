package com.sfr.dbuttonapplication.activity.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.sfr.dbuttonapplication.DButtonApplication;
import com.sfr.dbuttonapplication.R;
import com.sfr.dbuttonapplication.activity.NearMapActivity;
import com.sfr.dbuttonapplication.activity.adapter.AlarmListAdapter;
import com.sfr.dbuttonapplication.activity.alarm.AlarmDetailActivity;
import com.sfr.dbuttonapplication.activity.widget.LoadListView;
import com.sfr.dbuttonapplication.activity.widget.LoadingProgressDialog;
import com.sfr.dbuttonapplication.entity.AlarmListData;
import com.sfr.dbuttonapplication.entity.AlarmResultData;
import com.sfr.dbuttonapplication.http.HttpAnalyJsonManager;
import com.sfr.dbuttonapplication.http.HttpSendJsonManager;
import com.sfr.dbuttonapplication.utils.BaiduLocationUtils;
import com.sfr.dbuttonapplication.utils.LogUtil;
import com.sfr.dbuttonapplication.utils.MapViewUtil;
import com.sfr.dbuttonapplication.utils.SettingSharedPerferencesUtil;
import com.sfr.dbuttonapplication.utils.ToastUtils;

import java.util.ArrayList;


public class NearMapFragment extends Fragment {

    private MapView mMapView;
    private BaiduMap mBaiduMap;
    private static final int ALARM_LIST_SUCCESS = 1;
    private static final int ALARM_LIST_FALSE = 2;
    private LatLng mLatLng;

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
                    ToastUtils.shortToast(getActivity(), HttpAnalyJsonManager.lastError);
                    break;
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);


    }

    @Override
    public void onDestroy() {
        mBaiduMap.setMyLocationEnabled(false);
        mMapView.onDestroy();
        mMapView = null;
        super.onDestroy();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.near_map_fragment, null);
        //getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        //getActivity().getWindow().addFlags( WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        //getActivity().getWindow().setStatusBarColor(Color.TRANSPARENT);
        initTitle(view);
        setView(view);
        setListener();
        return view;
    }

    private TextView mActivityTitle, mTitleExtra;
    private ImageView mTitleBack, mIvUpdate;

    private void initTitle(View view) {
        mActivityTitle = (TextView) view.findViewById(R.id.title_info);
        mTitleExtra = (TextView) view.findViewById(R.id.title_extra);
        mTitleBack = (ImageView) view.findViewById(R.id.title_back_btn);
        mIvUpdate = (ImageView) view.findViewById(R.id.title_right_btn);
        mIvUpdate.setVisibility(View.VISIBLE);
        mIvUpdate.setBackgroundResource(R.drawable.update_btn_selector);
        mActivityTitle.setText(getResources().getString(R.string.menu_near_map));
        mTitleExtra.setVisibility(View.GONE);
        mTitleBack.setVisibility(View.GONE);
        mIvUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLatLng = BaiduLocationUtils.locationMyself(getActivity(),mBaiduMap);
                //刷新界面update
                LoadingProgressDialog.show(getActivity(), false, true, 30000);
                mAlarmListTask = new AlarmListTask();
                mAlarmListTask.execute("");

            }
        });
    }


    public void setView(View view) {
        mMapView = (MapView) view.findViewById(R.id.near_map_view);
        mBaiduMap = mMapView.getMap();
        //普通地图
        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
        mBaiduMap.setMyLocationEnabled(true);
        mMapView.showZoomControls(false);
        MapViewUtil.goneMap(mMapView,3);
        //如果已经有权限了直接初始化，否则再请求一次权限，并且成功的时候触发此方法
        mLatLng = BaiduLocationUtils.locationMyself(getActivity(),mBaiduMap);

    }


    private void setListener() {
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
        mLatLng = BaiduLocationUtils.locationMyself(getActivity(),mBaiduMap);
        //刷新界面update
        LoadingProgressDialog.show(getActivity(), false, true, 30000);
        mAlarmListTask = new AlarmListTask();
        mAlarmListTask.execute("");
    }

    @Override
    public void onPause() {
        mMapView.onPause();
        super.onPause();
    }

    private AlarmListTask mAlarmListTask;
    private AlarmListData mAlarmListData;

    private class AlarmListTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {
            mAlarmListData = HttpSendJsonManager.getNearAlarmList(getActivity(), String.valueOf(mLatLng.longitude), String.valueOf(mLatLng.latitude));
            LogUtil.println("getNearAlarmList mLatLng.longitude" + mLatLng.longitude+"|"+mLatLng.latitude);
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
