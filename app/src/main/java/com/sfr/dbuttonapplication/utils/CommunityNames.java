package com.sfr.dbuttonapplication.utils;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.Photo;
import android.text.TextUtils;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

public class CommunityNames {

	Context mContext = null;

	private static final String[] PHONES_PROJECTION = new String[] {
		Phone.DISPLAY_NAME, Phone.NUMBER, Photo.PHOTO_ID, Phone.CONTACT_ID };

	private static final int PHONES_DISPLAY_NAME_INDEX = 0;

	private static final int PHONES_NUMBER_INDEX = 1;

	private static final int PHONES_PHOTO_ID_INDEX = 2;

	private static final int PHONES_CONTACT_ID_INDEX = 3;

	public ArrayList<String> mContactsID = new ArrayList<String>();

	public ArrayList<String> mContactsName = new ArrayList<String>();

	public ArrayList<String> mContactsNumber = new ArrayList<String>();

	public ArrayList<Uri> mContactsPhonto = new ArrayList<Uri>();

	public ArrayList<HashMap<String, String>> Contactors =new ArrayList<HashMap<String, String>>();


    public CommunityNames(Context context){
    	mContext=context;
    }



	public ArrayList<HashMap<String, String>> getAllContacts()
	{
		Contactors =new ArrayList<HashMap<String, String>>();
		getPhoneContacts();
		getSIMContacts();
		return Contactors;

	}

	public void getPhoneContacts() {
		ContentResolver resolver = mContext.getContentResolver();

		Cursor phoneCursor = resolver.query(Phone.CONTENT_URI,PHONES_PROJECTION, null, null, null);


		if (phoneCursor != null) {
			while (phoneCursor.moveToNext()) {

				String phoneNumber = phoneCursor.getString(PHONES_NUMBER_INDEX);
				if (TextUtils.isEmpty(phoneNumber))
					continue;

				String contactName = phoneCursor.getString(PHONES_DISPLAY_NAME_INDEX);

				Long contactid = phoneCursor.getLong(PHONES_CONTACT_ID_INDEX);

				Long photoid = phoneCursor.getLong(PHONES_PHOTO_ID_INDEX);
				mContactsID.add(contactid+"");
				mContactsName.add(contactName);
				mContactsNumber.add(phoneNumber);


				Uri uriNumber2Contacts = Uri
						.parse("content://com.android.contacts/"
								+ "data/phones/filter/" + phoneNumber);
				Cursor cursorCantacts = resolver.query(uriNumber2Contacts, null, null,
								null, null);
				if (cursorCantacts.getCount() > 0) {
					cursorCantacts.moveToFirst();
					Long contactID = cursorCantacts.getLong(cursorCantacts
							.getColumnIndex("contact_id"));
					Uri uri = ContentUris.withAppendedId(
							ContactsContract.Contacts.CONTENT_URI, contactID);
					InputStream input = ContactsContract.Contacts
							.openContactPhotoInputStream(resolver, uri);
					Bitmap bitmap = BitmapFactory.decodeStream(input);
					mContactsPhonto.add(uri);

				} else {
					mContactsPhonto.add(null);

				}
				
				HashMap<String, String> map =new HashMap<String, String>();
				map.put("name", contactName);
				map.put("number", phoneNumber);
				Contactors.add(map);

			}

			phoneCursor.close();
		}
	}

	public void getSIMContacts() {
		ContentResolver resolver = mContext.getContentResolver();
		Uri uri = Uri.parse("content://icc/adn");
		Cursor phoneCursor = resolver.query(uri, PHONES_PROJECTION, null, null,
				null);

		if (phoneCursor != null) {
			while (phoneCursor.moveToNext()) {
				String phoneNumber = phoneCursor.getString(PHONES_NUMBER_INDEX);
				if (TextUtils.isEmpty(phoneNumber))
					continue;
				String contactName = phoneCursor
						.getString(PHONES_DISPLAY_NAME_INDEX);

				Long contactid = phoneCursor.getLong(PHONES_CONTACT_ID_INDEX);

				mContactsID.add(contactid+"");
				mContactsName.add(contactName);
				mContactsNumber.add(phoneNumber);

				HashMap<String, String> map =new HashMap<String, String>();
				map.put("name", contactName);
				map.put("number", phoneNumber);
				Contactors.add(map);

			}

			phoneCursor.close();
		}
	}


}