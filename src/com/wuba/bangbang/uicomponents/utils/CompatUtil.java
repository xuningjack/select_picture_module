/*******************************************************************************
 * Copyright 2011, 2012 Chris Banes.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.wuba.bangbang.uicomponents.utils;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Build.VERSION;
import android.view.MotionEvent;
import android.view.View;



/**
 * 适配的工具类
 * @author 徐宁
 * @date: 2015-2-26 下午6:09:37
 */
public class CompatUtil {

    private static final int SIXTY_FPS_INTERVAL = 1000 / 60;
    private static final int VERSION_CODES_HONEYCOMB = 11;

    public static void postOnAnimation(View view, Runnable runnable) {
            
        view.postDelayed(runnable, SIXTY_FPS_INTERVAL);
    }


    public static int getPointerIndex(int action) {
        if (VERSION.SDK_INT >= VERSION_CODES_HONEYCOMB)
            return getPointerIndexHoneyComb(action);
        else
            return getPointerIndexEclair(action);
    }

    @TargetApi(Build.VERSION_CODES.ECLAIR)
    private static int getPointerIndexEclair(int action) {
        return (action & MotionEvent.ACTION_POINTER_ID_MASK) >> MotionEvent.ACTION_POINTER_ID_SHIFT;
    }

    @TargetApi(CompatUtil.VERSION_CODES_HONEYCOMB)
    private static int getPointerIndexHoneyComb(int action) {
        return (action & MotionEvent.ACTION_POINTER_INDEX_MASK) >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
    }
}