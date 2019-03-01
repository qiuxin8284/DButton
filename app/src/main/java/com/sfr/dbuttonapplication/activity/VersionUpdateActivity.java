package com.sfr.dbuttonapplication.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.sfr.dbuttonapplication.R;
import com.sfr.dbuttonapplication.activity.widget.DeleteConfirmDialog;
import com.sfr.dbuttonapplication.activity.widget.LoadingProgressDialog;
import com.sfr.dbuttonapplication.entity.RenewData;
import com.sfr.dbuttonapplication.http.HttpAnalyJsonManager;
import com.sfr.dbuttonapplication.http.HttpSendJsonManager;
import com.sfr.dbuttonapplication.utils.APPUtils;
import com.sfr.dbuttonapplication.utils.LogUtil;
import com.sfr.dbuttonapplication.utils.ToastUtils;

public class VersionUpdateActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int OTA_VERSION_SUCCESS = 1;
    private static final int OTA_VERSION_FALSE = 2;
    private static final int UPDATE_PROGRESS = 3;
    private static int progress = 0;
    private TextView mTvNowVer, mTvUpdateVer;
    private Button mBtnUpdate;
    private TextView mTvSeekBar;//tv_seekbar
    private SeekBar mSeekBar;//seekbar
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case OTA_VERSION_SUCCESS:
                    LoadingProgressDialog.Dissmiss();
                    if (mRenewData != null) {
                        mTvNowVer.setText("现有版本：Ver " + mRenewData.getLowestVer());
                        mTvUpdateVer.setText("可用版本：Ver " + mRenewData.getNewVer());
                        LogUtil.println("DButtonApplication::getLink:= " + mRenewData.getLink());
                        if (mRenewData.getLowestVer().equals(mRenewData.getNewVer())) {
                            mBtnUpdate.setBackgroundResource(R.mipmap.btn_login_none);
                            mBtnUpdate.setClickable(false);
                        }else{
                            mBtnUpdate.setBackgroundResource(R.drawable.login_btn_selector);
                            mBtnUpdate.setClickable(true);
                        }
                    }
                    break;
                case OTA_VERSION_FALSE:
                    LoadingProgressDialog.Dissmiss();
                    ToastUtils.shortToast(VersionUpdateActivity.this, HttpAnalyJsonManager.lastError);
                    break;
                case UPDATE_PROGRESS:
                    if (progress < 100) {
                        progress = progress + 1;
                        mTvSeekBar.setText(progress + "%");
                        mSeekBar.setProgress(progress);
                        mHandler.sendEmptyMessageDelayed(UPDATE_PROGRESS, 1000);
                    } else {
                        mTvSeekBar.setVisibility(View.GONE);
                        mSeekBar.setVisibility(View.GONE);
                        mBtnUpdate.setText(R.string.update);
                        mBtnUpdate.setClickable(true);
                    }
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        APPUtils.setTranslucent(this);
        setContentView(R.layout.activity_otaupdate);
        initTitle();
        setView();
        setListener();
        LoadingProgressDialog.show(VersionUpdateActivity.this, false, true, 30000);
        mRenewTask = new RenewTask();
        mRenewTask.execute("");
    }

    private void setListener() {
        mBtnUpdate.setOnClickListener(this);
    }

    private void setView() {
        mTvNowVer = (TextView) findViewById(R.id.tv_now_ver);
        mTvUpdateVer = (TextView) findViewById(R.id.tv_update_ver);
        mBtnUpdate = (Button) findViewById(R.id.btn_update);
        mTvSeekBar = (TextView) findViewById(R.id.tv_seekbar);
        mSeekBar = (SeekBar) findViewById(R.id.seekbar);
    }

    private TextView mActivityTitle, mTitleExtra;
    private ImageView mTitleBack;

    private void initTitle() {
        mActivityTitle = (TextView) findViewById(R.id.title_info);
        mTitleExtra = (TextView) findViewById(R.id.title_extra);
        mTitleBack = (ImageView) findViewById(R.id.title_back_btn);
        mActivityTitle.setText(getResources().getString(R.string.version_update));
        mTitleExtra.setVisibility(View.GONE);
        mTitleBack.setVisibility(View.VISIBLE);
        mTitleBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_update:
                showDeleteConfirmDialog();
                break;
        }

    }

    private RenewTask mRenewTask;
    private RenewData mRenewData;

    private class RenewTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {
            mRenewData = HttpSendJsonManager.renew(VersionUpdateActivity.this, HttpSendJsonManager.RENEW_TYPE_ANDROID);
            if (mRenewData.isOK()) {
                mHandler.sendEmptyMessage(OTA_VERSION_SUCCESS);
            } else {
                mHandler.sendEmptyMessage(OTA_VERSION_FALSE);
            }
            return null;
        }
    }


    private DeleteConfirmDialog mDeleteConfirmDialog;

    private TextView mTVDeleteText;
    private TextView mTVDeleteHint;
    private TextView mTVCancel;
    private TextView mTVOK;

    public void showDeleteConfirmDialog() {
        mDeleteConfirmDialog = new DeleteConfirmDialog(VersionUpdateActivity.this,
                R.style.share_dialog);
        mDeleteConfirmDialog.show();
        Window window = mDeleteConfirmDialog.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.alpha = 1f;
        window.setAttributes(lp);
        mTVDeleteText = (TextView) window.findViewById(R.id.dialog_delete_confirm_text);
        mTVDeleteText.setText(R.string.ota_update_dialog_text);
        mTVDeleteHint = (TextView) window.findViewById(R.id.dialog_delete_confirm_textview);
        mTVDeleteHint.setText(R.string.ota_update_dialog_hint);
        mTVCancel = (TextView) window.findViewById(R.id.delete_confirm_cancel);
        mTVCancel.setText(R.string.common_cancel);
        mTVCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDeleteConfirmDialog.dismiss();
            }
        });
        mTVOK = (TextView) window.findViewById(R.id.delete_confirm_ok);
        mTVOK.setText(R.string.common_confirm);
        mTVOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDeleteConfirmDialog.dismiss();
                //执行OTA进度条
//                mBtnUpdate.setText(R.string.now_update);
//                //mBtnUpdate.setBackgroundResource(R.mipmap.btn_sms_code_no);
//                mBtnUpdate.setClickable(false);
//                mTvSeekBar.setVisibility(View.VISIBLE);
//                mSeekBar.setVisibility(View.VISIBLE);
                //执行软件下载升级
                Intent intent = new Intent(VersionUpdateActivity.this, DownloadActivity.class);
                intent.putExtra("link", mRenewData.getLink());
                startActivity(intent);
            }
        });
    }


}
