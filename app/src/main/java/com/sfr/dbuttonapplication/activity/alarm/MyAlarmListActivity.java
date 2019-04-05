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
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sfr.dbuttonapplication.DButtonApplication;
import com.sfr.dbuttonapplication.R;
import com.sfr.dbuttonapplication.activity.adapter.MyAlarmListAdapter;
import com.sfr.dbuttonapplication.activity.fragment.AlarmFragment;
import com.sfr.dbuttonapplication.activity.widget.LoadListView;
import com.sfr.dbuttonapplication.activity.widget.LoadingProgressDialog;
import com.sfr.dbuttonapplication.entity.AlarmListData;
import com.sfr.dbuttonapplication.entity.AlarmResultData;
import com.sfr.dbuttonapplication.http.HttpAnalyJsonManager;
import com.sfr.dbuttonapplication.http.HttpSendJsonManager;
import com.sfr.dbuttonapplication.utils.APPUtils;
import com.sfr.dbuttonapplication.utils.LogUtil;
import com.sfr.dbuttonapplication.utils.ToastUtils;

import java.util.ArrayList;

public class MyAlarmListActivity extends AppCompatActivity implements View.OnClickListener,LoadListView.ILoadListener2 {
    private LoadListView mLvAlarm;
    private MyAlarmListAdapter mAlarmListAdapter;
    private ArrayList<AlarmResultData> mAlarmList;
    private TextView mTvEmptyHint;
    private static final int ALARM_LIST_SUCCESS = 1;
    private static final int ALARM_LIST_FALSE = 2;
    private static final int ALARM_DEL_SUCCESS = 3;
    private static final int ALARM_DEL_FALSE = 4;
    private static final int ALARM_LIST_UPDATE_SUCCESS = 5;
    private RelativeLayout mLlEditBottom;
    private LinearLayout mBtnChoice, mBtnDelete ,mBtnCancel;
    private TextView mTvChoice;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case ALARM_LIST_UPDATE_SUCCESS:
                    LoadingProgressDialog.Dissmiss();
                    if (mAlarmListData != null) {
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
                    mTvChoice.setText(R.string.choice_all);
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

    private TextView mActivityTitle, mTitleExtra;
    private ImageView mTitleBack;

    private void initTitle() {
        mActivityTitle = (TextView) findViewById(R.id.title_info);
        mTitleExtra = (TextView) findViewById(R.id.title_extra);
        mTitleBack = (ImageView) findViewById(R.id.title_back_btn);
        mActivityTitle.setText(getResources().getString(R.string.my_alarm_list));
        mTitleExtra.setVisibility(View.GONE);
        mTitleExtra.setText(getResources().getString(R.string.edit));
        mTitleExtra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAlarmListAdapter.changeSelectMode();
                if (mAlarmListAdapter.isMulMode()) {
                    mLlEditBottom.setVisibility(View.VISIBLE);
                    mTitleExtra.setText(getResources().getString(R.string.abondon));
                } else {
                    mTvChoice.setText(R.string.choice_all);
                    mLlEditBottom.setVisibility(View.GONE);
                    mTitleExtra.setText(getResources().getString(R.string.edit));
                }
            }
        });
        mTitleBack.setVisibility(View.VISIBLE);
        mTitleBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void setView() {
        mLlEditBottom = (RelativeLayout) findViewById(R.id.ll_edit_bottom);
        mBtnChoice = (LinearLayout) findViewById(R.id.btn_choice_all);
        mBtnDelete = (LinearLayout) findViewById(R.id.btn_delete);
        mBtnCancel = (LinearLayout) findViewById(R.id.btn_cancel);
        mTvChoice = (TextView) findViewById(R.id.tv_choice_all);
        mAlarmList = new ArrayList<AlarmResultData>();
        mLvAlarm = (LoadListView) findViewById(R.id.lv_alarm);
        mLvAlarm.setInterface(this);
        mAlarmListAdapter = new MyAlarmListAdapter(MyAlarmListActivity.this, mAlarmList);
        mLvAlarm.setAdapter(mAlarmListAdapter);
        mTvEmptyHint = (TextView) findViewById(R.id.tv_alarm_empty_hint);
        mTvEmptyHint.setVisibility(View.GONE);
//        if (mAlarmList.size() == 0) {
//            mTvEmptyHint.setVisibility(View.VISIBLE);
//        } else {
//            mTvEmptyHint.setVisibility(View.GONE);
//        }
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
                        mTvChoice.setText(R.string.common_cancel);
                    } else {
                        mTvChoice.setText(R.string.choice_all);
                    }
                } else {
                    Intent intent = new Intent(MyAlarmListActivity.this, AlarmDetailActivity.class);
                    intent.putExtra("id", mAlarmList.get(position).getAlarmData().getId());//查不到是不是ID有问题
                    intent.putExtra("name",mAlarmList.get(position).getAlarmData().getVipName());
                    intent.putExtra("image",mAlarmList.get(position).getAlarmData().getVipImg());
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

                mLlEditBottom.setVisibility(View.VISIBLE);
                mTitleExtra.setText(getResources().getString(R.string.abondon));
                mAlarmListAdapter.changeSelectMode();
                return true;
            }
        });
        mBtnChoice.setOnClickListener(this);
        mBtnDelete.setOnClickListener(this);
        mBtnCancel.setOnClickListener(this);
    }

    private AlarmListTask mAlarmListTask;
    private UpdateAlarmListTask mUpdateAlarmListTask;
    private AlarmListData mAlarmListData;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_choice_all:
                mAlarmListAdapter.setSelecteAll();
                if (mAlarmListAdapter.getSelectSize() == mAlarmList.size()) {
                    mTvChoice.setText(R.string.common_cancel);
                } else {
                    mTvChoice.setText(R.string.choice_all);
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
            case R.id.btn_cancel:
                mTvChoice.setText(R.string.choice_all);
                mLlEditBottom.setVisibility(View.GONE);
                mTitleExtra.setText(getResources().getString(R.string.edit));
                mAlarmListAdapter.changeSelectMode();
                break;
        }
    }

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
            mAlarmListData = HttpSendJsonManager.myGetAlarmList(MyAlarmListActivity.this,String.valueOf(pageNo),String.valueOf(pageSize));
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
            mAlarmListData = HttpSendJsonManager.mySendAlarmList(MyAlarmListActivity.this,String.valueOf(pageNo),String.valueOf(pageSize));
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
