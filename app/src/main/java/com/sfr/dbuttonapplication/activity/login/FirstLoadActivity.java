package com.sfr.dbuttonapplication.activity.login;

import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;

import com.sfr.dbuttonapplication.R;
import com.sfr.dbuttonapplication.activity.adapter.MainFragmentPagerAdapter;
import com.sfr.dbuttonapplication.activity.fragment.FristStartFragment;
import com.sfr.dbuttonapplication.utils.APPUtils;

import java.util.ArrayList;

public class FirstLoadActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private MainFragmentPagerAdapter adapter;
    int currentPageIndex=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        APPUtils.setTranslucentGone(this);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_first_load);
        setView();
    }

    /**
     * 设置介绍界面
     */
    private void setView() {
        viewPager=(ViewPager)findViewById(R.id.viewPager_main);
        ArrayList<Fragment> list=new ArrayList<Fragment>();
        list.add(new FristStartFragment(1));
        list.add(new FristStartFragment(2));
        list.add(new FristStartFragment(3));
        list.add(new FristStartFragment(4));
        list.add(new FristStartFragment(5));
        //list.add(new FourthFragment());
        adapter=new MainFragmentPagerAdapter(getSupportFragmentManager(), list);
        viewPager.setAdapter(adapter);
    }
}
