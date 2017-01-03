package com.example.liuhaoyuan.mygoogleplay.adapter;

import android.view.View;

/**
 * Created by liuhaoyuan on 2016/12/28.
 */

public abstract class MyBaseHolder<T> {
    private View mRootView;

    public MyBaseHolder() {
        mRootView = inflateItemView();
        mRootView.setTag(this);
    }

    public View getRootView() {
        return mRootView;
    }

    public abstract void setData(T data);

    public abstract View inflateItemView();
}
