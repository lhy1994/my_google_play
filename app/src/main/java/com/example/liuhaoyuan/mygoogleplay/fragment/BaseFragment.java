package com.example.liuhaoyuan.mygoogleplay.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;

import com.example.liuhaoyuan.mygoogleplay.R;
import com.example.liuhaoyuan.mygoogleplay.utils.UIUtils;


public abstract class BaseFragment extends Fragment {

    private static final int STATE_LOAD_UNDO = 1;
    private static final int STATE_LOAD_LOADING = 2;
    private static final int STATE_LOAD_ERROR = 3;
    private static final int STATE_LOAD_EMPTY = 4;
    private static final int STATE_LOAD_SUCCESS = 5;

    private int mCurrentState = STATE_LOAD_LOADING;
    private FrameLayout mContainer;
    private View mLoadingView;
    private View mErrorView;
    private View mEmptyView;
    private View mSuccessView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return initView();
    }

    private View initView() {
        mContainer = new FrameLayout(getContext());
        mLoadingView = View.inflate(getContext(), R.layout.loading_view, null);
        mErrorView = View.inflate(getContext(), R.layout.error_view, null);
        mEmptyView = View.inflate(getContext(), R.layout.empty_view, null);

        mContainer.addView(mLoadingView);
        mContainer.addView(mEmptyView);
        mContainer.addView(mErrorView);

        Button button = (Button) mErrorView.findViewById(R.id.btn_retry);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initData(true);
            }
        });

        showView();
        initData(true);
        return mContainer;
    }

    public void initData(final boolean useCache) {
        Thread thread = new Thread() {
            @Override
            public void run() {
                super.run();
                LoadResult loadResult = loadData(useCache);
                mCurrentState = loadResult.getState();
                UIUtils.runOnUIThread(new Runnable() {
                    @Override
                    public void run() {
                        showView();
                    }
                });
            }
        };
        thread.start();
    }

    private void showView() {
        mLoadingView.setVisibility((mCurrentState == STATE_LOAD_LOADING || mCurrentState == STATE_LOAD_UNDO) ? View.VISIBLE : View.INVISIBLE);
        mEmptyView.setVisibility(mCurrentState == STATE_LOAD_EMPTY ? View.VISIBLE : View.INVISIBLE);
        mErrorView.setVisibility(mCurrentState == STATE_LOAD_ERROR ? View.VISIBLE : View.INVISIBLE);

        if (mCurrentState == STATE_LOAD_SUCCESS) {
            if (mSuccessView == null) {
                mSuccessView = onCreateSuccessView();
                mContainer.addView(mSuccessView);
            } else {
                mContainer.removeView(mSuccessView);
                mSuccessView = onCreateSuccessView();
                mContainer.addView(mSuccessView);
            }
            mSuccessView.setVisibility(View.VISIBLE);
        }else {
            if (mSuccessView!=null){
                mSuccessView.setVisibility(View.INVISIBLE);
            }
        }
    }


    public abstract LoadResult loadData(boolean useCache);

    public abstract View onCreateSuccessView();

    enum LoadResult {
        SUCCESS(STATE_LOAD_SUCCESS), ERROR(STATE_LOAD_ERROR), EMPTY(STATE_LOAD_EMPTY);

        private int state;

        private LoadResult(int state) {
            this.state = state;
        }

        public int getState() {
            return state;
        }
    }
}
