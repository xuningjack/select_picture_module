package com.wuba.bangbang.uicomponents.acitonsheet;

import android.support.v4.app.Fragment;

/**
 * ActionSheet的监听
 * @date: 2014-8-5 下午9:41:26
 */
public interface IActionSheetListener {

    /**
     * 隐藏时调用
     * @param actionSheet actionSheet
     * @param isCancel isCancel
     */
    void onDismiss(Fragment actionSheet, boolean isCancel);

    /**
     * 点击取消按钮是调用
     * @param actionSheet actionSheet
     * @param index  index
     */
    void onOtherButtonClick(Fragment actionSheet, int index);
}