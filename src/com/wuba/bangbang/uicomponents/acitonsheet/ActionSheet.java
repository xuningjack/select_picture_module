/**
 * @project: 58bangbang
 * @file: ActionSheet.java
 * @date: 2014-8-5 下午3:42:49
 * @copyright: 2014  58.com Inc.  All rights reserved. 
 */
package com.wuba.bangbang.uicomponents.acitonsheet;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import com.example.android_select_picture_module.R;

/**
 * 提示选择操作的弹出层
 * @author 徐宁
 * @date: 2015-2-27 下午2:22:30
 */
public class ActionSheet extends Fragment implements OnClickListener {

    private static final String ARG_CANCEL_BUTTON_TITLE = "cancel_button_title";
    private static final String ARG_OTHER_BUTTON_TITLES = "other_button_titles";
    private static final String ARG_CANCELABLE_ONTOUCHOUTSIDE = "cancelable_ontouchoutside";
    private static final int CANCEL_BUTTON_ID = 100;
    private static final int BG_VIEW_ID = 10;
    private static final int TRANSLATE_DURATION = 200;
    private static final int ALPHA_DURATION = 300;
    private boolean mDismissed = true;
    private IActionSheetListener mListener;
    private View mView;
    private LinearLayout mPanel;
    private ViewGroup mGroup;
    private View mBg;
    private Attributes mAttrs;
    private boolean mIsCancel = true;

    /**
     * 显示ActionSheet
     * @author 徐宁
     * @param manager
     * @param tag
     */
    public void show(FragmentManager manager, String tag) {
        if (!mDismissed) {
            return;
        }
        mDismissed = false;
        FragmentTransaction ft = manager.beginTransaction();
        ft.add(this, tag);
        ft.addToBackStack(null);
        ft.commit();
    }

    /**
     * 隐藏ActionSheet
     * @author 徐宁
     */
    public void dismiss() {
        if (mDismissed) {
            return;
        }
        mDismissed = true;
        getFragmentManager().popBackStack();
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.remove(this);
        ft.commit();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        InputMethodManager imm = (InputMethodManager) getActivity()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isActive()) {
            View focusView = getActivity().getCurrentFocus();
            if (focusView != null) {
                imm.hideSoftInputFromWindow(focusView.getWindowToken(), 0);
            }
        }

        mAttrs = readAttribute();

        mView = createView();
        mGroup = (ViewGroup) getActivity().getWindow().getDecorView();

        createItems();

        mGroup.addView(mView);
        mBg.startAnimation(createAlphaInAnimation());
        mPanel.startAnimation(createTranslationInAnimation());

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    private Animation createTranslationInAnimation() {
        int type = TranslateAnimation.RELATIVE_TO_SELF;
        TranslateAnimation an = new TranslateAnimation(type, 0, type, 0, type,
                1, type, 0);
        an.setDuration(TRANSLATE_DURATION);
        return an;
    }

    private Animation createAlphaInAnimation() {
        AlphaAnimation an = new AlphaAnimation(0, 1);
        an.setDuration(ALPHA_DURATION);
        return an;
    }

    private Animation createTranslationOutAnimation() {
        int type = TranslateAnimation.RELATIVE_TO_SELF;
        TranslateAnimation an = new TranslateAnimation(type, 0, type, 0, type,
                0, type, 1);
        an.setDuration(TRANSLATE_DURATION);
        an.setFillAfter(true);
        return an;
    }

    private Animation createAlphaOutAnimation() {
        AlphaAnimation an = new AlphaAnimation(1, 0);
        an.setDuration(ALPHA_DURATION);
        an.setFillAfter(true);
        return an;
    }

