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
package com.wuba.bangbang.uicomponents.pulltorefresh.internal;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.android_select_picture_module.R;
import com.wuba.bangbang.uicomponents.pulltorefresh.ILoadingLayout;
import com.wuba.bangbang.uicomponents.pulltorefresh.PullToRefreshBase.Mode;

@SuppressLint("ViewConstructor")
public abstract class LoadingLayout extends FrameLayout implements
        ILoadingLayout {

    static final String LOG_TAG = "PullToRefresh-LoadingLayout";

    static final Interpolator ANIMATION_INTERPOLATOR = new LinearInterpolator();

    protected RelativeLayout mInnerLayout;

    protected final ImageView mHeaderImage;

    private boolean mUseIntrinsicAnimation;

    protected final TextView mHeaderText;

    protected final Mode mMode;
    /**上拉加载更多*/
    private CharSequence mPullLabel;
    /**正在刷新*/
    private CharSequence mRefreshingLabel;
    /**松开即可加载*/
    private CharSequence mReleaseLabel;
    /**刷新结束时显示的文字提示，如“加载成功”*/
    protected CharSequence mFinishLabel;
    /**头部显示的ProgressBar*/
    protected ProgressBar mProgressBar;
    protected LinearLayout mHeader;
    

    public LoadingLayout(Context context, final Mode mode, TypedArray attrs) {
        super(context);
        mMode = mode;

        LayoutInflater.from(context).inflate(R.layout.pull_to_refresh_header_vertical, this);
        mInnerLayout = (RelativeLayout) findViewById(R.id.fl_inner);
        mHeaderText = (TextView) mInnerLayout.findViewById(R.id.pull_to_refresh_text);
        mHeaderImage = (ImageView) mInnerLayout.findViewById(R.id.pull_to_refresh_image);
        mProgressBar = (ProgressBar)findViewById(R.id.progressbar);
        mHeader = (LinearLayout)findViewById(R.id.header);
        readLabel();

        if (attrs.hasValue(R.styleable.pull_to_refresh_ptr_header_background)) {
            Drawable background = attrs.getDrawable(R.styleable.pull_to_refresh_ptr_header_background);
            if (null != background) {
                ViewCompat.setBackground(this, background);
            }
        }

        if (attrs.hasValue(R.styleable.pull_to_refresh_ptr_header_text_appearance)) {
            TypedValue styleID = new TypedValue();
            attrs.getValue(R.styleable.pull_to_refresh_ptr_header_text_appearance, styleID);
            setTextAppearance(styleID.data);
        }
        if (attrs.hasValue(R.styleable.pull_to_refresh_ptr_sub_header_text_appearance)) {
            TypedValue styleID = new TypedValue();
            attrs.getValue(
                    R.styleable.pull_to_refresh_ptr_sub_header_text_appearance,
                    styleID);
        }

        // Text Color attrs need to be set after TextAppearance attrs
        if (attrs.hasValue(R.styleable.pull_to_refresh_ptr_header_text_color)) {
            ColorStateList colors = attrs
                    .getColorStateList(R.styleable.pull_to_refresh_ptr_header_text_color);
            if (null != colors) {
                setTextColor(colors);
            }
        }

        // Try and get defined drawable from Attrs
        Drawable imageDrawable = null;
        if (attrs.hasValue(R.styleable.pull_to_refresh_ptr_drawable)) {
            imageDrawable = attrs
                    .getDrawable(R.styleable.pull_to_refresh_ptr_drawable);
        }

        // Check Specific Drawable from Attrs, these overrite the generic
        // drawable attr above
        switch (mode) {
        case PULL_FROM_START:
        default:
            if (attrs.hasValue(R.styleable.pull_to_refresh_ptr_drawable_start)) {
                imageDrawable = attrs
                        .getDrawable(R.styleable.pull_to_refresh_ptr_drawable_start);
            } else if (attrs
                    .hasValue(R.styleable.pull_to_refresh_ptr_drawable_top)) {
                Utils.warnDeprecation("ptrDrawableTop", "ptrDrawableStart");
                imageDrawable = attrs
                        .getDrawable(R.styleable.pull_to_refresh_ptr_drawable_top);
            }
            break;

        case PULL_FROM_END:
            if (attrs.hasValue(R.styleable.pull_to_refresh_ptr_drawable_end)) {
                imageDrawable = attrs
                        .getDrawable(R.styleable.pull_to_refresh_ptr_drawable_end);
            } else if (attrs
                    .hasValue(R.styleable.pull_to_refresh_ptr_drawable_bottom)) {
                Utils.warnDeprecation("ptrDrawableBottom", "ptrDrawableEnd");
                imageDrawable = attrs
                        .getDrawable(R.styleable.pull_to_refresh_ptr_drawable_bottom);
            }
            break;
        }

        // If we don't have a user defined drawable, load the default
        if (null == imageDrawable) {
            imageDrawable = context.getResources().getDrawable(
                    getDefaultDrawableResId());
        }

        // Set Drawable, and save width/height
        setLoadingDrawable(imageDrawable);

        reset();
    }

    /**
     * 获取Lable
     */
    private void readLabel() {
        if (mInnerLayout == null || mMode == null) {
            return;
        }

        Context context = getContext();
        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) mInnerLayout.getLayoutParams();
        mFinishLabel = context.getString(R.string.pull_up_refresh_complete_label);
        switch (mMode) {
        case PULL_FROM_END:
            lp.gravity = Gravity.TOP;

            // Load in labels
            mPullLabel = context
                    .getString(R.string.pull_to_refresh_from_bottom_pull_label);
            mRefreshingLabel = context
                    .getString(R.string.pull_to_refresh_from_bottom_refreshing_label);
            mReleaseLabel = context
                    .getString(R.string.pull_to_refresh_from_bottom_release_label);
            break;

        case PULL_FROM_START:
        default:
            lp.gravity = Gravity.BOTTOM;

            // Load in labels
            mPullLabel = context.getString(R.string.pull_to_refresh_pull_label);
            mRefreshingLabel = context
                    .getString(R.string.pull_to_refresh_refreshing_label);
            mReleaseLabel = context
                    .getString(R.string.pull_to_refresh_release_label);
            break;
        }
    }

    public final void setHeight(int height) {
        ViewGroup.LayoutParams lp = (ViewGroup.LayoutParams) getLayoutParams();
        lp.height = height;
        requestLayout();
    }

    public final void setWidth(int width) {
        ViewGroup.LayoutParams lp = (ViewGroup.LayoutParams) getLayoutParams();
        lp.width = width;
        requestLayout();
    }

    public final int getContentSize() {
        return mInnerLayout.getHeight();
    }

    public final void hideAllViews() {
        if (View.VISIBLE == mHeaderText.getVisibility()) {
            mHeaderText.setVisibility(View.INVISIBLE);
        }
        if (View.VISIBLE == mHeaderImage.getVisibility()) {
            mHeaderImage.setVisibility(View.INVISIBLE);
        }
    }

    public final void onPull(float scaleOfLayout) {
        if (!mUseIntrinsicAnimation) {
            onPullImpl(scaleOfLayout);
        }
    }

    public final void pullToRefresh() {
    	//显示提示文字，隐藏progressbar
    	if(mHeader.getVisibility() != View.VISIBLE){
      	   
      	   mHeader.setVisibility(View.VISIBLE);
        }
    	if(mProgressBar.getVisibility() == View.VISIBLE){
    		
    		mProgressBar.setVisibility(View.GONE);
    	}
        if (null != mHeaderText) {
            mHeaderText.setText(mPullLabel);
        }

        // Now call the callback
        pullToRefreshImpl();
    }

    public final void refreshing() {
    
       //隐藏提示文字
       if(mHeader.getVisibility() == View.VISIBLE){
    	   
    	   mHeader.setVisibility(View.GONE);
       }
	   if (null != mHeaderText) {
           mHeaderText.setText(mRefreshingLabel);
       }

        if (mUseIntrinsicAnimation) {
            ((AnimationDrawable) mHeaderImage.getDrawable()).start();
        } else {
            // Now call the callback
            refreshingImpl();
        }
    }

    public final void releaseToRefresh() {
        if (null != mHeaderText) {
            mHeaderText.setText(mReleaseLabel);
        }

        // Now call the callback
        releaseToRefreshImpl();
    }

    public final void reset() {
        if (null != mHeaderText) {
            mHeaderText.setText(mPullLabel);
        }
        mHeaderImage.setVisibility(View.VISIBLE);

        if (mUseIntrinsicAnimation) {
            ((AnimationDrawable) mHeaderImage.getDrawable()).stop();
        } else {
            // Now call the callback
            resetImpl();
        }
    }

    public final void setLoadingDrawable(Drawable imageDrawable) {
        // Set Drawable
        mHeaderImage.setImageDrawable(imageDrawable);
        mUseIntrinsicAnimation = (imageDrawable instanceof AnimationDrawable);

        // Now call the callback
        onLoadingDrawableSet(imageDrawable);
    }

    public void setPullLabel(CharSequence pullLabel) {
        mPullLabel = pullLabel;
    }

    public void setRefreshingLabel(CharSequence refreshingLabel) {
        mRefreshingLabel = refreshingLabel;
        // 更新视图显示
        // 下面这段逻辑会影响组件显示结果，如果下拉刷新的需求改变可以关注一下这部分逻辑还需不需要
        if (null != mHeaderText) {
            mHeaderText.setText(mRefreshingLabel);
        }
        readLabel();
    }

    public void setReleaseLabel(CharSequence releaseLabel) {
        mReleaseLabel = releaseLabel;
    }

    @Override
    public void setTextTypeface(Typeface tf) {
        mHeaderText.setTypeface(tf);
    }

    public final void showInvisibleViews() {
        if (View.INVISIBLE == mHeaderText.getVisibility()) {
            mHeaderText.setVisibility(View.VISIBLE);
        }
        if (View.INVISIBLE == mHeaderImage.getVisibility()) {
            mHeaderImage.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Callbacks for derivative Layouts
     */

    protected abstract int getDefaultDrawableResId();

    protected abstract void onLoadingDrawableSet(Drawable imageDrawable);
    /**下拉回调接口*/
    protected abstract void onPullImpl(float scaleOfLayout);

    protected abstract void pullToRefreshImpl();

    protected abstract void refreshingImpl();

    protected abstract void releaseToRefreshImpl();

    protected abstract void resetImpl();

    private void setTextAppearance(int value) {
        if (null != mHeaderText) {
            mHeaderText.setTextAppearance(getContext(), value);
        }
    }

    private void setTextColor(ColorStateList color) {
        if (null != mHeaderText) {
            mHeaderText.setTextColor(color);
        }
    }

	public CharSequence getmFinishLabel() {
		return mFinishLabel;
	}
}