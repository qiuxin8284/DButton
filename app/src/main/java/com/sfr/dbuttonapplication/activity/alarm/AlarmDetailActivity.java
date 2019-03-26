package com.sfr.dbuttonapplication.activity.alarm;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.sfr.dbuttonapplication.DButtonApplication;
import com.sfr.dbuttonapplication.R;
import com.sfr.dbuttonapplication.activity.widget.LoadingProgressDialog;
import com.sfr.dbuttonapplication.activity.widget.MapChooesDialog;
import com.sfr.dbuttonapplication.entity.AlarmData;
import com.sfr.dbuttonapplication.entity.Music;
import com.sfr.dbuttonapplication.http.HttpAnalyJsonManager;
import com.sfr.dbuttonapplication.http.HttpSendJsonManager;
import com.sfr.dbuttonapplication.player.MusicPlayer;
import com.sfr.dbuttonapplication.utils.APPUtils;
import com.sfr.dbuttonapplication.utils.BaiduLocationUtils;
import com.sfr.dbuttonapplication.utils.FileUtils;
import com.sfr.dbuttonapplication.utils.LogUtil;
import com.sfr.dbuttonapplication.utils.MapViewUtil;
import com.sfr.dbuttonapplication.utils.PictureUtil;
import com.sfr.dbuttonapplication.utils.ToastUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AlarmDetailActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String PLAY_STATE_previous = "previous";
    public static final String PLAY_STATE_next = "next";
    public static final String PLAY_STATE_play = "play";
    public static final String PLAY_STATE_pause = "pause";
    public static final String PLAY_STATE_function = "function";
    public static final String PLAY_STATE_CHANGE = "change";
    public static final String PLAY_STATE_STOP_CHANGE = "stop";

    public static final String BROADCAST_REFRESH_PROGRESS = "com.music.refreshprogress";
    public static final String BROADCAST_CHANGE_MUSIC = "com.music.changemusic";
    public static final String BROADCAST_NEXT_MUSIC = "com.music.nextmusic";
    private ImageView mIvStart, mIvGoMap, mIvHead;//iv_voice_start;
    private SeekBar mSeekBar;//sb_voice;
    private TextView mTvBeginTime, mTvEndTime, mTvAddress, mTvLocation, mTvUserName, mTvCurMusic;
    private MapView mMapView;
    private BaiduMap mBaiduMap;
    private static final int ALARM_DETAIL_SUCCESS = 3;
    private static final int ALARM_DETAIL_FALSE = 4;
    List<Music> musicList = new ArrayList<Music>();
    boolean isFrist = true;
    boolean isPause = false;
    boolean isPhonePause = false;
    int curMusic = 0, curPercent = 0, secondaryProgress = 0, position = 0;
    int function = 1;
    DButtonApplication mApp;
    String[] functionStr = {"顺序播放", "单曲循环", "随机播放"};
    String lastAddress = "";//地址
    Double lastLa;//纬度
    Double lastLo;//经度
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");// HH:mm:ss
    SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("yyyy/MM/dd HH:mm");// HH:mm:ss
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    mSeekBar.setProgress(curPercent);
                    mSeekBar.setSecondaryProgress(secondaryProgress);
                    LogUtil.println("voiceTime curPercent:" + curPercent);
                    LogUtil.println("voiceTime secondaryProgress:" + secondaryProgress);
                    //tvStare.setText(getStrTime(position));
                    break;
                case ALARM_DETAIL_SUCCESS:
                    LoadingProgressDialog.Dissmiss();
                    //经纬度地图使用
                    //刷新地图
                    mBaiduMap.clear();
                    Log.e("AlarmDetailActivity", "mHandler mAlarmData:" + mAlarmData.toString());
                    if (!TextUtils.isEmpty(mAlarmData.getPoint())) {
                        String point = mAlarmData.getPoint();
                        Log.e("AlarmDetailActivity", "mHandler point:" + point);
                        String[] pointData = point.split(",");
                        Log.e("AlarmDetailActivity", "mHandler pointData length:" + pointData.length);
                        List<LatLng> points = new ArrayList<LatLng>();
                        for (int i = 0; i < pointData.length; i++) {
                            String[] data = pointData[i].split("\\|");
                            Log.e("AlarmDetailActivity", "mHandler data length:" + data.length);
                            Log.e("AlarmDetailActivity", "mHandler data:" + data.toString());
                            lastLa = Double.valueOf(data[1]);//纬度
                            lastLo = Double.valueOf(data[0]);//经度
                            //定义Maker坐标点
                            LatLng ll = new LatLng(lastLa,
                                    lastLo);
                            //view转换成图
                            LayoutInflater inflater = getLayoutInflater();
                            View view = inflater.inflate(R.layout.point_view, null);
                            TextView txt = (TextView) view.findViewById(R.id.tv_point_time);

                            try {
                                if (i == pointData.length - 1) {
                                    Date date = new Date(Long.valueOf(data[2]));
                                    String pointTime = simpleDateFormat.format(date);
                                    txt.setText(pointTime);

                                    Bitmap viewBitmap = PictureUtil.getViewBitmap(view);
                                    BitmapDescriptor bitmap = BitmapDescriptorFactory.fromBitmap(viewBitmap);
                                    OverlayOptions option = new MarkerOptions()
                                            .position(ll)
                                            .icon(bitmap);
                                    mBaiduMap.addOverlay(option);
                                }
                            } catch (Exception e) {
                                txt.setText(data[2]);
                                e.printStackTrace();
                            }
                            points.add(ll);
//                            Bitmap viewBitmap = PictureUtil.getViewBitmap(view);
//                            //构建Marker图标
//                            BitmapDescriptor bitmap = BitmapDescriptorFactory.fromBitmap(viewBitmap);
//                            //构建Marker图标
////                            BitmapDescriptor bitmap = BitmapDescriptorFactory
////                                    .fromResource(R.mipmap.img_voice_index);
//                            //构建MarkerOption，用于在地图上添加Marker
//                            OverlayOptions option = new MarkerOptions()
//                                    .position(ll)
//                                    .icon(bitmap);
//                            //在地图上添加Marker，并显示
//                            mBaiduMap.addOverlay(option);
//                            points.add(ll);
                        }

                        if (points.size() > 1) {
                            //绘制折线
                            OverlayOptions ooPolyline = new PolylineOptions().width(10)
                                    .color(0xAAFF0000).points(points);
                            mBaiduMap.addOverlay(ooPolyline);
                        }

                        //缩小的到当前位置
                        MapStatusUpdate msu = MapStatusUpdateFactory.newLatLng(points.get(points.size() - 1));
                        MapStatusUpdate u = MapStatusUpdateFactory.zoomTo(18);
                        try {
                            mBaiduMap.animateMapStatus(u);
                            mBaiduMap.animateMapStatus(msu);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    //报警开始时间 结束时间和地址
                    lastAddress = mAlarmData.getAddress();
                    mTvBeginTime.setText("记录时间：" + simpleDateFormat2.format(new Date(Long.valueOf(mAlarmData.getBeginTime()))));
                    //mTvEndTime.setText("报警时间：" + simpleDateFormat.format(new Date(Long.valueOf(mAlarmData.getEndTime()))));
                    mTvEndTime.setText("报警时间：" + simpleDateFormat2.format(new Date(Long.valueOf(mAlarmData.getEndTime()))));
                    mTvAddress.setText(mAlarmData.getAddress());
                    //mTvLocation.setText("经度："+lastLo+"  ,  纬度："+lastLa);//后续清空后改成截取json的形式取最后一个点
                    initMusic();
                    break;
                case ALARM_DETAIL_FALSE:
                    LoadingProgressDialog.Dissmiss();
                    ToastUtils.shortToast(AlarmDetailActivity.this, HttpAnalyJsonManager.lastError);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        APPUtils.setTranslucent(this);
        setContentView(R.layout.activity_alarm_detail);
        initTitle();
        mApp = (DButtonApplication) getApplication();
        mID = getIntent().getStringExtra("id");
        mName = getIntent().getStringExtra("name");
        mImg = getIntent().getStringExtra("image");
        LogUtil.println("AlarmDetailActivity mID:" + mID);
        setView();
        setListener();
        LoadingProgressDialog.show(AlarmDetailActivity.this, false, true, 30000);
        mAlarmDetailTask = new AlarmDetailTask();
        mAlarmDetailTask.execute("");
        //initMusic();
        //loadFileData();
    }

    private void initMusic() {
        LogUtil.println("initMusic 1 mAlarmData.toString1:" + mAlarmData.toString() +"\n");
        musicList = new ArrayList<Music>();
        Music music = new Music();
        LogUtil.println("initMusic mAlarmData.getRecord()!=null:" + (mAlarmData.getRecord()!=null) +"\n");
        LogUtil.println("initMusic !TextUtils.isEmpty(mAlarmData.getRecord()):" + (!TextUtils.isEmpty(mAlarmData.getRecord())) +"\n");
        LogUtil.println("initMusic mAlarmData.getRecord():" + mAlarmData.getRecord());
        if (mAlarmData.getRecord()!=null&&!TextUtils.isEmpty(mAlarmData.getRecord())&&!mAlarmData.getRecord().equals("null")) {
            music.setName("dbuttonR");
            music.setSize(0);
            music.setUrl(mAlarmData.getRecord());
            if (!TextUtils.isEmpty(mAlarmData.getDuration()))
                music.setDuration(Long.valueOf(mAlarmData.getDuration()));
            musicList.add(music);
            LogUtil.println("initMusic music.toString1:" + music.toString() +"\n");
        } else {
            LogUtil.println("initMusic AlarmData.toString1:" + mAlarmData.toString() +"\n");
        }


        if (mAlarmData.getSession().contains(",")) {
            String[] session = mAlarmData.getSession().split(",");
            for (int i = 0; i < session.length; i++) {
                if (session[i]!=null&&!TextUtils.isEmpty(session[i])&&!session[i].equals("null")){
                    music = new Music();
                    music.setName("dbuttonS" + i);
                    music.setSize(0);
                    music.setUrl(session[i]);
                    if (!TextUtils.isEmpty(mAlarmData.getDuration()))
                        music.setDuration(Long.valueOf(mAlarmData.getDuration()));
                    musicList.add(music);
                    LogUtil.println("initMusic music.toString2:" + music.toString() + "\n");
                }
            }
            mApp.setMusicList(musicList);
        } else {
            if (mAlarmData.getSession()!=null&&!TextUtils.isEmpty(mAlarmData.getSession())&&!mAlarmData.getSession().equals("null")) {
                music = new Music();
                music.setName("dbuttonS");
                music.setSize(0);
                music.setUrl(mAlarmData.getSession());
                if (!TextUtils.isEmpty(mAlarmData.getDuration()))
                    music.setDuration(Long.valueOf(mAlarmData.getDuration()));
                musicList.add(music);
                LogUtil.println("initMusic music.toString3:" + music.toString() + "\n");
                mApp.setMusicList(musicList);
            }else{
                mApp.setMusicList(musicList);
            }
        }
    }

    private void loadFileData() {
        LogUtil.println("loadFileData:11111111111111111111111111");
        ContentResolver contentResolver = getContentResolver();
        Cursor cursor = contentResolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null,
                null, MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
        //cursor.moveToFirst();
        musicList = new ArrayList<Music>();
        if (cursor.moveToFirst()) {
            for (int i = 0; i < cursor.getCount(); i++) {
                Music music = new Music();
                String title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
                long size = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.SIZE));
                long duration = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION));
                String url = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                String album = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));

                if (duration >= 1000 && duration <= 900000) {
                    music.setName(title);
                    music.setSize(size);
                    music.setUrl(url);
                    music.setDuration(duration);
                    LogUtil.println("loadFileData:" + music.toString());
                    musicList.add(music);
                }
                cursor.moveToNext();
            }
        }
        LogUtil.println("loadFileData:" + "musicList.size:" + musicList.size());
        mApp.setMusicList(musicList);
        LogUtil.println("loadFileData:over");
    }

    private void setView() {
        mTvCurMusic = (TextView) findViewById(R.id.tv_cur_music);
        mTvUserName = (TextView) findViewById(R.id.tv_user_name);
        mTvUserName.setText(mName);
        mIvHead = (ImageView) findViewById(R.id.iv_your_head);
        ImageLoader.getInstance().displayImage(mImg, mIvHead);
        mTvBeginTime = (TextView) findViewById(R.id.tv_begin_time);
        mTvEndTime = (TextView) findViewById(R.id.tv_end_time);
        mTvAddress = (TextView) findViewById(R.id.tv_alarm_address);
        mTvLocation = (TextView) findViewById(R.id.tv_alarm_address_detail);
        mIvStart = (ImageView) findViewById(R.id.iv_voice_start);
        mIvGoMap = (ImageView) findViewById(R.id.iv_go_map);
        mSeekBar = (SeekBar) findViewById(R.id.sb_voice);
        mSeekBar.setOnSeekBarChangeListener(new SeekBarChangeEvent());
        mMapView = (MapView) findViewById(R.id.alarm_detail_map_view);
        mBaiduMap = mMapView.getMap();
        //普通地图
        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
        mBaiduMap.setMyLocationEnabled(true);
        mMapView.showZoomControls(false);
        MapViewUtil.goneMap(mMapView);
        //如果已经有权限了直接初始化，否则再请求一次权限，并且成功的时候触发此方法
        BaiduLocationUtils.initLocation(AlarmDetailActivity.this);

    }

    private void setListener() {
        mIvStart.setOnClickListener(this);
        mIvGoMap.setOnClickListener(this);
    }

    private TextView mActivityTitle, mTitleExtra;
    private ImageView mTitleBack, mIvUpdate;

    private void initTitle() {
        mActivityTitle = (TextView) findViewById(R.id.title_info);
        mTitleExtra = (TextView) findViewById(R.id.title_extra);
        mTitleBack = (ImageView) findViewById(R.id.title_back_btn);
        mIvUpdate = (ImageView) findViewById(R.id.title_right_btn);
        mIvUpdate.setVisibility(View.VISIBLE);
        mIvUpdate.setBackgroundResource(R.mipmap.img_update);
        mActivityTitle.setText(getResources().getString(R.string.detail));
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
                LoadingProgressDialog.show(AlarmDetailActivity.this, false, true, 30000);
                mAlarmDetailTask = new AlarmDetailTask();
                mAlarmDetailTask.execute("");
            }
        });
    }

    private AlarmDetailTask mAlarmDetailTask;
    private AlarmData mAlarmData;
    private String mID = "";
    private String mName = "";
    private String mImg = "";

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_voice_start:
                if (mApp.getMusicList().size() != 0) {
                    LogUtil.println("loadFileData:iv_voice_start");
                    Intent intent = new Intent(this, MusicPlayer.class);
                    intent.putExtra("position", -1);
                    if (!isPause) {
                        LogUtil.println("loadFileData:isPause false");
                        if (isFrist) {
                            LogUtil.println("loadFileData:isFrist true");
                            isFrist = false;
                            isPause = true;
//                        tvOver.setText(getStrTime(musicList.get(nowPosition).getDuration()));
                            mIvStart.setBackgroundResource(R.mipmap.img_voice_stop);
                            intent = new Intent(this, MusicPlayer.class);
                            intent.putExtra("state", PLAY_STATE_play);
                            intent.putExtra("position", 0);
                            intent.putExtra("function", -1);
                            startService(intent);
                        } else {
                            LogUtil.println("loadFileData:isFrist flase");
                            isPause = true;
                            mIvStart.setBackgroundResource(R.mipmap.img_voice_stop);
                            intent.putExtra("state", PLAY_STATE_play);
                        }
                    } else {
                        LogUtil.println("loadFileData:isPause true");
                        isPause = false;
                        mIvStart.setBackgroundResource(R.mipmap.img_voice_start);
                        intent.putExtra("state", PLAY_STATE_pause);
                    }
                    intent.putExtra("function", -1);
                    startService(intent);
//                    if (isFrist) {
//                        isFrist = false;
//                        isPause = true;
//                        //tvOver.setText(getStrTime(musicList.get(nowPosition).getDuration()));
//                        //btnPlayOrPause.setBackgroundResource(R.drawable.voice_stop);
//                        intent = new Intent(this, MusicPlayer.class);
//                        intent.putExtra("state", PLAY_STATE_play);
//                        intent.putExtra("position", 0);
//                        intent.putExtra("function", -1);
//                        startService(intent);
//                    } else {
//                        isPause = true;
//                        //btnPlayOrPause.setBackgroundResource(R.drawable.voice_stop);
//                        intent.putExtra("state", PLAY_STATE_play);
//                    }
////                if (nowPosition == 0) {
////                    nowPosition = musicList.size() - 1;
////                    tvOver.setText(getStrTime(musicList.get(nowPosition).getDuration()));
////                } else {
////                    nowPosition = nowPosition - 1;
////                    tvOver.setText(getStrTime(musicList.get(nowPosition).getDuration()));
////                }
//                    intent.putExtra("state", PLAY_STATE_previous);
//                    intent.putExtra("function", -1);
//                    startService(intent);
                }
                break;
            case R.id.iv_go_map:
                showMapDialog();
                break;
        }

    }

    class SeekBarChangeEvent implements SeekBar.OnSeekBarChangeListener {

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress,
                                      boolean fromUser) {
            // 原本是(progress/seekBar.getMax())*player.mediaPlayer.getDuration()
            Intent intent = new Intent(AlarmDetailActivity.this, MusicPlayer.class);
            intent.putExtra("state", PLAY_STATE_CHANGE);
            intent.putExtra("progress", progress);
            intent.putExtra("max", seekBar.getMax());
            startService(intent);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            // seekTo()的参数是相对与影片时间的数字，而不是与seekBar.getMax()相对的数字
            Intent intent = new Intent(AlarmDetailActivity.this, MusicPlayer.class);
            intent.putExtra("state", PLAY_STATE_STOP_CHANGE);
            startService(intent);
        }

    }

    private class AlarmDetailTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {
            mAlarmData = HttpSendJsonManager.alarmDetail(AlarmDetailActivity.this, mID);
            if (mAlarmData.isOK()) {
                mHandler.sendEmptyMessage(ALARM_DETAIL_SUCCESS);
            } else {
                mHandler.sendEmptyMessage(ALARM_DETAIL_FALSE);
            }
            return null;
        }
    }

    MusicBroadcastReceiver recevier;

    @Override
    protected void onResume() {
        recevier = new MusicBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BROADCAST_REFRESH_PROGRESS);
        intentFilter.addAction(BROADCAST_NEXT_MUSIC);
        this.registerReceiver(recevier, intentFilter);
        super.onResume();
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        mMapView.onResume();

    }

    @Override
    protected void onPause() {
        this.unregisterReceiver(recevier);
        super.onPause();
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        mMapView.onPause();

    }

    @Override
    public void onDestroy() {
        Intent intent = new Intent(this, MusicPlayer.class);
        intent.putExtra("position", -1);
        stopService(intent);
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        mMapView.onDestroy();

    }

    public class MusicBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(BROADCAST_REFRESH_PROGRESS)) {
                curMusic = intent.getIntExtra("curMusic", 0);
                mTvCurMusic.setText(String.valueOf(curMusic + 1));
                curPercent = intent.getIntExtra("curPercent", 0);
                position = intent.getIntExtra("position", 0);
                secondaryProgress = intent.getIntExtra("secondaryProgress", 0);
                mHandler.sendEmptyMessage(1);
            } else if (intent.getAction().equals(BROADCAST_CHANGE_MUSIC)) {
                curMusic = intent.getIntExtra("curMusic", 0);
                mTvCurMusic.setText(String.valueOf(curMusic + 1));
                curPercent = intent.getIntExtra("curPercent", 0);
                position = intent.getIntExtra("position", 0);
                secondaryProgress = intent.getIntExtra("secondaryProgress", 0);
                mHandler.sendEmptyMessage(2);
            } else if (intent.getAction().equals(BROADCAST_NEXT_MUSIC)) {
                Log.i("onStart", "------------BROADCAST_NEXT_MUSIC----------------");
                curMusic = intent.getIntExtra("curMusic", 0);
                mTvCurMusic.setText(String.valueOf(curMusic + 1));
                mHandler.sendEmptyMessage(3);
            }
        }

    }


    private void showMapDialog() {
        final Dialog dialog = new MapChooesDialog(AlarmDetailActivity.this,
                R.style.chooes_dialog_style);
        dialog.show();
        Window window = dialog.getWindow();
        RelativeLayout rlGaode = (RelativeLayout) window.findViewById(R.id.rl_chooes_dialog_gaode);
        RelativeLayout rlGaodeLine = (RelativeLayout) window.findViewById(R.id.rl_line_gaode);
        rlGaode.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                goToMapGaode(lastAddress, lastLa, lastLo);
                dialog.dismiss();
            }
        });
        RelativeLayout rlBaidu = (RelativeLayout) window.findViewById(R.id.rl_chooes_dialog_baidu);
        RelativeLayout rlBaiduLine = (RelativeLayout) window.findViewById(R.id.rl_line_baidu);
        rlBaidu.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                goToBaiduMap(lastAddress, lastLa, lastLo);
                dialog.dismiss();
            }
        });
        RelativeLayout rlTX = (RelativeLayout) window.findViewById(R.id.rl_chooes_dialog_tx);
        rlTX.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                goToMapTX(lastAddress, lastLa, lastLo);
                dialog.dismiss();
            }
        });
        RelativeLayout rlCancel = (RelativeLayout) window.findViewById(R.id.rl_chooes_dialog_cancel);
        rlCancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    /**
     * 启动高德App进行导航
     *
     * @param sourceApplication 必填 第三方调用应用名称。如 amap
     * @param poiname           非必填 目的地名称
     * @param lat               必填 终点纬度
     * @param lon               必填 终点经度
     * @param dev               必填 是否偏移(0:lat 和 lon 是已经加密后的,不需要国测加密; 1:需要国测加密)
     * @param style             必填 预设的导航方式 t = 0（驾车）= 1（公交）= 2（步行）= 3（骑行）= 4（火车）= 5（长途客车）
     */
    public void goToMapGaode(String address, double lat, double lon) {
        String sourceApplication = "com.sfr.dbuttonapplication";
        String poiname = "";
        String dev = "0";
        String style = "0";
//        //启动路径规划页面
//        String uri = "amapuri://route/plan/?dlat=" + lat + "&dlon=" + lon + "&dname=" + poiname + "&dev=1&t=0";
//        Intent intent = new Intent("android.intent.action.VIEW", android.net.Uri.parse(uri));
//        intent.setPackage("com.autonavi.minimap");
//        startActivity(intent);

        if (!FileUtils.isInstalled(AlarmDetailActivity.this, "com.autonavi.minimap")) {
            ToastUtils.shortToast(AlarmDetailActivity.this, "请先安装高德地图客户端");
            return;
        }
        LatLng endPoint = BD2GCJ(new LatLng(lat, lon));//坐标转换
        StringBuffer stringBuffer = new StringBuffer("androidamap://navi?sourceApplication=").append("amap");
        stringBuffer.append("&lat=").append(endPoint.latitude)
                .append("&lon=").append(endPoint.longitude).append("&keywords=" + address)
                .append("&dev=").append(dev)
                .append("&style=").append(style);
        Intent intent = new Intent("android.intent.action.VIEW", Uri.parse(stringBuffer.toString()));
        intent.setPackage("com.autonavi.minimap");
        startActivity(intent);

    }

    /**
     * 跳转百度地图
     *
     * @param address 目的地
     * @param lat     必填 纬度
     * @param lon     必填 经度
     */
    private void goToBaiduMap(String address, double lat, double lon) {
        if (!FileUtils.isInstalled(AlarmDetailActivity.this, "com.baidu.BaiduMap")) {
            ToastUtils.shortToast(AlarmDetailActivity.this, "请先安装百度地图客户端");
            return;
        }
        Intent intent = new Intent();
        intent.setData(Uri.parse("baidumap://map/direction?destination=latlng:"
                + lat + ","
                + lon + "|name:" + address + // 终点
                "&mode=driving" + // 导航路线方式
                "&src=" + getPackageName()));
        startActivity(intent); // 启动调用
    }


    /**
     * 启动腾讯地图App进行导航
     *
     * @param address 目的地
     * @param lat     必填 纬度
     * @param lon     必填 经度
     */
    public void goToMapTX(String address, double lat, double lon) {
//        // 启动路径规划页面
//        Intent naviIntent = new Intent("android.intent.action.VIEW",
//                android.net.Uri.parse("qqmap://map/routeplan?type=drive&from=&fromcoord=&to="
//                        + address + "&tocoord=" + lat + "," + lon + "&policy=0&referer=appName"));
//        startActivity(naviIntent);
        if (!FileUtils.isInstalled(AlarmDetailActivity.this, "com.tencent.map")) {
            ToastUtils.shortToast(AlarmDetailActivity.this, "请先安装腾讯地图客户端");
            return;
        }
        LatLng endPoint = BD2GCJ(new LatLng(lat, lon));//坐标转换
        StringBuffer stringBuffer = new StringBuffer("qqmap://map/routeplan?type=drive")
                .append("&tocoord=").append(endPoint.latitude).append(",").append(endPoint.longitude).append("&to=" + address);
        Intent intent = new Intent("android.intent.action.VIEW", Uri.parse(stringBuffer.toString()));
        startActivity(intent);
    }

    /**
     * BD-09 坐标转换成 GCJ-02 坐标
     */
    public static LatLng BD2GCJ(LatLng bd) {
        double x = bd.longitude - 0.0065, y = bd.latitude - 0.006;
        double z = Math.sqrt(x * x + y * y) - 0.00002 * Math.sin(y * Math.PI);
        double theta = Math.atan2(y, x) - 0.000003 * Math.cos(x * Math.PI);

        double lng = z * Math.cos(theta);//lng
        double lat = z * Math.sin(theta);//lat
        return new LatLng(lat, lng);
    }

    /**
     * GCJ-02 坐标转换成 BD-09 坐标
     */
    public static LatLng GCJ2BD(LatLng bd) {
        double x = bd.longitude, y = bd.latitude;
        double z = Math.sqrt(x * x + y * y) + 0.00002 * Math.sin(y * Math.PI);
        double theta = Math.atan2(y, x) + 0.000003 * Math.cos(x * Math.PI);
        double tempLon = z * Math.cos(theta) + 0.0065;
        double tempLat = z * Math.sin(theta) + 0.006;
        return new LatLng(tempLat, tempLon);
    }


}
