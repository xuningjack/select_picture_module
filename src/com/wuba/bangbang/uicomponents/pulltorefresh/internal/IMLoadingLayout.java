package com.wuba.bangbang.uicomponents.pulltorefresh.internal;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.view.View;

import com.example.android_select_picture_module.R;
import com.wuba.bangbang.uicomponents.pulltorefresh.PullToRefreshBase.Mode;

public class IMLoadingLayout extends LoadingLayout {

    public IMLoadingLayout(Context context, Mode mode, TypedArray attrs) {
        super(context, mode, attrs);
    }

    @Override
    protected int getDefaultDrawableResId() {
        return R.drawable.pull_to_refresh_icon;
    }

    @Override
    protected void onLoadingDrawableSet(Drawable imageDrawable) {

    }

    @Override
    protected void onPullImpl(float scaleOfLayout) {
    	
    	System.out.println("-------->onPullImpl");
	    if(mProgressBar.getVisibility() == View.VISIBLE){
			
			mProgressBar.setVisibility(View.GONE);
		}
		if(mInnerLayout.getVisibility() != View.VISIBLE){
			
			mInnerLayout.setVisibility(View.VISIBLE);
		}
    }

    @Override
    protected void pullToRefreshImpl() {
    	System.out.println("-------->pullToRefreshImpl");
    }

    @Override
    protected void refreshingImpl() {
    	System.out.println("-------->refreshingImpl");
    }

    @Override
    protected void releaseToRefreshImpl() {

    	System.out.println("-------->releaseToRefreshImpl");
    }

    @Override
    protected void resetImpl() {
    	
    	System.out.println("--------->resetImpl");
    	mHeaderText.setText(mFinishLabel);
    }
}