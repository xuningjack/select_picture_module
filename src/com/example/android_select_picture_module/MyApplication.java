/**
 * @project: 58bangbang
 * @file: MyApplication.java
 * @date: 2015-2-24 上午7:49:11
 * @copyright: 2015  58.com Inc.  All rights reserved. 
 */
package com.example.android_select_picture_module;

import android.app.Application;

import com.wuba.bangbang.uicomponents.utils.ImageLoaderUtils;

/**
 * 自定义的Application类
 * @author 徐宁
 * @date: 2015-2-24 上午7:49:11
 */
public class MyApplication extends Application {

    @Override
    public void onCreate() {

        super.onCreate();
        ImageLoaderUtils.init(getApplicationContext());
    }
}