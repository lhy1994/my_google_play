package com.example.liuhaoyuan.mygoogleplay.fragment;

import android.graphics.Color;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;

import com.example.liuhaoyuan.mygoogleplay.utils.MyHttpUtils;
import com.example.liuhaoyuan.mygoogleplay.view.fly.ShakeListener;
import com.example.liuhaoyuan.mygoogleplay.view.fly.StellarMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.xutils.http.RequestParams;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by liuhaoyuan on 2016/12/27.
 */

public class RecommendFragment extends BaseFragment {
    private final String url = "http://127.0.0.1:8090/recommend";
    private MyHttpUtils<ArrayList<String>> mUtils;
    private ArrayList<String> mData;

    @Override
    public LoadResult loadData(boolean useCache) {
        mUtils = new MyHttpUtils<>(getContext());
        RequestParams params = new RequestParams(url);
        params.addParameter("index", "0");
        RecommendResultCallback resultCallback = new RecommendResultCallback();
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
        final StellarMap stellarMap = new StellarMap(getContext());
        stellarMap.setAdapter(new RecommendAdapter());
        stellarMap.setInnerPadding(10, 10, 10, 10);
        stellarMap.setRegularity(6, 9);
        stellarMap.setGroup(0, true);

        ShakeListener shakeListener = new ShakeListener(getContext());
        shakeListener.setOnShakeListener(new ShakeListener.OnShakeListener() {
            @Override
            public void onShake() {
                stellarMap.zoomIn();
            }
        });
        return stellarMap;
    }

    static class RecommendResultCallback implements MyHttpUtils.resultCallBack<ArrayList<String>> {

        @Override
        public ArrayList<String> parseData(String result) {
            try {
                JSONArray jsonArray = new JSONArray(result);
                ArrayList<String> list = new ArrayList<>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    String string = jsonArray.getString(i);
                    list.add(string);
                }
                return list;
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    class RecommendAdapter implements StellarMap.Adapter {

        @Override
        public int getGroupCount() {
            return 2;
        }

        @Override
        public int getCount(int group) {
            int count = mData.size() / getGroupCount();
            if (group == getGroupCount() - 1) {
                count += mData.size() % getGroupCount();
            }
            return count;
        }

        @Override
        public View getView(int group, int position, View convertView) {
            position += group * getCount(group - 1);

            TextView textView = new TextView(getContext());
            textView.setText(mData.get(position));

            Random random = new Random();
            int size = 18 + random.nextInt(12);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, size);
            int r = 30 + random.nextInt(200);
            int g = 30 + random.nextInt(200);
            int b = 30 + random.nextInt(200);
            textView.setTextColor(Color.rgb(r, g, b));
            return textView;
        }

        @Override
        public int getNextGroupOnZoom(int group, boolean isZoomIn) {
            if (isZoomIn) {
                if (group > 0) {
                    group--;
                } else {
                    group = getGroupCount() - 1;
                }
            } else {
                if (group < getGroupCount() - 1) {
                    group++;
                } else {
                    group = 0;
                }
            }
            return group;
        }
    }
}
