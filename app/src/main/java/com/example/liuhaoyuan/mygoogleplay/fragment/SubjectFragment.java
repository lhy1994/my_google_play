package com.example.liuhaoyuan.mygoogleplay.fragment;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.liuhaoyuan.mygoogleplay.ConstansValue;
import com.example.liuhaoyuan.mygoogleplay.R;
import com.example.liuhaoyuan.mygoogleplay.adapter.MyBaseAdapter;
import com.example.liuhaoyuan.mygoogleplay.adapter.MyBaseHolder;
import com.example.liuhaoyuan.mygoogleplay.domain.SubjectInfo;
import com.example.liuhaoyuan.mygoogleplay.utils.MyHttpUtils;
import com.example.liuhaoyuan.mygoogleplay.view.RefreshListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;
import java.util.logging.ErrorManager;

/**
 * Created by liuhaoyuan on 2016/12/27.
 */

public class SubjectFragment extends BaseFragment {

    private MyHttpUtils<ArrayList<SubjectInfo>> mUtils;
    private final String url = "http://127.0.0.1:8090/subject";
    private SubjectResultCallBack mResultCallBack;
    private ArrayList<SubjectInfo> mData;


    @Override
    public LoadResult loadData(boolean useCache) {
        mUtils = new MyHttpUtils<>(getContext());
        RequestParams params=new RequestParams(url);
        params.addParameter("index",0);
        mResultCallBack = new SubjectResultCallBack();
        mData = mUtils.getData(params, mResultCallBack, useCache);
        if (mData==null){
            return LoadResult.ERROR;
        }else if (mData.size()==0){
            return LoadResult.EMPTY;
        }
        return LoadResult.SUCCESS;
    }

    @Override
    public View onCreateSuccessView() {
        RefreshListView listView = new RefreshListView(getContext());
        final SubjectAdapter adapter=new SubjectAdapter(mData);
        listView.setDividerHeight(0);
        listView.setAdapter(adapter);
        listView.setOnRefreshListener(new RefreshListView.OnRefreshListener() {
            @Override
            public boolean onRefresh() {
                initData(false);
                return true;
            }

            @Override
            public boolean onLoadMore() {
                RequestParams params=new RequestParams(url);
                params.addParameter("index",mData.size());
                ArrayList<SubjectInfo> moreData = mUtils.getData(params, mResultCallBack, true);
                if (moreData != null) {
                    mData.addAll(moreData);
                    adapter.updateData(mData);
                    return true;
                } else {
                    return false;
                }
            }
        });
        return listView;
    }

    class SubjectResultCallBack implements MyHttpUtils.resultCallBack<ArrayList<SubjectInfo>>{

        @Override
        public ArrayList<SubjectInfo> parseData(String result) {
            try {
                JSONArray jsonArray=new JSONArray(result);
                ArrayList<SubjectInfo> list=new ArrayList<>();
                for (int i=0;i<jsonArray.length();i++){
                    SubjectInfo info=new SubjectInfo();
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    info.des=jsonObject.getString("des");
                    info.url=jsonObject.getString("url");
                    list.add(info);
                }
                return list;
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    class SubjectAdapter extends MyBaseAdapter<SubjectInfo>{

        public SubjectAdapter(ArrayList<SubjectInfo> data) {
            super(data);
        }

        @Override
        public MyBaseHolder<SubjectInfo> getHolder() {
            return new SubjectHolder();
        }
    }

    class SubjectHolder extends MyBaseHolder<SubjectInfo>{

        private ImageView mImageView;
        private TextView mTextView;

        @Override
        public void setData(SubjectInfo data) {
            x.image().bind(mImageView, ConstansValue.SERVER_URL + "image?name=" + data.url);
            mTextView.setText(data.des);
        }

        @Override
        public View inflateItemView() {
            View view=View.inflate(getContext(), R.layout.item_subject,null);
            mImageView = (ImageView) view.findViewById(R.id.iv_subject);
            mTextView = (TextView) view.findViewById(R.id.tv_subject);
            return view;
        }
    }
}
