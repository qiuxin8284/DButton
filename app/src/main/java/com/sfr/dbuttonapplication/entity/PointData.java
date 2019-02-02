package com.sfr.dbuttonapplication.entity;

import android.location.Location;

/**
 * Created by ASUS on 2018/12/3.
 */

public class PointData {
    private Location location;
    private long pointTime;

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
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
