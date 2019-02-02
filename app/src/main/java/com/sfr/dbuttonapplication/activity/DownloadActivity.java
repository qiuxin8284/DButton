package com.sfr.dbuttonapplication.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.sfr.dbuttonapplication.R;
import com.sfr.dbuttonapplication.utils.APPUtils;

public class DownloadActivity extends AppCompatActivity {

    private String id, link, title;
    private WebView wv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        APPUtils.setTranslucent(this);
        setContentView(R.layout.activity_download);
        initTitle();
        setView();
    }

    private TextView mActivityTitle, mTitleExtra, mTitleBack;

    private void initTitle() {
        mActivityTitle = (TextView) findViewById(R.id.title_info);
        mTitleExtra = (TextView) findViewById(R.id.title_extra);
        mTitleBack = (TextView) findViewById(R.id.title_back);
        mActivityTitle.setText(getResources().getString(R.string.version_update));
        mTitleExtra.setVisibility(View.GONE);
        mTitleBack.setVisibility(View.VISIBLE);
        mTitleBack.setText(getResources().getString(R.string.go_up));
        mTitleBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    private void setView() {
        link = getIntent().getStringExtra("link");
        wv = (WebView) findViewById(R.id.web_view);
        WebSettings webSettings = wv.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setAllowFileAccess(true);
        //webSettings.setBuiltInZoomControls(true);
        wv.setBackgroundColor(0x00111111);
        wv.setBackgroundResource(R.color.background);
        //第一次进入程序时，加载URL显示加载提示框
        //progressFirst = ProgressDialog.show(TrainDetailActivity.this, null, "请稍后,正在加载.....");

        wv.loadUrl(link);
        wv.setWebViewClient(new webViewClient());
        wv.setDownloadListener(new MyWebViewDownLoadListener());

    }

    private class MyWebViewDownLoadListener implements DownloadListener {

        @Override
        public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype,
                                    long contentLength) {
            Uri uri = Uri.parse(url);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        }

    }
    private class webViewClient extends WebViewClient {
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }

    @Override
    protected void onResume() {
        if (wv != null) {
            wv.onResume();
        }
        super.onResume();
    }

    @Override
    protected void onPause() {
        if (wv != null) {
            wv.onPause();
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        if (wv != null) {
            //wv.getSettings().setBuiltInZoomControls(true);
            wv.setVisibility(View.GONE);
            wv.destroy();
            wv = null;
        }
        super.onDestroy();
    }

}
