/**
 * @project: 58bangbang
 * @file: IMCirclePageIndicator.java
 * @date: 2014年10月23日 上午9:50:16
 * @copyright: 2014  58.com Inc.  All rights reserved. 
 */
package com.example.uicomponents;

import android.content.Context;
import android.util.AttributeSet;

import com.wuba.bangbang.uicomponents.viewpagerindicator.CirclePageIndicator;

/**
 * ViewPager的分页指示器，继承自开源项目viewpagerindicator. <br/>
 *               https://github.com/JakeWharton/Android-ViewPagerIndicator
 */
public class IMCirclePageIndicator extends CirclePageIndicator {

    /**
     * Creates a new instance of IMCirclePageIndicator.
     * 
     * @param context
     */
    public IMCirclePageIndicator(Context context) {
        super(context);
    }

    /**
     * Creates a new instance of IMCirclePageIndicator.
     * 
     * @param context
     * @param attrs
     */
    public IMCirclePageIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * Creates a new instance of IMCirclePageIndicator.
     * 
     * @param context
     * @param attrs
     * @param defStyle
     */
    public IMCirclePageIndicator(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

}
