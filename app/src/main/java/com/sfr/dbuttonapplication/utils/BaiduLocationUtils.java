package com.sfr.dbuttonapplication.utils;

import android.content.Context;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.sfr.dbuttonapplication.DButtonApplication;
import com.sfr.dbuttonapplication.R;

public class BaiduLocationUtils {

    private BaiduLocationUtils() {
        super();
        // TODO Auto-generated constructor stub
    }

    /**
     * 开启定位 (同步定位)
     *
     * @param context
     * @param baiduMap
     */
    public static LatLng locationMyself(Context context, BaiduMap baiduMap) {
        LocationClient bdClient = DButtonApplication.getBDClient();
        if(bdClient==null){
        }
        BDLocation location = bdClient.getLastKnownLocation();
        if(location==null){
        }
        if (location == null)
            location= DButtonApplication.bdLocation;
        if (location == null) {
            Toast.makeText(context, context
                    .getResources().getString(R.string.common_gps_false), Toast.LENGTH_SHORT).show();
            return null;
        }

        MyLocationData locData = new MyLocationData.Builder()
                .accuracy(location.getRadius())// 角度
                .direction(location.getDirection())// 方向
                .latitude(location.getLatitude())// 纬度
                .longitude(location.getLongitude())// 经度
                .build();
        DButtonApplication.nowLatitude = location.getLatitude();// 纬度
        DButtonApplication.nowLongitude = location.getLongitude();// 经度
        // 设置定位数据
        baiduMap.setMyLocationData(locData);
        LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());

        MapStatusUpdate msu = MapStatusUpdateFactory.newLatLng(ll);
        MapStatusUpdate u = MapStatusUpdateFactory.zoomTo(18);
        baiduMap.animateMapStatus(u);
        baiduMap.animateMapStatus(msu);
        return ll;
    }

    public static String initLocation(Context context) {
        LocationClient bdClient = DButtonApplication.getBDClient();
        if(bdClient==null){
        }
        BDLocation location = bdClient.getLastKnownLocation();
        if(location==null){
        }
        if (location == null) {
            //location = DButtonApplication.bdLocation;
            int request_location_code = bdClient.requestLocation();
            android.util.Log.e("SlashInfo", "initLocation::result1= " + request_location_code+"|bdClient.isStarted():"+bdClient.isStarted());
            if(!bdClient.isStarted()){
                bdClient.start();
            }
            request_location_code = bdClient.requestLocation();
            android.util.Log.e("SlashInfo", "initLocation::result2= " + request_location_code+"|bdClient.isStarted():"+bdClient.isStarted());
        }
        if (location == null) {
            //ToastUtils.shortToast(context, context.getResources().getString(R.string.common_gps_false));
        } else {
            DButtonApplication.nowLatitude = location.getLatitude();// 纬度
            DButtonApplication.nowLongitude = location.getLongitude();// 经度
            return location.getAddress().address;
        }
        return "";
    }

    public static String locationMyselfHasRemark(Context context, BaiduMap baiduMap) {
        LocationClient bdClient = DButtonApplication.getBDClient();
        if(bdClient==null){
        }
        BDLocation location = bdClient.getLastKnownLocation();
        if(location==null){
        }
        if (location == null)
            location= DButtonApplication.bdLocation;
        if (location == null) {
            Toast.makeText(context, context
                    .getResources().getString(R.string.common_gps_false), Toast.LENGTH_SHORT).show();
            return "";
        }

        MyLocationData locData = new MyLocationData.Builder()
                .accuracy(location.getRadius())// 角度
                .direction(location.getDirection())// 方向
                .latitude(location.getLatitude())// 纬度
                .longitude(location.getLongitude())// 经度
                .build();
        // 设置定位数据
        baiduMap.setMyLocationData(locData);
        LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());

        baiduMap.clear();
        //定义Maker坐标点
        // 构建Marker图标
        BitmapDescriptor bitmap = BitmapDescriptorFactory
                .fromResource(R.mipmap.img_cake);
        //构建MarkerOption，用于在地图上添加Marker
        OverlayOptions option = new MarkerOptions()
                .position(ll)
                .icon(bitmap);
        //在地图上添加Marker，并显示
        baiduMap.addOverlay(option);
        MapStatusUpdate msu = MapStatusUpdateFactory.newLatLng(ll);
        MapStatusUpdate u = MapStatusUpdateFactory.zoomTo(18);
        baiduMap.animateMapStatus(u);
        baiduMap.animateMapStatus(msu);

//        DButtonApplication.mMapAddressData = new MapAddressData();
//        DButtonApplication.mMapAddressData.setAddress(location.getAddress().address);
//        DButtonApplication.mMapAddressData.setCity(location.getAddress().city);
//        DButtonApplication.mMapAddressData.setDistrict(location.getAddress().district);
//        DButtonApplication.mMapAddressData.setLatitude(String.valueOf(location.getLatitude()));
//        DButtonApplication.mMapAddressData.setLongitude(String.valueOf(location.getLongitude()));
//        DButtonApplication.mMapAddressData.setProvince(location.getAddress().province);
//        DButtonApplication.mMapAddressData.setStreet(location.getAddress().street);
        return location.getAddress().address;
    }

    /**
     * 返回同步定位的location (location在定位失败会返回为空)
     *
     * @return location
     */

    public static BDLocation getBDLocation() {
        LocationClient bdClient = DButtonApplication.getBDClient();
        if(bdClient==null){
        }
        BDLocation location = bdClient.getLastKnownLocation();
        if(location==null){
        }
        if (location == null)
            location= DButtonApplication.bdLocation;
        return location;
    }

//	private void showToast(String str) {
//		// Toast.makeText(context, text, duration)
//	}
}
