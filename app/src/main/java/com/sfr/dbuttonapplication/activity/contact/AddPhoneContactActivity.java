package com.sfr.dbuttonapplication.activity.contact;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sfr.dbuttonapplication.DButtonApplication;
import com.sfr.dbuttonapplication.R;
import com.sfr.dbuttonapplication.activity.widget.ClearEditText;
import com.sfr.dbuttonapplication.activity.widget.DeleteConfirmDialog;
import com.sfr.dbuttonapplication.activity.widget.LoadingProgressDialog;
import com.sfr.dbuttonapplication.activity.widget.SideBar;
import com.sfr.dbuttonapplication.entity.ContactsData;
import com.sfr.dbuttonapplication.entity.SortModel;
import com.sfr.dbuttonapplication.entity.UserData;
import com.sfr.dbuttonapplication.http.HttpAnalyJsonManager;
import com.sfr.dbuttonapplication.http.HttpSendJsonManager;
import com.sfr.dbuttonapplication.utils.APPUtils;
import com.sfr.dbuttonapplication.utils.CharacterParser;
import com.sfr.dbuttonapplication.utils.LogUtil;
import com.sfr.dbuttonapplication.utils.PhoneContactsUtil;
import com.sfr.dbuttonapplication.utils.PinyinComparator;
import com.sfr.dbuttonapplication.utils.ToastUtils;
import com.sfr.dbuttonapplication.utils.Tools;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AddPhoneContactActivity extends AppCompatActivity implements View.OnClickListener {


    private ClearEditText mEtSearch;
    private ListView sortListView;
    private SideBar sideBar;
    private TextView dialog;
    private PhoneContactSortAdapter adapter;
    private InputMethodManager imm;// InputMethodManager是一个用于控制显示或隐藏输入法面板的类
    /**
     * 汉字转换成拼音的类
     */
    private CharacterParser characterParser;
    private List<SortModel> SourceDateList;
    /**
     * 根据拼音来排列ListView里面的数据类
     */
    private PinyinComparator pinyinComparator;
    private ArrayList<ContactsData> dblist;
    /**
     * 顶部两个Rl按钮的操作
     */
    private View headView;
    private RelativeLayout rlNewFriend;
    //加载或者没有联系人的时候提示
    private RelativeLayout rlShowReason;
    private TextView tvShowReason;
    public static final int ADD_PHONE_CONTACT_SUCCESS = 4;
    public static final int ADD_PHONE_CONTACT_FALSE = 5;
    private static final int CONTACT_SEARCH_SUCCESS = 6;
    private static final int CONTACT_SEARCH_FALSE = 7;
    public static final int UPLOAD_FALSE = 9;
    public static final int UPLOAD_SUCCESS = 10;
    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 0:
                    adapter.updateListView(SourceDateList);
                    adapter.notifyDataSetChanged();
                    break;
                case 2:
                    rlShowReason.setVisibility(View.GONE);
                    break;
                case 3:
                    if (dblist.size() == 0) {
                        rlShowReason.setVisibility(View.VISIBLE);
                        tvShowReason.setText("暂时没有手机联系人");
                    } else {
                        rlShowReason.setVisibility(View.GONE);
                    }

                    SourceDateList = filledData(dblist);
                    // 根据a-z进行排序
                    Collections.sort(SourceDateList, pinyinComparator);
                    adapter.updateListView(SourceDateList);

                    break;
                case 8:
                    Tools.showInfo(AddPhoneContactActivity.this, "删除成功");
                    break;
                case UPLOAD_SUCCESS:
                    String mImg = (String) msg.obj;
                    adapter.addContact(mImg);
                    break;
                case UPLOAD_FALSE:
                    ToastUtils.shortToast(AddPhoneContactActivity.this, getResources().getString(R.string.upload_photo_false));
                    break;
                case ADD_PHONE_CONTACT_SUCCESS:
                    DButtonApplication.mAddContact = true;
                    mPhone = (String) msg.obj;
                    mContactSearchTask = new ContactSearchTask();
                    mContactSearchTask.execute("");
                    break;
                case ADD_PHONE_CONTACT_FALSE:
                    LoadingProgressDialog.Dissmiss();
                    ToastUtils.shortToast(AddPhoneContactActivity.this, HttpAnalyJsonManager.lastError);
                    break;
                case CONTACT_SEARCH_SUCCESS:
                    LoadingProgressDialog.Dissmiss();
                    ToastUtils.shortToast(AddPhoneContactActivity.this, R.string.add_contact_suceess);
                    adapter.updateListView(SourceDateList);
                    break;
                case CONTACT_SEARCH_FALSE:
                    LoadingProgressDialog.Dissmiss();
                    if (HttpAnalyJsonManager.lastError.equals("不存在")||HttpAnalyJsonManager.lastError.equals("手机号输入格式有误")) {
                        //弹出dialog
                        showDeleteConfirmDialog();
                    } else {
                        ToastUtils.shortToast(AddPhoneContactActivity.this, HttpAnalyJsonManager.lastError);
                        //在Contact.list状态中添加这个对应的人名
                        //finish();
                    }
                    break;
            }
        }

        ;
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        APPUtils.setTranslucent(this);
        setContentView(R.layout.activity_add_phone_contact2);
        initTitle();
        setView();
        setListener();
    }

    private void setView() {
        // 实例化InputMethodManager 用来控制输入法面板
        imm = (InputMethodManager) AddPhoneContactActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
        dblist = new ArrayList<ContactsData>();
        mGetPhoneContacts = new GetPhoneContacts(handler);
        mGetPhoneContacts.execute("");
        //实例化汉字转拼音类
        characterParser = CharacterParser.getInstance();
        pinyinComparator = new PinyinComparator();
        sideBar = (SideBar) findViewById(R.id.sb_add_phone_contact);
        dialog = (TextView) findViewById(R.id.tv_add_phone_contact);
        rlShowReason = (RelativeLayout) findViewById(R.id.rl_add_phone_contact_show_reason);
        tvShowReason = (TextView) findViewById(R.id.tv_add_phone_contact_show_reason);
        sideBar.setTextView(dialog);
        //设置右侧触摸监听
        sideBar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {

            @Override
            public void onTouchingLetterChanged(String s) {
                //该字母首次出现的位置
                int position = adapter.getPositionForSection(s.charAt(0));
                if (position != -1) {
                    sortListView.setSelection(position);
                }

            }
        });
        sortListView = (ListView) findViewById(R.id.lv_add_phone_contact);
        sortListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position,
                                    long arg3) {
                if (position == 0) {
                    //Tools.showInfo(ContactsActivity.this, "这是头组");
                } else {
                    //传过去朋友名-之后是资料界面

                }
            }
        });
        SourceDateList = filledData(dblist);
        // 根据a-z进行排序
        Collections.sort(SourceDateList, pinyinComparator);
        SourceDateList.toString();
        adapter = new PhoneContactSortAdapter(AddPhoneContactActivity.this, SourceDateList, sortListView, handler);
        sortListView.setAdapter(adapter);
        handler.sendEmptyMessage(3);
        mEtSearch = (ClearEditText) findViewById(R.id.action_bar_search);
        //根据输入框输入值的改变来过滤搜索
        mEtSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //当输入框里面的值为空，更新为原来的列表，否则为过滤数据列表
                filterData(s.toString());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void setListener() {

    }

    private TextView mActivityTitle, mTitleExtra;
    private ImageView mTitleBack;

    private void initTitle() {
        mActivityTitle = (TextView) findViewById(R.id.title_info);
        mTitleExtra = (TextView) findViewById(R.id.title_extra);
        mTitleBack = (ImageView) findViewById(R.id.title_back_btn);
        mActivityTitle.setText(getResources().getString(R.string.address_list_contact));
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

        }
    }

    /**
     * 为ListView填充数据
     *
     * @return
     */
    private List<SortModel> filledData(ArrayList<ContactsData> list) {
        List<SortModel> mSortList = new ArrayList<SortModel>();
        if (list.size() != 0) {
            LogUtil.i("ContactsFragment---数据库list-contacts", "" + list.size());
            /*for(int i=0;i<dblist.size();i++){
                LogUtil.i("ContactsFragment---数据库dblist-contacts-3", ""+dblist.get(i).toString());
			}*/
            for (int i = 0; i < list.size(); i++) {
                LogUtil.println("ContactsFragment---数据库list-contacts"+ "" + list.get(i).toString());
            }
            for (int i = 0; i < list.size(); i++) {/*
                if(list.get(i).getCallName()==null){
					list.get(i).setCallName(list.get(i).getName());
				}*/
                SortModel sortModel = new SortModel();
                sortModel.setName(list.get(i).getCallName());
                sortModel.setNameId(list.get(i).getName());
                sortModel.setBitHead(list.get(i).getBitHead());
                if (list.get(i).getHead() != null) {
                    LogUtil.i("ContactsFragment", "头像不为空");
                    sortModel.setHead(list.get(i).getHead());
                }
                LogUtil.println("ContactsFragment-callName" + list.get(i).toString());
                //汉字转换成拼音
                String pinyin;
                if (list.get(i).getCallName() == null) {
                    String callname = list.get(i).getNickName();
                    callname = callname.replace(" ", "");
                    LogUtil.i("GetCallName", callname + "--------------ContactFragment-------------------" + list.get(i).getName());
                    pinyin = characterParser.getSelling(callname);
                } else {
                    String callname = list.get(i).getCallName();
                    callname = callname.replace(" ", "");
                    pinyin = characterParser.getSelling(callname);
                }
                String sortString = "#";
                try {
                    sortString = pinyin.substring(0, 1).toUpperCase();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                LogUtil.i(pinyin, sortString);
                // 正则表达式，判断首字母是否是英文字母
                if (sortString.matches("[A-Z]")) {
                    sortModel.setSortLetters(sortString.toUpperCase());
                } else {
                    sortModel.setSortLetters("#");
                }

                mSortList.add(sortModel);
            }
        } else {
        }
        return mSortList;

    }

    /**
     * 根据输入框中的值来过滤数据并更新ListView
     *
     * @param filterStr
     */
    private void filterData(String filterStr) {
        List<SortModel> filterDateList = new ArrayList<SortModel>();

        if (TextUtils.isEmpty(filterStr)) {
            filterDateList = SourceDateList;
        } else {
            filterDateList.clear();
            for (SortModel sortModel : SourceDateList) {
                String name = sortModel.getName();
                name = name.replace(" ", "");
                if (name.indexOf(filterStr.toString()) != -1 || characterParser.getSelling(name).startsWith(filterStr.toString())) {
                    filterDateList.add(sortModel);
                }
            }
        }

        // 根据a-z进行排序
        Collections.sort(filterDateList, pinyinComparator);
        adapter.updateListView(filterDateList);
    }

    private GetPhoneContacts mGetPhoneContacts;

    private class GetPhoneContacts extends AsyncTask<String, Void, Void> {

        Handler mH;

        public GetPhoneContacts(Handler handler) {
            mH = handler;
        }

        @Override
        protected Void doInBackground(String... params) {
            dblist = PhoneContactsUtil.getPhoneContactsList(AddPhoneContactActivity.this);
            mH.sendEmptyMessage(3);
            return null;
        }

    }

    private String mPhone = "";
    private ContactSearchTask mContactSearchTask;
    private UserData mUserData;

    private class ContactSearchTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {
            mUserData = HttpSendJsonManager.searchContact(AddPhoneContactActivity.this, mPhone);
            if (mUserData.isOK()) {
                handler.sendEmptyMessage(CONTACT_SEARCH_SUCCESS);
            } else {
                handler.sendEmptyMessage(CONTACT_SEARCH_FALSE);
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
        mDeleteConfirmDialog = new DeleteConfirmDialog(AddPhoneContactActivity.this,
                R.style.share_dialog);
        mDeleteConfirmDialog.show();
        Window window = mDeleteConfirmDialog.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.alpha = 1f;
        window.setAttributes(lp);
        mTVDeleteText = (TextView) window.findViewById(R.id.dialog_delete_confirm_text);
        mTVDeleteHint = (TextView) window.findViewById(R.id.dialog_delete_confirm_textview);
        mTVCancel = (TextView) window.findViewById(R.id.delete_confirm_cancel);
        mTVCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDeleteConfirmDialog.dismiss();
                adapter.updateListView(SourceDateList);
            }
        });
        mTVOK = (TextView) window.findViewById(R.id.delete_confirm_ok);
        mTVOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //微信分享
                mDeleteConfirmDialog.dismiss();
                new ShareAction(AddPhoneContactActivity.this)
                        .withText("hello")
                        .setDisplayList(SHARE_MEDIA.WEIXIN)
                        .setCallback(umShareListener).open();
                adapter.updateListView(SourceDateList);

                //finish();
            }
        });
        mTVDeleteText.setText(R.string.add_phone_no_exist_text);
        mTVDeleteHint.setText(R.string.add_phone_no_exist_hint);
        mTVCancel.setText(R.string.add_phone_no_exist_cancel);
        mTVOK.setText(R.string.add_phone_no_exist_ok);
    }

    private UMShareListener umShareListener = new UMShareListener() {
        /**
         * @descrption 分享开始的回调
         * @param platform 平台类型
         */
        @Override
        public void onStart(SHARE_MEDIA platform) {

        }

        /**
         * @descrption 分享成功的回调
         * @param platform 平台类型
         */
        @Override
        public void onResult(SHARE_MEDIA platform) {
            Toast.makeText(AddPhoneContactActivity.this, "成功了", Toast.LENGTH_LONG).show();
        }

        /**
         * @descrption 分享失败的回调
         * @param platform 平台类型
         * @param t 错误原因
         */
        @Override
        public void onError(SHARE_MEDIA platform, Throwable t) {
            Toast.makeText(AddPhoneContactActivity.this, "失败" + t.getMessage(), Toast.LENGTH_LONG).show();
        }

        /**
         * @descrption 分享取消的回调
         * @param platform 平台类型
         */
        @Override
        public void onCancel(SHARE_MEDIA platform) {
            Toast.makeText(AddPhoneContactActivity.this, "取消了", Toast.LENGTH_LONG).show();

        }
    };

}
