package com.project.livefeed;

import android.content.Context;
import android.widget.Toast;

public class AndroidUtlils {

	public static void toastShort(Context context, String text){
		Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
	}
	
	public static void toastLong(Context context, String text){
		Toast.makeText(context, text, Toast.LENGTH_LONG).show();
	}
}
