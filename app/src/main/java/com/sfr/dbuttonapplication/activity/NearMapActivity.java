package com.sfr.dbuttonapplication.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.sfr.dbuttonapplication.R;
import com.sfr.dbuttonapplication.activity.alarm.AlarmDetailActivity;
import com.sfr.dbuttonapplication.activity.widget.LoadingProgressDialog;
import com.sfr.dbuttonapplication.utils.APPUtils;

public class NearMapActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        APPUtils.setTranslucent(this);
        setContentView(R.layout.activity_near_map);
        initTitle();
    }

    private TextView mActivityTitle, mTitleExtra;
    private ImageView mTitleBack, mIvUpdate;

    private void initTitle() {
        mActivityTitle = (TextView) findViewById(R.id.title_info);
        mTitleExtra = (TextView) findViewById(R.id.title_extra);
        mTitleBack = (ImageView) findViewById(R.id.title_back_btn);
        mIvUpdate = (ImageView) findViewById(R.id.title_right_btn);
        mIvUpdate.setVisibility(View.VISIBLE);
        mIvUpdate.setBackgroundResource(R.drawable.update_btn_selector);
        mActivityTitle.setText(getResources().getString(R.string.help_map));
        mTitleExtra.setVisibility(View.GONE);
        mTitleBack.setVisibility(View.VISIBLE);
        mTitleBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mIvUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //刷新界面update

            }
        });
    }
}
