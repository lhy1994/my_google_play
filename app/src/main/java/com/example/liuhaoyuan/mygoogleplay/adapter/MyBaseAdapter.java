package com.example.liuhaoyuan.mygoogleplay.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;

public abstract class MyBaseAdapter<T> extends BaseAdapter {

    private ArrayList<T> data;

    public MyBaseAdapter(ArrayList<T> data) {
        this.data = data;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public T getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MyBaseHolder<T> holder;
        if (convertView == null) {
            holder = getHolder();
            convertView = holder.getRootView();
        } else {
            holder = (MyBaseHolder<T>) convertView.getTag();
        }
        holder.setData(getItem(position));
        return convertView;
    }

    public void updateData(ArrayList<T> newData){
        this.data=newData;
        notifyDataSetChanged();
    }

    public abstract MyBaseHolder<T> getHolder();
}
