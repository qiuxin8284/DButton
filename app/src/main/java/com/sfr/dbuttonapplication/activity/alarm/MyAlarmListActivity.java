package com.sfr.dbuttonapplication.activity.alarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.sfr.dbuttonapplication.DButtonApplication;
import com.sfr.dbuttonapplication.R;
import com.sfr.dbuttonapplication.activity.adapter.MyAlarmListAdapter;
import com.sfr.dbuttonapplication.activity.widget.LoadingProgressDialog;
import com.sfr.dbuttonapplication.entity.AlarmListData;
import com.sfr.dbuttonapplication.entity.AlarmResultData;
import com.sfr.dbuttonapplication.http.HttpAnalyJsonManager;
import com.sfr.dbuttonapplication.http.HttpSendJsonManager;
import com.sfr.dbuttonapplication.utils.APPUtils;
import com.sfr.dbuttonapplication.utils.LogUtil;
import com.sfr.dbuttonapplication.utils.ToastUtils;

import java.util.ArrayList;

public class MyAlarmListActivity extends AppCompatActivity implements View.OnClickListener {
    private ListView mLvAlarm;
    private MyAlarmListAdapter mAlarmListAdapter;
    private ArrayList<AlarmResultData> mAlarmList;
    private TextView mTvEmptyHint;
    private static final int ALARM_LIST_SUCCESS = 1;
    private static final int ALARM_LIST_FALSE = 2;
    private static final int ALARM_DEL_SUCCESS = 3;
    private static final int ALARM_DEL_FALSE = 4;
    private LinearLayout mLlEditBottom;
    private Button mBtnChoice, mBtnDelete;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case ALARM_LIST_SUCCESS:
                    LoadingProgressDialog.Dissmiss();
                    if (mAlarmListData != null) {
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
                    LoadingProgressDialog.Dissmiss();
                    ToastUtils.shortToast(MyAlarmListActivity.this, HttpAnalyJsonManager.lastError);
                    break;
                case ALARM_DEL_SUCCESS:
                    //刷新list
//                    mAlarmList.remove(mRemovePostion);
//                    if (mAlarmList.size() == 0) {
//                        mTvEmptyHint.setVisibility(View.VISIBLE);
//                    } else {
//                        mTvEmptyHint.setVisibility(View.GONE);
//                    }
//                    mAlarmListAdapter.setServiceList(mAlarmList);
                    mBtnChoice.setText(R.string.choice_all);
                    mLlEditBottom.setVisibility(View.GONE);
                    mTitleExtra.setText(getResources().getString(R.string.edit));
                    mAlarmListAdapter.changeSelectMode();
                    mAlarmListTask = new AlarmListTask();
                    mAlarmListTask.execute("");
                    break;
                case ALARM_DEL_FALSE:
                    LoadingProgressDialog.Dissmiss();
                    ToastUtils.shortToast(MyAlarmListActivity.this, HttpAnalyJsonManager.lastError);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        APPUtils.setTranslucent(this);
        setContentView(R.layout.activity_my_alarm_list);
        initTitle();
        setView();
        setListener();
        mAlarmReceiver = new AlarmReceiver();//广播接受者实例
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(DButtonApplication.ACTION_ALARM_LIST_UPDATE);
        registerReceiver(mAlarmReceiver, intentFilter);
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mAlarmReceiver);
    }

    private TextView mActivityTitle, mTitleExtra, mTitleBack;

    private void initTitle() {
        mActivityTitle = (TextView) findViewById(R.id.title_info);
        mTitleExtra = (TextView) findViewById(R.id.title_extra);
        mTitleBack = (TextView) findViewById(R.id.title_back);
        mActivityTitle.setText(getResources().getString(R.string.my_alarm_list));
        mTitleExtra.setVisibility(View.VISIBLE);
        mTitleExtra.setText(getResources().getString(R.string.edit));
        mTitleExtra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAlarmListAdapter.changeSelectMode();
                if (mAlarmListAdapter.isMulMode()) {
                    mLlEditBottom.setVisibility(View.VISIBLE);
                    mTitleExtra.setText(getResources().getString(R.string.abondon));
                } else {
                    mBtnChoice.setText(R.string.choice_all);
                    mLlEditBottom.setVisibility(View.GONE);
                    mTitleExtra.setText(getResources().getString(R.string.edit));
                }
            }
        });
        mTitleBack.setVisibility(View.VISIBLE);
        mTitleBack.setText(getResources().getString(R.string.go_up));
        mTitleBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void setView() {
        mLlEditBottom = (LinearLayout) findViewById(R.id.ll_edit_bottom);
        mBtnChoice = (Button) findViewById(R.id.btn_choice_all);
        mBtnDelete = (Button) findViewById(R.id.btn_delete);
        mAlarmList = new ArrayList<AlarmResultData>();
        mLvAlarm = (ListView) findViewById(R.id.lv_alarm);
        mAlarmListAdapter = new MyAlarmListAdapter(MyAlarmListActivity.this, mAlarmList);
        mLvAlarm.setAdapter(mAlarmListAdapter);
        mTvEmptyHint = (TextView) findViewById(R.id.tv_alarm_empty_hint);
        if (mAlarmList.size() == 0) {
            mTvEmptyHint.setVisibility(View.VISIBLE);
        } else {
            mTvEmptyHint.setVisibility(View.GONE);
        }
        LoadingProgressDialog.show(MyAlarmListActivity.this, false, true, 30000);
        mAlarmListTask = new AlarmListTask();
        mAlarmListTask.execute("");
    }

    private void setListener() {
        mLvAlarm.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mAlarmListAdapter.isMulMode()) {
                    LogUtil.println("setListener mAlarmListAdapter.isMulMode():" + mAlarmListAdapter.isMulMode());
                    mAlarmListAdapter.setSelecte(position);
                    if (mAlarmListAdapter.getSelectSize() == mAlarmList.size()) {
                        mBtnChoice.setText(R.string.cancel_choice_all);
                    } else {
                        mBtnChoice.setText(R.string.choice_all);
                    }
                } else {
                    Intent intent = new Intent(MyAlarmListActivity.this, AlarmDetailActivity.class);
                    intent.putExtra("id", mAlarmList.get(position).getAlarmData().getId());//查不到是不是ID有问题
                    startActivity(intent);
                }
            }
        });
        mLvAlarm.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
