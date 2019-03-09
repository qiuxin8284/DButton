package com.sfr.dbuttonapplication.activity;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.sfr.dbuttonapplication.R;
import com.sfr.dbuttonapplication.utils.APPUtils;

public class PhoneSearchActivity extends AppCompatActivity implements View.OnClickListener {

    private Button mBtnStart, mBtnStop;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        APPUtils.setTranslucent(this);
        setContentView(R.layout.activity_phone_search);
        setView();
        setListener();
        initAction();
    }

    private void initAction() {
        View statusBar = findViewById(R.id.statusBarView);
        ViewGroup.LayoutParams layoutParams = statusBar.getLayoutParams();
        layoutParams.height = getStatusBarHeight();
    }
    public int getStatusBarHeight() {
        int result = 0; //获取状态栏高度的资源id
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    private void setListener() {
        mBtnStart.setOnClickListener(this);
        mBtnStop.setOnClickListener(this);
    }
    MediaPlayer player;
    private void setView() {
        mBtnStart = (Button) findViewById(R.id.btn_start_search_phone);
        mBtnStop = (Button) findViewById(R.id.btn_stop_search_phone);
    }
    Ringtone rt;
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_start_search_phone:
//                Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//                rt = RingtoneManager.getRingtone(getApplicationContext(), uri);
//                rt.play();
                player = new MediaPlayer();
                Uri alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
                try {
                    player.setDataSource(this, alert);  //后面的是try 和catch ，自动添加的
                }catch (Exception e){
                    e.printStackTrace();
                }
                final AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                if (audioManager.getStreamVolume(AudioManager.STREAM_ALARM) != 0) {
                    player.setAudioStreamType(AudioManager.STREAM_ALARM);
                    player.setLooping(true); //如果是门铃呼叫取消循环即可

                    try {
                        player.prepare();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
                player.start();

                break;
            case R.id.btn_stop_search_phone:
//                rt.stop();
                try {
                    player.stop();
                }catch (Exception e){
                    e.printStackTrace();
                }
                break;
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (player != null) {
            player.stop();
            player.release();

        }
    }
}


