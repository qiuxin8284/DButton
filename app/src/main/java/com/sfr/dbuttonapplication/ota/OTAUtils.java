package com.sfr.dbuttonapplication.ota;

import android.support.annotation.NonNull;

import com.icen.blelibrary.BleBaseApplication;

public class OTAUtils {

    private static final int OTA_STEP_PRE_PAIRE = 0;
    private static final int OTA_STEP_START = OTA_STEP_PRE_PAIRE + 1;
    private static final int OTA_STEP_WRITE = OTA_STEP_PRE_PAIRE + 2;
    private static final int OTA_STEP_LAST = OTA_STEP_PRE_PAIRE + 3;
    private static final int OTA_STEP_END = OTA_STEP_PRE_PAIRE + 4;

    private BleBaseApplication mApp;
    private OTAPacketParser mParser;
    private String mFilePath;
    private int mCurrentStep;

    //file_path本地路径
    public OTAUtils(@NonNull BleBaseApplication context, @NonNull String file_path) {
        mApp = context;
        mCurrentStep = OTA_STEP_PRE_PAIRE;
        mFilePath = file_path;
        mParser = new OTAPacketParser();
        mParser.set(OTAConfig.readFirmware(file_path));
    }

    public OTAPacketParser getParser(){return mParser;}

    /**
     * 执行OTA的入口
     * @return 表示当前指示，0:开始，1:写入，2：结束，-1：异常
     */
    public int processOTA(){
        switch (mCurrentStep) {
            case OTA_STEP_PRE_PAIRE:
                if (!sendOTAPrepareCommand()){
                    return -1;
                } else {
                    mCurrentStep = OTA_STEP_START;
                    return 0;
                }
            case OTA_STEP_START:
                if (!sendOTAStartCommand()){
                    return -1;
                } else {
                    mCurrentStep = OTA_STEP_WRITE;
                    return 0;
                }
            case OTA_STEP_WRITE:
                if (mParser.hasNextPacket()) {
                    byte[] current_content = mParser.getNextPacket();
                    if (mParser.isLast()) {
                        mCurrentStep = OTA_STEP_LAST;
                    } else {
                        mCurrentStep = OTA_STEP_WRITE;
                    }

                    if (!sendOTAPackage(current_content)){
                        return -1;
                    } else {
                        return 1;
                    }
                }
                break;
            case OTA_STEP_LAST:
                if (! sendOTAEndCommand()) {
                    return  -1;
                } else {
                    mCurrentStep = OTA_STEP_END;
                    return 1;
                }
            case OTA_STEP_END:
                return 2;
            default:
                return -1;
        }
        return 2;
    }

    /**
     * OTA准备：发送准备OTA指令
     * @return 成功或者失败
     */
    private boolean sendOTAPrepareCommand(){
        byte[] send_content = new byte[]{OTAConfig.OTA_PREPARE & 0xFF, (byte) (OTAConfig.OTA_PREPARE >> 8 & 0xFF)};
        boolean is_success = mApp.getManager().writeCharacteristic(OTAConfig.CHARACTERISTIC_UUID_WRITE, send_content);
        return is_success;
    }

    /**
     * OTA准备：发送开始OTA指令
     * @return 成功或者失败
     */
    private boolean sendOTAStartCommand() {
        byte[] send_content = new byte[]{OTAConfig.OTA_START & 0xFF, (byte) (OTAConfig.OTA_START >> 8 & 0xFF)};
        boolean is_success = mApp.getManager().writeCharacteristic(OTAConfig.CHARACTERISTIC_UUID_WRITE, send_content);
        return is_success;
    }

    private boolean sendOTAPackage(byte[] send_content){
        boolean is_success = mApp.getManager().writeCharacteristic(OTAConfig.CHARACTERISTIC_UUID_WRITE, send_content);
        return is_success;
    }

    private boolean sendOTAEndCommand(){
        int current_index = mParser.getIndex();
        byte[] data = new byte[8];
        data[0] = OTAConfig.OTA_END & 0xFF;
        data[1] = (byte) ((OTAConfig.OTA_END >> 8) & 0xFF);
        data[2] = (byte) (current_index & 0xFF);
        data[3] = (byte) (current_index >> 8 & 0xFF);
        data[4] = (byte) (~current_index & 0xFF);
        data[5] = (byte) (~current_index >> 8 & 0xFF);

        int crc = mParser.crc16(data);
        mParser.fillCrc(data, crc);
        boolean is_success = mApp.getManager().writeCharacteristic(OTAConfig.CHARACTERISTIC_UUID_WRITE, data);
        return is_success;
    }
}
