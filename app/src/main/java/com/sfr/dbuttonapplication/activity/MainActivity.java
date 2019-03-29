package com.sfr.dbuttonapplication.activity;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsMessage;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
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
import com.sfr.dbuttonapplication.activity.widget.ReceiveAlarmDialog;
import com.sfr.dbuttonapplication.entity.AlarmIDData;
import com.sfr.dbuttonapplication.http.HttpAnalyJsonManager;
import com.sfr.dbuttonapplication.http.HttpSendJsonManager;
import com.sfr.dbuttonapplication.utils.APPUtils;
import com.sfr.dbuttonapplication.utils.ToastUtils;
import com.sfr.dbuttonapplication.utils.ViewColor;

import java.text.SimpleDateFormat;
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
                case 22:
                    stopRingTone();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //APPUtils.setTranslucent(this);
        setContentView(R.layout.activity_main);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            //window.setStatusBarColor(0XFFFFFFFF);
            // 设置状态栏底色白色
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(Color.WHITE);
        }
        // 设置状态栏字体黑色
//      window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        ///ViewColor.transparencyBar(this);
        //ViewColor.setColor(this, 0XFFFFFFFF);//黑色可以0XFF000000，红色可以0XFFFF0000|白色就是透明低栏0XFFFFFFFF
        //ViewColor.fullScreen(this);
        setView();
        setListener();
//        //和Activity一样都用Intent传值
//        Intent startIntent = new Intent(this, DButtonControlService.class);
//        //启动service用startService方法
//        startService(startIntent);

        mDButtonControlReceiver = new DButtonControlReceiver();//广播接受者实例
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(DButtonApplication.ACTION_LONG_CLICK);
        intentFilter.addAction("android.provider.Telephony.SMS_RECEIVED");
        registerReceiver(mDButtonControlReceiver, intentFilter);
        //showAlarmConfirmDialogDialog();

        // 获得蓝牙适配器对象
//        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
//        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
//            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//            startActivityForResult(enableBtIntent, 1000);
//        }
    }


    private BluetoothAdapter bluetoothAdapter;

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

    private Button mLvDialog;

    public void showAlarmConfirmDialogDialog() {
        if (mAlarmConfirmDialog == null) {
            mAlarmConfirmDialog = new AlarmConfirmDialog(MainActivity.this,
                    R.style.share_dialog);
            mAlarmConfirmDialog.show();
            Window window = mAlarmConfirmDialog.getWindow();
            WindowManager.LayoutParams lp = window.getAttributes();
            lp.alpha = 1f;
            window.setAttributes(lp);
            mLvDialog = (Button) window.findViewById(R.id.dialog_alarm_confirm);
            mLvDialog.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.setAction(DButtonApplication.ACTION_ONE_CLICK);
                    sendBroadcast(intent);
                    mAlarmConfirmDialog.dismiss();
                }
            });
            mAlarmConfirmDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
                @Override
                public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                    if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {

                        return true;

                    } else if (keyCode == KeyEvent.KEYCODE_SEARCH) {
                        return true;
                    } else {
                        return false; //默认返回 false
                    }
                }
            });
            mAlarmConfirmDialog.setCancelable(false);
        } else {
            mAlarmConfirmDialog.show();
        }
    }

    private ReceiveAlarmDialog mReceiveAlarmDialog;

    private Button mBtnDialog;

    public void showReceiveAlarmDialog() {
        if (mReceiveAlarmDialog == null) {
            mReceiveAlarmDialog = new ReceiveAlarmDialog(MainActivity.this,
                    R.style.share_dialog);
            mReceiveAlarmDialog.show();
            Window window = mReceiveAlarmDialog.getWindow();
            WindowManager.LayoutParams lp = window.getAttributes();
            lp.alpha = 1f;
            window.setAttributes(lp);
            mBtnDialog = (Button) window.findViewById(R.id.dialog_receive_alarm);
            mBtnDialog.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    Intent intent = new Intent();
//                    intent.setAction(DButtonApplication.ACTION_ONE_CLICK);
//                    sendBroadcast(intent);
                    mHandler.sendEmptyMessage(22);
                    mReceiveAlarmDialog.dismiss();
                }
            });
            mReceiveAlarmDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
                @Override
                public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                    if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {

                        return true;

                    } else if (keyCode == KeyEvent.KEYCODE_SEARCH) {
                        return true;
                    } else {
                        return false; //默认返回 false
                    }
                }
            });
            mReceiveAlarmDialog.setCancelable(false);
        } else {
            mReceiveAlarmDialog.show();
        }
    }

    private DButtonControlReceiver mDButtonControlReceiver;

    public class DButtonControlReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(DButtonApplication.ACTION_LONG_CLICK)) {
                showAlarmConfirmDialogDialog();
            } else if (action.equals("android.provider.Telephony.SMS_RECEIVED")) {
                Object[] object = (Object[]) intent.getExtras().get("pdus");
                StringBuilder sb = new StringBuilder();
                for (Object pdus : object) {
                    byte[] pdusMsg = (byte[]) pdus;
                    SmsMessage sms = SmsMessage.createFromPdu(pdusMsg);
                    String mobile = sms.getOriginatingAddress();//发送短信的手机号
                    String content = sms.getMessageBody();//短信内容
                    //下面是获取短信的发送时间
                    Date date = new Date(sms.getTimestampMillis());
                    String date_time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
                    //追加到StringBuilder中
                    sb.append("短信发送号码：" + mobile + "\n短信内容：" + content + "\n发送时间：" + date_time + "\n\n");

                    //如果找到对应关键内容，直接触发刷新list和响铃--响铃多久合适
                    if(content.contains("出事了")) {
                        PlayRingTone(MainActivity.this, RingtoneManager.TYPE_RINGTONE);
                        intent = new Intent();
                        intent.setAction(DButtonApplication.ACTION_ALARM_LIST_UPDATE);
                        sendBroadcast(intent);
                        showReceiveAlarmDialog();
                    }
                }
                Log.e("StringBuilder", "sb() +++:" + sb.toString());
            }
        }
    }
    //RingtoneManager.TYPE_NOTIFICATION;   通知声音
    //RingtoneManager.TYPE_ALARM;  警告
    //RingtoneManager.TYPE_RINGTONE; 铃声

    /**
     * 获取的是铃声的Uri
     * @param ctx
     * @param type
     * @return
     */
    public Uri getDefaultRingtoneUri(Context ctx, int type) {

        return RingtoneManager.getActualDefaultRingtoneUri(ctx, type);

    }
    /**
     * 获取的是铃声相应的Ringtone
     * @param ctx
     * @param type
     */
    public Ringtone getDefaultRingtone(Context ctx, int type) {

        return RingtoneManager.getRingtone(ctx,
                RingtoneManager.getActualDefaultRingtoneUri(ctx, type));

    }
    private MediaPlayer mMediaPlayer;
    /**
     * 播放铃声
     * @param ctx
     * @param type
     */
    public void PlayRingTone(Context ctx,int type){
        mMediaPlayer = MediaPlayer.create(ctx,
                getDefaultRingtoneUri(ctx,type));
        mMediaPlayer.setLooping(true);
        mMediaPlayer.start();

    }
    public void stopRingTone(){
        mMediaPlayer.stop();
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

