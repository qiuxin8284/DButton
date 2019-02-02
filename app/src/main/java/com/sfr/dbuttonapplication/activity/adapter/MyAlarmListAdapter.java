package com.sfr.dbuttonapplication.activity.adapter;

import android.content.Context;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.sfr.dbuttonapplication.R;
import com.sfr.dbuttonapplication.entity.AlarmResultData;
import com.sfr.dbuttonapplication.http.HttpSendJsonManager;
import com.sfr.dbuttonapplication.utils.LogUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;


public class MyAlarmListAdapter extends BaseAdapter {

    private ArrayList<AlarmResultData> mList = new ArrayList<AlarmResultData>();
    protected LayoutInflater mInflater;
    protected Context cxt;
    private boolean mIsMulMode = false;
    private HashMap<Integer, AlarmResultData> mSelectedMap = new HashMap<Integer, AlarmResultData>();
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");// HH:mm:ss
    private int mSelectedCount;

    public MyAlarmListAdapter(Context context, ArrayList<AlarmResultData> list) {
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
            v = mInflater.inflate(R.layout.my_alarm_list_adapter, null);
            holder = new HolderView();
            holder.tvTime = (TextView)v.findViewById(R.id.tv_alarm_time);
            holder.tvAddress = (TextView)v.findViewById(R.id.tv_alarm_address);
            holder.ivRight = (ImageView)v.findViewById(R.id.iv_right);
            v.setTag(holder);
        } else {
            holder = (HolderView) v.getTag();
        }

        AlarmResultData alarmResultData = mList.get(position);
        holder.tvTime.setText("报警时间:"+ simpleDateFormat.format(new Date(Long.valueOf(alarmResultData.getAlarmData().getBeginTime()))));
        holder.tvAddress.setText("报警地址:"+alarmResultData.getAlarmData().getAddress());


        if (mIsMulMode) {
            if (mSelectedMap.containsKey(position)) {
                System.out.print("containKey:"+alarmResultData.getAlarmId());
                v.setSelected(true);
                holder.ivRight.setImageResource(R.mipmap.checkbox_yes);
            } else {
                System.out.print("!containKey:"+alarmResultData.getAlarmId());
                v.setSelected(false);
                holder.ivRight.setImageResource(R.mipmap.checkbox_no);
            }
        }else{
            holder.ivRight.setImageResource(R.mipmap.img_right);
        }

//        if(!TextUtils.isEmpty(DButtonApplication.mUserData.getImg())){
//            ImageLoader.getInstance().displayImage(DButtonApplication.mUserData.getImg(),mIvHead);
//        }
        return v;
    }


    public class HolderView {
        private TextView tvTime;
        private TextView tvAddress;
        private ImageView ivRight;
    }

    private String mIDS = "";//调用赋值
    private DelAlarmTask mDelAlarmTask;

    private class DelAlarmTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {
            if (HttpSendJsonManager.delAlarm(cxt,mIDS,"1")) {
                //直接记录删除position提示即可-或者list刷新
                //mHandler.sendEmptyMessage(CONTACT_SET_SUCCESS);
            } else {
                //弹出错误提示
                //mHandler.sendEmptyMessage(CONTACT_SET_FALSE);
            }
            return null;
        }
    }

    public void setSelectModel(boolean flag) {
        mIsMulMode = flag;
    }

    public void changeSelectMode() {
        if (mIsMulMode) {
            mIsMulMode = false;
            mSelectedMap.clear();
            mSelectedMap = new HashMap<Integer,AlarmResultData>();
            mSelectedCount = mSelectedMap.size();
        } else {
            mIsMulMode = true;
            mSelectedCount = mSelectedMap.size();
        }
        notifyDataSetChanged();
    }

    public HashMap<Integer, AlarmResultData> getmSelectedMap() {
        return mSelectedMap;
    }

    public void setSelecte(int position){
        LogUtil.println("setSelecte position:"+position);
        if(mSelectedMap.containsKey(position)){
            LogUtil.println("setSelecte remove");
            mSelectedMap.remove(position);
        }else {
            LogUtil.println("setSelecte put");
            mSelectedMap.put(position, mList.get(position));
        }
        LogUtil.println("setSelecte mSelectedMap.size():"+mSelectedMap.size());
        notifyDataSetChanged();
    }
    public void setSelecteAll(){
        if(mSelectedMap.size() == mList.size()){
            mSelectedMap = new HashMap<Integer,AlarmResultData>();
        }else {
            mSelectedMap = new HashMap<Integer,AlarmResultData>();
            for(int i = 0;i<mList.size();i++){
                mSelectedMap.put(i,mList.get(i));
            }
        }
        notifyDataSetChanged();
        LogUtil.println("setSelecte mSelectedMap.size():"+mSelectedMap.size());
        notifyDataSetChanged();
    }
    public boolean isMulMode() {
        return mIsMulMode;
    }

    public int getSelectSize(){
        return mSelectedMap.size();
    }
}
