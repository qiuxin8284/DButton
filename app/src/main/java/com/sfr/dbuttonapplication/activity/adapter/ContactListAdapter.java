package com.sfr.dbuttonapplication.activity.adapter;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.sfr.dbuttonapplication.R;
import com.sfr.dbuttonapplication.activity.fragment.ContactFragment;
import com.sfr.dbuttonapplication.activity.widget.DeleteConfirmDialog;
import com.sfr.dbuttonapplication.activity.widget.LoadingProgressDialog;
import com.sfr.dbuttonapplication.entity.UserData;
import com.sfr.dbuttonapplication.http.HttpSendJsonManager;

import java.util.ArrayList;


public class ContactListAdapter extends BaseAdapter {

    protected DisplayImageOptions options;
    private ArrayList<UserData> mList = new ArrayList<UserData>();
    protected LayoutInflater mInflater;
    protected Context cxt;
    private Handler mHandler;

    public ContactListAdapter(Context context, ArrayList<UserData> list, Handler handler) {
        cxt = context;
        mInflater = LayoutInflater.from(this.cxt);
        mList = list;
        this.mHandler = handler;
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.mipmap.img_default_head)
                .showImageForEmptyUri(R.mipmap.img_default_head)
                .cacheInMemory(true)
                .cacheOnDisc(true)
                .showImageOnFail(R.mipmap.img_default_head)
                .considerExifParams(true).build();
    }

    public void setServiceList(ArrayList<UserData> list) {
        mList = list;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public UserData getItem(int position) {
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
            v = mInflater.inflate(R.layout.contact_list_adapter, null);
            holder = new HolderView();
            holder.ivHead = (ImageView)v.findViewById(R.id.iv_your_head);
            holder.ivDelete = (ImageView) v.findViewById(R.id.iv_delete);
            holder.tvName = (TextView) v.findViewById(R.id.tv_user_name);
            holder.ivUrgent = (ImageView) v.findViewById(R.id.iv_is_urgent);
            v.setTag(holder);
        } else {
            holder = (HolderView) v.getTag();
        }
        holder.tvName.setText(mList.get(position).getName());
        if (mList.get(position).getIsUrgent().equals("1")) {
            holder.ivUrgent.setVisibility(View.VISIBLE);
            holder.ivDelete.setVisibility(View.GONE);
        }else{
            holder.ivUrgent.setVisibility(View.GONE);
            holder.ivDelete.setVisibility(View.GONE);
        }
        if(!TextUtils.isEmpty(mList.get(position).getImg())){
            ImageLoader.getInstance().displayImage(mList.get(position).getImg(),holder.ivHead,options);
        }
        return v;
    }

    public class HolderView {
        private ImageView ivDelete;
        private ImageView ivHead;
        private TextView tvName;
        private ImageView ivUrgent;
//        private TextView order_top_tv;
    }


}
