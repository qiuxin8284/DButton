package com.sfr.dbuttonapplication.entity;

import android.location.Location;

import com.baidu.mapapi.model.LatLng;

/**
 * Created by ASUS on 2018/12/3.
 */

public class PointData {
    private LatLng location;
    private long pointTime;

    public LatLng getLocation() {
        return location;
    }

    public void setLocation(LatLng location) {
        this.location = location;
    }

    public long getPointTime() {
        return pointTime;
    }

    public void setPointTime(long pointTime) {
        this.pointTime = pointTime;
    }

    @Override
    public String toString() {
        return "PointData{" +
                "location=" + location +
                ", pointTime=" + pointTime +
                '}';
    }
}
