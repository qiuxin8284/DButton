package com.sfr.dbuttonapplication.service;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

/**
 * Created by ASUS on 2018/9/9.
 */

public class DbuttonPhoneStateService extends Service {
    private MediaRecorder mRecorder;
    private boolean isrecoding;// 电话接听状态

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        isrecoding = false;
        // 监听系统的电话状态
        TelephonyManager manager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        manager.listen(new Listener(), PhoneStateListener.LISTEN_CALL_STATE);

    }

    public class Listener extends PhoneStateListener {
        /**
         * 电话状态发生改变时调用 电话状态分为响铃，空闲，接听
         */
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            switch (state) {
                case TelephonyManager.CALL_STATE_IDLE:// 空闲
                    System.out.println("电话空闲状态");
                    if (isrecoding) {
                        mRecorder.stop();
                        isrecoding = false;
                        // 上传文件到服务器
                        System.out.println("上传文件到服务器");

                    } else {

                    }
                    break;
                case TelephonyManager.CALL_STATE_RINGING:// 响铃
                    System.out.println("电话铃响了");
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:// 接听
//
                    System.out.println("开始通话");

                    MediaPlayer mplayer = new MediaPlayer();
                    mplayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                    String path = "";
                    try {
                        mplayer.setDataSource(path);
                        mplayer.prepare();
                        mplayer.start();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                    break;

                default:
                    break;
            }

            super.onCallStateChanged(state, incomingNumber);
        }

    }

}

