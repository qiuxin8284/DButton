package com.sfr.dbuttonapplication.activity.adapter;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.sfr.dbuttonapplication.R;
import com.sfr.dbuttonapplication.activity.fragment.ContactFragment;
import com.sfr.dbuttonapplication.activity.widget.DeleteConfirmDialog;
import com.sfr.dbuttonapplication.activity.widget.LoadingProgressDialog;
import com.sfr.dbuttonapplication.entity.UserData;
import com.sfr.dbuttonapplication.http.HttpSendJsonManager;

import java.util.ArrayList;


public class ChooesListAdapter extends BaseAdapter {

    private ArrayList<String> mList = new ArrayList<String>();
    protected LayoutInflater mInflater;
    protected Context cxt;
    private Handler mHandler;
    private int mWhat;

    public ChooesListAdapter(Context context, ArrayList<String> list, Handler handler,int what) {
        cxt = context;
        mInflater = LayoutInflater.from(this.cxt);
        mList = list;
        this.mHandler = handler;
        this.mWhat = what;
    }

    public void setList(ArrayList<String> list) {
        mList = list;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public String getItem(int position) {
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
            v = mInflater.inflate(R.layout.chooes_list_adapter, null);
            holder = new HolderView();
            holder.cbChooes = (CheckBox)v.findViewById(R.id.cb_chooes);
            holder.tvName = (TextView) v.findViewById(R.id.tv_name);
            v.setTag(holder);
        } else {
            holder = (HolderView) v.getTag();
        }
        holder.tvName.setText(mList.get(position));
        holder.cbChooes.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    Message message = new Message();
                    message.what = mWhat;
                    message.obj = mList.get(position);
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
