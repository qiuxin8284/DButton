package com.sfr.dbuttonapplication.http;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpUtil {
	public static boolean networkStatusOK(Context mContext) {
		boolean netStatus = false; 
		try{   
			ConnectivityManager connectManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo activeNetworkInfo = connectManager.getActiveNetworkInfo();
			if (activeNetworkInfo != null)
			{   
				if (activeNetworkInfo.isAvailable() && activeNetworkInfo.isConnected()) 
				{     
					netStatus = true;   
				}   
			} 
		} 
		catch (Exception e)
		{
			e.printStackTrace();
		}  
			return netStatus; 
	}
	public static byte[] getData(String htmlpath) throws Exception {
		byte[] data = null; 
		URL url = new URL(htmlpath);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		conn.setConnectTimeout(5*1000);
		conn.connect();
		InputStream in=conn.getInputStream();
		ByteArrayOutputStream bos=new ByteArrayOutputStream();
		byte[] buffer=new byte[1024];
		int len = 0;
		while((len=in.read(buffer))!=-1){
			bos.write(buffer,0,len);
		}
		data=bos.toByteArray();
		bos.close();
		in.close();
		return data; 

	} 
}
