package com.jordan.httplibrary;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;

import com.jordan.httplibrary.utils.CommonUtils;
import com.jordan.httplibrary.utils.data.ResponseData;

/**
 * Created by icean on 2017/2/1.
 */

public abstract class BaseTask extends AsyncTask<String, String, String> {

    protected Context mContext;
    protected String mRemoteServerAddress, mUserToken;
    protected boolean mIsGranted;
    protected Handler mMainHandler;

    public BaseTask(Context ctx, String target_address, String user_token, Handler main_handler, boolean is_granted) {
        mContext = ctx;
        mRemoteServerAddress = target_address;
        mUserToken = user_token;
        mMainHandler = main_handler;
        mIsGranted = is_granted;
    }

    @Override
    protected String doInBackground(String... params) {
        return doTask();
    }

    @Override
    protected void onPostExecute(String result_data) {
        super.onPostExecute(result_data);
        if ("999999".equals(result_data)){
            onException();
        } else {
            android.util.Log.e("Photo", "onPostExecute");
            android.util.Log.e("Photo", "result_data:" + result_data);
            String finally_data = CommonUtils.getDataStrFromResult(result_data);
            android.util.Log.e("Photo", "finally_data:" + finally_data);
            if (CommonUtils.isSuccess(result_data)) {
                onSuccess(finally_data);
            } else {
                onFalse(finally_data);
            }
        }
    }

    public abstract String doTask();
    public abstract void onSuccess(String result_json);
    public abstract void onFalse(String false_json);
    public abstract void onException();
}