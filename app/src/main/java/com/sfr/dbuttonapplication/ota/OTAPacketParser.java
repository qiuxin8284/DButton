package com.sfr.dbuttonapplication.ota;

public class OTAPacketParser {

    private int mPacketTotal;
    private int mCurrentIndex = -1;
    private byte[] mFileData;
    private int mProgress;

    public void clear() {
        mProgress = 0;
        mPacketTotal = 0;
        mCurrentIndex = -1;
        mFileData = null;
    }

    public void set(byte[] data) {
        clear();
        mFileData = data;
        int length = mFileData.length;
        int size = 16;
        if (length % size == 0) {
            mPacketTotal = length / size;
        } else {
            mPacketTotal = (int) Math.floor(length / size + 1);
        }
    }

    public boolean hasNextPacket() {
        return mPacketTotal > 0 && (mCurrentIndex + 1) < mPacketTotal;
    }

    public boolean isLast() {
        return (mCurrentIndex + 1) == mPacketTotal;
    }

    public int getNextPacketIndex() {
        return mCurrentIndex + 1;
    }

    public byte[] getNextPacket() {
        int index = getNextPacketIndex();
        byte[] packet = getPacket(index);
        mCurrentIndex = index;
        return packet;
    }

    public byte[] getPacket(int index){
        int length = mFileData.length;
        int size = 16;
        int packetSize;

        if (length > size) {
            if ((index + 1) == mPacketTotal) {
                packetSize = length - index * size;
            } else {
                packetSize = size;
            }
        } else {
            packetSize = length;
        }
        packetSize = packetSize + 4;
        byte[] packet = new byte[20];
        for (int i = 0; i < 20; i++) {
            packet[i] = (byte) 0xFF;
        }
        System.arraycopy(mFileData, index * size, packet, 2, packetSize - 4);
        fillIndex(packet, index);
        int crc = crc16(packet);
        fillCrc(packet, crc);
        return packet;
    }

    public void fillIndex(byte[] packet, int index) {
        int offset = 0;
        packet[offset++] = (byte) (index & 0xFF);
        packet[offset] = (byte) (index >> 8 & 0xFF);
    }

    public void fillCrc(byte[] packet, int crc) {
        int offset = packet.length - 2;
        packet[offset++] = (byte) (crc & 0xFF);
        packet[offset] = (byte) (crc >> 8 & 0xFF);
    }

    public int crc16(byte[] packet) {
        int length = packet.length - 2;
        short[] poly = new short[]{0, (short) 0xA001};
        int crc = 0xFFFF;
        int ds;

        for (int j = 0; j < length; j++) {
            ds = packet[j];
            for (int i = 0; i < 8; i++) {
                crc = (crc >> 1) ^ poly[(crc ^ ds) & 1] & 0xFFFF;
                ds = ds >> 1;
            }
        }
        return crc;
    }

    public boolean invalidateProgress() {
        float a = getNextPacketIndex();
        float b = mPacketTotal;
        int progress = (int) Math.floor((a / b * 100));
        if (progress == mProgress)
            return false;
        mProgress = progress;

        return true;
    }

    public int getProgress() {
        return mProgress;
    }

    public int getIndex(){
        return mCurrentIndex;
    }

    public int getTotal(){return  mPacketTotal;}
}
