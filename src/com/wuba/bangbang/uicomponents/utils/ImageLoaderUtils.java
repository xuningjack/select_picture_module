/**
 * @project: 58bangbang
 * @file: ImageLoaderUtils.java
 * @date: 2014-10-22 上午9:50:43
 * @copyright: 2014  58.com Inc.  All rights reserved.
 */
package com.wuba.bangbang.uicomponents.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ListView;

import com.example.android_select_picture_module.R;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LRULimitedMemoryCache;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;

/**
 * IamgeLaoder设置相应属性的工具类
 * @author Jack
 * @version 创建时间：2014-10-22 上午9:50:43
 */
public class ImageLoaderUtils {

	/**
	 * 初始化ImageLoader（需要在应用入口调用 ）
	 */
	public static void init(Context context) {
		if (ImageLoader.getInstance().isInited()) {
			return;
		}
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
				.threadPriority(Thread.NORM_PRIORITY - 2)
				.denyCacheImageMultipleSizesInMemory()
				.diskCacheFileNameGenerator(new Md5FileNameGenerator())
				.memoryCache(new WeakMemoryCache())  //防止内存OOM
				.memoryCacheSize(4 * 1024 * 1024)  
				.diskCacheSize(50 * 1024 * 1024)   // 50 Mb
				.tasksProcessingOrder(QueueProcessingType.LIFO)
				.writeDebugLogs() // Remove for release app
				.build();
		ImageLoader.getInstance().init(config);   // Initialize ImageLoader with configuration.
	}

        /**
         * 在Activity的onStop阶段停止加载图片
         */
        public static void stopLoadPic(){

            ImageLoader.getInstance().stop();
        }


	/**
	 * 清除ImageLoader的缓存
	 */
	public static void clearImageCache() {

		ImageLoader.getInstance().clearDiskCache();
		ImageLoader.getInstance().clearMemoryCache();
	}

	// TODO 根据自己的图片加载业务情况自定义不同的DisplayImageOptions
	
	/**
	 * 获得默认图片的加载options
	 * 
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public static DisplayImageOptions getDefaultOptions() {
		DisplayImageOptions options = new DisplayImageOptions.Builder()
				.cacheInMemory(true).cacheOnDisk(true)
				.resetViewBeforeLoading(true)
				.displayer(new FadeInBitmapDisplayer(300))
				.bitmapConfig(Bitmap.Config.ARGB_8888)
				.imageScaleType(ImageScaleType.EXACTLY)
				.showImageForEmptyUri(R.drawable.default_picture)
				.showImageOnFail(R.drawable.default_picture)
				.showStubImage(R.drawable.default_picture).build();

		return options;
	}


	/**
	 * 在快滑的时候停止listview中的item使用ImageLoader的实例加载图片
	 * @param listView
	 */
	public static void setPauseLoadingImageOnFly(ListView listView) {

		boolean pauseOnScroll = false; // scroll时加载
		boolean pauseOnFling = true; // fly时不加载
		PauseOnScrollListener listener = new PauseOnScrollListener(
				ImageLoader.getInstance(), pauseOnScroll, pauseOnFling);
		listView.setOnScrollListener(listener);
	}

	public static DisplayImageOptions getCarDetailOptions() {
		DisplayImageOptions options = new DisplayImageOptions.Builder()
				.showImageForEmptyUri(R.drawable.default_picture)
				.showImageOnFail(R.drawable.default_picture)
				.resetViewBeforeLoading(true).cacheOnDisk(true)
				.cacheInMemory(true)
				.imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
				.bitmapConfig(Bitmap.Config.RGB_565)
				.displayer(new FadeInBitmapDisplayer(300)).build();
		return options;
	}

	 /**
     * 从相册多选图options
     * @return
     * @author 牟沛文
     */
    public static DisplayImageOptions getAllImagesOptions() {
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .considerExifParams(true)
                .cacheInMemory(true)
                .cacheOnDisk(false)
                .resetViewBeforeLoading(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .imageScaleType(ImageScaleType.EXACTLY)
                .showImageForEmptyUri(R.drawable.default_picture)
                .showImageOnFail(R.drawable.default_picture)
                .showStubImage(R.drawable.default_picture)
                .build();

        return options;
    }
    
    
    /**
     * 默认的configure，注意和bangbang保持一致
     * @param context
     * @return
     */
    public static ImageLoaderConfiguration getDefaultConfigure(Context context) {
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                .diskCacheFileNameGenerator(new Md5FileNameGenerator())
                .memoryCache(new WeakMemoryCache())  //防止内存OOM
                .memoryCacheSize(4 * 1024 * 1024)
                .diskCacheSize(50 * 1024 * 1024)   // 50 Mb
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .writeDebugLogs() // Remove for release app
                .build();
        return config;
    }
    
    /**
     * 从相册选图configure
     * @return
     */
    public static ImageLoaderConfiguration getAllImagesConfigure(Context context) {
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                .memoryCache(new LRULimitedMemoryCache(4 * 1024 * 1024))
                .diskCacheFileNameGenerator(new Md5FileNameGenerator())
                .memoryCacheExtraOptions(200, 200)
                .memoryCacheSize(4 * 1024 * 1024)
                .tasksProcessingOrder(QueueProcessingType.FIFO)
                .writeDebugLogs() // Remove for release app
                .build();
        return config;
    }
}