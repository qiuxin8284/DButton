package com.sfr.dbuttonapplication.activity.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.sfr.dbuttonapplication.R;
import com.sfr.dbuttonapplication.entity.AlarmResultData;
import com.sfr.dbuttonapplication.utils.LogUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class AlarmListAdapter extends BaseAdapter {

    private ArrayList<AlarmResultData> mList = new ArrayList<AlarmResultData>();
    protected LayoutInflater mInflater;
    protected Context cxt;
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");// HH:mm:ss

    public AlarmListAdapter(Context context, ArrayList<AlarmResultData> list) {
        cxt = context;
        mInflater = LayoutInflater.from(this.cxt);
        mList = list;
    }

    public void setServiceList(ArrayList<AlarmResultData> list) {
        mList = list;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public AlarmResultData getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View v, ViewGroup parent) {
        HolderView holder;
        if (null == v) {
            v = mInflater.inflate(R.layout.alarm_list_adapter, null);
            holder = new HolderView();
            holder.ivHead = (ImageView)v.findViewById(R.id.iv_your_head);
            holder.tvName = (TextView)v.findViewById(R.id.tv_user_name);
            holder.tvID = (TextView)v.findViewById(R.id.tv_user_id);
            holder.tvTime = (TextView)v.findViewById(R.id.tv_alarm_time);
            holder.tvAddress = (TextView)v.findViewById(R.id.tv_alarm_address);
            v.setTag(holder);
        } else {
            holder = (HolderView) v.getTag();
        }
        AlarmResultData alarmResultData = mList.get(position);
        holder.tvName.setText(alarmResultData.getUserData().getName());
        holder.tvID.setText("手机号码:"+alarmResultData.getUserData().getPhone());
        holder.tvAddress.setText("报警地址:"+alarmResultData.getAlarmData().getAddress());
        holder.tvTime.setText("报警时间:"+ simpleDateFormat.format(new Date(Long.valueOf(alarmResultData.getAlarmData().getBeginTime()))));

//        if(!TextUtils.isEmpty(alarmResultData.getUserData().getImg())){
//            ImageLoader.getInstance().displayImage(alarmResultData.getUserData().getImg(),holder.ivHead);
//        }
        if(!TextUtils.isEmpty(alarmResultData.getAlarmData().getVipImg())){
            ImageLoader.getInstance().displayImage(alarmResultData.getAlarmData().getVipImg(),holder.ivHead);
        }
        LogUtil.println("alarmResultData:"+alarmResultData.toString());
        return v;
    }


    public class HolderView {
        private ImageView ivHead;
        private TextView tvName;
        private TextView tvID;
        private TextView tvTime;
        private TextView tvAddress;
    }

}
