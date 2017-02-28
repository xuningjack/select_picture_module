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
package com.wuba.bangbang.uicomponents.gesture;

import android.view.MotionEvent;
import com.wuba.bangbang.uicomponents.gesture.listener.OnGestureListener;

/**
 * 不同api的手势监听器需要实现的接口
 * TODO 添加类的功能描述. 
 * @author 徐宁
 * @date: 2015-2-27 上午11:26:15
 */
public interface IGestureDetector {

    public boolean onTouchEvent(MotionEvent ev);

    public boolean isScaling();

    public void setOnGestureListener(OnGestureListener listener);

}
