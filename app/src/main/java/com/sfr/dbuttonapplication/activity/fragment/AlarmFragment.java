package com.sfr.dbuttonapplication.activity.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.sfr.dbuttonapplication.DButtonApplication;
import com.sfr.dbuttonapplication.R;
import com.sfr.dbuttonapplication.activity.adapter.AlarmListAdapter;
import com.sfr.dbuttonapplication.activity.alarm.AlarmDetailActivity;
import com.sfr.dbuttonapplication.activity.contact.AddPhoneContactActivity;
import com.sfr.dbuttonapplication.activity.login.LoginActivity;
import com.sfr.dbuttonapplication.activity.widget.LoadListView;
import com.sfr.dbuttonapplication.entity.AlarmListData;
import com.sfr.dbuttonapplication.entity.AlarmResultData;
import com.sfr.dbuttonapplication.http.HttpAnalyJsonManager;
import com.sfr.dbuttonapplication.http.HttpSendJsonManager;
import com.sfr.dbuttonapplication.utils.LogUtil;
import com.sfr.dbuttonapplication.utils.SettingSharedPerferencesUtil;
import com.sfr.dbuttonapplication.utils.ToastUtils;

import java.util.ArrayList;


public class AlarmFragment extends Fragment implements LoadListView.ILoadListener2 {
    private LoadListView mLvAlarm;
    private AlarmListAdapter mAlarmListAdapter;
    private ArrayList<AlarmResultData> mAlarmList;
    private TextView mTvEmptyHint;
    private static final int ALARM_LIST_SUCCESS = 1;
    private static final int ALARM_LIST_UPDATE_SUCCESS = 4;
    private static final int ALARM_LIST_FALSE = 2;
    private static final int FOR_ALARM_LIST = 3;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case ALARM_LIST_UPDATE_SUCCESS:
                    if(mAlarmListData!=null) {
                        //DButtonApplication.mAddAlarm = false;
                        mAlarmList.addAll(mAlarmListData.getAlarmDataArrayList());
                        if (mAlarmList.size() == 0) {
                            mTvEmptyHint.setVisibility(View.VISIBLE);
                        } else {
                            mTvEmptyHint.setVisibility(View.GONE);
                        }
                        mAlarmListAdapter.setServiceList(mAlarmList);
                    }
                    /**
                     * 设置默认显示为Listview最后一行
                     */
//                    mLvAlarm.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
//                    mLvAlarm.setStackFromBottom(true);
                    //通知listView加载完毕，底部布局消失
                    mLvAlarm.loadComplete();
                    break;
                case ALARM_LIST_SUCCESS:
                    if(mAlarmListData!=null) {
                        //DButtonApplication.mAddAlarm = false;
                        mAlarmList = mAlarmListData.getAlarmDataArrayList();
                        if (mAlarmList.size() == 0) {
                            mTvEmptyHint.setVisibility(View.VISIBLE);
                        } else {
                            mTvEmptyHint.setVisibility(View.GONE);
                        }
                        mAlarmListAdapter.setServiceList(mAlarmList);
                    }
                    break;
                case ALARM_LIST_FALSE:
                    ToastUtils.shortToast(getActivity(), HttpAnalyJsonManager.lastError);
                    break;
                case FOR_ALARM_LIST:
                    mAlarmListTask = new AlarmListTask();
                    mAlarmListTask.execute("");
                    //mHandler.sendEmptyMessageDelayed(FOR_ALARM_LIST,60000);
                    break;
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        mAlarmReceiver = new AlarmReceiver();//广播接受者实例
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(DButtonApplication.ACTION_DBUTTON_CONNECT);
        intentFilter.addAction(DButtonApplication.ACTION_ALARM_LIST_UPDATE);
        getActivity().registerReceiver(mAlarmReceiver, intentFilter);


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(mAlarmReceiver);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.alarm_fragment, null);
        //getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        //getActivity().getWindow().addFlags( WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        //getActivity().getWindow().setStatusBarColor(Color.TRANSPARENT);
        initTitle(view);
        setView(view);
        setListener();
        return view;
    }


    private TextView mActivityTitle, mTitleExtra;

    private ImageView mIvRigth;
    private void initTitle(View view) {
        mActivityTitle = (TextView) view.findViewById(R.id.title_info);
        mTitleExtra = (TextView) view.findViewById(R.id.title_extra);
        mIvRigth = (ImageView) view.findViewById(R.id.title_right_btn);
        mIvRigth.setVisibility(View.VISIBLE);
        mActivityTitle.setText(getResources().getString(R.string.menu_alarm));
        mTitleExtra.setVisibility(View.GONE);
        mTitleExtra.setText(R.string.no_connection);
        mIvRigth.setBackgroundResource(R.drawable.no_connect_selector);
        mIsConnection = false;
        mIvRigth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!mIsConnection){
                    startService();
                }else{
                    //可以尝试断开操作
                    //DButtonApplication.mInstance.disconnect();
                }
            }
        });
    }

    private boolean mIsConnection = false;
    private void startService(){
        DButtonApplication.mNowMac = SettingSharedPerferencesUtil.GetBindDbuttonMACValue(
                getActivity(), DButtonApplication.mUserData.getPhone());
        LogUtil.println("DButtonApplication::Login::mNowMac= " + DButtonApplication.mNowMac);
        DButtonApplication.mInstance.startScanDevice();
    }

    public void setView(View view) {
        mAlarmList = new ArrayList<AlarmResultData>();
        mLvAlarm = (LoadListView) view.findViewById(R.id.lv_alarm);
        mAlarmListAdapter = new AlarmListAdapter(getActivity(), mAlarmList);
        mLvAlarm.setAdapter(mAlarmListAdapter);
        mLvAlarm.setInterface(this);
        mTvEmptyHint = (TextView) view.findViewById(R.id.tv_alarm_empty_hint);
        mTvEmptyHint.setVisibility(View.GONE);
//        if (mAlarmList.size() == 0) {
//            mTvEmptyHint.setVisibility(View.VISIBLE);
//        } else {
//            mTvEmptyHint.setVisibility(View.GONE);
//        }
        mHandler.sendEmptyMessage(FOR_ALARM_LIST);
    }

    private void setListener() {
        mLvAlarm.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), AlarmDetailActivity.class);
                intent.putExtra("id",mAlarmList.get(position).getAlarmId());
                intent.putExtra("name",mAlarmList.get(position).getAlarmData().getVipName());
                intent.putExtra("image",mAlarmList.get(position).getAlarmData().getVipImg());
                startActivity(intent);
            }
        });
    }
    private AlarmListTask mAlarmListTask;
    private UpdateAlarmListTask mUpdateAlarmListTask;
    private AlarmListData mAlarmListData;

    @Override
    public void onLoad() {
        pageNo = pageNo+1;
        mUpdateAlarmListTask = new UpdateAlarmListTask();
        mUpdateAlarmListTask.execute("");
    }

    private int pageNo = 1;
    private int pageSize = 10;
    private class UpdateAlarmListTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {
            mAlarmListData = HttpSendJsonManager.myGetAlarmList(getActivity(),String.valueOf(pageNo),String.valueOf(pageSize));
            if (mAlarmListData.isOK()) {
                mHandler.sendEmptyMessage(ALARM_LIST_UPDATE_SUCCESS);
            } else {
                mHandler.sendEmptyMessage(ALARM_LIST_FALSE);
            }
            return null;
        }
    }
    private class AlarmListTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {
            pageNo = 1;
            mAlarmListData = HttpSendJsonManager.myGetAlarmList(getActivity(),String.valueOf(pageNo),String.valueOf(pageSize));
            if (mAlarmListData.isOK()) {
                mHandler.sendEmptyMessage(ALARM_LIST_SUCCESS);
            } else {
                mHandler.sendEmptyMessage(ALARM_LIST_FALSE);
            }
            return null;
        }
    }
    @Override
    public void onResume() {
        super.onResume();
        if (DButtonApplication.mAddAlarm) {
            mAlarmListTask = new AlarmListTask();
            mAlarmListTask.execute("");
        }
    }

    private AlarmReceiver mAlarmReceiver;

    public class AlarmReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(DButtonApplication.ACTION_DBUTTON_CONNECT)) {
                boolean is_success = intent.getBooleanExtra("is_success", false);
                if(is_success){
                    mTitleExtra.setText(R.string.connection_ing);
                    mIvRigth.setBackgroundResource(R.mipmap.has_connection);
                    mIsConnection = true;
                }else{
                    mTitleExtra.setText(R.string.no_connection);
                    mIvRigth.setBackgroundResource(R.drawable.no_connect_selector);
                    mIsConnection = false;
                }
            } else if (action.equals(DButtonApplication.ACTION_ALARM_LIST_UPDATE)) {
                mAlarmListTask = new AlarmListTask();
                mAlarmListTask.execute("");
            }
        }
    }

}
