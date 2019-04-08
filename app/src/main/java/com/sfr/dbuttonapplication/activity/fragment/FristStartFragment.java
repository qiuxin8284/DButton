package com.sfr.dbuttonapplication.activity.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sfr.dbuttonapplication.R;
import com.sfr.dbuttonapplication.activity.login.LoginActivity;


public class FristStartFragment extends Fragment {
	private int number;
	@SuppressLint({"NewApi", "ValidFragment"})
	public FristStartFragment(){}
	@SuppressLint({"NewApi", "ValidFragment"})
    public FristStartFragment(int number){
		this.number = number;
	}
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

     @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
    		Bundle savedInstanceState) {
    	View view=inflater.inflate(R.layout.frist_start_fragment, null);
		 switch (number){
			 case 1:
				 view.setBackgroundResource(R.mipmap.frist_start_01);
			 	break;
			 case 2:
				 view.setBackgroundResource(R.mipmap.frist_start_02);
			 	break;
			 case 3:
				 view.setBackgroundResource(R.mipmap.frist_start_03);
			 	break;
			 case 4:
				 view.setBackgroundResource(R.mipmap.frist_start_04);
			 	break;
			 case 5:
				 view.setBackgroundResource(R.mipmap.frist_start_05);
				 view.setOnClickListener(new View.OnClickListener() {
					 @Override
					 public void onClick(View v) {
						 Intent intent = new Intent(getActivity(), LoginActivity.class);
						 startActivity(intent);
						 getActivity().finish();
					 }
				 });
				 break;

		 }
    	return view;
    }
}
