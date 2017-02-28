package com.wuba.bangbang.uicomponents.imselectpicture;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.media.ThumbnailUtils;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.example.android_select_picture_module.R;
import com.example.uicomponents.IMImageView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.wuba.bangbang.uicomponents.acitonsheet.ActionSheet;
import com.wuba.bangbang.uicomponents.acitonsheet.IActionSheetListener;
import com.wuba.bangbang.uicomponents.utils.ImageLoaderUtils;
import com.wuba.bangbang.uicomponents.viewpagerindicator.CirclePageIndicator;

/**
 * 发布页已选择图片显示的View
 * 界面初始化完成后必须调用Step:
 * 1、setFragmentManager
 * 2、setMaxPicture
 * 3、setListener
 */
public class IMSelectPictureView extends RelativeLayout implements IActionSheetListener {
	
	/**布局的高度*/
    private int mViewHeight;
    /**加载的每张图片的宽高尺寸*/
    private int pictureItemSize;
    /**加载图片的内边距*/
    private int padding;
    /**加载的每张图片的内容宽高*/
    private int pictureSize;
    /**最大选择的图片数*/
    private int maxPictureCount = -1;
    private FragmentManager mFragmentManager;
    /**选择图片的监听*/
    private ISelectPictureListener mListener;
    /**加号的拍照按钮*/
    private ImageView tackPictureButton;
    /**显示已选择的照片的ViewPager*/
    private ViewPager mViewPager;
    /**选择图片的ViewPagerAdapter*/
    private SelectPictureViewPagerAdapter pagerAdapter;
    /**ViewPager中显示的数据源*/
    private List<View> viewPagerData;
    /**已选照片的数据*/
    private ArrayList<String> pictureData;
    /**ViewPager下面的角标提示*/
    private CirclePageIndicator mIndicator;
    /**单击+的提示文字*/
    private String[] mActionSheetTitle = {"拍照", "从相册选择"};
    /**初始化时一张照片都没有的情况下的添加照片的ImageView*/
    private ImageView mTakePicture;
    public static final String DEFAULT_TAG = "DEFAULT";
    /**图片的url*/
    private String url ;
    
    public IMSelectPictureView(Context context) {
        this(context, null);
    }