    /**
     * 构建视图View
     * @author 徐宁
     * @return
     */
    private View createView() {
        
        FrameLayout parent = new FrameLayout(getActivity());
        parent.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT));
        mBg = new View(getActivity());
        mBg.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT));
        mBg.setBackgroundColor(Color.argb(80, 0, 0, 0));
        mBg.setId(BG_VIEW_ID);
        mBg.setOnClickListener(this);

        //设置ActionSheet的位置
        mPanel = new LinearLayout(getActivity());
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.BOTTOM;
        mPanel.setLayoutParams(params);
        mPanel.setOrientation(LinearLayout.VERTICAL);

        parent.addView(mBg);
        parent.addView(mPanel);
        return parent;
    }

    private void createItems() {
        
        String[] titles = getOtherButtonTitles();
        if (titles != null) {
            for (int i = 0; i < titles.length; i++) {
                Button bt = new Button(getActivity());
                bt.setId(CANCEL_BUTTON_ID + i + 1);
                bt.setOnClickListener(this);
                bt.setBackgroundDrawable(getOtherButtonBg(titles, i));
                bt.setTypeface(null, Typeface.NORMAL);
                bt.setText(titles[i]);
                bt.setTextColor(mAttrs.mOtherButtonTextColor);
                bt.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                        mAttrs.mActionSheetTextSize);
                if (i > 0) {
                    LinearLayout.LayoutParams params = createButtonLayoutParams();
                    params.topMargin = mAttrs.mOtherButtonSpacing;
                    mPanel.addView(bt, params);
                } else {
                    mPanel.addView(bt);
                }
            }
        }
        Button bt = new Button(getActivity());
        bt.getPaint().setFakeBoldText(true);
        bt.setTextSize(TypedValue.COMPLEX_UNIT_PX, mAttrs.mActionSheetTextSize);
        bt.setId(CANCEL_BUTTON_ID);
        bt.setBackgroundDrawable(mAttrs.mCancelButtonBackground);
        bt.setTypeface(null, Typeface.NORMAL);
        bt.setText(getCancelButtonTitle());
        bt.setTextColor(mAttrs.mCancelButtonTextColor);
        bt.setOnClickListener(this);
        LinearLayout.LayoutParams params = createButtonLayoutParams();
        params.topMargin = mAttrs.mCancelButtonMarginTop;
        mPanel.addView(bt, params);

        mPanel.setBackgroundDrawable(mAttrs.mBackground);
        mPanel.setPadding(mAttrs.mPadding, mAttrs.mPadding, mAttrs.mPadding,
                mAttrs.mPadding);
    }

    /**
     * 创建Button的属性
     * @return LinearLayout.LayoutParams
     */
    public LinearLayout.LayoutParams createButtonLayoutParams() {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        return params;
    }

    private Drawable getOtherButtonBg(String[] titles, int i) {
        if (titles.length == 1) {
            return mAttrs.mOtherButtonSingleBackground;
        }
        if (titles.length == 2) {
            switch (i) {
            case 0:
                return mAttrs.mOtherButtonTopBackground;
            case 1:
                return mAttrs.mOtherButtonBottomBackground;
            default:
                break;
            }
        }
        if (titles.length > 2) {
            if (i == 0) {
                return mAttrs.mOtherButtonTopBackground;
            }
            if (i == (titles.length - 1)) {
                return mAttrs.mOtherButtonBottomBackground;
            }
            return mAttrs.getOtherButtonMiddleBackground();
        }
        return null;
    }

    @Override
    public void onDestroyView() {
        mPanel.startAnimation(createTranslationOutAnimation());
        mBg.startAnimation(createAlphaOutAnimation());
        mView.postDelayed(new Runnable() {
            @Override
            public void run() {
                mGroup.removeView(mView);
            }
        }, ALPHA_DURATION);
        if (mListener != null) {
            mListener.onDismiss(this, mIsCancel);
        }
        super.onDestroyView();
    }

    /**
     * 设置ActionSheet的相关属性
     * @author 徐宁
     * @return
     */
    private Attributes readAttribute() {
        
        Attributes attrs = new Attributes(getActivity());
        TypedArray a = getActivity().getResources().obtainTypedArray(R.style.im_action_sheet);
        Drawable background = a.getDrawable(R.styleable.action_sheet_action_sheet_background);
        if (background != null) {
            attrs.mBackground = background;
        }
        Drawable cancelButtonBackground = a.getDrawable(R.styleable.action_sheet_cancel_button_background);
        if (cancelButtonBackground != null) {
            attrs.mCancelButtonBackground = cancelButtonBackground;
        }
        Drawable otherButtonTopBackground = a.getDrawable(R.styleable.action_sheet_other_button_top_background);
        if (otherButtonTopBackground != null) {
            attrs.mOtherButtonTopBackground = otherButtonTopBackground;
        }
        Drawable otherButtonMiddleBackground = a.getDrawable(R.styleable.action_sheet_other_button_middle_background);
        if (otherButtonMiddleBackground != null) {
            attrs.mOtherButtonMiddleBackground = otherButtonMiddleBackground;
        }
        Drawable otherButtonBottomBackground = a.getDrawable(R.styleable.action_sheet_other_button_bottom_background);
        if (otherButtonBottomBackground != null) {
            attrs.mOtherButtonBottomBackground = otherButtonBottomBackground;
        }
        Drawable otherButtonSingleBackground = a.getDrawable(R.styleable.action_sheet_other_button_single_background);
        if (otherButtonSingleBackground != null) {
            attrs.mOtherButtonSingleBackground = otherButtonSingleBackground;
        }
        attrs.mCancelButtonTextColor = a.getColor(R.styleable.action_sheet_cancel_button_text_color, attrs.mCancelButtonTextColor);
        attrs.mOtherButtonTextColor = a.getColor(R.styleable.action_sheet_other_button_text_color,attrs.mOtherButtonTextColor);
        attrs.mPadding = (int) a.getDimension(R.styleable.action_sheet_action_sheet_padding, attrs.mPadding);
        attrs.mOtherButtonSpacing = (int) a.getDimension(R.styleable.action_sheet_other_button_spacing,attrs.mOtherButtonSpacing);
        attrs.mCancelButtonMarginTop = (int) a.getDimension(R.styleable.action_sheet_cancel_button_margin_top,attrs.mCancelButtonMarginTop);
        attrs.mActionSheetTextSize = a.getDimensionPixelSize(R.styleable.action_sheet_action_sheet_text_size,(int) attrs.mActionSheetTextSize);

        a.recycle();
        return attrs;
    }

    private String getCancelButtonTitle() {
        return getArguments().getString(ARG_CANCEL_BUTTON_TITLE);
    }

    private String[] getOtherButtonTitles() {
        return getArguments().getStringArray(ARG_OTHER_BUTTON_TITLES);
    }

    private boolean getCancelableOnTouchOutside() {
        return getArguments().getBoolean(ARG_CANCELABLE_ONTOUCHOUTSIDE);
    }

    /**
     * 设置监听
     * @param listener 监听处理
     */
    public void setActionSheetListener(IActionSheetListener listener) {
        mListener = listener;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == BG_VIEW_ID && !getCancelableOnTouchOutside()) {
            return;
        }
        dismiss();
        if (v.getId() != CANCEL_BUTTON_ID && v.getId() != BG_VIEW_ID) {
            if (mListener != null) {
                mListener.onOtherButtonClick(this, v.getId() - CANCEL_BUTTON_ID - 1);
            }
            mIsCancel = false;
        }
    }

    /**
     * 创建Builder
     * @param context  context
     * @param fragmentManager  fragmentManager
     * @return Builder
     */
    public static Builder createBuilder(Context context,
            FragmentManager fragmentManager) {
        return new Builder(context, fragmentManager);
    }

    /**
     * 属性处理
     */
    private static class Attributes {
        private Context mContext;

        public Attributes(Context context) {
            mContext = context;
            this.mBackground = new ColorDrawable(Color.TRANSPARENT);
            this.mCancelButtonBackground = new ColorDrawable(Color.BLACK);
            ColorDrawable gray = new ColorDrawable(Color.GRAY);
            this.mOtherButtonTopBackground = gray;
            this.mOtherButtonMiddleBackground = gray;
            this.mOtherButtonBottomBackground = gray;
            this.mOtherButtonSingleBackground = gray;
            this.mCancelButtonTextColor = Color.WHITE;
            this.mOtherButtonTextColor = Color.BLACK;
            this.mPadding = dp2px(20);
            this.mOtherButtonSpacing = dp2px(2);
            this.mCancelButtonMarginTop = dp2px(10);
            this.mActionSheetTextSize = dp2px(16);
        }

        private int dp2px(int dp) {
            return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                    dp, mContext.getResources().getDisplayMetrics());
        }

        public Drawable getOtherButtonMiddleBackground() {
            if (mOtherButtonMiddleBackground instanceof StateListDrawable) {
                TypedArray a = mContext.getResources().obtainTypedArray(R.style.im_action_sheet);
                mOtherButtonMiddleBackground = a.getDrawable(R.styleable.action_sheet_other_button_middle_background);
                a.recycle();
            }
            return mOtherButtonMiddleBackground;
        }

        private Drawable mBackground;
        private Drawable mCancelButtonBackground;
        private Drawable mOtherButtonTopBackground;
        private Drawable mOtherButtonMiddleBackground;
        private Drawable mOtherButtonBottomBackground;
        private Drawable mOtherButtonSingleBackground;
        private int mCancelButtonTextColor;
        private int mOtherButtonTextColor;
        private int mPadding;
        private int mOtherButtonSpacing;
        private int mCancelButtonMarginTop;
        private float mActionSheetTextSize;
    }

    /**
     * ActionSheet创建者
     */
    public static class Builder {

        private Context mContext;
        private FragmentManager mFragmentManager;
        private String mCancelButtonTitle;
        private String[] mOtherButtonTitles;
        private String mTag = "actionSheet";
        private boolean mCancelableOnTouchOutside;
        private IActionSheetListener mListener;

        /**
         * Creates a new instance of Builder.
         * @param context context
         * @param fragmentManager  fragmentManager
         */
        public Builder(Context context, FragmentManager fragmentManager) {
            mContext = context;
            mFragmentManager = fragmentManager;
        }

        /**
         * 设置取消按钮文字
         * @param title 按钮文字
         * @return Builder
         */
        public Builder setCancelButtonTitle(String title) {
            mCancelButtonTitle = title;
            return this;
        }

        /**
         * 设置取消按钮文字
         * @param strId  按钮文字
         * @return Builder
         */
        public Builder setCancelButtonTitle(int strId) {
            return setCancelButtonTitle(mContext.getString(strId));
        }

        /**
         * 设置其他按钮文字
         * @param titles  titles
         * @return Builder
         */
        public Builder setOtherButtonTitles(String... titles) {
            mOtherButtonTitles = titles;
            return this;
        }

        /**
         * 设置tag
         * @param tag tag
         * @return Builder
         */
        public Builder setTag(String tag) {
            mTag = tag;
            return this;
        }

        /**
         * 设置监听
         * @param listener 监听
         * @return Builder
         */
        public Builder setListener(IActionSheetListener listener) {
            this.mListener = listener;
            return this;
        }

        /**
         * 设置是否可以点击取消
         * @param cancelable cancelable
         * @return Builder
         */
        public Builder setCancelableOnTouchOutside(boolean cancelable) {
            mCancelableOnTouchOutside = cancelable;
            return this;
        }

        /**
         * 准备属性
         * @return Bundle
         */
        public Bundle prepareArguments() {
            Bundle bundle = new Bundle();
            bundle.putString(ARG_CANCEL_BUTTON_TITLE, mCancelButtonTitle);
            bundle.putStringArray(ARG_OTHER_BUTTON_TITLES, mOtherButtonTitles);
            bundle.putBoolean(ARG_CANCELABLE_ONTOUCHOUTSIDE,
                    mCancelableOnTouchOutside);
            return bundle;
        }

        /**
         * 显示ActionSheet
         * @return ActionSheet
         */
        public ActionSheet show() {
            
            ActionSheet actionSheet = (ActionSheet) Fragment.instantiate(
                    mContext, ActionSheet.class.getName(), prepareArguments());
            actionSheet.setActionSheetListener(mListener);
            actionSheet.show(mFragmentManager, mTag);
            return actionSheet;
        }
    }
}