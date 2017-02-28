package com.wuba.bangbang.uicomponents.pulltorefresh.internal;

import android.content.Context;
import android.util.Log;

public class Utils {

    static final String LOG_TAG = "PullToRefresh";

    public static void warnDeprecation(String depreacted, String replacement) {
        Log.w(LOG_TAG, "You're using the deprecated " + depreacted
                + " attr, please switch over to " + replacement);
    }

    /**
     * 获得手机的android版本号
     * @param context
     * @return
     */
    public static int getVersionCode(Context context){
    	
    	int result = 0;
    	result = android.os.Build.VERSION.SDK_INT;
    	return result;
    }
    
    
}
