package com.wuba.bangbang.uicomponents.gesture;

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

import android.content.Context;
import android.os.Build;

import com.wuba.bangbang.uicomponents.gesture.listener.OnGestureListener;

/**
 * 根据不同的sdk版本，生成手势监听器
 * @author 徐宁
 * @date: 2015-2-27 上午11:01:57
 */
public final class VersionedGestureGenerator {

    public static IGestureDetector newInstance(Context context, OnGestureListener listener) {
        
        final int sdkVersion = Build.VERSION.SDK_INT;
        IGestureDetector detector;

        if (sdkVersion < Build.VERSION_CODES.ECLAIR) {  //api5
            detector = new CupcakeGestureDetector(context);
        } else if (sdkVersion < Build.VERSION_CODES.FROYO) {  //api8
            detector = new EclairGestureDetector(context);
        } else {
            detector = new FroyoGestureDetector(context);
        }
        detector.setOnGestureListener(listener);
        return detector;
    }
}