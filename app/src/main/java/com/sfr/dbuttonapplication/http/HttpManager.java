package com.sfr.dbuttonapplication.http;


import android.content.Context;

import com.jordan.httplibrary.utils.Base64;
import com.jordan.httplibrary.utils.DES3Util;
import com.safari.core.protocol.ResponseMessage;
import com.sfr.dbuttonapplication.DButtonApplication;
import com.sfr.dbuttonapplication.R;
import com.sfr.dbuttonapplication.utils.LogUtil;

import org.json.JSONObject;

public class HttpManager {	
	private static HttpManager sInstance = null;
	private boolean OnlineFlag;
	private Context mContext;
	public static HttpManager getInstance(Context context) {
		LogUtil.println("getInstance");
		if (null == sInstance) {
			sInstance = new HttpManager(context);
		};
		return sInstance;
	}

	private HttpManager(Context context)
	{
		LogUtil.println("HttpManager");
		this.mContext = context;
	}
	public String SyncHttpCommunicate(final String url, final String json)
	{
		OnlineFlag=true;
		if(!HttpUtil.networkStatusOK(mContext))
		{
			//HttpAnalyJsonManager.lastError=mContext.getResources().getString(R.string.please_link_network);
			OnlineFlag=false;
			return "{\"result\":\"0\"}";
		}

		HttpAnalyJsonManager.lastError="";
		final StringKeyValue tempResult = new StringKeyValue();
		tempResult.Value="";
		new Thread(){
			@Override
			public void run(){
				JSONObject resultJson=null;
				JSONObject dataJson = null;
				try {
					String resultJsonString= DButtonApplication.communicator.HttpPostSend(CommunicateConfig.GetHttpClientAdress()+url,json);
					resultJson = new JSONObject(resultJsonString);
					String datas = resultJson.getString("data");
					byte[] dataByte = Base64.decode(datas);
					ResponseMessage.Response response = ResponseMessage.Response.parseFrom(dataByte);
					String resultCode = response.getResult();
					String data = response.getData();
					LogUtil.println("sendSMS synchronousResult2 data" + data+" resultCode:"+resultCode);
					data = DES3Util.decode(data);
					LogUtil.println("sendSMS synchronousResult3 data" + data);
					if(resultCode.equals("1"))
					{
						dataJson = new JSONObject(data);
						tempResult.Value=dataJson.getString("msg");
						HttpAnalyJsonManager.lastErrorDefaultValue(mContext,tempResult.Value);
						LogUtil.println("@@@@@@@HttpCommunicateFailed:");
						LogUtil.println(HttpAnalyJsonManager.lastError);
					}
					if(resultJsonString=="")
					{
						HttpAnalyJsonManager.lastError=mContext.getResources().getString(R.string.failed_to_obtain_network_information);
					}
					tempResult.Value=data;
				} catch (Exception e) {
					tempResult.Value= HttpAnalyJsonManager.lastError=mContext.getResources().getString(R.string.network_connection_failed);
					e.printStackTrace();
				}
				this.interrupt();
			}
		}.start();
		while(tempResult.Value=="")
		{
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				LogUtil.println(e.getMessage());
				e.printStackTrace();
			}
		}
		return tempResult.Value;
	}
}
