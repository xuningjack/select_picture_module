/**
 * @project: 58bangbang
 * @file: IMLocalImagePager.java
 * @date: 2014年10月23日 上午11:23:54
 * @copyright: 2014  58.com Inc.  All rights reserved. 
 */
package com.example.uicomponents;

import java.util.List;

import android.content.Context;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.android_select_picture_module.R;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.wuba.bangbang.uicomponents.photoview.PhotoViewAttacher;
import com.wuba.bangbang.uicomponents.utils.ImageLoaderUtils;
import com.wuba.bangbang.uicomponents.viewpagerindicator.CirclePageIndicator;
import com.wuba.bangbang.uicomponents.viewpagerindicator.HackyViewPager;
import com.wuba.bangbang.uicomponents.viewpagerindicator.PageIndicator;

/**
 * 本地图片浏览器。依赖开源项目PhotoView、ViewPagerIndicator。<br/>
 * @author Jack <br/>
 */
public class  IMLocalImagePager extends FrameLayout {
	
    /** 支持滑块的ViewPager */
    private HackyViewPager mHackyViewPager;
    /** ViewPager适配器 */
    private ImagePagerAdapter mPageAdapter;
    private LayoutInflater mInflater;
    /** 显示图片的数据源 */
    private List<String> images;
    /** 点击事件的回调函数 */
    private OnClickListener mOnClickListener;

    /**
     * Creates a new instance of IMImagePager.
     * @param context
     */
    public IMLocalImagePager(Context context) {
        this(context, null);
    }

    /**
     * Creates a new instance of IMImagePager.
     * @param context
     * @param attrs
     * @param defStyleAttr
     */
    public IMLocalImagePager(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * Creates a new instance of IMImagePager.
     * @param context
     * @param attrs
     */
    public IMLocalImagePager(Context context, AttributeSet attrs) {
    	
    	super(context, attrs);
        mInflater = LayoutInflater.from(context);
        mInflater.inflate(R.layout.common_im_image_pager, this);

        //ViewPager
        mHackyViewPager = (HackyViewPager) findViewById(R.id.common_im_image_pager_viewpager);
        mPageAdapter = new ImagePagerAdapter(context);
        mHackyViewPager.setAdapter(mPageAdapter);
        mHackyViewPager.setCurrentItem(0);
        //导航滑块设置
        PageIndicator indicator = (CirclePageIndicator) findViewById(R.id.common_im_image_pager_indicator);
        indicator.setViewPager(mHackyViewPager);
    }

    /**
     * 添加图片点击处理<br/>
     * 注意：这里会造成循环引用，需要在上级destory时删除
     */
    @Override
    public void setOnClickListener(OnClickListener l) {

    	mOnClickListener = l;
    }
    
    /**
     * 设置图片数据源，会触发重新渲染
     * @param images
     */
    public void setImages(List<String> images) {
    	
    	this.images = images;
        if (mPageAdapter != null) {
        	
            mPageAdapter.update(images);
        }
    }
    
    /**
     * 设置当前滚动到的位置
     * @param position
     * @author lijc setImagePosition:(这里用一句话描述这个方法的作用). <br/>
     */
    public void setImagePosition(int position) {
    	
        if (mHackyViewPager != null && images != null && images.size() > position) {
        	
        	mHackyViewPager.setCurrentItem(position);
        }
    }

    /**
     * ViewPager适配器
     */
    private class ImagePagerAdapter extends PagerAdapter {
        
        /** 数据源 */
        private List<String> mImages;
        private Context mContext;

        ImagePagerAdapter(Context context) {
            this.mContext = context;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            ((ViewPager) container).removeView((View) object);
        }

        @Override
        public void finishUpdate(View container) {
        }

        @Override
        public int getCount() {
            if (mImages != null) {
                return mImages.size();
            } else {
                return 0;
            }
        }

        @Override
        public Object instantiateItem(ViewGroup view, int position) {
            View imageLayout = mInflater.inflate(R.layout.common_im_image_pager_item, view, false);

            IMPhotoView imageView = (IMPhotoView) imageLayout.findViewById(R.id.common_im_image_pager_item_photoview);
            //点击事件的响应
            if (mOnClickListener != null) {
                imageView.setOnViewTapListener(new PhotoViewAttacher.OnViewTapListener() {

                    @Override
                    public void onViewTap(View view, float x, float y) {
                        if (mOnClickListener != null) {
                            mOnClickListener.onClick(view);
                        }
                    }
                });
            }

            final ProgressBar spinner = (ProgressBar) imageLayout.findViewById(R.id.common_im_image_pager_item_loading);
            spinner.setVisibility(View.GONE);
            try {
                
                String url = mImages.get(position);
                String[] temp = url.split("/");
                
                if(temp != null && temp.length > 0 && !temp[temp.length - 1].contains(".")){  //加载手机拍摄的图片
                    
                    url =  "content://media/" + url;
                 }else{
                     
                     if(mImages.get(position).startsWith("http://")){   //加载网络图片，防止过大造成OOM

                         url = mImages.get(position);
                         url = url.replace("//tiny//", "//big//");
                     }else{   //加载本地相册的图片
                         
                         url = "file://" + url;
                     }
                 }
                 ImageLoader.getInstance().displayImage(url, imageView, ImageLoaderUtils.getDefaultOptions());
            } catch (Exception e) {
                e.printStackTrace();
                String message = "图片加载失败";
                Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
            }
            ((ViewPager) view).addView(imageLayout, 0);
            return imageLayout;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view.equals(object);
        }

        @Override
        public void restoreState(Parcelable state, ClassLoader loader) {
        }

        @Override
        public Parcelable saveState() {
            return null;
        }

        @Override
        public void startUpdate(View container) {
        }

        /**
         * 更新列表
         * @param images 数据源
         */
        public void update(List<String> images) {
            this.mImages = images;
            this.notifyDataSetChanged();
        }
    }

	public List<String> getImages() {
		return images;
	}
}