package com.sfr.dbuttonapplication.activity.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import com.sfr.dbuttonapplication.R;


public class BindOverDialog extends Dialog {
    Context context;

    public BindOverDialog(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
        this.context = context;

    }

    public BindOverDialog(Context context, int theme) {
        super(context, theme);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        this.setCancelable(false);// 设置点击屏幕Dialog不消失
        this.setContentView(R.layout.bind_over_dialog);

    }

}