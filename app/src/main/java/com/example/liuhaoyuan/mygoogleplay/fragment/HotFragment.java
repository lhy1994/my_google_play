package com.example.liuhaoyuan.mygoogleplay.fragment;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.liuhaoyuan.mygoogleplay.utils.MyHttpUtils;
import com.example.liuhaoyuan.mygoogleplay.utils.UIUtils;
import com.example.liuhaoyuan.mygoogleplay.view.FlowLayout;

import org.xutils.http.RequestParams;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by liuhaoyuan on 2016/12/27.
 */

public class HotFragment extends BaseFragment {
    private final String url = "http://127.0.0.1:8090/hot";
    private MyHttpUtils<ArrayList<String>> mUtils;
    private ArrayList<String> mData;
    @Override
    public LoadResult loadData(boolean useCache) {
        mUtils = new MyHttpUtils<>(getContext());
        RequestParams params=new RequestParams(url);
        params.addParameter("index","0");
        RecommendFragment.RecommendResultCallback resultCallback=new RecommendFragment.RecommendResultCallback();
        mData = mUtils.getData(params, resultCallback, useCache);
        if (mData == null) {
            return LoadResult.ERROR;
        } else if (mData.size() == 0) {
            return LoadResult.EMPTY;
        }
        return LoadResult.SUCCESS;
    }

    @Override
    public View onCreateSuccessView() {
        ScrollView scrollView=new ScrollView(getContext());
        FlowLayout flowLayout=new FlowLayout(getContext());
        flowLayout.setPadding(10,10,10,10);
        scrollView.setPadding(15,15,15,15);
        for (String s : mData) {
            TextView textView=new TextView(getContext());
            textView.setText(s);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP,22);
            textView.setTextColor(Color.WHITE);
            textView.setPadding(10,10,10,10);
            textView.setGravity(Gravity.CENTER);

            Random random=new Random();
            int r = 30 + random.nextInt(200);
            int g = 30 + random.nextInt(200);
            int b = 30 + random.nextInt(200);
            GradientDrawable normal = UIUtils.getGradientDrawable(Color.rgb(r, g, b), 10);
            GradientDrawable pressed = UIUtils.getGradientDrawable(Color.rgb(r-10, g-10, b-10), 10);
            StateListDrawable selecter = UIUtils.getSelecter(normal, pressed);
            textView.setBackgroundDrawable(selecter);
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                }
            });
            flowLayout.addView(textView);
        }
        scrollView.addView(flowLayout);
        return scrollView;
    }
}