    public IMSelectPictureView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public IMSelectPictureView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs);
        initData();
        initView();
    }

    /**
     * 设置FragmentManager
     * @param fragmentManager FragmentManager
     */
    public void setFragmentManager(FragmentManager fragmentManager) {
        mFragmentManager = fragmentManager;
    }

    /**
     * 设置显示图片的最大数
     * @param count 图片最大数
     */
    public void setMaxPicture(int count) {
        maxPictureCount = count;
    }

    /**
     * 设置监听(包括拍照、选择本地图片，单击已经选择的图片)
     * @param listener 监听
     */
    public void setListener(ISelectPictureListener listener) {
        mListener = listener;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    	
//        this.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, mViewHeight));   //TODO
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
    	super.onDraw(canvas);
    	mTakePicture.setScaleType(ScaleType.FIT_XY);
    }

    private void initData() {
        if (!(getContext() instanceof Activity)) {
            throw new Error("Context必须是Activity");
        }

        DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
        padding = dip2px(8);
        int contentWidth = displayMetrics.widthPixels - padding * 2;
        pictureItemSize = contentWidth / 4;
        mViewHeight = pictureItemSize + padding * 2;
        //设置最小高度
        int minHeight = dip2px(108);
        if(mViewHeight < minHeight){
        	mViewHeight = minHeight;
        }
        pictureSize = pictureItemSize - padding * 2;

        viewPagerData = new ArrayList<View>();
        pictureData = new ArrayList<String>();
    }

    /**
     * 初始化UI
     */
    private void initView() {

        View view = View.inflate(getContext(), R.layout.im_select_picture_view_layout, this);
        tackPictureButton = (ImageView) view.findViewById(R.id.takepicture);
        tackPictureButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ActionSheet
                        .createBuilder(getContext(), mFragmentManager)
                        .setCancelButtonTitle("取消")
                        .setOtherButtonTitles(mActionSheetTitle)
                        .setCancelableOnTouchOutside(true)
                        .setListener(IMSelectPictureView.this).show();
            }
        });

        mViewPager = (ViewPager) view.findViewById(R.id.view_pager);
        pagerAdapter = new SelectPictureViewPagerAdapter(viewPagerData);
        mViewPager.setAdapter(pagerAdapter);

        mIndicator = (CirclePageIndicator) findViewById(R.id.indicator);
        mIndicator.setViewPager(mViewPager);
        mTakePicture = (ImageView)findViewById(R.id.takepicture);
    }

    /**
     * 删除“+”按钮的显示（当已选择的照片的个数达到设置的最大值时）
     */
    private void cleanPictureDataDefault() {

    	if(pictureData.size() >= maxPictureCount){

    		int defaultIndex = pictureData.indexOf(DEFAULT_TAG);
	        while (defaultIndex >= 0) {
	            pictureData.remove(defaultIndex);
	            defaultIndex = pictureData.indexOf(DEFAULT_TAG);
	        }
    	}
    }

    /**
     * 获取已选图片数据
     * @return 图片数据
     */
    public ArrayList<String> getPictureData() {
        cleanPictureDataDefault();
		if(pictureData.contains(DEFAULT_TAG)){

			pictureData.remove(DEFAULT_TAG);
		}
        return pictureData;
    }

    /**
     * 更改图片数据
     * @param pictureUrls url数组
     * @param isReplace   是否覆盖
     */
    public void addPictureData(ArrayList<String> pictureUrls, boolean isReplace) {
        
        if (pictureUrls == null || viewPagerData == null || maxPictureCount < 0) {
            return;
        }

        if (isReplace) {
            pictureData = pictureUrls;
        } else {
            for (int i = pictureData.size() - 1; i >= 0; i--) {   //去掉重复图片
                String oldUrl = pictureData.get(i);
                for (String url : pictureUrls) {
                    if (url.equals(oldUrl)) {
                        pictureData.remove(i);
                        break;
                    }
                }
            }
            pictureData.addAll(pictureUrls);
        }

        cleanPictureDataDefault();

        if (pictureData == null
        		|| (pictureData != null && pictureData.size() == 0)
        		|| pictureData != null && pictureData.size() < maxPictureCount && (pictureUrls.size() > 0 && !TextUtils.equals(pictureData.get(0), DEFAULT_TAG))) {   //不到最大值时添加“+”按钮
            pictureData.add(0, DEFAULT_TAG);
        }

        int viewPagerDataFlag = 0;
        boolean isNewLayout = false;
        LinearLayout pictureGroup = null;   //一张已选择的图片的全部View
        for (int i = 0; i < pictureData.size(); i++) {
            if (pictureGroup == null) {
                if (viewPagerData.size() > 0 && viewPagerDataFlag <= viewPagerData.size() - 1) {
                    pictureGroup = (LinearLayout) viewPagerData.get(viewPagerDataFlag);
                    pictureGroup.removeAllViews();
                    isNewLayout = false;
                } else {
                    pictureGroup = new LinearLayout(getContext());
                    pictureGroup.setPadding(0, 2 * padding, 0, padding);
                    LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    pictureGroup.setLayoutParams(params);
                    isNewLayout = true;
                }
                viewPagerDataFlag++;
            }

            url = pictureData.get(i);

            View view = LayoutInflater.from(getContext()).inflate(R.layout.item_select_picture_view, pictureGroup, false);
            final IMImageView selectPicture = (IMImageView) view.findViewById(R.id.select_picture);
            selectPicture.setUrl(url);
            //start by zhaobo for set the select picture size
            ViewGroup.LayoutParams lp = selectPicture.getLayoutParams();
            lp.width = pictureSize;
            lp.height = pictureSize;
            selectPicture.setLayoutParams(lp);
            //end by zhaobo
            final IMImageView SELECTPICTUREVIEW = selectPicture;
            selectPicture.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {

                    mListener.onClickPicture(getPictureData(), SELECTPICTUREVIEW);
                }
            });
            ImageView delete = (ImageView)view.findViewById(R.id.delete);
            LayoutParams params = new LayoutParams(pictureItemSize, pictureItemSize);
            selectPicture.setUrl(url);
        	Log.e("jack", "--------------->" + url);
            params.setMargins(4, 0, 4, 0);
            //图片加载，需要扩展支持网络图片
            if (DEFAULT_TAG.equals(url)) {   //有照片时的最后一项“+”的按钮
            	selectPicture.setImageResource(R.drawable.select_new_picture);
            	if(delete.getVisibility() == View.VISIBLE){
            		
            		delete.setVisibility(View.GONE);
            	}
            	selectPicture.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ActionSheet
                                .createBuilder(getContext(), mFragmentManager)
                                .setCancelButtonTitle("取消")
                                .setOtherButtonTitles(mActionSheetTitle)
                                .setCancelableOnTouchOutside(true)
                                .setListener(IMSelectPictureView.this).show();
                    }
                });
            } else {   
            	
            	if(url.contains("http://")){

            		ImageLoader.getInstance().displayImage(url, selectPicture, ImageLoaderUtils.getDefaultOptions(), new ImageLoadingListener() {

						@Override
						public void onLoadingStarted(String arg0, View arg1) {
						}

                        @Override
						public void onLoadingFailed(String arg0, View arg1, FailReason arg2) {
						}

                        @Override
						public void onLoadingComplete(String url, View view, Bitmap bitmap) {
                            setBitmapToImageView((ImageView)view, bitmap);
						}

                        @Override
						public void onLoadingCancelled(String arg0, View arg1) {
						}
					});
            	}else{

            	    String[] temp = url.split("/");
            	    if(temp != null && temp.length > 0 && !temp[temp.length - 1].contains(".")){
            	        
            	       url =  "content://media/" + url;
            	    }else{
            	        
            	        url = "file://" + url;
            	    }
        	        ImageLoader.getInstance().displayImage(url, selectPicture, ImageLoaderUtils.getDefaultOptions(),
                            new ImageLoadingListener() {

                                @Override
                                public void onLoadingStarted(String arg0, View arg1) {
                                }

                                @Override
                                public void onLoadingFailed(String arg0, View arg1, FailReason arg2) {
                                }

                                @Override
                                public void onLoadingComplete(String arg0, View view, Bitmap bitmap) {
                                    setBitmapToImageView((ImageView)view, bitmap);
                                }

                                @Override
                                public void onLoadingCancelled(String arg0, View arg1) {
                                }
                            });
                }
                if(delete.getVisibility() != View.VISIBLE){
            		
            		delete.setVisibility(View.VISIBLE);
            	}
                delete.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String url = selectPicture.getUrl(); 
                        pictureData.remove(url);
                        addPictureData(pictureData, true);
                    }
                });
            }
            pictureGroup.addView(view, params);
            if (pictureGroup.getChildCount() >= 4 || i == pictureData.size() - 1) {
                if (isNewLayout) {
                    viewPagerData.add(pictureGroup);
                }
                pictureGroup = null;
            }
        }

        if (viewPagerDataFlag < viewPagerData.size()) {
            for (int i = viewPagerData.size() - 1; i >= viewPagerDataFlag; i--) {
                viewPagerData.remove(i);
            }
        }

        pagerAdapter.notifyDataSetChanged(viewPagerData); 

        if (viewPagerData.size() > 0) {  //有照片时就隐藏初始时加载的button
            tackPictureButton.setVisibility(GONE);
        }
    }

    @Override
    public void onDismiss(Fragment actionSheet, boolean isCancel) {

    }

    @Override
    public void onOtherButtonClick(Fragment actionSheet, int index) {
        String title = mActionSheetTitle[index];
        if (title.equals("拍照")) {
            mListener.onClickTackPicture();
        } else {
            mListener.onSelectPicture();
        }
    }

    /**
     * 选择图片的监听
     */
    public interface ISelectPictureListener {
    	
    	/**拍照选择*/
        void onClickTackPicture();
        /**从相册选择*/
        void onSelectPicture();
        /**单击选择的照片查看详情*/
        void onClickPicture(List<String> pictures, IMImageView imageView);
    }
    
    /**
     * 选择图片的ViewPagerAdapter
     */
    private class SelectPictureViewPagerAdapter extends PagerAdapter {
    	
        private List<View> mData;

        private SelectPictureViewPagerAdapter(List<View> data) {
        	
            super();
            mData = data;
        }

        @Override
        public int getCount() {
            return mData.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object o) {
            return view == o;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
        	
        	  ((ViewPager) container).addView(mData.get(position));
        	  return mData.get(position);
        }
        
        @Override
        public void destroyItem(View arg0, int arg1, Object arg2) {
        
        	 ((ViewPager) arg0).removeView((View) arg2);
        }
        
        @Override
        public int getItemPosition(Object object) {   //需要重写才能刷新ViewPager

        	return POSITION_NONE;
        }
        
        public void notifyDataSetChanged(List<View> dataList){
        	
        	this.mData = dataList;
        	if(this.mData.size() < 2){  //ViewPager的数据小于2页，隐藏indicator
        		
        		mIndicator.setVisibility(View.GONE);
        	}else{
        		
        		mIndicator.setVisibility(View.VISIBLE);
        	}
        	notifyDataSetChanged();
        }
    }

	private int dip2px(float dpValue) {
        final float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public int getMaxPictureCount() {
        return maxPictureCount;
	}

	public void setMaxPictureCount(int maxPictureCount) {
		this.maxPictureCount = maxPictureCount;
	}

    /**
     * 将imageView显示指定bitmap，并设置宽高限制
     * @param imageView
     * @param bitmap
     * @author 牟沛文
     */
    private void setBitmapToImageView(ImageView imageView, Bitmap bitmap) {
        if (imageView == null)  return;
        if (bitmap != null && !bitmap.isRecycled()) {
            // 注意bitmap应该由ImageLoader确定回收时机，不要在此回收。//截取图片中间的一部分显示
            Bitmap smallBitmap = ThumbnailUtils.extractThumbnail(bitmap, pictureSize, pictureSize, 0);
            if (smallBitmap != null && !smallBitmap.isRecycled()) {
                imageView.setImageBitmap(smallBitmap);
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) imageView.getLayoutParams();
                params.width = pictureSize;
                params.height = pictureSize;
                imageView.setLayoutParams(params);
            }
        }
    }

    /**
     * @return the url
     */
    public String getUrl() {
        return url;
    }
    
    
    /**
     * 获得图片的位置
     * @param url
     * @param pictures
     * @return
     */
    public int getPosition(String url, List<String> pictures){
        
        int result = 0;
        
        if(url.startsWith("http://")){
            
            for(int i = 0; i < pictures.size(); i++){
                
                if(TextUtils.equals(url, pictures.get(i))){
                    
                    result = i;
                }
            }
        }else{
            
            String[] arr = url.split("/");
            String source = arr[arr.length - 1];   //源名称
            for(int i = 0; i < pictures.size(); i++){
                
                String picture = pictures.get(i);
                String[] temp = picture.split("/");
                if(TextUtils.equals(source, temp[temp.length - 1])){
                    
                    result = i;
                }
            }
        }
        return result;
    }
}