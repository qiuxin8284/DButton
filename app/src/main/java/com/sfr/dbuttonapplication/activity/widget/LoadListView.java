package com.sfr.dbuttonapplication.activity.widget;


import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;

import com.sfr.dbuttonapplication.R;

public class LoadListView extends ListView implements AbsListView.OnScrollListener {

    View footer;
    int totalItemCount;
    int lastVisibleItem;
    boolean isLoading;
    ILoadListener2 iListener2;
    public LoadListView(Context context) {
        super(context);
        initView(context);
    }
    public LoadListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }
    public LoadListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView(context);
    }

    private void initView(Context context){
        LayoutInflater inflater = LayoutInflater.from(context);
        footer = inflater.inflate(R.layout.footer, null);
        footer.findViewById(R.id.load_layout).setVisibility(GONE);
        this.addFooterView(footer);
        this.setOnScrollListener(this);
    }
    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        this.lastVisibleItem = firstVisibleItem + visibleItemCount;
        this.totalItemCount = totalItemCount;
    }
    @Override
    public void onScrollStateChanged(AbsListView view, int scrollStatus) {
        if(totalItemCount == lastVisibleItem
                && scrollStatus == SCROLL_STATE_IDLE){
            if(!isLoading){
                isLoading = true;
                footer.findViewById(R.id.load_layout).setVisibility(VISIBLE);
                //加载更多数据
                iListener2.onLoad();
            }
        }
    }

    public void loadComplete(){
        isLoading = false;
        footer.findViewById(R.id.load_layout).setVisibility(GONE);
    }

    public void setInterface(ILoadListener2 iListener2){
        this.iListener2 = iListener2;
    }

    public interface ILoadListener2{
        public void onLoad();
    }

}