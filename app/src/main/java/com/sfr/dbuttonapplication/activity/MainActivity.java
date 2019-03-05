package com.sfr.dbuttonapplication.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.sfr.dbuttonapplication.DButtonApplication;
import com.sfr.dbuttonapplication.R;
import com.sfr.dbuttonapplication.activity.adapter.MainFragmentAdapter;
import com.sfr.dbuttonapplication.activity.fragment.AlarmFragment;
import com.sfr.dbuttonapplication.activity.fragment.ContactFragment;
import com.sfr.dbuttonapplication.activity.fragment.MyFragment;
import com.sfr.dbuttonapplication.activity.widget.AlarmConfirmDialog;
import com.sfr.dbuttonapplication.activity.widget.CustomViewPager;
import com.sfr.dbuttonapplication.entity.AlarmIDData;
import com.sfr.dbuttonapplication.http.HttpAnalyJsonManager;
import com.sfr.dbuttonapplication.http.HttpSendJsonManager;
import com.sfr.dbuttonapplication.utils.APPUtils;
import com.sfr.dbuttonapplication.utils.ToastUtils;

import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, RadioGroup.OnCheckedChangeListener {
    private RadioGroup mMenuGroup;
    private int currentItem;
    private CustomViewPager mPager;
    private ArrayList<Fragment> fragmentList;
    private int imgs[] = new int[]{R.drawable.menu_alarm_btn,
            R.drawable.menu_contact_btn, R.drawable.menu_my_btn};

    private static final int ALARM_UP_SUCCESS = 1;
    private static final int ALARM_UP_FALSE = 2;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case ALARM_UP_SUCCESS:
                    //只有收到别人推送的才会刷新ALarmList
                    DButtonApplication.mAddAlarm = true;
                    break;
                case ALARM_UP_FALSE:
                    ToastUtils.shortToast(MainActivity.this, HttpAnalyJsonManager.lastError);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        APPUtils.setTranslucent(this);
        setContentView(R.layout.activity_main);
        setView();
        setListener();
//        //和Activity一样都用Intent传值
//        Intent startIntent = new Intent(this, DButtonControlService.class);
//        //启动service用startService方法
//        startService(startIntent);

        mDButtonControlReceiver = new DButtonControlReceiver();//广播接受者实例
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(DButtonApplication.ACTION_LONG_CLICK);
        registerReceiver(mDButtonControlReceiver, intentFilter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mDButtonControlReceiver);
    }

    private void setListener() {
    }

    private void setView() {
        mMenuGroup = (RadioGroup) findViewById(R.id.menu_panel_rg);
        mPager = (CustomViewPager) findViewById(R.id.viewpager);
        fragmentList = new ArrayList<Fragment>();
        fragmentList.add(new AlarmFragment());
        fragmentList.add(new ContactFragment());
        fragmentList.add(new MyFragment());
        MainFragmentAdapter adapter = new MainFragmentAdapter(
                getSupportFragmentManager(), fragmentList);
        mPager.setAdapter(adapter);
        mMenuGroup.setOnCheckedChangeListener(this);
        mPager.setCurrentItem(currentItem);
        mPager.setOffscreenPageLimit(3);
        ((RadioButton) mMenuGroup.getChildAt(currentItem)).setChecked(true);
        for (int i = 0; i < 3; i++) {
            RadioButton rb = ((RadioButton) mMenuGroup.getChildAt(i));
            if (i == currentItem) {
                rb.setChecked(true);
            }
            Drawable d = getResources().getDrawable(imgs[i]);
            d.setBounds(0, 0, 75, 75);
            rb.setCompoundDrawables(null, d, null, null);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {

        switch (checkedId) {
            case R.id.menu_alarm:
                currentItem = 0;
                mPager.setCurrentItem(currentItem);
//                mAlarmUpTask = new AlarmUpTask();
//                mAlarmUpTask.execute("");
//                Intent intent = new Intent();
//                intent.setAction(DButtonApplication.ACTION_ONE_CLICK);
//                sendBroadcast(intent);
                break;

            case R.id.menu_contact:
                currentItem = 1;
                mPager.setCurrentItem(currentItem);
//                intent = new Intent();
//                intent.setAction(DButtonApplication.ACTION_DOUBLE_CLICK);
//                sendBroadcast(intent);
                break;

            case R.id.menu_my:
                currentItem = 2;
                mPager.setCurrentItem(currentItem);
//                intent = new Intent();
//                intent.setAction(DButtonApplication.ACTION_LONG_CLICK);
//                sendBroadcast(intent);
                break;

        }
    }

    private AlarmUpTask mAlarmUpTask;

    private class AlarmUpTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {
            if (DButtonApplication.mContactList.size() > 0) {
                String contactIds = "";
                for (int i = 0; i < DButtonApplication.mContactList.size(); i++) {
                    if (i == 0) {
                        contactIds = DButtonApplication.mContactList.get(i).getId();
                    } else {
                        contactIds = contactIds + "," + DButtonApplication.mContactList.get(i).getId();
                    }
                }
                AlarmIDData alarmIDData = HttpSendJsonManager.alarmUp(MainActivity.this, contactIds, "33.33,33.33",
                        String.valueOf(new Date().getTime()), String.valueOf(new Date().getTime()),
                        "南山区大冲新城花园", "", "");
                if (alarmIDData.isOK()) {
                    mHandler.sendEmptyMessage(ALARM_UP_SUCCESS);
                } else {
                    mHandler.sendEmptyMessage(ALARM_UP_FALSE);
                }
            }
            return null;
        }
    }
//
//    @Override
//    public void onResume() {
//        super.onResume();
//        this.startLockTask();
//    }

//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (keyCode == KeyEvent.KEYCODE_BACK)
//            return true;
//        return super.onKeyDown(keyCode, event);
//    }


    private AlarmConfirmDialog mAlarmConfirmDialog;

    private ImageView mLvDialog;

    public void showAlarmConfirmDialogDialog() {
        if (mAlarmConfirmDialog == null) {
            mAlarmConfirmDialog = new AlarmConfirmDialog(MainActivity.this,
                    R.style.share_dialog);
            mAlarmConfirmDialog.show();
            Window window = mAlarmConfirmDialog.getWindow();
            WindowManager.LayoutParams lp = window.getAttributes();
            lp.alpha = 1f;
            window.setAttributes(lp);
            mLvDialog = (ImageView) window.findViewById(R.id.dialog_alarm_confirm);
            mLvDialog.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.setAction(DButtonApplication.ACTION_ONE_CLICK);
                    sendBroadcast(intent);
                    mAlarmConfirmDialog.dismiss();
                }
            });
        }else{
            mAlarmConfirmDialog.show();
        }
    }


    private DButtonControlReceiver mDButtonControlReceiver;

    public class DButtonControlReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(DButtonApplication.ACTION_LONG_CLICK)) {
                showAlarmConfirmDialogDialog();
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent home = new Intent(Intent.ACTION_MAIN);
            home.addCategory(Intent.CATEGORY_HOME);
            startActivity(home);
        }
        return super.onKeyDown(keyCode, event);
    }
}

