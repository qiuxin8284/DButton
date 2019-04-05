package com.sfr.dbuttonapplication.activity.adapter;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.sfr.dbuttonapplication.R;
import com.sfr.dbuttonapplication.entity.Music;

import java.util.ArrayList;


public class VoiceListAdapter extends BaseAdapter {

    private ArrayList<Music> mList = new ArrayList<Music>();
    protected LayoutInflater mInflater;
    protected Context cxt;
    private Handler mHandler;
    private int mWhat;

    public VoiceListAdapter(Context context, ArrayList<Music> list, Handler handler, int what) {
        cxt = context;
        mInflater = LayoutInflater.from(this.cxt);
        mList = list;
        this.mHandler = handler;
        this.mWhat = what;
    }

    public void setList(ArrayList<Music> list) {
        mList = list;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Music getItem(int position) {
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
            v = mInflater.inflate(R.layout.voice_list_adapter, null);
            holder = new HolderView();
            holder.cbChooes = (CheckBox)v.findViewById(R.id.cb_chooes);
            holder.tvName = (TextView) v.findViewById(R.id.tv_name);
            v.setTag(holder);
        } else {
            holder = (HolderView) v.getTag();
        }
        holder.tvName.setText(String.valueOf(position+1));
        holder.cbChooes.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    Message message = new Message();
                    message.what = mWhat;
                    message.obj = position+1;
                    mHandler.sendMessage(message);
                }
            }
        });
        return v;
    }

    public class HolderView {
        private TextView tvName;
        private CheckBox cbChooes;
    }


}
