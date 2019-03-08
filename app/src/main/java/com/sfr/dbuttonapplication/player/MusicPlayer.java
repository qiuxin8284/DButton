package com.sfr.dbuttonapplication.player;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnInfoListener;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.sfr.dbuttonapplication.DButtonApplication;
import com.sfr.dbuttonapplication.activity.alarm.AlarmDetailActivity;
import com.sfr.dbuttonapplication.entity.Music;
import com.sfr.dbuttonapplication.utils.LogUtil;

import java.io.IOException;
import java.util.List;


public class MusicPlayer extends Service implements OnBufferingUpdateListener,
        OnCompletionListener, MediaPlayer.OnPreparedListener, OnInfoListener,
        OnErrorListener {

	public static final String PLAY_STATE_previous = "previous";
	public static final String PLAY_STATE_next = "next";
	public static final String PLAY_STATE_play = "play";
	public static final String PLAY_STATE_pause = "pause";
	public static final String PLAY_STATE_function = "function";
	public static final String PLAY_STATE_CHANGE = "change";
	public static final String PLAY_STATE_STOP_CHANGE = "stop";

	public MediaPlayer mediaPlayer;
	List<Music> musicList;
	int Max;
	int curMusic = 0;
	int bufferingProgress;
	int curFunction = 0;
	private boolean isPause=false;

	DButtonApplication mApp;
	int progress;
	int sprogress;
	int max;

	Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			mHandler.sendEmptyMessageDelayed(1, 500);
			switch(msg.what) {
			case 1:
				if(mediaPlayer == null) return ;
				int position = mediaPlayer.getCurrentPosition();
				int duration = mediaPlayer.getDuration();
				int percent = 0;
				if(duration!=0) percent = position * 100 / duration;
				LogUtil.println("voiceTime position:" + position);
				LogUtil.println("voiceTime duration:" + duration);
				LogUtil.println("voiceTime bufferingProgress:" + bufferingProgress);
				LogUtil.println("voiceTime mediaPlayer:" + mediaPlayer.toString());


				Intent intent = new Intent(AlarmDetailActivity.BROADCAST_REFRESH_PROGRESS);
				intent.putExtra("curMusic", curMusic);
				intent.putExtra("curPercent", percent);
				intent.putExtra("position", position/1000);
				intent.putExtra("secondaryProgress", bufferingProgress);
				sendBroadcast(intent);

				break;
			case 2:
				sprogress = progress * mediaPlayer.getDuration()
				/ max;
				Log.i("mHandler","------------2----------------");
				Log.i("mHandler","------------sprogress---------------:"+sprogress);
				Log.i("mHandler","------------progress---------------:"+progress);
				Log.i("mHandler","------------max---------------:"+max);
				Log.i("mHandler","------------mediaPlayer.getDuration()---------------:"+mediaPlayer.getDuration());
				break;
			case 3:
				mediaPlayer.seekTo(sprogress);
				Log.i("mHandler","------------3----------------");
				Log.i("mHandler","------------sprogress---------------:"+sprogress);
				break;
			}
		}

	};

	@Override
	public void onCreate() {
		mApp = (DButtonApplication) getApplication();
		musicList = mApp.getMusicList();
		Max = musicList.size();
		super.onCreate();
	}

	private void sendMessage() {
		if(mHandler.hasMessages(1)) {
			mHandler.removeMessages(1);
		}
		mHandler.sendEmptyMessage(1);
	}
	private void removeMessage() {
		if(mHandler.hasMessages(1)) {
			mHandler.removeMessages(1);
		}
	}


	public void play() {
		LogUtil.println("loadFileData:play");
		LogUtil.println("loadFileData："+"------------play----------------");
		releaseMediaPlay();
		try {
			mediaPlayer = new MediaPlayer();
			mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			mediaPlayer.setOnBufferingUpdateListener(this);
			mediaPlayer.setOnPreparedListener(this);
			mediaPlayer.setOnCompletionListener(this);
			//			Uri uri = Uri.parse(musicList.get(curMusic).getUrl());  

			mediaPlayer.reset();
			String dateSource = musicList.get(curMusic).getUrl();
			if(musicList.get(curMusic).getUrl().contains(" ")){
				dateSource=dateSource.replaceAll(" ", "%20");
			}
			//replaceAll("\\+","%20")
			//			if(musicList.get(curMusic).getUrl().contains("+")){
			//				dateSource=dateSource.replace("+", "%2B");
			//			}
			//			if(musicList.get(curMusic).getUrl().contains("/")){
			//				dateSource=dateSource.replace("/", "%2F");
			//			}
			//			if(musicList.get(curMusic).getUrl().contains("?")){
			//				dateSource=dateSource.replace("?", "%3F");
			//			}
			//			if(musicList.get(curMusic).getUrl().contains("%")){
			//				dateSource=dateSource.replace("%", "%25");
			//			}
			//			if(musicList.get(curMusic).getUrl().contains("#")){
			//				dateSource=dateSource.replace("#", "%23");
			//			}
			//			if(musicList.get(curMusic).getUrl().contains("&")){
			//				dateSource=dateSource.replace("&", "%26");
			//			}
			//			if(musicList.get(curMusic).getUrl().contains("=")){
			//				dateSource=dateSource.replace("=", "%3D");
			//			}
			LogUtil.println("loadFileData："+"musicList.get(curMusic).getUrl():"+dateSource);
			mediaPlayer.setDataSource(dateSource);
			mediaPlayer.prepare();
			int duration = mediaPlayer.getDuration();
			LogUtil.println("voiceTime setDataSource duration:" + duration);

			sendChangeMusicBroadcast();

		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void sendChangeMusicBroadcast() {
		removeMessage();
		if(mediaPlayer == null) return ;
		int position = mediaPlayer.getCurrentPosition();
		int duration = mediaPlayer.getDuration();
		int percent = position * 100 / duration;

		Intent intent = new Intent(AlarmDetailActivity.BROADCAST_CHANGE_MUSIC);
		intent.putExtra("curMusic", curMusic);
		intent.putExtra("curPercent", percent);
		intent.putExtra("secondaryProgress", bufferingProgress);
		sendBroadcast(intent);

		mHandler.sendEmptyMessageDelayed(1, 500);
	}


	public void pause() {
		if(mediaPlayer != null && mediaPlayer.isPlaying()) {
			mediaPlayer.pause();
			isPause=true;
			removeMessage();
		}
	}

	public void resumePlay() {
		if(mediaPlayer == null) return ;
		LogUtil.i("mediaPlayer", String.valueOf(mediaPlayer.isPlaying()));
		if(mediaPlayer.isPlaying()) {
			mediaPlayer.start();
			sendMessage();
		}else if(isPause){
			isPause=false;
			mediaPlayer.start();
			sendMessage();
		} else {
			play();
		}


	}

	public void stop() {
		if (mediaPlayer != null) {
			mediaPlayer.stop();
			mediaPlayer.release();
			removeMessage();
			mediaPlayer = null;
		}
	}

	public void previous() {
		if (curMusic == 0) {
			curMusic = Max - 1;
			play();
		} else {
			curMusic--;
			play();
		}
	}

	public void next() {
		if (curMusic + 1 == Max) {
			curMusic = 0;
			play();
		} else {
			curMusic++;
			play();
		}
	}

	/**
	 * 顺序播放
	 */
	public void slidShow() {
		curMusic++;
		LogUtil.println("loadFileData："+"------------slidShow curMusic----------------"+Max);
		LogUtil.println("loadFileData："+"------------slidShow Max----------------"+Max);
		LogUtil.println("loadFileData："+"------------sendBroadcast----------------");
		if (curMusic == Max) {
			curMusic=0;
			play();
			//			releaseMediaPlay();
			//			removeMessage();
		} else {
			play();
		}
		Intent intent = new Intent(AlarmDetailActivity.BROADCAST_NEXT_MUSIC);
//		BuddhismApplication.curMusice=curMusic;
//		BuddhismApplication.isCurMusic=true;
		intent.putExtra("curMusic", curMusic);
		sendBroadcast(intent);
	}

	/**
	 * 循环播放
	 */
	public void loop() {
		curMusic++;
		if (curMusic == Max) {
			curMusic = 0;
		} 
		play();
	}

	/**
	 * 随机播放
	 */
	public void random() {
		curMusic = (int) (Math.random() * Max);
		play();
	}

	public void releaseMediaPlay() {
		if (mediaPlayer != null) {
			if (mediaPlayer.isPlaying()) {
				mediaPlayer.reset();
			}
			mediaPlayer.release();
			mediaPlayer = null;
		}
	}

	@Override
	public void onPrepared(MediaPlayer arg0) {
		arg0.start();
		int duration = mediaPlayer.getDuration();
		LogUtil.println("voiceTime onPrepared duration:" + duration);
		Log.e("mediaPlayer", "onPrepared");
	}

	@Override
	public void onCompletion(MediaPlayer arg0) {
		LogUtil.println("loadFileData："+"------------play----------------"+curFunction);
		switch(curFunction) {
		case 0:
			slidShow();
			break;
		case 1:
			loop();
			break;
		case 2:
			play();
			break;
		case 3:
			random();
			break;
		}



	}

	@Override
	public boolean onInfo(MediaPlayer mp, int what, int extra) {
		Log.i("yy", "onInfo what:" + what + ":" + extra);
		return false;
	}

	@Override
	public void onBufferingUpdate(MediaPlayer arg0, int bufferingProgress) {
		this.bufferingProgress = bufferingProgress;
		Log.i("yy", bufferingProgress + "% buffer");
	}

	@Override
	public boolean onError(MediaPlayer mp, int what, int extra) {
		play();
		return false;
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onStart(Intent intent, int startId) {
		LogUtil.println("loadFileData："+"------------onStart----------------");
		String state = intent.getStringExtra("state");
		if(state.trim().equals(PLAY_STATE_CHANGE)) {
			LogUtil.println("loadFileData："+"PLAY_STATE_CHANGE");
			progress = intent.getIntExtra("progress", -1);
			max = intent.getIntExtra("max", -1);
			mHandler.sendEmptyMessage(2);
		} else if(state.trim().equals(PLAY_STATE_STOP_CHANGE)) {
			LogUtil.println("loadFileData："+"PLAY_STATE_STOP_CHANGE");
			mHandler.sendEmptyMessage(3);
		}else{
			LogUtil.println("loadFileData："+"state:"+state);
			int position = intent.getIntExtra("position", -1);
			LogUtil.println("loadFileData："+"position:"+position);
			int function = intent.getIntExtra("function", -1);
			LogUtil.println("loadFileData："+"function:"+function);
			if(position != -1) curMusic = position; 
			if(function != -1) curFunction = function;
			LogUtil.println("loadFileData："+"curMusic"+curMusic);
			LogUtil.println("loadFileData："+"curFunction"+curFunction);
			if(state.trim().equals(PLAY_STATE_previous)) {
				LogUtil.println("loadFileData："+"PLAY_STATE_previous");
				previous();
			} else if(state.trim().equals(PLAY_STATE_next)) {
				LogUtil.println("loadFileData："+"PLAY_STATE_next");
				next();
			} else if(state.trim().equals(PLAY_STATE_play)) {
				LogUtil.println("loadFileData："+"PLAY_STATE_play");
				resumePlay();
				if(position == -1) return ;
			} else if(state.trim().equals(PLAY_STATE_pause)) {
				LogUtil.println("loadFileData："+"PLAY_STATE_pause");
				pause();
				return ;
			} else if(state.trim().equals(PLAY_STATE_function)) {
				LogUtil.println("loadFileData："+"PLAY_STATE_function");
				return ;
			}
			play();
		}
		super.onStart(intent, startId);
	}

	@Override
	public void onDestroy() {
		stop();
		super.onDestroy();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	public class inComingTelegram extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if(action.trim().equals(TelephonyManager.EXTRA_STATE_RINGING)) {

			} else {

			}
		}

	}


}