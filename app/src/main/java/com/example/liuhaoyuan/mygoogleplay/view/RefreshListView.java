package com.example.liuhaoyuan.mygoogleplay.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;


import com.example.liuhaoyuan.mygoogleplay.R;

import java.text.SimpleDateFormat;
import java.util.Date;

public class RefreshListView extends ListView implements OnScrollListener,
        AdapterView.OnItemClickListener {
    private static final int STATE_PULL_REFRESH = 0;
    private static final int STATE_RELEASE_REFRESH = 1;
    private static final int STATE_REFRESHING = 2;

    private int currentState = STATE_PULL_REFRESH;
    private View mHeaderView;
    private View mFooterView;
    private int startY;
    private int headerHight;
    private boolean isLoadingMore;
    private int footerViewHeight;
    private TextView mTitle;
    private ProgressBar mHeadProgressBar;
    private Button mRetryButton;
    private TextView mFooterTextView;
    private ProgressBar mFooterProgressBar;

    @SuppressLint("NewApi")
    public RefreshListView(Context context, AttributeSet attrs,
                           int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initHeaderView();
        initFooterView();
    }

    public RefreshListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initHeaderView();
        initFooterView();

    }

    public RefreshListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initHeaderView();
        initFooterView();

    }

    public RefreshListView(Context context) {
        super(context);
        initHeaderView();
        initFooterView();
    }

    public void initHeaderView() {
        mHeaderView = View.inflate(getContext(), R.layout.header_refresh, null);
        this.addHeaderView(mHeaderView);
        mTitle = (TextView) mHeaderView.findViewById(R.id.tv_refresh_title);
        mHeadProgressBar = (ProgressBar) findViewById(R.id.pb_refresh);

        mHeaderView.measure(0, 0);
        headerHight = mHeaderView.getMeasuredHeight();

//        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//            @Override
//            public void onGlobalLayout() {
//                headerHight=mHeaderView.getHeight();
//                getViewTreeObserver().removeGlobalOnLayoutListener(this);
//            }
//        });
        mHeaderView.setPadding(0, -headerHight, 0, 0);
    }

    public void initFooterView() {
        mFooterView = View.inflate(getContext(), R.layout.footer_refresh, null);
        mRetryButton = (Button) mFooterView.findViewById(R.id.btn_refresh_retry);
        mFooterTextView = (TextView) mFooterView.findViewById(R.id.tv_refresh_footer);
        mFooterProgressBar = (ProgressBar) mFooterView.findViewById(R.id.pb_refresh_footer);
        this.addFooterView(mFooterView);

        mFooterView.measure(0, 0);
        footerViewHeight = mFooterView.getMeasuredHeight();
//        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//            @Override
//            public void onGlobalLayout() {
//                footerViewHeight=mFooterTextView.getHeight();
//                getViewTreeObserver().removeGlobalOnLayoutListener(this);
//            }
//        });

        mFooterView.setPadding(0, -footerViewHeight, 0, 0);
        this.setOnScrollListener(this);
        mRetryButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mRefreshListener != null) {
                    onRefreshComplete(mRefreshListener.onLoadMore());
                }
            }
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:

                startY = (int) ev.getRawY();
                break;

            case MotionEvent.ACTION_MOVE:

                if (startY == -1) {
                    startY = (int) ev.getRawY();
                }
                if (currentState == STATE_REFRESHING) {
                    break;
                }
                int endY = (int) ev.getRawY();
                int dy = endY - startY;
                if (dy > 0 && getFirstVisiblePosition() == 0) {
                    int padding = dy - headerHight;
                    mHeaderView.setPadding(0, padding, 0, 0);

                    if (padding > 0 && currentState != STATE_RELEASE_REFRESH) {
                        currentState = STATE_RELEASE_REFRESH;
                        updateRefreshState();
                    } else if (padding < 0 && currentState != STATE_PULL_REFRESH) {
                        currentState = STATE_PULL_REFRESH;
                        updateRefreshState();
                    }
                    return true;
                }

                break;

            case MotionEvent.ACTION_UP:
                startY = -1;
                if (currentState == STATE_RELEASE_REFRESH) {
                    currentState = STATE_REFRESHING;
                    mHeaderView.setPadding(0, 0, 0, 0);
                    updateRefreshState();
                } else if (currentState == STATE_PULL_REFRESH) {
                    mHeaderView.setPadding(0, -headerHight, 0, 0);
                }
                break;

        }
        return super.onTouchEvent(ev);
    }

    private void updateRefreshState() {
        switch (currentState) {
            case STATE_PULL_REFRESH:
                mTitle.setText("下拉刷新");
                mHeadProgressBar.setVisibility(View.INVISIBLE);
                break;

            case STATE_REFRESHING:
                mTitle.setText("正在刷新");
                mHeadProgressBar.setVisibility(View.VISIBLE);
                if (mRefreshListener != null) {
                    onRefreshComplete(mRefreshListener.onRefresh());
                }
                break;
            case STATE_RELEASE_REFRESH:
                mTitle.setText("松开刷新...");
                mHeadProgressBar.setVisibility(View.INVISIBLE);
                break;
            default:
                break;
        }
    }

    private OnRefreshListener mRefreshListener;

    public void setOnRefreshListener(OnRefreshListener listener) {
        mRefreshListener = listener;
    }

    public interface OnRefreshListener {
        boolean onRefresh();
        boolean onLoadMore();
    }

    public void onRefreshComplete(boolean isSuccess) {
        if (isLoadingMore) {
            if (isSuccess){
                hideRetryButton();
                mFooterView.setPadding(0, -footerViewHeight, 0, 0);
                isLoadingMore = false;
            }else {
                showRetryButton();
            }
        } else {
            currentState = STATE_PULL_REFRESH;
            mTitle.setText("刷新完成");
            mHeadProgressBar.setVisibility(View.INVISIBLE);
            mHeaderView.setPadding(0, -headerHight, 0, 0);
        }
    }

    private void showRetryButton(){
        mRetryButton.setVisibility(VISIBLE);
        mFooterTextView.setVisibility(INVISIBLE);
        mFooterProgressBar.setVisibility(INVISIBLE);
    }

    private void hideRetryButton(){
        mRetryButton.setVisibility(INVISIBLE);
        mFooterTextView.setVisibility(VISIBLE);
        mFooterProgressBar.setVisibility(VISIBLE);
    }
    @SuppressLint("SimpleDateFormat")
    public String getCurrentTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss");
        return dateFormat.format(new Date());
    }



    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (scrollState == SCROLL_STATE_IDLE
                || scrollState == SCROLL_STATE_FLING && !isLoadingMore) {
            if (getLastVisiblePosition() == getCount() - 1) {
                mFooterView.setPadding(0, 0, 0, 0);
                setSelection(getCount());
                isLoadingMore = true;
                if (mRefreshListener != null) {
                    onRefreshComplete(mRefreshListener.onLoadMore());
                }
            }
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem,
                         int visibleItemCount, int totalItemCount) {

    }

    OnItemClickListener itemClickListener;

    @Override
    public void setOnItemClickListener(
            OnItemClickListener listener) {
        super.setOnItemClickListener(this);
        itemClickListener = listener;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        if (itemClickListener != null) {
            itemClickListener.onItemClick(parent, view, position
                    - getHeaderViewsCount(), id);
        }
    }
}
