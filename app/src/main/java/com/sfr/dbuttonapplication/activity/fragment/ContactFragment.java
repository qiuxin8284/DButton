package com.sfr.dbuttonapplication.activity.fragment;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.sfr.dbuttonapplication.DButtonApplication;
import com.sfr.dbuttonapplication.R;
import com.sfr.dbuttonapplication.activity.adapter.ContactListAdapter;
import com.sfr.dbuttonapplication.activity.contact.AddPhoneContactActivity;
import com.sfr.dbuttonapplication.activity.contact.ContactDetailActivity;
import com.sfr.dbuttonapplication.activity.widget.LoadingProgressDialog;
import com.sfr.dbuttonapplication.entity.ContactData;
import com.sfr.dbuttonapplication.entity.UserData;
import com.sfr.dbuttonapplication.http.HttpAnalyJsonManager;
import com.sfr.dbuttonapplication.http.HttpSendJsonManager;
import com.sfr.dbuttonapplication.utils.LogUtil;
import com.sfr.dbuttonapplication.utils.ToastUtils;

import java.util.ArrayList;
import java.util.HashMap;


public class ContactFragment extends Fragment {
    private ListView mLvContact;
    private ContactListAdapter mContactListAdapter;
    private ArrayList<UserData> mContactList;
    private TextView mTvEmptyHint;
    private static final int CONTACT_LIST_SUCCESS = 1;
    private static final int CONTACT_LIST_FALSE = 2;
    public static final int CONTACT_DEL_SUCCESS = 3;
    public static final int CONTACT_DEL_FALSE = 4;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case CONTACT_LIST_SUCCESS:
                    LoadingProgressDialog.Dissmiss();
                    if(mContactData!=null) {
                        mContactList = mContactData.getList();
                        DButtonApplication.mContactList = mContactList;
                        DButtonApplication.mContactMap = new HashMap<String,UserData>();
                        for(int i =0 ; i<mContactList.size();i++){
                            DButtonApplication.mContactMap.put(mContactList.get(i).getPhone(),mContactList.get(i));
                        }
                        if (mContactList.size() == 0) {
                            DButtonApplication.mIsEmpty = true;
                            mTvEmptyHint.setVisibility(View.VISIBLE);
                        } else {
                            DButtonApplication.mIsEmpty = false;
                            mTvEmptyHint.setVisibility(View.GONE);
                        }
                        mContactListAdapter.setServiceList(mContactList);
                    }
                    break;
                case CONTACT_LIST_FALSE:
                    LoadingProgressDialog.Dissmiss();
                    ToastUtils.shortToast(getActivity(), HttpAnalyJsonManager.lastError);
                    break;
                case CONTACT_DEL_SUCCESS:
                    mContactListTask = new ContactListTask();
                    mContactListTask.execute("");
                    break;
                case CONTACT_DEL_FALSE:
                    LoadingProgressDialog.Dissmiss();
                    ToastUtils.shortToast(getActivity(), HttpAnalyJsonManager.lastError);
                    break;
            }
        }
    };
    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.contact_fragment, null);
        initTitle(view);
        setView(view);
        setListener();
        return view;
    }
    private TextView mActivityTitle,mTitleExtra;
    private void initTitle(View view) {
        mActivityTitle = (TextView) view.findViewById(R.id.title_info);
        mTitleExtra = (TextView) view.findViewById(R.id.title_extra);
        mActivityTitle.setText(getResources().getString(R.string.menu_contact));
        mTitleExtra.setText(getResources().getString(R.string.menu_contact));
        mTitleExtra.setVisibility(View.VISIBLE);
        mTitleExtra.setText(getResources().getString(R.string.add_contact));
        mTitleExtra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AddPhoneContactActivity.class);
                startActivity(intent);
            }
        });
    }

    public void setView(View view) {
        mContactList = new ArrayList<UserData>();
        mLvContact = (ListView) view.findViewById(R.id.lv_contact);
        mContactListAdapter = new ContactListAdapter(getActivity(), mContactList,mHandler);
        mLvContact.setAdapter(mContactListAdapter);
        mTvEmptyHint = (TextView) view.findViewById(R.id.tv_contact_empty_hint);
        if (mContactList.size() == 0) {
            mTvEmptyHint.setVisibility(View.VISIBLE);
        } else {
            mTvEmptyHint.setVisibility(View.GONE);
        }
        mContactListTask = new ContactListTask();
        mContactListTask.execute("");
    }

    private void setListener() {
        mLvContact.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                LogUtil.println("mContactList.get(position):"+mContactList.get(position).toString());
                Intent intent = new Intent(getActivity(), ContactDetailActivity.class);
                intent.putExtra("id",mContactList.get(position).getId());
                intent.putExtra("name",mContactList.get(position).getName());
                intent.putExtra("phone",mContactList.get(position).getPhone());
                intent.putExtra("isUrgent",mContactList.get(position).getIsUrgent());
                startActivity(intent);
            }
        });
    }

    private ContactListTask mContactListTask;
    private ContactData mContactData;

    private class ContactListTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {
            mContactData = HttpSendJsonManager.contactList(getActivity());
            if (mContactData.isOK()) {
                DButtonApplication.mAddContact = false;
                mHandler.sendEmptyMessage(CONTACT_LIST_SUCCESS);
            } else {
                mHandler.sendEmptyMessage(CONTACT_LIST_FALSE);
            }
            return null;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if(DButtonApplication.mAddContact) {
            LoadingProgressDialog.show(getActivity(), false, true, 30000);
            mContactListTask = new ContactListTask();
            mContactListTask.execute("");
        }
    }
}
