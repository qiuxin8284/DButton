package com.sfr.dbuttonapplication.ota;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;

import com.linkingdigital.ble.BeadsAudio;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileUtils {
//    public static final String FILE_PATH = Environment.getExternalStorageDirectory() + File.separator + "dbutton.mp3";
//    public static final String WAV_FILE_PATH = Environment.getExternalStorageDirectory() + File.separator + "dbutton.wav";

    private String file_path = "";
    private String wav_file_path = "";

    public static final int MESSAGE_FILE_BEGIN = 0;
    public static final int MESSAGE_FILE_PROCESS = MESSAGE_FILE_BEGIN + 1;
    public static final int MESSAGE_FILE_END = MESSAGE_FILE_BEGIN + 2;

    private Context mContext;
    private BufferedOutputStream bos = null;
    private FileOutputStream fos = null;
    private File file = null;
    private File wav_file = null;
    private boolean mIsRecording;

    private Handler mFileHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MESSAGE_FILE_BEGIN:
                    try {
                        File dir = new File(file_path);
                        if (!dir.exists() && dir.isDirectory()) {//判断文件目录是否存在
                            dir.mkdirs();
                        }
                        file = new File(file_path);
                        if (file.exists()) {
                            file.delete();
                        }
                        file.createNewFile();

                        wav_file = new File(wav_file_path);
                        if (wav_file.exists()) {
                            wav_file.delete();
                        }
                        wav_file.createNewFile();

                        fos = new FileOutputStream(file);
                        bos = new BufferedOutputStream(fos);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case MESSAGE_FILE_PROCESS:
                    byte[] content = msg.getData().getByteArray("content");
                    if (null != bos && null != content) {
                        try {
                            byte[] mp3_content = BeadsAudio.adpcm_to_mp3(content);
                            bos.write(mp3_content);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                case MESSAGE_FILE_END:
                    if (null != bos) {
                        try {
                            byte[] end_byte = BeadsAudio.mp3_stop();
                            if(null!=end_byte) {
                                bos.write(end_byte);
                                bos.close();
                                fos.close();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    //pcm2wav();
                    break;
            }
        }
    };

    public FileUtils(Context ctx) {
        mContext = ctx;
        mIsRecording = false;
    }

    public void beginWriteFile(String filePath,String wavFilePath) {
        BeadsAudio.mp3_start(4000);
        this.file_path = Environment.getExternalStorageDirectory() + File.separator + filePath;
        this.wav_file_path = Environment.getExternalStorageDirectory() + File.separator + wavFilePath;
        mIsRecording = true;
        mFileHandler.sendEmptyMessage(MESSAGE_FILE_BEGIN);
    }

    public void processWriteFile(byte[] content) {
        Message msg = mFileHandler.obtainMessage(MESSAGE_FILE_PROCESS);
        Bundle data = new Bundle();
        data.putByteArray("content", content);
        msg.setData(data);
        mFileHandler.dispatchMessage(msg);
    }

    public void endWriteFile() {
        mIsRecording = false;
        mFileHandler.sendEmptyMessage(MESSAGE_FILE_END);
    }

    public boolean isRecording(){
        return mIsRecording;
    }

    private void pcm2wav(){
        try {
            FileInputStream fis = new FileInputStream(file_path);
            FileOutputStream fos = new FileOutputStream(wav_file_path);
            int PCMSize = 0;
            byte[] buf = new byte[1024 * 4];
            int size = fis.read(buf);
            while (size != -1) {
                PCMSize += size;
                size = fis.read(buf);
            }
            fis.close();
            WaveHeader header = new WaveHeader(PCMSize + (44 - 8));
            byte[] h = header.getHeader();
            fos.write(h, 0, h.length);
            fis = new FileInputStream(file_path);
            size = fis.read(buf);
            while (size != -1) {
                fos.write(buf, 0, size);
                size = fis.read(buf);
            }
            fis.close();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
