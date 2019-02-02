package com.sfr.dbuttonapplication.ota;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public final class OTAConfig {
    public static final String SERVICE_UUID = "00008072-0000-544c-8267-4c4442454144";
    public static final String CHARACTERISTIC_UUID_WRITE = "00007201-0000-544c-8267-4c4442454144";

    public static final int OTA_PREPARE = 0xFF00;
    public static final int OTA_START = 0xFF01;
    public static final int OTA_END = 0xFF02;

    private static final int TAG_OTA_WRITE = 1;
    private static final int TAG_OTA_READ = 2;
    private static final int TAG_OTA_LAST = 3;
    private static final int TAG_OTA_LAST_READ = 10;
    private static final int TAG_OTA_PRE_READ = 4;
    private static final int TAG_OTA_PREPARE = 5; // prepare
    private static final int TAG_OTA_START = 7;
    private static final int TAG_OTA_END = 8;
    private static final int TAG_OTA_ENABLE_NOTIFICATION = 9;

    public static byte[] readFirmware(String fw_file_path) {
        try {
            InputStream stream = new FileInputStream(fw_file_path);
            int length = stream.available();
            byte[] firmware = new byte[length];
            stream.read(firmware);
            stream.close();
            return firmware;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
