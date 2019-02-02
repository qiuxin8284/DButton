package com.sfr.dbuttonapplication.activity.login;

import android.Manifest;
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
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jordan.httplibrary.utils.CommonUtils;
import com.sfr.dbuttonapplication.BuildConfig;
import com.sfr.dbuttonapplication.R;
import com.sfr.dbuttonapplication.activity.widget.ChooesDialog;
import com.sfr.dbuttonapplication.activity.widget.LoadingProgressDialog;
import com.sfr.dbuttonapplication.entity.RegisterData;
import com.sfr.dbuttonapplication.entity.UploadData;
import com.sfr.dbuttonapplication.http.HttpAnalyJsonManager;
import com.sfr.dbuttonapplication.http.HttpSendJsonManager;
import com.sfr.dbuttonapplication.utils.APPUtils;
import com.sfr.dbuttonapplication.utils.FileUtils;
import com.sfr.dbuttonapplication.utils.ToastUtils;
import com.sfr.dbuttonapplication.utils.UploadPictureHasZoomUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Calendar;
import java.util.Date;

public class RegisterDataActivity extends AppCompatActivity implements OnClickListener {

    private Button mBtnRegisterOver;
    private String mPhone, mCode;
    private static final int UPLOAD_SUCCESS = 1;
    private static final int UPLOAD_FALSE = 2;
    private static final int REGISTER_SUCCESS = 3;
    private static final int REGISTER_FALSE = 4;
    private static final int N0_PERMISSION = 5;
    private static final int NO_PIC = 6;;
    private EditText mEtName;
    private TextView mEtBorthDay;//et_input_your_name,et_input_your_bor
    private ImageView mIvHead;//iv_input_your_head
    private ImageView mIvTime;
    private CheckBox mCBSexMan,mCbSexWoman;//cb_sex_man,cb_sex_woman涉及分组
    private CheckBox mCBBloodA,mCBBloodB,mCBBloodAB,mCBBloodO,mCBBloodOther;//cb_blood_a，cb_blood_b，cb_blood_ab,cb_blood_o,cb_blood_other涉及分组
    private boolean mIsGrant;
    private RelativeLayout mRlBorthDay;
    String file_full_path = "";
    Bitmap photo;



    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case UPLOAD_SUCCESS:
                    mImg = mUploadData.getUrl();
                    break;
                case UPLOAD_FALSE:
                    ToastUtils.shortToast(RegisterDataActivity.this, getResources().getString(R.string.upload_photo_false));
                    break;
                case REGISTER_SUCCESS:
                    LoadingProgressDialog.Dissmiss();
                    Intent intent = new Intent(RegisterDataActivity.this, RegisterSucessActivity.class);
                    startActivity(intent);
                    finish();
                    break;
                case REGISTER_FALSE:
                    LoadingProgressDialog.Dissmiss();
                    ToastUtils.shortToast(RegisterDataActivity.this, HttpAnalyJsonManager.lastError);
                    break;
                case NO_PIC:
                    ToastUtils.shortToast(RegisterDataActivity.this, getResources().getString(R.string.no_photo));
                    break;
                case N0_PERMISSION:
                    ToastUtils.shortToast(RegisterDataActivity.this, getResources().getString(R.string.please_open_permission));
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        APPUtils.setTranslucent(this);
        setContentView(R.layout.activity_register_data);
        initTitle();
        setView();
        setListener();
        mIsGrant = false;
        checkCameraPermission();
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
        mPhone = getIntent().getStringExtra("phone");
        mCode = getIntent().getStringExtra("code");
        mBtnRegisterOver = (Button) findViewById(R.id.btn_register_over);

        mEtName = (EditText) findViewById(R.id.et_input_your_name);
        mEtBorthDay = (TextView) findViewById(R.id.et_input_your_bor);
        mRlBorthDay = (RelativeLayout)findViewById(R.id.rl_input_your_bor);

        mIvHead = (ImageView) findViewById(R.id.iv_input_your_head);
        mIvTime = (ImageView) findViewById(R.id.iv_time);

        mCBSexMan = (CheckBox) findViewById(R.id.cb_sex_man);
        mCbSexWoman = (CheckBox) findViewById(R.id.cb_sex_woman);

