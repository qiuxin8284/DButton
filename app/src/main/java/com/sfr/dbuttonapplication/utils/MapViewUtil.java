package com.sfr.dbuttonapplication.utils;

import android.view.View;
import android.widget.ZoomControls;

import com.baidu.mapapi.map.MapView;

public class MapViewUtil {

    public static void goneMap(MapView mMapView) {

        int childCount = mMapView.getChildCount();
        View zoom = null;
        for (int i = 0; i < childCount; i++) {
            View child = mMapView.getChildAt(i);
            if (child instanceof ZoomControls) {
                zoom = child;
                break;
            }
        }
        //zoom.setVisibility(View.GONE);
        mMapView.removeViewAt(1);
    }

    public static void goneMap(MapView mMapView,int size) {

        int childCount = mMapView.getChildCount();
        View zoom = null;
        for (int i = 0; i < childCount; i++) {
            View child = mMapView.getChildAt(i);
            if (child instanceof ZoomControls) {
                zoom = child;
                break;
            }
        }
        //zoom.setVisibility(View.GONE);
        mMapView.removeViewAt(size);
    }
}
