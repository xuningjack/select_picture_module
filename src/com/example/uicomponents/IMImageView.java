/**
 * @project: 58bangbang
 * @file: IMImageView.java
 * @date: 2014-8-5 下午3:42:49
 * @copyright: 2014  58.com Inc.  All rights reserved. 
 */
package com.example.uicomponents;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;


public class IMImageView extends ImageView {
	
    /**显示图片的路径*/
	private String url;
	
    /**
     * Creates a new instance of IMImageView.
     * 
     * @param context
     *            context
     */
    public IMImageView(Context context) {
        super(context);
    }

    /**
     * Creates a new instance of IMImageView.
     * 
     * @param context
     *            context
     * @param attrs
     *            attrs
     */
    public IMImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * Creates a new instance of IMImageView.
     * 
     * @param context
     *            context
     * @param attrs
     *            attrs
     * @param defStyle
     *            defStyle
     */
    public IMImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

	/**
	 * @return the url
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * @param url the url to set
	 */
	public void setUrl(String url) {
		this.url = url;
	}
}