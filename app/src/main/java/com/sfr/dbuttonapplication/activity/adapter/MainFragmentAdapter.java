package com.sfr.dbuttonapplication.activity.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import java.util.ArrayList;

public class MainFragmentAdapter extends FragmentPagerAdapter {
	ArrayList<Fragment> list;
	public MainFragmentAdapter(FragmentManager fm, ArrayList<Fragment> list) {
		super(fm);
		this.list = list;

	}


	@Override
	public Fragment getItem(int arg0) {
		return list.get(arg0);
	}

	@Override
	public int getCount() {
		return list.size();
	}

	public Fragment currentFragment;

	@Override
	public void setPrimaryItem(ViewGroup container, int position, Object object) {
		currentFragment = (Fragment) object;
		super.setPrimaryItem(container, position, object);
	}

	public Fragment getFragment(){
		return currentFragment;
	}

	public void UpdateList(ArrayList<Fragment> list){
		this.list.clear();
		this.list=list;
		notifyDataSetChanged();
	}

}

