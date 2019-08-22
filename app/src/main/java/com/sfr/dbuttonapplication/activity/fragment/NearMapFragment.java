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
import android.view.Gravity;
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
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.GroundOverlayOptions;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import com.sfr.dbuttonapplication.DButtonApplication;
import com.sfr.dbuttonapplication.R;
import com.sfr.dbuttonapplication.activity.NearMapActivity;
import com.sfr.dbuttonapplication.activity.adapter.AlarmListAdapter;
import com.sfr.dbuttonapplication.activity.alarm.AlarmDetailActivity;
import com.sfr.dbuttonapplication.activity.widget.LoadListView;
import com.sfr.dbuttonapplication.activity.widget.LoadingProgressDialog;
import com.sfr.dbuttonapplication.entity.AlarmData;
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
                    LogUtil.println("getNearAlarmList ALARM_LIST_SUCCESS 1");
                    //刷新地图显示的点
                    //定义Ground的显示地理范围
//                    LatLngBounds.Builder builder = new LatLngBounds.Builder();
//                    for (int i = 0; i < mAlarmListData.getAlarmDataArrayList().size(); i++) {
//                        AlarmResultData alarmResultData = mAlarmListData.getAlarmDataArrayList().get(i);
//                        LatLng latLng = new LatLng(Double.valueOf(alarmResultData.getAlarmData().getLatitude())
//                                , (Double.valueOf(alarmResultData.getAlarmData().getLongitude())));
//                        builder.include(latLng);
//                    }
//                    LatLngBounds bounds = builder.build();
//
//                    //定义Ground显示的图片
//                    BitmapDescriptor bdGround = BitmapDescriptorFactory.fromResource(R.mipmap.img_head);
//                    //定义GroundOverlayOptions对象
//                    OverlayOptions ooGround = new GroundOverlayOptions()
//                            .positionFromBounds(bounds)
//                            .image(bdGround)
//                            .transparency(0.8f); //覆盖物透明度
//
//                    //在地图中添加Ground覆盖物
//                    mBaiduMap.addOverlay(ooGround);

                    LogUtil.println("getNearAlarmList ALARM_LIST_SUCCESS 2");
                    Marker marker;
                    //定义Maker坐标点
                    for (int i = 0; i < mAlarmListData.getAlarmDataArrayList().size(); i++) {
                        AlarmResultData alarmResultData = mAlarmListData.getAlarmDataArrayList().get(i);
                        LatLng latLng = new LatLng(Double.valueOf(alarmResultData.getAlarmData().getLatitude())
                                , (Double.valueOf(alarmResultData.getAlarmData().getLongitude())));
                        //构建Marker图标
                        BitmapDescriptor bitmap = BitmapDescriptorFactory
                                .fromResource(R.mipmap.sex_man);
                        //构建MarkerOption，用于在地图上添加Marker
                        OverlayOptions option = new MarkerOptions()
                                .position(latLng)
                                .icon(bitmap);
                        //添加marker
                        marker = (Marker) mBaiduMap.addOverlay(option);
                        //使用marker携带info信息，当点击事件的时候可以通过marker获得info信息
                        Bundle bundle = new Bundle();
                        //info必须实现序列化接口
                        bundle.putSerializable("info", alarmResultData);
                        marker.setExtraInfo(bundle);
                    }
                    LogUtil.println("getNearAlarmList ALARM_LIST_SUCCESS 3");
                    //添加marker点击事件的监听
                    mBaiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
                        @Override
                        public boolean onMarkerClick(Marker marker) {
                            LogUtil.println("getNearAlarmList onMarkerClick");
                            //从marker中获取info信息
                            Bundle bundle = marker.getExtraInfo();
                            AlarmResultData alarmResultData = (AlarmResultData) bundle.getSerializable("info");
                            Intent intent = new Intent(getActivity(), AlarmDetailActivity.class);
                            intent.putExtra("id",alarmResultData.getAlarmData().getId());
                            intent.putExtra("name",alarmResultData.getAlarmData().getVipName());
                            intent.putExtra("image",alarmResultData.getAlarmData().getVipImg());
                            startActivity(intent);
                            LogUtil.println("getNearAlarmList onMarkerClick marker alarmResultData：" + alarmResultData.toString());
                            return true;
                        }
                    });

                    LogUtil.println("getNearAlarmList ALARM_LIST_SUCCESS 4");
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
                mLatLng = BaiduLocationUtils.locationMyself(getActivity(), mBaiduMap);
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
        MapViewUtil.goneMap(mMapView, 3);
        //如果已经有权限了直接初始化，否则再请求一次权限，并且成功的时候触发此方法
        mLatLng = BaiduLocationUtils.locationMyself(getActivity(), mBaiduMap);

    }


    private void setListener() {
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
        mLatLng = BaiduLocationUtils.locationMyself(getActivity(), mBaiduMap);
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
            mAlarmListData = HttpSendJsonManager.getNearAlarmList(getActivity(), "114.11908400", "22.59484800");
            //mAlarmListData = HttpSendJsonManager.getNearAlarmList(getActivity(), String.valueOf(mLatLng.longitude)+"00", String.valueOf(mLatLng.latitude)+"00");
            //LogUtil.println("getNearAlarmList mLatLng.longitude:" + mLatLng.longitude+"00"+"|latitude:"+mLatLng.latitude+"00");
            if (mAlarmListData.isOK()) {
                DButtonApplication.mAddContact = false;
                LogUtil.println("getNearAlarmList ALARM_LIST_SUCCESS");
                mHandler.sendEmptyMessage(ALARM_LIST_SUCCESS);
            } else {
                mHandler.sendEmptyMessage(ALARM_LIST_FALSE);
            }
            return null;
        }
    }
}
