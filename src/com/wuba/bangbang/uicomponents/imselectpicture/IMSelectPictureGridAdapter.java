package com.wuba.bangbang.uicomponents.imselectpicture;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.example.android_select_picture_module.R;
import com.example.uicomponents.IMImageView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.wuba.bangbang.uicomponents.utils.ImageLoaderUtils;

/**
 * 选择本地图片的adapter
 */
public class IMSelectPictureGridAdapter extends BaseAdapter implements OnClickListener {

    private Context mContext;
    /**全部的图片*/
    private List<IMSelectPictureActivity.LocalPic> dataList;
    /**已选择的图片*/
    private List<String> mSelectedDataList;
    private DisplayMetrics dm;
    private DisplayImageOptions mOption;
    private OnItemClickListener mOnItemClickListener;
    

    public IMSelectPictureGridAdapter(Context c, List<IMSelectPictureActivity.LocalPic> dataList, List<String> selectedDataList) {
        
        mContext = c;
        this.dataList = dataList;
        this.mSelectedDataList = selectedDataList;
        dm = new DisplayMetrics();
        ((Activity) mContext).getWindowManager().getDefaultDisplay().getMetrics(dm);
        mOption = ImageLoaderUtils.getAllImagesOptions();
    }

    public void notifyDataSetChanged(ArrayList<IMSelectPictureActivity.LocalPic> subDataList){

        this.dataList = subDataList;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        
        return dataList.size();
    }

    @Override
    public Object getItem(int position) {
        
        return dataList.get(position).path;
    }

    @Override
    public long getItemId(int position) {
        
        return position;
    }

    private class ViewHolder {
        
        public IMImageView imageView, check;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        
        ViewHolder viewHolder;
        if (convertView == null) {
            
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_select_picture_grid, parent, false);
            viewHolder.imageView = (IMImageView) convertView.findViewById(R.id.select_picture_grid_item_image);
            viewHolder.check = (IMImageView) convertView.findViewById(R.id.select_picture_grid_item_check);
            convertView.setTag(viewHolder);
        } else {
            
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.imageView.setImageResource(R.drawable.default_picture);
        String path = "file://" + dataList.get(position).getPath();
        ImageLoader.getInstance().displayImage(path, viewHolder.imageView, mOption);
        viewHolder.check.setTag(position);
        if (isInSelectedDataList(dataList.get(position).path)) {
            
            viewHolder.check.setVisibility(View.VISIBLE);
        } else {
            
            viewHolder.check.setVisibility(View.GONE);
        }
        convertView.setOnClickListener(this);
        return convertView;
    }

    /**
     * 判断图片是否选择
     * @param selectedString
     * @return
     */
    private boolean isInSelectedDataList(String selectedString) {

        if(mSelectedDataList != null && mSelectedDataList.size() > 0 && !TextUtils.isEmpty(selectedString)){

            for (int i = 0; i < mSelectedDataList.size(); i++) {

                if (mSelectedDataList.get(i).equals(selectedString)) {

                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void onClick(View view) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();

        if (viewHolder == null) {
            return;
        }

        int position = (Integer) viewHolder.check.getTag();
        if (viewHolder.check.getVisibility() == View.VISIBLE) {
            if (mOnItemClickListener.onItemClick(position, dataList.get(position).path, false)) {
                viewHolder.check.setVisibility(View.GONE);
            }
        } else {
            if (mOnItemClickListener.onItemClick(position, dataList.get(position).path, true)) {
                viewHolder.check.setVisibility(View.VISIBLE);
            }
        }
    }

    public void setOnItemClickListener(OnItemClickListener l) {
        
        mOnItemClickListener = l;
    }

    /**
     * 单击某一项的监听
     */
    public interface OnItemClickListener {

        /**
         * 单击某一个item
         * @author 徐宁
         * @param position item的位置
         * @param path 显示图片的路径
         * @param isSelected 图片是否已选中
         * @return
         */
        public boolean onItemClick(int position, String path, boolean isSelected);
    }

    /**
     * @return the mSelectedDataList
     */
    public List<String> getmSelectedDataList() {
        return mSelectedDataList;
    }
}