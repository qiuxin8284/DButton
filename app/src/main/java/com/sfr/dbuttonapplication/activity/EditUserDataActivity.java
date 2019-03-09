package com.sfr.dbuttonapplication.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jordan.httplibrary.utils.CommonUtils;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.sfr.dbuttonapplication.BuildConfig;
import com.sfr.dbuttonapplication.DButtonApplication;
import com.sfr.dbuttonapplication.R;
import com.sfr.dbuttonapplication.activity.adapter.ChooesListAdapter;
import com.sfr.dbuttonapplication.activity.login.RegisterDataActivity;
import com.sfr.dbuttonapplication.activity.widget.ChooesDialog;
import com.sfr.dbuttonapplication.activity.widget.ChooesListDialog;
import com.sfr.dbuttonapplication.activity.widget.InputDialog;
import com.sfr.dbuttonapplication.activity.widget.LoadingProgressDialog;
import com.sfr.dbuttonapplication.activity.widget.RegisterOverDialog;
import com.sfr.dbuttonapplication.entity.RegisterData;
import com.sfr.dbuttonapplication.entity.UploadData;
import com.sfr.dbuttonapplication.entity.UserData;
import com.sfr.dbuttonapplication.http.HttpAnalyJsonManager;
import com.sfr.dbuttonapplication.http.HttpSendJsonManager;
import com.sfr.dbuttonapplication.utils.APPUtils;
import com.sfr.dbuttonapplication.utils.FileUtils;
import com.sfr.dbuttonapplication.utils.ToastUtils;
import com.sfr.dbuttonapplication.utils.UploadPictureHasZoomUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class EditUserDataActivity extends AppCompatActivity implements View.OnClickListener {

    private Button mBtnEditOver;
    private static final int UPLOAD_SUCCESS = 1;
    private static final int UPLOAD_FALSE = 2;
    private static final int EDIT_USER_DATA_SUCCESS = 3;
    private static final int EDIT_USER_DATA_FALSE = 4;
    private static final int N0_PERMISSION = 5;
    private static final int NO_PIC = 6;
    ;
    private static final int USER_INFO_SUCCESS = 7;
    private static final int USER_INFO_FALSE = 8;
//    private EditText mEtName;//et_input_your_name,et_input_your_bor
//    private TextView mTvBorthDay;
    private ImageView mIvHead;//iv_input_your_head
//    private ImageView mIvTime;
//    private CheckBox mCBSexMan, mCbSexWoman;//cb_sex_man,cb_sex_woman涉及分组
//    private CheckBox mCBBloodA, mCBBloodB, mCBBloodAB, mCBBloodO, mCBBloodOther;//cb_blood_a，cb_blood_b，cb_blood_ab,cb_blood_o,cb_blood_other涉及分组

    private RelativeLayout mRlHead,mRlName,mRlBlood,mRlSex,mRlBorthDay;
    private boolean mIsGrant;
    String file_full_path = "";
    Bitmap photo;
    private static final int UPDATE_SEX = 10;
    private static final int UPDATE_BLOOD = 11;
    private TextView mTvName,mTvBlood,mTvSex,mTvBorthDay;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    ArrayList<String> bloodList = new ArrayList<String>();
    ArrayList<String> sexList = new ArrayList<String>();


    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case UPLOAD_SUCCESS:
                    android.util.Log.e("uploadData", "mUploadData:" + mUploadData.toString());
                    mImg = mUploadData.getUrl();
                    android.util.Log.e("uploadData", "mImg:" + mImg);
                    break;
                case UPLOAD_FALSE:
                    ToastUtils.shortToast(EditUserDataActivity.this, getResources().getString(R.string.upload_photo_false));
                    break;
                case EDIT_USER_DATA_SUCCESS:
                    LoadingProgressDialog.Dissmiss();
                    mUserInfoTask = new UserInfoTask();
                    mUserInfoTask.execute("");
                    break;
                case EDIT_USER_DATA_FALSE:
                    LoadingProgressDialog.Dissmiss();
                    ToastUtils.shortToast(EditUserDataActivity.this, HttpAnalyJsonManager.lastError);
                    break;
                case NO_PIC:
                    ToastUtils.shortToast(EditUserDataActivity.this, getResources().getString(R.string.no_photo));
                    break;
                case N0_PERMISSION:
                    ToastUtils.shortToast(EditUserDataActivity.this, getResources().getString(R.string.please_open_permission));
                    break;
                case USER_INFO_SUCCESS:
                    //刷新界面
                    if (mUserData != null) {
                        finish();
                    }
                    break;
                case USER_INFO_FALSE:
                    //重新获取
                    mUserInfoTask = new UserInfoTask();
                    mUserInfoTask.execute("");
                    break;
                case UPDATE_SEX:
                    String text = (String) msg.obj;
                    mTvSex.setText(text);
                    mChooesListDialog.dismiss();
                    break;
                case UPDATE_BLOOD:
                    text = (String) msg.obj;
                    mTvBlood.setText(text);
                    mChooesListDialog.dismiss();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        APPUtils.setTranslucent(this);
        setContentView(R.layout.activity_edit_user_data);
        initTitle();
        setView();
        setListener();
        mIsGrant = false;
        checkCameraPermission();
        initAction();
    }

    private void initAction() {
        View statusBar = findViewById(R.id.statusBarView);
        ViewGroup.LayoutParams layoutParams = statusBar.getLayoutParams();
        layoutParams.height = getStatusBarHeight();
    }
    public int getStatusBarHeight() {
        int result = 0; //获取状态栏高度的资源id
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    private void checkCameraPermission() {
        int is_granted = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        if (PackageManager.PERMISSION_GRANTED != is_granted) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1000);
        } else {
            mIsGrant = true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (PackageManager.PERMISSION_GRANTED == grantResults[0]) {
            mIsGrant = true;
        }
    }

    private void setView() {
        mBtnEditOver = (Button) findViewById(R.id.btn_edit_over);

        mIvHead = (ImageView) findViewById(R.id.iv_input_your_head);

        mRlName = (RelativeLayout)findViewById(R.id.rl_user_name);
        mRlBlood = (RelativeLayout)findViewById(R.id.rl_user_blood_group);
        mRlSex = (RelativeLayout)findViewById(R.id.rl_user_sex);
        mRlBorthDay = (RelativeLayout)findViewById(R.id.rl_input_your_bor);

        mIvHead = (ImageView) findViewById(R.id.iv_input_your_head);
        mRlHead = (RelativeLayout) findViewById(R.id.rl_input_your_head);
        mTvName = (TextView) findViewById(R.id.tv_user_name);
        mTvBlood = (TextView) findViewById(R.id.tv_user_blood_group);
        mTvSex = (TextView) findViewById(R.id.tv_user_sex);
        mTvBorthDay = (TextView) findViewById(R.id.tv_user_date_of_birth);

        bloodList = new ArrayList<String>();
        bloodList.add("A型");
        bloodList.add("B型");
        bloodList.add("O型");
        bloodList.add("AB型");
        bloodList.add("保密");
        sexList = new ArrayList<String>();
        sexList.add("男");
        sexList.add("女");

        if (DButtonApplication.mUserData != null) {
            mTvName.setText(DButtonApplication.mUserData.getName());
            if(!TextUtils.isEmpty(DButtonApplication.mUserData.getGender())) {
                if (DButtonApplication.mUserData.getGender().equals("1")) {
                    mTvSex.setText(sexList.get(0));
                } else if (DButtonApplication.mUserData.getGender().equals("2")) {
                    mTvSex.setText(sexList.get(1));
                }
            }
            //mTvAge.setText(DButtonApplication.mUserData.getAge());
            if(!TextUtils.isEmpty(DButtonApplication.mUserData.getGender())) {
                if (DButtonApplication.mUserData.getBlood().equalsIgnoreCase("a")) {
                    mTvBlood.setText(bloodList.get(0));
                } else if (DButtonApplication.mUserData.getBlood().equalsIgnoreCase("ab")) {
                    mTvBlood.setText(bloodList.get(1));
                } else if (DButtonApplication.mUserData.getBlood().equalsIgnoreCase("b")) {
                    mTvBlood.setText(bloodList.get(2));
                } else if (DButtonApplication.mUserData.getBlood().equalsIgnoreCase("o")) {
                    mTvBlood.setText(bloodList.get(3));
                } else if (DButtonApplication.mUserData.getBlood().equalsIgnoreCase("other")) {
                    mTvBlood.setText(bloodList.get(4));
                }
            }
            //mTvBlood.setText(DButtonApplication.mUserData.getBlood());
            //mTvPhoneNumber.setText(DButtonApplication.mUserData.getPhone());
            //mIvHead url赋值
            if(!TextUtils.isEmpty(DButtonApplication.mUserData.getImg())){
                mImg = DButtonApplication.mUserData.getImg();
                ImageLoader.getInstance().displayImage(DButtonApplication.mUserData.getImg(),mIvHead);
            }

            if(DButtonApplication.mUserData.getBirth()!=0){
                mTvBorthDay.setText(sdf.format(new Date(DButtonApplication.mUserData.getBirth())));
            }
        }
    }

    private void setListener() {
        mBtnEditOver.setOnClickListener(this);
        mRlBorthDay.setOnClickListener(this);
        mRlHead.setOnClickListener(this);
        mRlName.setOnClickListener(this);
        mRlBlood.setOnClickListener(this);
        mRlSex.setOnClickListener(this);
    }

    private TextView mActivityTitle, mTitleExtra;
    private ImageView mTitleBack;

    private void initTitle() {
        mActivityTitle = (TextView) findViewById(R.id.title_info);
        mTitleExtra = (TextView) findViewById(R.id.title_extra);
        mTitleBack = (ImageView) findViewById(R.id.title_back_btn);
        mActivityTitle.setText(getResources().getString(R.string.edit_user_data));
        mTitleExtra.setVisibility(View.GONE);
        mTitleBack.setVisibility(View.VISIBLE);
        mTitleBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private String mName, mGender, mBirth, mBlood, mImg = "";
    private String mPicName, mFile, mTime;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_input_your_head:
                //调用Dialog 拍照或者相册
                if (mIsGrant) {
                    showHeadDialog();
                } else {
                    mHandler.sendEmptyMessage(N0_PERMISSION);
                }
                break;
            case R.id.btn_edit_over:
                mName = mTvName.getText().toString();
                mBirth = mTvBorthDay.getText().toString();
                mGender = "";
                if(mTvSex.getText().toString().equals(sexList.get(0))) mGender=HttpSendJsonManager.SEX_MAN;
                if(mTvSex.getText().toString().equals(sexList.get(1))) mGender=HttpSendJsonManager.SEX_WOMAN;
                mBlood = "";
                if(mTvBlood.getText().toString().equals(bloodList.get(0)))  mBlood=HttpSendJsonManager.BLOOD_A;
                if(mTvBlood.getText().toString().equals(bloodList.get(1)))  mBlood=HttpSendJsonManager.BLOOD_B;
                if(mTvBlood.getText().toString().equals(bloodList.get(2)))  mBlood=HttpSendJsonManager.BLOOD_AB;
                if(mTvBlood.getText().toString().equals(bloodList.get(3)))  mBlood=HttpSendJsonManager.BLOOD_O;
                if(mTvBlood.getText().toString().equals(bloodList.get(4)))  mBlood=HttpSendJsonManager.BLOOD_OTHER;


                if (TextUtils.isEmpty(mName) || TextUtils.isEmpty(mName)) {
                    Toast.makeText(this, R.string.please_input_your_name_hint, Toast.LENGTH_LONG).show();
                    return;
                } else if (TextUtils.isEmpty(mGender) || TextUtils.isEmpty(mGender)) {
                    Toast.makeText(this, R.string.please_choies_your_sex_hint, Toast.LENGTH_LONG).show();
                    return;
                } else if (TextUtils.isEmpty(mBlood) || TextUtils.isEmpty(mBlood)) {
                    Toast.makeText(this, R.string.please_input_your_blood_group_hint, Toast.LENGTH_LONG).show();
                    return;
                } else if (TextUtils.isEmpty(mBirth) || TextUtils.isEmpty(mBirth)) {
                    Toast.makeText(this, R.string.please_input_your_date_of_birth_hint, Toast.LENGTH_LONG).show();
                    return;
                } else if (TextUtils.isEmpty(mImg) || TextUtils.isEmpty(mImg)) {
                    Toast.makeText(this, R.string.please_input_your_head_hint, Toast.LENGTH_LONG).show();
                    return;
                } else {
                    LoadingProgressDialog.show(EditUserDataActivity.this, false, true, 30000);
                    mSetUserInfoTask = new SetUserInfoTask();
                    mSetUserInfoTask.execute("");
                }
                break;
            case R.id.rl_input_your_head:
                //调用Dialog 拍照或者相册
                if (mIsGrant) {
                    showHeadDialog();
                } else {
                    mHandler.sendEmptyMessage(N0_PERMISSION);
                }
                break;
            case R.id.rl_input_your_bor:
                initTimeDialog();
                break;
//            case R.id.iv_time:
//                initTimeDialog();
//                break;

            case R.id.rl_user_name:
                showInputDialog("昵称");
                break;
            case R.id.rl_user_blood_group:
                showListDialog(bloodList,"设置血型",UPDATE_BLOOD);
                break;
            case R.id.rl_user_sex:
                showListDialog(sexList,"选择性别",UPDATE_SEX);
                break;
        }
    }

    Calendar startcal;

    private void initTimeDialog() {
        long time = System.currentTimeMillis();
        Calendar mCalendar = Calendar.getInstance();
        mCalendar.setTimeInMillis(time);

        Date date = new Date();
        DatePickerDialog datePickerDialog = new DatePickerDialog(EditUserDataActivity.this, AlertDialog.THEME_HOLO_LIGHT,new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                startcal = Calendar.getInstance();
                startcal.set(Calendar.YEAR,year);
                startcal.set(Calendar.MONTH,month);
                startcal.set(Calendar.DAY_OF_MONTH,dayOfMonth);
                mTvBorthDay.setText(year+"-"+(month+1)+"-"+dayOfMonth);

//                TimePickerDialog dialog = new TimePickerDialog(RegisterDataActivity.this, new TimePickerDialog.OnTimeSetListener() {
//                    @Override
//                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
//
//                        startcal.set(Calendar.HOUR_OF_DAY,hourOfDay);
//                        startcal.set(Calendar.MINUTE, minute);
//
//                        String date = new java.text.SimpleDateFormat("yyyy/MM/dd HH:mm").format(new java.util.Date(startcal.getTimeInMillis()));
//
//                    }
//                },0,0,false);
//                dialog.show();

            }
        },mCalendar.get(Calendar.YEAR),mCalendar.get(Calendar.MONTH),mCalendar.get(Calendar.DAY_OF_MONTH));

        datePickerDialog.show();
    }

    private void showHeadDialog() {
        final Dialog dialog = new ChooesDialog(EditUserDataActivity.this,
                R.style.chooes_dialog_style);
        dialog.show();
        Window window = dialog.getWindow();
        RelativeLayout btnPhoto = (RelativeLayout) window.findViewById(R.id.rl_chooes_dialog_photo);
        btnPhoto.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                android.util.Log.e("Photo", "btnPhoto");
                UploadPictureHasZoomUtils.setPhoto(EditUserDataActivity.this);
                dialog.dismiss();
            }
        });
        RelativeLayout btnImage = (RelativeLayout) window.findViewById(R.id.rl_chooes_dialog_image);
        btnImage.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                android.util.Log.e("Photo", "btnImage");
                //UploadPictureHasZoomUtils.setManually(EditUserDataActivity.this);
                UploadPictureHasZoomUtils.callGalleryForInputImage(100, EditUserDataActivity.this);
                dialog.dismiss();
            }
        });
        RelativeLayout btnCancel = (RelativeLayout) window.findViewById(R.id.rl_chooes_dialog_cancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

    }

    private UploadTask mUploadTask;
    private UploadData mUploadData;

    private class UploadTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {
            File current_file = new File(file_full_path);
            mPicName = current_file.getName();
            mFile = CommonUtils.encodeToBase64(file_full_path);
            mTime = "";//图片无时长
            mUploadData = new UploadData();
            mUploadData = HttpSendJsonManager.upload(EditUserDataActivity.this, HttpSendJsonManager.UPLOAD_TPYE_HEAD, mPicName, mFile, mTime);
            if (mUploadData.isOK()) {
                mHandler.sendEmptyMessage(UPLOAD_SUCCESS);
            } else {
                mHandler.sendEmptyMessage(UPLOAD_FALSE);
            }
            return null;
        }
    }

    private SetUserInfoTask mSetUserInfoTask;
    private RegisterData mRegisterData;

    private class SetUserInfoTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {
            mRegisterData = HttpSendJsonManager.setUserInfo(EditUserDataActivity.this, mName, mGender, mBirth, mBlood, mImg);
            if (mRegisterData.isOK()) {
                mHandler.sendEmptyMessage(EDIT_USER_DATA_SUCCESS);
            } else {
                mHandler.sendEmptyMessage(EDIT_USER_DATA_FALSE);
            }
            return null;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        android.util.Log.e("Photo", "onActivityResult:" + "resultCode " + resultCode + "|requestCode"
                + requestCode);

        if (resultCode == -1) {
            if (requestCode == UploadPictureHasZoomUtils.REQUEST_IMAGE) { // 相册
                android.util.Log.e("Photo", "REQUEST_IMAGE");
                Uri imageFilePath = data.getData();
                file_full_path = FileUtils.getFileAbsolutePath(EditUserDataActivity.this, imageFilePath);
                UploadPictureHasZoomUtils.startPhotoZoom(imageFilePath, this);//要求截图
            }
            if (requestCode == UploadPictureHasZoomUtils.PHOTOHRAPH) {
                try {
                    android.util.Log.e("Photo", "PHOTOHRAPH");
                    file_full_path = UploadPictureHasZoomUtils.IMAGE_FILE_PATH;
                    File pictureFile = new File(file_full_path);
                    if (Build.VERSION.SDK_INT < 24) {
                        android.util.Log.e("Photo", "PHOTOHRAPH Build.VERSION.SDK_INT:" + Build.VERSION.SDK_INT);
                        UploadPictureHasZoomUtils.startPhotoZoom(Uri.fromFile(pictureFile), this);
                    } else {
                        android.util.Log.e("Photo", "PHOTOHRAPH Build.VERSION.SDK_INT:" + Build.VERSION.SDK_INT);
                        UploadPictureHasZoomUtils.startPhotoZoom(FileProvider.getUriForFile(EditUserDataActivity.this,
                                BuildConfig.APPLICATION_ID + ".provider",
                                pictureFile), this);
                    }
                    android.util.Log.e("Photo", "PHOTOHRAPH over");
                } catch (Exception e) {
                    android.util.Log.e("Photo", "PHOTOHRAPH ex");
                    e.printStackTrace();
                }
            }


            if (requestCode == UploadPictureHasZoomUtils.ZOOMOK)//截图完毕
            {
                android.util.Log.e("Photo", "ZOOMOK");
                Bundle extras = data.getExtras();
                try {
                    Uri imageFilePath = UploadPictureHasZoomUtils.imageUri;
                    file_full_path = FileUtils.getFileAbsolutePath(EditUserDataActivity.this, imageFilePath);
                    Log.e("Photo", "ZOOMOK:" + file_full_path);
                    //需要压缩文件
                    android.util.Log.e("Photo", "ZOOMOK decodeStream");
                    photo = BitmapFactory.decodeStream(getContentResolver().openInputStream(UploadPictureHasZoomUtils.imageUri));
                    mIvHead.setImageBitmap(photo);
                    mUploadTask = new UploadTask();
                    mUploadTask.execute("");
                    android.util.Log.e("Photo", "ZOOMOK over");
                } catch (FileNotFoundException e) {
                    android.util.Log.e("Photo", "ZOOMOK ex");
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        } else {
            mHandler.sendEmptyMessage(NO_PIC);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private UserInfoTask mUserInfoTask;
    private UserData mUserData;

    private class UserInfoTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {
            mUserData = new UserData();
            mUserData = HttpSendJsonManager.userInfo(EditUserDataActivity.this);
            DButtonApplication.mUserData = mUserData;
            if (mUserData.isOK()) {
                mHandler.sendEmptyMessage(USER_INFO_SUCCESS);
            } else {
                mHandler.sendEmptyMessageDelayed(USER_INFO_FALSE, 10000);
            }
            return null;
        }
    }


    private ChooesListDialog mChooesListDialog;
    private TextView mTvTitle;
    private LinearLayout mLlCancel;
    private ListView mLvChooes;

    public void showListDialog(ArrayList<String> list,String title,int what) {
        mChooesListDialog = new ChooesListDialog(EditUserDataActivity.this,
                R.style.share_dialog);
        mChooesListDialog.show();
        Window window = mChooesListDialog.getWindow();
        //设置Dialog从窗体底部弹出
        window.setGravity(Gravity.BOTTOM);
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.y = 60;//设置Dialog距离底部的距离
        lp.alpha = 1f;
        window.setAttributes(lp);
        mTvTitle = (TextView) window.findViewById(R.id.chooes_list_title);
        mTvTitle.setText(title);
        mLlCancel = (LinearLayout) window.findViewById(R.id.chooes_list_cancel);
        mLlCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mChooesListDialog.dismiss();
            }
        });
        mLvChooes = (ListView) window.findViewById(R.id.lv_chooes_list);
        ChooesListAdapter mChooesListAdapter = new ChooesListAdapter(EditUserDataActivity.this,list,mHandler,what);
        mLvChooes.setAdapter(mChooesListAdapter);
    }
    private InputDialog mInputDialog;
    private TextView mTvNameTitle;
    private LinearLayout mLlNameCancel,mLlNameOK;
    private EditText mEtNickName;

    public void showInputDialog(String title) {
        mInputDialog = new InputDialog(EditUserDataActivity.this,
                R.style.share_dialog);
        mInputDialog.show();
        Window window = mInputDialog.getWindow();
        //设置Dialog从窗体底部弹出
        window.setGravity(Gravity.BOTTOM);
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.y = 60;//设置Dialog距离底部的距离
        lp.alpha = 1f;
        window.setAttributes(lp);
        mTvNameTitle = (TextView) window.findViewById(R.id.input_title);
        mTvNameTitle.setText(title);
        mLlNameCancel = (LinearLayout) window.findViewById(R.id.input_cancel);
        mLlNameCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mInputDialog.dismiss();
            }
        });
        mLlNameOK = (LinearLayout) window.findViewById(R.id.input_ok);
        mLlNameOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTvName.setText(mEtNickName.getText().toString());
                mInputDialog.dismiss();
            }
        });
        mEtNickName = (EditText) window.findViewById(R.id.et_input_your_name);
    }
    private RegisterOverDialog mRegisterOverDialog;
    private TextView mTvRegisterTitle,mTvRegisterText;
    private LinearLayout mLlRegisterBind,mLlRegisterLogin;

    public void showRegisterSuccessDialog(String title,String text) {
        mRegisterOverDialog = new RegisterOverDialog(EditUserDataActivity.this,
                R.style.share_dialog);
        mRegisterOverDialog.show();
        Window window = mRegisterOverDialog.getWindow();
        //设置Dialog从窗体底部弹出
        //window.setGravity(Gravity.BOTTOM);
        WindowManager.LayoutParams lp = window.getAttributes();
        //lp.y = 60;//设置Dialog距离底部的距离
        lp.alpha = 1f;
        window.setAttributes(lp);
        mTvRegisterTitle = (TextView) window.findViewById(R.id.register_over_title);
        mTvRegisterTitle.setText(title);
        mTvRegisterText = (TextView) window.findViewById(R.id.register_over_text);
        mTvRegisterText.setText(text);
        mLlRegisterBind = (LinearLayout) window.findViewById(R.id.register_over_bind);
        mLlRegisterBind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EditUserDataActivity.this, BindDButtonActivity.class);
                startActivity(intent);
                mRegisterOverDialog.dismiss();
                finish();
            }
        });
        mLlRegisterLogin = (LinearLayout) window.findViewById(R.id.register_over_login);
        mLlRegisterLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(RegisterDataActivity.this, LoginActivity.class);
//                startActivity(intent);
                mRegisterOverDialog.dismiss();
                finish();
            }
        });
    }
}
