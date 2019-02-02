package com.sfr.dbuttonapplication.activity.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.sfr.dbuttonapplication.R;
import com.sfr.dbuttonapplication.entity.DButtonData;

import java.util.ArrayList;


public class BindDButtonListAdapter extends BaseAdapter {

    private ArrayList<DButtonData> mList = new ArrayList<DButtonData>();
    protected LayoutInflater mInflater;
    protected Context cxt;

    public BindDButtonListAdapter(Context context, ArrayList<DButtonData> list) {
        cxt = context;
        mInflater = LayoutInflater.from(this.cxt);
        mList = list;
    }

    public void setServiceList(ArrayList<DButtonData> list) {
        mList = list;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public DButtonData getItem(int position) {
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
            v = mInflater.inflate(R.layout.bind_dbutton_list_adapter, null);
            holder = new HolderView();
            holder.mTvMac = (TextView)v.findViewById(R.id.tv_mac);
            v.setTag(holder);
        } else {
            holder = (HolderView) v.getTag();
        }

        holder.mTvMac.setText(mList.get(position).getDevice_mac());
        return v;
    }


    public class HolderView {
        //private LinearLayout mLlBind;
        private TextView mTvMac;
    }

}
