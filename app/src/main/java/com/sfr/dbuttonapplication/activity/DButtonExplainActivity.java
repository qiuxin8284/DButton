package com.sfr.dbuttonapplication.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sfr.dbuttonapplication.R;
import com.sfr.dbuttonapplication.utils.APPUtils;

public class DButtonExplainActivity extends AppCompatActivity {

    private ImageView mIvExplain;
    private TextView mTvExplain;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        APPUtils.setTranslucent(this);
        setContentView(R.layout.activity_dbutton_explain);
        initTitle();
        mIvExplain = (ImageView)findViewById(R.id.iv_explain);
        mIvExplain.setBackgroundResource(R.mipmap.dbutton_explain_bg);
        mTvExplain = (TextView)findViewById(R.id.tv_explain);
        mTvExplain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    private TextView mActivityTitle,mTitleExtra;
    private ImageView mTitleBack;
    private void initTitle() {
        mActivityTitle = (TextView) findViewById(R.id.title_info);
        mTitleExtra = (TextView) findViewById(R.id.title_extra);
        mTitleBack = (ImageView) findViewById(R.id.title_back_btn);
        mActivityTitle.setText(getResources().getString(R.string.dbutton_explain));
        mTitleExtra.setVisibility(View.GONE);
        mTitleBack.setVisibility(View.VISIBLE);
        mTitleBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
