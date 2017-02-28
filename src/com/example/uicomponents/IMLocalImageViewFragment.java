package com.example.uicomponents;

import java.util.ArrayList;
import java.util.List;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

/**
 * 在当前界面弹出的图片查看的Fragment
 * @author 徐宁
 * @date: 2015-2-27 上午8:52:26
 */
public class IMLocalImageViewFragment extends Fragment implements View.OnClickListener {
	
    private boolean mDismissed = true;
    private IMLocalImagePager mImageView;
    private List<String> mData = new ArrayList<String>();
    private ViewGroup mGroup;
    private View mView;
    /**初始化的位置*/
    private int mPosition;
    private final String TAG = "IMLocalImageView";

    /**
     * 显示视图
     * @param manager FragmentManager
     */
    public void show(FragmentManager manager) {
        if (!mDismissed) {
            return;
        }

        mDismissed = false;

        FragmentTransaction ft = manager.beginTransaction();
        ft.add(this, TAG);
        ft.addToBackStack(null);
        ft.commit();
    }

    /**
     * 隐藏视图
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

    /**
     * 设置数据
     * @param data 图片数据
     */
    public void setData(List<String> data) {
        mData.clear();
        mData.addAll(data);
        if (mImageView != null) {
            mImageView.setImages(mData);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        hideKeyboard();

        mView = createView();

        mGroup = (ViewGroup) getActivity().getWindow().getDecorView();
        mGroup.addView(mView);
        mGroup.setOnClickListener(this);

        mView.startAnimation(createAlphaInAnimation());
        setPosition(mPosition);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        mView.startAnimation(createAlphaOutAnimation());
        mView.postDelayed(new Runnable() {
            @Override
            public void run() {
                mGroup.removeView(mView);
            }
        }, 100);
        super.onDestroyView();
    }

    private View createView() {
        
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        RelativeLayout panel = new RelativeLayout(getActivity());
        panel.setLayoutParams(params);

        View background = new View(getActivity());
        background.setBackgroundColor(Color.rgb(0, 0, 0));
        background.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT));

        RelativeLayout.LayoutParams ImageViewParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        ImageViewParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        mImageView = new IMLocalImagePager(getActivity());  
        mImageView.setLayoutParams(ImageViewParams);
        if (mData != null) {
            mImageView.setImages(mData);
        }
        mImageView.setOnClickListener(this);

        panel.addView(background);
        panel.addView(mImageView);

        return panel;
    }


    /**
     * 隐藏键盘
     * @author 徐宁
     */
    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isActive()) {
            View focusView = getActivity().getCurrentFocus();
            if (focusView != null) {
                imm.hideSoftInputFromWindow(focusView.getWindowToken(), 0);
            }
        }
    }

    private Animation createAlphaInAnimation() {
        AlphaAnimation an = new AlphaAnimation(0, 1);
        an.setDuration(300);
        return an;
    }

    private Animation createAlphaOutAnimation() {
        AlphaAnimation an = new AlphaAnimation(1, 0);
        an.setDuration(300);
        an.setFillAfter(true);
        return an;
    }

    @Override
    public void onClick(View v) {
        dismiss();
    }
    
    public void setPosition(int position){
    	
    	if(mImageView == null){
    		
    		mImageView = new IMLocalImagePager(getActivity());
    	}
    	mImageView.setImagePosition(position);
    }
    
	public void setmPosition(int mPosition) {
		this.mPosition = mPosition;
	}
}