//                if (mAlarmListAdapter.isMulMode()) {
//                    mAlarmListAdapter.setSelecte(position);
//                } else {
//                    mRemovePostion = position;
//                    mAlarmDelTask = new AlarmDelTask();
//                    mAlarmDelTask.execute(mAlarmList.get(position).getAlarmId());
//                }
                return false;
            }
        });
        mBtnChoice.setOnClickListener(this);
        mBtnDelete.setOnClickListener(this);
    }

    private AlarmListTask mAlarmListTask;
    private AlarmListData mAlarmListData;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_choice_all:
                mAlarmListAdapter.setSelecteAll();
                if (mAlarmListAdapter.getSelectSize() == mAlarmList.size()) {
                    mBtnChoice.setText(R.string.cancel_choice_all);
                } else {
                    mBtnChoice.setText(R.string.choice_all);
                }
                break;
            case R.id.btn_delete:
                //mRemovePostion = new ArrayList<AlarmResultData>();
                String ids = "";
                for (Integer key : mAlarmListAdapter.getmSelectedMap().keySet()) {
                    if (key == 0) {
                        ids = mAlarmListAdapter.getmSelectedMap().get(key).getAlarmData().getId();
                    } else {
                        ids = ids + "," + mAlarmListAdapter.getmSelectedMap().get(key).getAlarmData().getId();
                    }
                    //mRemovePostion.add(mAlarmList.get(i));
                }
                LoadingProgressDialog.show(MyAlarmListActivity.this, false, true, 30000);
                mAlarmDelTask = new AlarmDelTask();
                mAlarmDelTask.execute(ids);
                break;
        }
    }

    private class AlarmListTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {
            mAlarmListData = HttpSendJsonManager.mySendAlarmList(MyAlarmListActivity.this, "1", "1000");
            if (mAlarmListData.isOK()) {
                mHandler.sendEmptyMessage(ALARM_LIST_SUCCESS);
            } else {
                mHandler.sendEmptyMessage(ALARM_LIST_FALSE);
            }
            return null;
        }
    }

    private AlarmDelTask mAlarmDelTask;
    private ArrayList<AlarmResultData> mRemovePostion;

    private class AlarmDelTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {
            String mID = params[0];
            if (HttpSendJsonManager.delAlarm(MyAlarmListActivity.this, mID,"1")) {
                mHandler.sendEmptyMessage(ALARM_DEL_SUCCESS);
            } else {
                mHandler.sendEmptyMessage(ALARM_DEL_FALSE);
            }
            return null;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (DButtonApplication.mAddAlarm) {
            LoadingProgressDialog.show(MyAlarmListActivity.this, false, true, 30000);
            mAlarmListTask = new AlarmListTask();
            mAlarmListTask.execute("");
        }
    }

    private AlarmReceiver mAlarmReceiver;

    public class AlarmReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(DButtonApplication.ACTION_ALARM_LIST_UPDATE)) {
                LoadingProgressDialog.show(MyAlarmListActivity.this, false, true, 30000);
                mAlarmListTask = new AlarmListTask();
                mAlarmListTask.execute("");
            }
        }
    }
}
