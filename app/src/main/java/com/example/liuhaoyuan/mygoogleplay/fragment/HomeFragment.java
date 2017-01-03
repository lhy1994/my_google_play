package com.example.liuhaoyuan.mygoogleplay.fragment;


import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.liuhaoyuan.mygoogleplay.ConstansValue;
import com.example.liuhaoyuan.mygoogleplay.R;
import com.example.liuhaoyuan.mygoogleplay.activity.AppDetailActivity;
import com.example.liuhaoyuan.mygoogleplay.adapter.MyBaseAdapter;
import com.example.liuhaoyuan.mygoogleplay.adapter.MyBaseHolder;
import com.example.liuhaoyuan.mygoogleplay.domain.AppInfo;
import com.example.liuhaoyuan.mygoogleplay.utils.MyHttpUtils;
import com.example.liuhaoyuan.mygoogleplay.view.RefreshListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.net.URL;
import java.util.ArrayList;

public class HomeFragment extends BaseFragment {

    private ArrayList<AppInfo> mData;
    private HomeResultCallBack mResultCallBack;
    private final String url = "http://127.0.0.1:8090/home";
    private MyHttpUtils<ArrayList<AppInfo>> mUtils;


    @Override
    public LoadResult loadData(boolean useCache) {
        mUtils = new MyHttpUtils<>(getContext());
        RequestParams params = new RequestParams(url);
        params.addParameter("index", "0");
        mResultCallBack = new HomeResultCallBack();
        mData = mUtils.getData(params, mResultCallBack,useCache);
        if (mData == null) {
            return LoadResult.ERROR;
        } else if (mData.size() == 0) {
            return LoadResult.EMPTY;
        }
        return LoadResult.SUCCESS;
    }

    @Override
    public View onCreateSuccessView() {
        final RefreshListView listView = new RefreshListView(getContext());
        final HomeAdapter adapter = new HomeAdapter(mData);
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
                RequestParams params = new RequestParams(url);
                params.addParameter("index", mData.size());
                Log.e("tset", mData.size() + "...................");
                ArrayList<AppInfo> moreData = mUtils.getData(params, mResultCallBack,true);
                if (moreData != null) {
                    Log.e("more data", moreData.size() + "..............");
                    mData.addAll(moreData);
                    adapter.updateData(mData);
                    return true;
                } else {
                    return false;
                }
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String packageName = adapter.getItem(position).packageName;
                Intent intent=new Intent(getContext(), AppDetailActivity.class);
                intent.putExtra("packageName",packageName);
                startActivity(intent);
            }
        });

        return listView;
    }

    class HomeResultCallBack implements MyHttpUtils.resultCallBack<ArrayList<AppInfo>> {

        @Override
        public ArrayList<AppInfo> parseData(String result) {
            try {
                JSONObject jsonObject = new JSONObject(result);
                JSONArray jsonArray = jsonObject.getJSONArray("list");
                ArrayList<AppInfo> appInfos = new ArrayList<>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject object = jsonArray.getJSONObject(i);
                    AppInfo appInfo = new AppInfo();
                    appInfo.des = object.getString("des");
                    appInfo.downloadUrl = object.getString("downloadUrl");
                    appInfo.iconUrl = object.getString("iconUrl");
                    appInfo.id = object.getString("id");
                    appInfo.name = object.getString("name");
                    appInfo.packageName = object.getString("packageName");
                    appInfo.size = object.getLong("size");
                    appInfo.starts = (float) object.getDouble("stars");
                    appInfos.add(appInfo);
                }
                return appInfos;
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    class HomeAdapter extends MyBaseAdapter<AppInfo> {

                HomeAdapter(ArrayList<AppInfo> data) {
                    super(data);
                }

                @Override
                public MyBaseHolder<AppInfo> getHolder() {
                    return new HomeHolder();
                }
            }

            class HomeHolder extends MyBaseHolder<AppInfo> {
                private ImageView mIconImageView;
                private TextView mNameTextView;
                private TextView mSizeTextView;
                private TextView mDesTextView;
                private Button mdownloadButton;
                private RatingBar mRatingBar;

                @Override
                public void setData(AppInfo data) {
                    x.image().bind(mIconImageView, ConstansValue.SERVER_URL + "image?name=" + data.iconUrl);
                    mNameTextView.setText(data.name);
                    mSizeTextView.setText(data.size + "");
                    mDesTextView.setText(data.des);
                    mRatingBar.setRating(data.starts);
                }

        @Override
        public View inflateItemView() {
            View view = View.inflate(getContext(), R.layout.item_home, null);
            mIconImageView = (ImageView) view.findViewById(R.id.iv_app_icon);
            mNameTextView = (TextView) view.findViewById(R.id.tv_app_name);
            mSizeTextView = (TextView) view.findViewById(R.id.tv_app_size);
            mDesTextView = (TextView) view.findViewById(R.id.tv_app_des);
            mdownloadButton = (Button) view.findViewById(R.id.btn_download);
            mRatingBar = (RatingBar) view.findViewById(R.id.rb_app);
            return view;
        }
    }
}