        mCBBloodA = (CheckBox) findViewById(R.id.cb_blood_a);
        mCBBloodB = (CheckBox) findViewById(R.id.cb_blood_b);
        mCBBloodAB = (CheckBox) findViewById(R.id.cb_blood_ab);
        mCBBloodO = (CheckBox) findViewById(R.id.cb_blood_o);
        mCBBloodOther = (CheckBox) findViewById(R.id.cb_blood_other);
    }

    private void setListener() {

        mBtnRegisterOver.setOnClickListener(this);
        mIvHead.setOnClickListener(this);
        mRlBorthDay.setOnClickListener(this);
        mEtBorthDay.setKeyListener(null);
        mIvTime.setOnClickListener(this);
        mCBSexMan.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    mCbSexWoman.setChecked(false);
                }
            }
        });
        mCbSexWoman.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    mCBSexMan.setChecked(false);
                }
            }
        });
        mCBBloodA.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    mCBBloodB.setChecked(false);
                    mCBBloodAB.setChecked(false);
                    mCBBloodO.setChecked(false);
                    mCBBloodOther.setChecked(false);
                }
            }
        });
        mCBBloodB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    mCBBloodA.setChecked(false);
                    mCBBloodAB.setChecked(false);
                    mCBBloodO.setChecked(false);
                    mCBBloodOther.setChecked(false);
                }
            }
        });
        mCBBloodAB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    mCBBloodA.setChecked(false);
                    mCBBloodB.setChecked(false);
                    mCBBloodO.setChecked(false);
                    mCBBloodOther.setChecked(false);
                }
            }
        });
        mCBBloodO.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    mCBBloodA.setChecked(false);
                    mCBBloodB.setChecked(false);
                    mCBBloodAB.setChecked(false);
                    mCBBloodOther.setChecked(false);
                }
            }
        });
        mCBBloodOther.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    mCBBloodA.setChecked(false);
                    mCBBloodB.setChecked(false);
                    mCBBloodAB.setChecked(false);
                    mCBBloodO.setChecked(false);
                }
            }
        });

    }

    private TextView mActivityTitle, mTitleExtra, mTitleBack;

    private void initTitle() {
        mActivityTitle = (TextView) findViewById(R.id.title_info);
        mTitleExtra = (TextView) findViewById(R.id.title_extra);
        mTitleBack = (TextView) findViewById(R.id.title_back);
        mActivityTitle.setText(getResources().getString(R.string.new_user_register));
        mTitleExtra.setVisibility(View.GONE);
        mTitleBack.setVisibility(View.VISIBLE);
        mTitleBack.setText(getResources().getString(R.string.go_up));
        mTitleBack.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private String mName, mGender, mBirth, mBlood, mImg="";
    private String mPicName, mFile, mTime;
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_register_over:
                //参数判定
                mName = mEtName.getText().toString();
                mBirth = mEtBorthDay.getText().toString();
                mGender = "";
                if(mCBSexMan.isChecked()) mGender=HttpSendJsonManager.SEX_MAN;
                if(mCbSexWoman.isChecked()) mGender=HttpSendJsonManager.SEX_WOMAN;
                mBlood = "";
                if(mCBBloodA.isChecked()) mBlood=HttpSendJsonManager.BLOOD_A;
                if(mCBBloodB.isChecked()) mBlood=HttpSendJsonManager.BLOOD_B;
                if(mCBBloodAB.isChecked()) mBlood=HttpSendJsonManager.BLOOD_AB;
                if(mCBBloodO.isChecked()) mBlood=HttpSendJsonManager.BLOOD_O;
                if(mCBBloodOther.isChecked()) mBlood=HttpSendJsonManager.BLOOD_OTHER;

                if (TextUtils.isEmpty(mName) || TextUtils.isEmpty(mName)) {
                    Toast.makeText(this, R.string.please_input_your_name, Toast.LENGTH_LONG).show();
                    return;
                }else if (TextUtils.isEmpty(mGender) || TextUtils.isEmpty(mGender)) {
                    Toast.makeText(this, R.string.please_choies_your_sex, Toast.LENGTH_LONG).show();
                    return;
                }else if (TextUtils.isEmpty(mBlood) || TextUtils.isEmpty(mBlood)) {
                    Toast.makeText(this, R.string.please_input_your_blood_group, Toast.LENGTH_LONG).show();
                    return;
                }else if (TextUtils.isEmpty(mBirth) || TextUtils.isEmpty(mBirth)) {
                    Toast.makeText(this, R.string.please_input_your_date_of_birth, Toast.LENGTH_LONG).show();
                    return;
                }
                else if (TextUtils.isEmpty(mImg) || TextUtils.isEmpty(mImg)) {
                    Toast.makeText(this, R.string.please_input_your_head, Toast.LENGTH_LONG).show();
                    return;
                }
                else{
                    LoadingProgressDialog.show(RegisterDataActivity.this, false, true, 30000);
                    mRegisterAccountTask = new RegisterAccountTask();
                    mRegisterAccountTask.execute("");
                }
                break;
            case R.id.iv_input_your_head:
                //调用Dialog 拍照或者相册
                if (mIsGrant) {
                    showHeadDialog();
                } else {
                    mHandler.sendEmptyMessage(N0_PERMISSION);
                }
                break;
            case R.id.rl_input_your_bor:
                //initTimeDialog();
                break;
            case R.id.iv_time:
                initTimeDialog();
                break;
        }
    }

    Calendar startcal;
    private void initTimeDialog() {
        long time= System.currentTimeMillis();
        Calendar mCalendar= Calendar.getInstance();
        mCalendar.setTimeInMillis(time);

        Date date = new Date();
        DatePickerDialog datePickerDialog = new DatePickerDialog(RegisterDataActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                startcal = Calendar.getInstance();
                startcal.set(Calendar.YEAR,year);
                startcal.set(Calendar.MONTH,month);
                startcal.set(Calendar.DAY_OF_MONTH,dayOfMonth);
                mEtBorthDay.setText(year+"-"+(month+1)+"-"+dayOfMonth);

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
        final Dialog dialog = new ChooesDialog(RegisterDataActivity.this,
                R.style.chooes_dialog_style);
        dialog.show();
        Window window = dialog.getWindow();
        RelativeLayout btnPhoto = (RelativeLayout) window.findViewById(R.id.rl_chooes_dialog_photo);
        btnPhoto.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                android.util.Log.e("Photo", "btnPhoto");
                UploadPictureHasZoomUtils.setPhoto(RegisterDataActivity.this);
                dialog.dismiss();
            }
        });
        RelativeLayout btnImage = (RelativeLayout) window.findViewById(R.id.rl_chooes_dialog_image);
        btnImage.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                android.util.Log.e("Photo", "btnImage");
                //UploadPictureHasZoomUtils.setManually(RegisterDataActivity.this);
                UploadPictureHasZoomUtils.callGalleryForInputImage(100,RegisterDataActivity.this);
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
            mUploadData = HttpSendJsonManager.upload(RegisterDataActivity.this, HttpSendJsonManager.UPLOAD_TPYE_HEAD, mPicName, mFile, mTime);
            if (mUploadData.isOK()) {
                mHandler.sendEmptyMessage(UPLOAD_SUCCESS);
            } else {
                mHandler.sendEmptyMessage(UPLOAD_FALSE);
            }
            return null;
        }
    }
    private RegisterAccountTask mRegisterAccountTask;
    private RegisterData mRegisterData;

    private class RegisterAccountTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {
            mRegisterData = HttpSendJsonManager.registerAccount(RegisterDataActivity.this, mPhone, mCode ,mName, mGender, mBirth, mBlood, mImg);
            if (mRegisterData.isOK()) {
                mHandler.sendEmptyMessage(REGISTER_SUCCESS);
            } else {
                mHandler.sendEmptyMessage(REGISTER_FALSE);
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
                file_full_path = FileUtils.getFileAbsolutePath(RegisterDataActivity.this, imageFilePath);
                UploadPictureHasZoomUtils.startPhotoZoom(imageFilePath, this);//要求截图
            }
            if (requestCode == UploadPictureHasZoomUtils.PHOTOHRAPH) {
                try {
                    android.util.Log.e("Photo", "PHOTOHRAPH");
                    file_full_path = UploadPictureHasZoomUtils.IMAGE_FILE_PATH;
                    File pictureFile = new File(file_full_path);
                    if (Build.VERSION.SDK_INT < 24) {
                        android.util.Log.e("Photo", "PHOTOHRAPH Build.VERSION.SDK_INT:"+ Build.VERSION.SDK_INT);
                        UploadPictureHasZoomUtils.startPhotoZoom(Uri.fromFile(pictureFile),this);
                    }else{
                        android.util.Log.e("Photo", "PHOTOHRAPH Build.VERSION.SDK_INT:"+ Build.VERSION.SDK_INT);
                        UploadPictureHasZoomUtils.startPhotoZoom(FileProvider.getUriForFile(RegisterDataActivity.this,
                                BuildConfig.APPLICATION_ID + ".provider",
                                pictureFile),this);
                    }
                    android.util.Log.e("Photo", "PHOTOHRAPH over");
                } catch (Exception e) {
                    android.util.Log.e("Photo", "PHOTOHRAPH ex");
                    e.printStackTrace();
                }
            }


            if(requestCode == UploadPictureHasZoomUtils.ZOOMOK)//截图完毕
            {
                android.util.Log.e("Photo", "ZOOMOK");
                Bundle extras = data.getExtras();
                try {
                    Uri imageFilePath = UploadPictureHasZoomUtils.imageUri;
                    file_full_path = FileUtils.getFileAbsolutePath(RegisterDataActivity.this, imageFilePath);
                    Log.e("Photo", "ZOOMOK:"+file_full_path);
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
}
