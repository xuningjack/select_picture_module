/**
* @project: 58bangbang
* @file: DensityUtil.java
* @date: 2014年10月8日 下午4:52:53
* @copyright: 2014  58.com Inc.  All rights reserved. 
*/
package com.wuba.bangbang.uicomponents.utils;

import android.content.Context;

/**
 * TODO 添加类的功能描述. 
 * @author 赵彦辉
 * @date: 2014年10月8日 下午4:52:53
 */
public class DensityUtil {
    /** 
     * 根据手机的分辨率从 dip 的单位 转成为 px(像素) 
     */  
    public static int dip2px(Context context, float dpValue) {  
        final float scale = context.getResources().getDisplayMetrics().density;  
        return (int) (dpValue * scale + 0.5f);  
    }  
  
    /** 
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp 
     */  
    public static int px2dip(Context context, float pxValue) {  
        final float scale = context.getResources().getDisplayMetrics().density;  
        return (int) (pxValue / scale + 0.5f);  
    }  
}
