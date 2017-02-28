package com.wuba.bangbang.uicomponents.imselectpicture;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.WeakHashMap;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.android_select_picture_module.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.wuba.bangbang.uicomponents.pulltorefresh.PullToRefreshBase.Mode;
import com.wuba.bangbang.uicomponents.pulltorefresh.PullToRefreshGridView;
import com.wuba.bangbang.uicomponents.utils.ImageLoaderUtils;

/**
 * 查看可选的本地照片的详情页
 * @author Jack
 * @version 创建时间：2014-11-22   下午2:16:05
 */
public class IMSelectPictureActivity extends Activity {

    /** 展示图片的下拉刷新GridView */
    private PullToRefreshGridView mPullToRefreshGridView;
    private GridView mGridView;
    private WeakHashMap<String, ImageView> mHashMap = new WeakHashMap<String, ImageView>();
    /** 全部图片路径列表 */
    private List<LocalPic> dataList = new ArrayList<LocalPic>();
    /** ImageLoader是否默认配置 */
    private boolean mIsDefaultConfigure = false;
    /** 已选择的图片路径列表 */
    private ArrayList<String> selectedDataList = new ArrayList<String>();
    /** 加载图片时的ProgressBar*/
    private ProgressBar mProgressBar;
    /** 选择本地图片的adapter */
    private IMSelectPictureGridAdapter mSelectPictureAdapter;
    /** 显示已经选择的图片布局 */
    private LinearLayout selectedImageLayout;
    /** 右下角的完成按钮 */
    private Button okButton;
    /** 查看已选择图片的ScrollView */
    private HorizontalScrollView scrollView;
    /** 可以选择的总照片数 */
    private int mTotalSize = 0;
    /** 已选择图片的ImageLoader option */
    private DisplayImageOptions mSelectedOption;
    /** 规定分页显示的每页显示图片数目 */
    private final int PAGE_SIZE = 15;
    /**已经加载到的图片index*/
    private int mIndex;

    
    @SuppressWarnings("unchecked")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_picture);
        Intent intent = getIntent();
        mTotalSize = intent.getIntExtra("size", 0);
        Bundle bundle = intent.getExtras();
        if(bundle.getSerializable("selectdata") != null && bundle.getSerializable("selectdata") instanceof ArrayList<?>){

            selectedDataList = (ArrayList<String>) bundle.getSerializable("selectdata");
        }
        // ImageLoader切换专用配置
        changeToDefaultConfigure(false);
        init();
        initListener();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!mIsDefaultConfigure) {
            // ImageLoader切换到默认配置
            changeToDefaultConfigure(true);
        }
    }

    /**
     * 初始化UI
     */
    private void init() {
        mProgressBar = (ProgressBar) findViewById(R.id.select_picture_progressbar);
        mProgressBar.setVisibility(View.GONE);
        mPullToRefreshGridView = (PullToRefreshGridView) findViewById(R.id.select_picture_grid_view);
        mPullToRefreshGridView.setMode(Mode.PULL_FROM_END);
        //设置图片加载时的监听
        mIndex = PAGE_SIZE;
        mSelectPictureAdapter = new IMSelectPictureGridAdapter(this, dataList, getChildList(selectedDataList, mIndex));
        mGridView = mPullToRefreshGridView.getRefreshableView();
        mGridView.setAdapter(mSelectPictureAdapter);
        refreshData();
        selectedImageLayout = (LinearLayout) findViewById(R.id.selected_image_layout);
        okButton = (Button) findViewById(R.id.select_picture_ok_button);
        scrollView = (HorizontalScrollView) findViewById(R.id.select_picture_scroll_view);

        mSelectedOption = ImageLoaderUtils.getDefaultOptions();

        initSelectImage();
    }

    /**
     * 获得显示的子列表
     * @param list
     * @param end
     * @return
     */
    List<String> getChildList(List<String> list, int end){

        List<String> result = new ArrayList<String>();
        if(list != null && list.size() >= end){

            result.addAll(list.subList(0, end));
        }else if(list != null && list.size() < end){

            result.addAll(list);
        }
        return result;
    }

    /**
     * 初始化已选中的图片
     */
    private void initSelectImage() {

        if (selectedDataList == null){

            return;
        }
        for (final String path : selectedDataList) {
            ImageView imageView = (ImageView) LayoutInflater.from(IMSelectPictureActivity.this).inflate(R.layout.selected_picture_choose_imageview, selectedImageLayout, false);
            selectedImageLayout.addView(imageView);
            mHashMap.put(path, imageView);

            if(path.startsWith("http://")){   //加载网络图片

                ImageLoader.getInstance().displayImage(path, imageView, mSelectedOption, new ImageLoadingListener() {

                    @Override
                    public void onLoadingStarted(String arg0, View view) {

                        ((ImageView)view).setImageResource(R.drawable.default_picture);
                    }

                    @Override
                    public void onLoadingFailed(String arg0, View arg1, FailReason arg2) {
                    }

                    @Override
                    public void onLoadingComplete(String arg0, View view, Bitmap bitmap) {
                        ImageView imageView = (ImageView) view;
                        if (bitmap != null && !bitmap.isRecycled()) {   //截取图片中间的一部分显示
                            Bitmap mBitmap = ThumbnailUtils
                                    .extractThumbnail(bitmap,
                                            100,
                                            100,
                                            ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
                            imageView.setImageBitmap(mBitmap);
                            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) imageView.getLayoutParams();
                            params.width = 100;
                            params.height = 100;
                            imageView.setLayoutParams(params);
                        }
                    }

                    @Override
                    public void onLoadingCancelled(String arg0, View arg1) {
                    }
                });
            }else{  //加载本地的图片

                ImageLoader.getInstance().displayImage("file://" + path, imageView, mSelectedOption, new ImageLoadingListener() {

                    @Override
                    public void onLoadingStarted(String arg0, View arg1) {
                    }

                    @Override
                    public void onLoadingFailed(String arg0, View arg1, FailReason arg2) {
                    }

                    @Override
                    public void onLoadingComplete(String arg0, View view, Bitmap bitmap) {

                        ImageView imageView = (ImageView) view;
                        if (bitmap != null && !bitmap.isRecycled()) {  //截取图片中间的一部分显示
                            Bitmap mBitmap = ThumbnailUtils
                                    .extractThumbnail(bitmap,
                                            100,
                                            100,
                                            ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
                            imageView.setImageBitmap(mBitmap);
                            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) imageView.getLayoutParams();
                            params.width = 100;
                            params.height = 100;
                            imageView.setLayoutParams(params);
                        }

                    }

                    @Override
                    public void onLoadingCancelled(String arg0, View arg1) {
                    }
                });
            }
            //点击item删除图片
//            imageView.setOnClickListener(new View.OnClickListener() {
//                  @Override
//                  public void onClick(View v) {
//                     removePath(path);
//                      gridImageAdapter.notifyDataSetChanged();
//                  }
//            });
        }
        okButton.setText(" 完成(" + selectedDataList.size() + "/" + mTotalSize + ") ");
    }

    /**
     * 初始化单击item的监听
     */
    private void initListener() {
        /*返回按钮切换加载图片的配置
           ((IMHeadBar) findViewById(R.id.select_picture_head_bar)).setOnBackClickListener(
                new IMHeadBar.IOnBackClickListener() {
                    @Override
                    public void onBackClick(View v) {
                        changeToDefaultConfigure(true);
                        finish();
                    }
                }
        );*/

        mSelectPictureAdapter.setOnItemClickListener(new IMSelectPictureGridAdapter.OnItemClickListener() {

            @Override
            public boolean onItemClick(int position, final String path, boolean isSelected) {

                if (isSelected && selectedDataList.size() >= mTotalSize) {
                    if (!removePath(path)) {
                        Toast.makeText(IMSelectPictureActivity.this, getString(R.string.only_select_pictures, mTotalSize), Toast.LENGTH_SHORT).show();
                    }
                    return false;
                }

                if (isSelected) {   //图片被选中
                    if (!mHashMap.containsKey(path)) {
                        ImageView imageView = (ImageView) LayoutInflater.from(IMSelectPictureActivity.this).inflate(R.layout.selected_picture_choose_imageview, selectedImageLayout, false);
                        selectedImageLayout.addView(imageView);
                        imageView.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                int off = selectedImageLayout.getMeasuredWidth() - scrollView.getWidth();
                                if (off > 0) {
                                    scrollView.smoothScrollTo(off, 0);
                                }
                            }
                        }, 100);
                        mHashMap.put(path, imageView);
                        selectedDataList.add(path);
                        mSelectPictureAdapter.getmSelectedDataList().add(path);

                        ImageLoader.getInstance().displayImage("file://" + path, imageView, mSelectedOption, new ImageLoadingListener() {

                            @Override
                            public void onLoadingStarted(String arg0, View arg1) {
                            }

                            @Override
                            public void onLoadingFailed(String arg0, View arg1, FailReason arg2) {
                            }

                            @Override
                            public void onLoadingComplete(String arg0, View view, Bitmap bitmap) {

                                ImageView imageView = (ImageView) view;
                                if (bitmap != null && !bitmap.isRecycled()) {   //截取图片中间的一部分显示
                                    Bitmap mBitmap = ThumbnailUtils
                                            .extractThumbnail(bitmap,
                                                    100,
                                                    100,
                                                    ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
                                    imageView.setImageBitmap(mBitmap);
                                    LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) imageView
                                            .getLayoutParams();
                                    params.width = 100;
                                    params.height = 100;
                                    imageView.setLayoutParams(params);
                                }
                            }

                            @Override
                            public void onLoadingCancelled(String arg0, View arg1) {
                            }
                        });
                        okButton.setText(" 完成(" + selectedDataList.size() + "/" + mTotalSize + ") ");
                        return true;
                    } else {
                        return false;
                    }
                } else { //取消选中
                    removePath(path);
                    return true;
                }
            }
        });

