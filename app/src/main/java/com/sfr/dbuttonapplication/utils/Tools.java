package com.sfr.dbuttonapplication.utils;

import android.content.Context;
import android.widget.Toast;


/**
 * Tools??????
 * @author qx
 *
 */
public class Tools {

	public static boolean isNull(String str)
	{
		if("".equals(str)|| null==str || " ".equals(str)|| "null".equals(str))
		{
			return true;
		}
		return false;
	}

	public static void showInfo(Context context, String msg) {
		try{
			Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	public static void showInfo(Context context, String msg, boolean islong) {
		if (islong) {
			try{
				Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
			}catch (Exception e) {
				e.printStackTrace();
			}
		}else{
			try{
				Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
			}catch (Exception e) {
				e.printStackTrace();
			}
		}
	
	}

}
