package com.example.liuhaoyuan.mygoogleplay.domain;

import android.os.Environment;

import com.example.liuhaoyuan.mygoogleplay.MyApplication;
import com.example.liuhaoyuan.mygoogleplay.utils.DownloadManager;

import java.io.File;

/**
 * Created by liuhaoyuan on 2017/1/2.
 */

public class DownloadInfo {
    public String id;
    public String name;
    public String downloadUrl;
    public long size;
    public String packageName;
    public long currentPos;
    public int currentState;
    public String path;

    public float getProgress(){
        return currentPos/(float)size;
    }

    public String getFilePath(){
        StringBuilder builder=new StringBuilder();
//        String path = Environment.getExternalStorageDirectory().getAbsolutePath();
        String absolutePath = MyApplication.getContext().getFilesDir().getAbsolutePath();
        builder.append(absolutePath);
        builder.append(File.separator);
        builder.append("myGooglePlayDownload");

//        if (createDir(builder.toString())) {
//            return builder.toString()+File.separator+name+".apk";
//        }else {
//            return null;
//        }
        builder.append(File.separator);
        builder.append(name+".apk");
        return builder.toString();
    }

    public static DownloadInfo copy(AppInfo appInfo){
        DownloadInfo downloadInfo=new DownloadInfo();
        downloadInfo.name=appInfo.name;
        downloadInfo.size=appInfo.size;
        downloadInfo.currentPos=0;
        downloadInfo.downloadUrl=appInfo.downloadUrl;
        downloadInfo.packageName=appInfo.packageName;
        downloadInfo.id=appInfo.id;
        downloadInfo.currentState= DownloadManager.STATE_UNDO;
        downloadInfo.path=downloadInfo.getFilePath();
        return downloadInfo;
    }

    public boolean createDir(String path){
        File dir=new File(path);
        if (!dir.exists() || !dir.isDirectory()){
            return dir.mkdirs();
        }
        return true;
    }
}
