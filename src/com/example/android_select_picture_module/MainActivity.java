package com.example.android_select_picture_module;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.Media;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.View;

import com.example.uicomponents.IMImageView;
import com.example.uicomponents.IMLocalImageViewFragment;
import com.wuba.bangbang.uicomponents.acitonsheet.ActionSheet;
import com.wuba.bangbang.uicomponents.acitonsheet.IActionSheetListener;
import com.wuba.bangbang.uicomponents.imselectpicture.IMSelectPictureActivity;
import com.wuba.bangbang.uicomponents.imselectpicture.IMSelectPictureView;


public class MainActivity extends FragmentActivity implements IActionSheetListener{

    /**提示框显示的内容*/
    private String[] mActionSheetTitle; 
    /**显示图片的控件*/
    private IMSelectPictureView mSelectPictureView;
    /**已选择的图片路径集合*/
    private ArrayList<String> mPicRawPath;   //TODO 拿到图片名称列表后可以进行操作
    /**拍照、选择本地图片的标识*/
    private final int TAKE_PICTURE = 0X1, FROM_LOCAL = 0X2;
    /**拍摄照片后添加到的位置*/
    private final int TAKE_PICTURE_POSITION = 0;
    /**可以选择的最大照片数*/
    private final int MAX_COUNT = 12;       //TODO 设置还可以选择的照片个数
    /**拍摄照片的路径uri*/
    private Uri mPhotoUri;
    private final String DATALIST = "dataList", SELECTDATA = "selectdata", SIZE = "size";
    
    
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        mSelectPictureView = (IMSelectPictureView)findViewById(R.id.selectPictureView);
        mSelectPictureView.setFragmentManager(getSupportFragmentManager());
        mSelectPictureView.setMaxPictureCount(MAX_COUNT);
        mSelectPictureView.setListener(new IMSelectPictureView.ISelectPictureListener() {
            
            @Override
            public void onSelectPicture() {
                
                Intent intent = new Intent(MainActivity.this, IMSelectPictureActivity.class);
                Bundle bundle = new Bundle();
                bundle.putStringArrayList(DATALIST, new ArrayList<String>());
                bundle.putStringArrayList(SELECTDATA, mSelectPictureView.getPictureData());  //传递已选择的照片
                bundle.putInt(SIZE, MAX_COUNT);   
                intent.putExtras(bundle);
                startActivityForResult(intent, FROM_LOCAL);
            }
            
            @Override
            public void onClickTackPicture() {   //单击拍照后生成指定文件名的图片，针对返回的data为空。
                
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                SimpleDateFormat timeStampFormat = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.CHINA);
                String filename = timeStampFormat.format(new Date());
                ContentValues values = new ContentValues();
                values.put(Media.TITLE, filename);
                mPhotoUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, mPhotoUri);
                startActivityForResult(intent, TAKE_PICTURE);
            }
            
            @Override
            public void onClickPicture(List<String> pictures, IMImageView imageView) {
                
                IMLocalImageViewFragment myView = new IMLocalImageViewFragment();
                myView.setData(pictures); 
                String url = imageView.getUrl();
                int position = 0;
                position = mSelectPictureView.getPosition(url, pictures);
                myView.setmPosition(position);
                myView.show(getSupportFragmentManager());
            }
        });
        
        
        mActionSheetTitle = new String[]{getString(R.string.take_photo), getString(R.string.upload_from_album)};
        
        findViewById(R.id.textView).setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View arg0) {
                
                ActionSheet.createBuilder(MainActivity.this, getSupportFragmentManager())
                .setCancelButtonTitle(R.string.cancel)
                .setOtherButtonTitles(mActionSheetTitle)
                .setCancelableOnTouchOutside(true)
                .setListener(MainActivity.this)
                .show();
            }
        });
    }

    /**
     * @see com.wuba.bangbang.uicomponents.acitonsheet.IActionSheetListener#onOtherButtonClick(android.support.v4.app.Fragment, int)
     * @param actionSheet
     * @param index
     */
    @Override
    public void onOtherButtonClick(Fragment actionSheet, int index) {
        
        String title = mActionSheetTitle[index];
        if(TextUtils.equals(title, getString(R.string.take_photo))){  //拍照片
            
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            SimpleDateFormat timeStampFormat = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.CHINA);
            String filename = timeStampFormat.format(new Date());
            ContentValues values = new ContentValues();
            values.put(Media.TITLE, filename);
            mPhotoUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, mPhotoUri);
            startActivityForResult(intent, TAKE_PICTURE);
        }else{  //从相册选图
            
            Intent intent = new Intent(MainActivity.this, IMSelectPictureActivity.class);
            Bundle bundle = new Bundle();
            bundle.putStringArrayList("dataList", new ArrayList<String>());
            bundle.putStringArrayList("selectdata", mSelectPictureView.getPictureData());  //传递已选择的照片
            bundle.putInt("size", MAX_COUNT);   //设置还可以选择的照片个数
            intent.putExtras(bundle);
            startActivityForResult(intent, FROM_LOCAL);
        }
    }
    
    /**
     * @see android.support.v4.app.FragmentActivity#onActivityResult(int, int, android.content.Intent)
     * @param arg0
     * @param arg1
     * @param arg2
     */ 
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) { 
            if(data != null){  //选择图片完成后返回
                
                Bundle bundle = data.getExtras();
                mPicRawPath = bundle.getStringArrayList("dataList");
                if(mSelectPictureView.getMaxPictureCount() > mPicRawPath.size()) {
                    mPicRawPath.add(TAKE_PICTURE_POSITION, IMSelectPictureView.DEFAULT_TAG);
                }
                mSelectPictureView.addPictureData(mPicRawPath, true);
            }else if(data == null && mPhotoUri != null){  //拍照片
                
                if(mPicRawPath == null){
                    
                    mPicRawPath = new ArrayList<String>();
                }
                if(mPicRawPath.size() > 0){
                    
                    mPicRawPath.remove(TAKE_PICTURE_POSITION);
                }
                mPicRawPath.add(0, mPhotoUri.getPath());
                mSelectPictureView.addPictureData(mPicRawPath, true);
            }
        } 
    }
    
    @Override
    public void onDismiss(Fragment actionSheet, boolean isCancel) {
    }
}