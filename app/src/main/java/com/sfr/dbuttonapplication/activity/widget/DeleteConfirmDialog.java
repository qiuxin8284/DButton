package com.sfr.dbuttonapplication.activity.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import com.sfr.dbuttonapplication.R;


public class DeleteConfirmDialog extends Dialog {
	private Context context;

	public DeleteConfirmDialog(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		this.context = context;
	}
	public DeleteConfirmDialog(Context context, int theme) {
		super(context, theme);
		this.context = context;
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.dialog_delete_confirm);

	}
}
