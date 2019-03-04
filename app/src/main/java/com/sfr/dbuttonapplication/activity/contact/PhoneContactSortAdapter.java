package com.sfr.dbuttonapplication.activity.contact;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.sfr.dbuttonapplication.DButtonApplication;
import com.sfr.dbuttonapplication.R;
import com.sfr.dbuttonapplication.activity.widget.LoadingProgressDialog;
import com.sfr.dbuttonapplication.entity.SortModel;
import com.sfr.dbuttonapplication.entity.UploadData;
import com.sfr.dbuttonapplication.entity.UserData;
import com.sfr.dbuttonapplication.http.HttpSendJsonManager;
import com.sfr.dbuttonapplication.utils.PictureUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * ???????????
 *
 * @author Administrator
 */
public class PhoneContactSortAdapter extends BaseAdapter implements SectionIndexer {
    private List<SortModel> list = new ArrayList<SortModel>();
    private Context mContext;
    ListView listView;
    private Handler mHandler;
    private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
    private DisplayImageOptions options;

    public PhoneContactSortAdapter(Context mContext, List<SortModel> list, ListView listView, Handler handler) {
        this.mContext = mContext;
        this.list = list;
        this.listView = listView;
        this.mHandler = handler;
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.mipmap.img_default_head)
                .showImageForEmptyUri(R.mipmap.img_default_head)
                .showImageOnFail(R.mipmap.img_default_head).cacheInMemory(true)
                .cacheOnDisk(true).considerExifParams(true).build();
    }

    public void updateListView(List<SortModel> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    public int getCount() {
        return this.list.size();
    }

    public Object getItem(int position) {
        try {
            return list.get(position);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View view, ViewGroup arg2) {
        ViewHolder viewHolder = null;
        final SortModel mContent = list.get(position);

        if (view == null) {
            viewHolder = new ViewHolder();
            view = LayoutInflater.from(mContext).inflate(R.layout.add_phone_contacts_item, null);
            viewHolder.tvLetter = (TextView) view.findViewById(R.id.tv_contacts_item_catalog);
            viewHolder.ivHead = (ImageView) view.findViewById(R.id.iv_add_phone_contacts_item_head);
            viewHolder.rlLine = (RelativeLayout) view.findViewById(R.id.rl_message_item_bottom);
            viewHolder.rlLetter = (RelativeLayout) view.findViewById(R.id.rl_contacts_item_catalog);
            viewHolder.tvFriendName = (TextView) view.findViewById(R.id.tv_add_phone_contacts_item_friendname);
            viewHolder.tvLastMessage = (TextView) view.findViewById(R.id.tv_add_phone_contacts_item_lastmessage);
            viewHolder.btnAdd = (Button) view.findViewById(R.id.btn_friend_state);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        final SortModel model = list.get(position);

        int section = getSectionForPosition(position);

        if (position == getPositionForSection(section)) {
            viewHolder.rlLetter.setVisibility(View.VISIBLE);
            viewHolder.tvLetter.setText(mContent.getSortLetters());
            viewHolder.rlLine.setVisibility(View.GONE);
        } else {
            viewHolder.rlLetter.setVisibility(View.GONE);
            viewHolder.rlLine.setVisibility(View.VISIBLE);
        }

        if (list.get(position).getNameId() != null) {
            viewHolder.tvLastMessage.setVisibility(View.VISIBLE);
            viewHolder.tvLastMessage.setText(list.get(position).getNameId());
        } else {
            viewHolder.tvLastMessage.setVisibility(View.GONE);
        }

        if (this.list.get(position).getBitHead() != null) {
            //viewHolder.ivHead.setImageBitmap(this.list.get(position).getBitHead());
            ImageLoader.getInstance().displayImage(list.get(position).getBitHead().toString(), viewHolder.ivHead, options, animateFirstListener);
        } else {
            viewHolder.ivHead.setTag(list.get(position).getNameId());
            viewHolder.ivHead.setImageResource(R.mipmap.img_default_head);
        }
        viewHolder.tvFriendName.setText(list.get(position).getName());

        viewHolder.btnAdd.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!DButtonApplication.mContactMap.containsKey(list.get(position).getNameId())) {
                    //mPhone = "13316834116";
                    mPhone = list.get(position).getNameId();
                    mName = list.get(position).getName();
//                    if (list.get(position).getBitHead() != null) {
//                        InputStream input = ContactsContract.Contacts
//                                .openContactPhotoInputStream(mContext.getContentResolver(), list.get(position).getBitHead());
//                        mBitmap = BitmapFactory.decodeStream(input);
//                        mUploadTask = new UploadTask();
//                        mUploadTask.execute(mName);
//                    } else {
                        mImg = "";
                    LoadingProgressDialog.show(mContext, false, true, 30000);
                        mContactAddTask = new ContactAddTask();
                        mContactAddTask.execute("");
                    }
//                }
            }
        });
        if (DButtonApplication.mContactMap.containsKey(list.get(position).getNameId())) {
            viewHolder.btnAdd.setText(R.string.has_add);
            viewHolder.btnAdd.setBackgroundResource(R.mipmap.btn_login_none);
        } else {
            viewHolder.btnAdd.setText(R.string.add);
            viewHolder.btnAdd.setBackgroundResource(R.drawable.add_phone_contact_btn_selector);
        }
        return view;

    }
    public void addContact(String img){
        mImg = img;
        LoadingProgressDialog.show(mContext, false, true, 30000);
        mContactAddTask = new ContactAddTask();
        mContactAddTask.execute("");
    }

    final static class ViewHolder {
        TextView tvLetter;
        ImageView ivHead;
        RelativeLayout rlLine;
        RelativeLayout rlLetter;
        TextView tvFriendName;
        TextView tvLastMessage;
        Button btnAdd;
    }


    public int getSectionForPosition(int position) {
        return list.get(position).getSortLetters().charAt(0);
    }

    public int getPositionForSection(int section) {
        for (int i = 0; i < getCount(); i++) {
            String sortStr = list.get(i).getSortLetters();
            char firstChar = sortStr.toUpperCase().charAt(0);
            if (firstChar == section) {
                return i;
            }
        }

        return -1;
    }

    private String getAlpha(String str) {
        String sortStr = str.trim().substring(0, 1).toUpperCase();
        if (sortStr.matches("[A-Z]")) {
            return sortStr;
        } else {
            return "#";
        }
    }

    @Override
    public Object[] getSections() {
        return null;
    }

    private ContactAddTask mContactAddTask;

    private class ContactAddTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {
            String isUrgent = HttpSendJsonManager.IS_URGENT_NO;
            if (DButtonApplication.mContactList.size() == 0) {
                isUrgent = HttpSendJsonManager.IS_URGENT_YES;
            } else {
                isUrgent = HttpSendJsonManager.IS_URGENT_NO;
            }
            if (HttpSendJsonManager.addContact(mContext, mPhone, mName, isUrgent, mImg)) {
                UserData userData = new UserData();
                userData.setName(mPhone);
                userData.setPhone(mName);
                userData.setIsUrgent(isUrgent);
                userData.setImg(mImg);
                DButtonApplication.mContactMap.put(mPhone,userData);
                Message message = new Message();
                message.what = AddPhoneContactActivity.ADD_PHONE_CONTACT_SUCCESS;
                message.obj = mPhone;
                mHandler.sendMessage(message);
            } else {
                mHandler.sendEmptyMessage(AddPhoneContactActivity.ADD_PHONE_CONTACT_FALSE);
            }
            return null;
        }
    }


    private UploadTask mUploadTask;
    private UploadData mUploadData;
    private Bitmap mBitmap;
    private String mPhone;
    private String mName;
    private String mImg ;

    private class UploadTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {
//            File current_file = new File(file_full_path);
//            String mPicName = params[0];
//            String mFile = CommonUtils.encodeToBase64(file_full_path);
//            String mTime = "";
            String mPicName = params[0]+".jpg";
            String mFile = PictureUtil.bitmapToBase64(mBitmap);
            String mTime = "";
            mUploadData = new UploadData();
            mUploadData = HttpSendJsonManager.upload(mContext, HttpSendJsonManager.UPLOAD_TPYE_HEAD, mPicName, mFile, mTime);
            if (mUploadData.isOK()) {
                Message message = new Message();
                message.what = AddPhoneContactActivity.UPLOAD_SUCCESS;
                message.obj = mUploadData.getUrl();
                mHandler.sendMessage(message);
            } else {
                mHandler.sendEmptyMessage(AddPhoneContactActivity.UPLOAD_FALSE);
            }
            return null;
        }
    }

    private static class AnimateFirstDisplayListener extends SimpleImageLoadingListener {

        static final List<String> displayedImages = Collections.synchronizedList(new LinkedList<String>());

        @Override
        public void onLoadingComplete(String imageUri, View view,
                                      Bitmap loadedImage) {
            if (loadedImage != null) {
                ImageView imageView = (ImageView) view;
                boolean firstDisplay = !displayedImages.contains(imageUri);
                if (firstDisplay) {
                    FadeInBitmapDisplayer.animate(imageView, 500);
                    displayedImages.add(imageUri);
                }
            }
        }
    }
}