//    	ImageLoaderUtils.setPauseLoadingImageOnFly(mGridView);

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putStringArrayList("dataList", selectedDataList);   //设置相册中选择的照片
                intent.putExtras(bundle);
                setResult(RESULT_OK, intent);
                changeToDefaultConfigure(true);
                finish();
            }
        });
    }

    /**
     * 删除指定图片
     * @param path
     * @return
     */
    private boolean removePath(String path) {

        mSelectPictureAdapter.getmSelectedDataList().remove(path);
        if (mHashMap.containsKey(path)) {
            selectedImageLayout.removeView(mHashMap.get(path));
            mHashMap.remove(path);
            removeOneData(selectedDataList, path);
            okButton.setText(" 完成(" + selectedDataList.size() + "/" + mTotalSize + ") ");
            return true;
        } else {
            return false;
        }
    }

    /**
     * 清除指定路径的图片
     * @param arrayList  路径集合
     * @param s 指定路径
     */
    private void removeOneData(ArrayList<String> arrayList, String s) {
        for (int i = 0; i < arrayList.size(); i++) {
            if (arrayList.get(i).equals(s)) {
                arrayList.remove(i);
                return;
            }
        }
    }

    /**
     * 获取本地的图片数据的异步任务
     */
    private void refreshData() {
        new AsyncTask<Void, Void, ArrayList<LocalPic>>() {

            @Override
            protected void onPreExecute() {
                mProgressBar.setVisibility(View.VISIBLE);
                super.onPreExecute();
            }

            @Override
            protected ArrayList<LocalPic> doInBackground(Void... params) {

                return listAllDir();
            }

            protected void onPostExecute(ArrayList<LocalPic> tmpList) {

                if (IMSelectPictureActivity.this == null || IMSelectPictureActivity.this.isFinishing()) {
                    return;
                }
                if(tmpList == null || tmpList.size() == 0){

                    Toast.makeText(IMSelectPictureActivity.this, "本地暂无图片", Toast.LENGTH_LONG).show();
                    IMSelectPictureActivity.this.finish();
                }
                mProgressBar.setVisibility(View.GONE);
                mSelectPictureAdapter.notifyDataSetChanged(getAllowableList(tmpList));
            }
        }.execute();
    }

    /**
     * 获得允许使用的图片
     * @param tmpList
     * @return
     */
    ArrayList<LocalPic> getAllowableList(ArrayList<LocalPic> tmpList){

        ArrayList<LocalPic> result = new ArrayList<LocalPic>();
        for (LocalPic local : tmpList) {
            String temp = local.path;
            if (null == temp)   continue;
            temp = temp.toLowerCase(Locale.ENGLISH);
            if ((temp.endsWith(".png") || temp.endsWith(".jpg") || temp.endsWith(".jpeg")) && !temp.startsWith("/system/media")) {   //需要过滤图片的条件
                result.add(local);
            }
        }
        return result;
    }


    /**
     * 获取本地图库图片所有路径
     * @return
     * @author 牟沛文
     */
    private ArrayList<LocalPic> listAllDir() {
        SparseArray<LocalPic> mImagePathMap = new SparseArray<LocalPic>();
        ArrayList<LocalPic> list = new ArrayList<LocalPic>();
        String[] proj = {MediaStore.Images.Media._ID, MediaStore.Images.Media.DATA};
        String sortOrder = MediaStore.Images.Media.DATE_ADDED + " desc";
        Cursor cursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                proj, null, null, sortOrder);
        if (cursor != null) {   //判断本地是否有图片
            while (cursor.moveToNext()) {
                LocalPic temp = new LocalPic();
                temp.path = cursor.getString(1);
                temp.thumbnails = null;
                list.add(temp);
                mImagePathMap.put(cursor.getInt(0), temp);
            }
        }
        if (cursor != null) {
            cursor.close();
        }

        proj = new String[]{MediaStore.Images.Thumbnails.IMAGE_ID, MediaStore.Video.Thumbnails.DATA};
        cursor = getContentResolver().query(MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI,
                proj, MediaStore.Images.Thumbnails.KIND + " = ?", new String[] {String.valueOf(MediaStore.Images.Thumbnails.MINI_KIND)}, null);
        if (cursor != null) {   //判断本地是否有图片
             while (cursor.moveToNext()) {
                LocalPic temp = mImagePathMap.get(cursor.getInt(0));
                if (temp != null) {
                    temp.thumbnails = cursor.getString(1);
                }
            }
        }
        if (cursor != null) {
            cursor.close();
        }
        mImagePathMap.clear();
        return list;
    }

    /**
     * 存储系统图库原始路径、缩略图路径
     * @author 牟沛文
     */
    public class LocalPic {
        public String path;
        public String thumbnails;

        public String getPath() {
            if (!TextUtils.isEmpty(thumbnails)) {
                if (new File(thumbnails).exists()) {
                    return thumbnails;
                }
                thumbnails = null;
            }
            return path;
        }
    }

    @Override
    public void onBackPressed() {
        changeToDefaultConfigure(true);
        super.onBackPressed();
    }

    /**
     * 切换ImageLoader配置
     * @param toDefault   <b>true</b> - 切换到默认配置；<b>false</b> - 切换到多选图配置
     */
    private void changeToDefaultConfigure(boolean toDefault) {
        ImageLoader.getInstance().clearMemoryCache();
        ImageLoader.getInstance().destroy();
        ImageLoader.getInstance().init(toDefault ? ImageLoaderUtils.getDefaultConfigure(this) : ImageLoaderUtils.getAllImagesConfigure(this));
        mIsDefaultConfigure = toDefault;
    }
}