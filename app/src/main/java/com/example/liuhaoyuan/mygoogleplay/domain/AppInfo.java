package com.example.liuhaoyuan.mygoogleplay.domain;

import java.util.ArrayList;

/**
 * Created by liuhaoyuan on 2016/12/28.
 */

public class AppInfo {
    public String des;
    public String downloadUrl;
    public String iconUrl;
    public String id;
    public String name;
    public String packageName;
    public long size;
    public float starts;

    public String author;
    public String date;
    public String downloadNum;
    public String version;
    public ArrayList<SafeInfo> safe;
    public ArrayList<String> screen;

    public static class SafeInfo{
        public String safeDes;
        public String safeDesUrl;
        public String safeUrl;
    }

}
