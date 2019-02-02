package com.sfr.dbuttonapplication.utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;

import com.sfr.dbuttonapplication.entity.ContactsData;

import java.util.ArrayList;


public class PhoneContactsUtil {

	public static ArrayList<ContactsData> getPhoneContactsList(Context context) {
		ArrayList<ContactsData> list = new ArrayList<ContactsData>();
		CommunityNames communityNames = new CommunityNames(context);
		communityNames.getPhoneContacts();
		for (int i = 0; i < communityNames.mContactsNumber.size(); i++) {
			ContactsData contactsData = new ContactsData();
			contactsData.setCallName(communityNames.mContactsName.get(i));
			contactsData.setName(communityNames.mContactsNumber.get(i).replace(" ", ""));
			if (communityNames.mContactsPhonto.get(i) != null) {
				contactsData.setBitHead(communityNames.mContactsPhonto.get(i));
			}
			LogUtil.i("phoneContact", contactsData.toString());
			list.add(contactsData);
		}
		return list;
	}

	public static String getUserPhoneNumber(Context context) {
		TelephonyManager phoneMgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED &&
				ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
			return "";
		}
		return phoneMgr.getLine1Number();
	}
	public static boolean isPhoneNumber(String phoneNumber){
		if(phoneNumber!=null){
			if(!phoneNumber.trim().equals("")){
				if(phoneNumber.length()==11){
					return true;
				}
			}
		}
		return false;
	}
}
