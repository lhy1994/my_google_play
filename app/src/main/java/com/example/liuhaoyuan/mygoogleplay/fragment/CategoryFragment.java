package com.example.liuhaoyuan.mygoogleplay.fragment;

import android.graphics.Color;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.liuhaoyuan.mygoogleplay.ConstansValue;
import com.example.liuhaoyuan.mygoogleplay.R;
import com.example.liuhaoyuan.mygoogleplay.domain.CategoryInfo;
import com.example.liuhaoyuan.mygoogleplay.utils.MyHttpUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;

/**
 * Created by liuhaoyuan on 2016/12/27.
 */

public class CategoryFragment extends BaseFragment {
    private final String url = "http://127.0.0.1:8090/category";
    private ArrayList<CategoryInfo> mData;

    @Override
    public LoadResult loadData(boolean useCache) {
        MyHttpUtils<ArrayList<CategoryInfo>> utils = new MyHttpUtils<>(getContext());
        RequestParams params=new RequestParams(url);
        mData = utils.getData(params, new CategoryCallBack(), useCache);
        if (mData==null){
            return LoadResult.ERROR;
        }else if (mData.size()==0){
            return LoadResult.EMPTY;
        }
        return LoadResult.SUCCESS;
    }

    @Override
    public View onCreateSuccessView() {
        Log.e("test","............................");
        ListView listView=new ListView(getContext());
        listView.setAdapter(new CateGoryAdapter(mData));
        return listView;
    }

    class CategoryCallBack implements MyHttpUtils.resultCallBack<ArrayList<CategoryInfo>> {

        @Override
        public ArrayList<CategoryInfo> parseData(String result) {
            try {
                JSONArray jsonArray = new JSONArray(result);
                ArrayList<CategoryInfo> list = new ArrayList<>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    CategoryInfo info = new CategoryInfo();
                    info.title = jsonObject.getString("title");
                    info.isTitle = true;
                    list.add(info);

                    JSONArray infos = jsonObject.getJSONArray("infos");
                    for (int j = 0; j < infos.length(); j++) {
                        JSONObject jsonObject1 = infos.getJSONObject(j);
                        CategoryInfo info1 = new CategoryInfo();
                        info1.isTitle = false;
                        info1.name1 = jsonObject1.getString("name1");
                        info1.name2 = jsonObject1.getString("name2");
                        info1.name3 = jsonObject1.getString("name3");
                        info1.url1 = jsonObject1.getString("url1");
                        info1.url2 = jsonObject1.getString("url2");
                        info1.url3 = jsonObject1.getString("url3");
                        list.add(info1);
                    }
                }
                return list;
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    class CateGoryAdapter extends BaseAdapter {

        private ArrayList<CategoryInfo> data;

        CateGoryAdapter(ArrayList<CategoryInfo> data) {
            this.data = data;
        }

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public int getItemViewType(int position) {
            if (getItem(position).isTitle) {
                return 0;
            } else {
                return 1;
            }
        }

        @Override
        public int getViewTypeCount() {
            return super.getViewTypeCount() + 1;
        }

        @Override
        public CategoryInfo getItem(int position) {
            return data.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            int type=getItemViewType(position);
            if (type==0){
                TextView textView = new TextView(getContext());
                textView.setText(getItem(position).title);
                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
                textView.setTextColor(Color.BLACK);
                return textView;
            }else {
                if (convertView == null) {
                    convertView = View.inflate(getContext(), R.layout.item_category, null);
                }
                ViewHolder holder = ViewHolder.getInstance(convertView);
                x.image().bind(holder.imageView1, ConstansValue.SERVER_URL + "image?name=" + getItem(position).url1);
                x.image().bind(holder.imageView2, ConstansValue.SERVER_URL + "image?name=" + getItem(position).url2);
                x.image().bind(holder.imageView3, ConstansValue.SERVER_URL + "image?name=" + getItem(position).url3);
                holder.textView1.setText(getItem(position).name1);
                holder.textView2.setText(getItem(position).name2);
                holder.textView3.setText(getItem(position).name3);
                return convertView;
            }
        }
    }

    private static class ViewHolder {
        ImageView imageView1;
        ImageView imageView2;
        ImageView imageView3;
        TextView textView1;
        TextView textView2;
        TextView textView3;

        ViewHolder(View convertView) {
            imageView1 = (ImageView) convertView.findViewById(R.id.iv_grid1);
            imageView2 = (ImageView) convertView.findViewById(R.id.iv_grid2);
            imageView3 = (ImageView) convertView.findViewById(R.id.iv_grid3);
            textView1 = (TextView) convertView.findViewById(R.id.tv_grid1);
            textView2 = (TextView) convertView.findViewById(R.id.tv_grid2);
            textView3 = (TextView) convertView.findViewById(R.id.tv_grid3);
        }

        static ViewHolder getInstance(View convertView) {
            ViewHolder holder = (ViewHolder) convertView.getTag();
            if (holder == null) {
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            }
            return holder;
        }
    }
}
