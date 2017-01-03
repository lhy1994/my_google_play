package com.example.liuhaoyuan.mygoogleplay.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import com.example.liuhaoyuan.mygoogleplay.R;

/**
 * Created by liuhaoyuan on 2016/12/27.
 */

public abstract class Loadingpage extends FrameLayout {
    private static final int STATE_LOAD_UNDO = 1;
    private static final int STATE_LOAD_LOADING = 2;
    private static final int STATE_LOAD_ERROR = 3;
    private static final int STATE_LOAD_EMPTY = 4;
    private static final int STATE_LOAD_SUCCESS = 5;

    private int mCurrentState = STATE_LOAD_UNDO;
    private View mLoadingView;
    private View mErrorPage;
    private View mEmptyPage;
    private View mSuccessView;

    public Loadingpage(Context context) {
        super(context);
        initView();
    }

    public Loadingpage(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public Loadingpage(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        if (mLoadingView == null) {
            mLoadingView = View.inflate(getContext(), R.layout.loading_view, null);
            addView(mLoadingView);
        }
        if (mErrorPage == null) {
            mErrorPage = View.inflate(getContext(), R.layout.error_view, null);
            addView(mErrorPage);
        }
        if (mEmptyPage == null) {
            mEmptyPage = View.inflate(getContext(), R.layout.empty_view, null);
            addView(mEmptyPage);
        }
        showPage();
    }

    private void showPage() {
        if (mCurrentState==STATE_LOAD_UNDO||mCurrentState==STATE_LOAD_LOADING){
            mLoadingView.setVisibility(VISIBLE);
        }else {
            mLoadingView.setVisibility(INVISIBLE);
        }
        if (mCurrentState==STATE_LOAD_ERROR){
            mErrorPage.setVisibility(VISIBLE);
        }else {
            mErrorPage.setVisibility(INVISIBLE);
        }
        if (mCurrentState==STATE_LOAD_EMPTY){
            mEmptyPage.setVisibility(VISIBLE);
        }else {
            mEmptyPage.setVisibility(INVISIBLE);
        }
        if (mCurrentState==STATE_LOAD_SUCCESS){
            mSuccessView = createSuccessPage();
            if (mSuccessView !=null){
                addView(mSuccessView);
            }
        }else {
            if (mSuccessView!=null){
                mSuccessView.setVisibility(INVISIBLE);
            }
        }


    }

    public abstract View createSuccessPage();

    private void loadData(){
        Thread thread=new Thread(){
            @Override
            public void run() {
                super.run();

            }
        };
        thread.start();
    }
}
