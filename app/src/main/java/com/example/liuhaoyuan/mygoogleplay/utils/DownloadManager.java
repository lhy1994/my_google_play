package com.example.liuhaoyuan.mygoogleplay.utils;

import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.ProgressBar;

import com.example.liuhaoyuan.mygoogleplay.ConstansValue;
import com.example.liuhaoyuan.mygoogleplay.MyApplication;
import com.example.liuhaoyuan.mygoogleplay.domain.AppInfo;
import com.example.liuhaoyuan.mygoogleplay.domain.DownloadInfo;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by liuhaoyuan on 2017/1/2.
 */

public class DownloadManager {
    public static final int STATE_UNDO = 1;
    public static final int STATE_WAITING = 2;
    public static final int STATE_DOWNLOADING = 3;
    public static final int STATE_PAUSE = 4;
    public static final int STATE_ERROR = 5;
    public static final int STATE_SUCCESS = 6;

    private static DownloadManager mDownloadManager;
    private ArrayList<DownloadObserver> observers = new ArrayList<>();
    private HashMap<String, DownloadInfo> downloadInfoHashMap = new HashMap<>();
    private HashMap<String, DownloadTask> taskHashMap = new HashMap<>();

    public static synchronized DownloadManager getInstance() {
        if (mDownloadManager == null) {
            mDownloadManager = new DownloadManager();
        }
        return mDownloadManager;
    }

    public void download(AppInfo appInfo) {
        DownloadInfo downloadInfo = downloadInfoHashMap.get(appInfo.id);
        if (downloadInfo == null) {
            downloadInfo = DownloadInfo.copy(appInfo);
        }

        downloadInfo.currentState = STATE_WAITING;
        notifyDownloadStateChanged(downloadInfo);
        downloadInfoHashMap.put(downloadInfo.id, downloadInfo);

        DownloadTask task = new DownloadTask(downloadInfo);
        task.run();
//        ThreadManager.getInstance().execute(task);
        taskHashMap.put(downloadInfo.id, task);
    }

    public void pause(AppInfo appInfo) {
        DownloadInfo downloadInfo = downloadInfoHashMap.get(appInfo.id);
        if (downloadInfo != null) {
            if (downloadInfo.currentState == STATE_WAITING || downloadInfo.currentState == STATE_DOWNLOADING) {
                downloadInfo.currentState = STATE_PAUSE;
                notifyDownloadStateChanged(downloadInfo);

                DownloadTask task = taskHashMap.get(downloadInfo.id);
                if (task != null) {
//                    ThreadManager.getInstance().cancel(task);
                    task.cancel();
                }
            }
        }
    }

    public void install(AppInfo appInfo) {
        DownloadInfo downloadInfo = downloadInfoHashMap.get(appInfo);
        if (downloadInfo != null) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setDataAndType(Uri.parse("file://" + downloadInfo.path), "application/vnd.android.package.archive");
            MyApplication.getContext().startActivity(intent);
        }
    }

    public void registerObserver(DownloadObserver observer) {
        if (observer != null && !observers.contains(observer)) {
            observers.add(observer);
        }
    }

    public void unRegisterObserver(DownloadObserver observer) {
        if (observer != null && observers.contains(observer)) {
            observers.remove(observer);
        }
    }

    public DownloadInfo getDownloadInfo(AppInfo appInfo){
        return downloadInfoHashMap.get(appInfo.id);
    }

    public void notifyDownloadStateChanged(DownloadInfo info) {
        for (DownloadObserver observer : observers) {
            observer.onDownloadStateChanged(info);
        }
    }

    public void notifyDownloadProgressChanged(DownloadInfo info) {
        for (DownloadObserver observer : observers) {
            observer.onDownloadProgressChanged(info);
        }
    }

    public interface DownloadObserver {
        void onDownloadStateChanged(DownloadInfo info);

        void onDownloadProgressChanged(DownloadInfo info);
    }

    class DownloadTask implements Runnable {

        private DownloadInfo downloadInfo;
        private Callback.Cancelable cancelable;

        DownloadTask(DownloadInfo downloadInfo) {
            this.downloadInfo = downloadInfo;
        }

        public void cancel(){
            if (cancelable!=null){
                cancelable.cancel();
            }
        }

        @Override
        public void run() {
            downloadInfo.currentState = STATE_DOWNLOADING;
            notifyDownloadStateChanged(downloadInfo);
            File file = new File(downloadInfo.path);
            if (!file.exists()){
                boolean mkdirs = file.mkdirs();
                Log.e("test",downloadInfo.path);
                if (mkdirs){
                    Log.e("test","success");
                }else {
                    Log.e("test","fail");
                }
            }
            RequestParams params = new RequestParams(ConstansValue.SERVER_URL + "download");
            params.addParameter("name", downloadInfo.downloadUrl);
            params.addParameter("range",file.length());
            Log.e("test",file.length()+"...............");
            params.setSaveFilePath(file.getAbsolutePath());
            params.setAutoRename(false);
            params.setAutoResume(true);
            cancelable = x.http().get(params, new Callback.ProgressCallback<File>() {
                @Override
                public void onWaiting() {

                }

                @Override
                public void onStarted() {

                }

                @Override
                public void onLoading(long total, long current, boolean isDownloading) {
                    if (isDownloading) {
                        Log.e("test",total+"");
                        Log.e("test",current+"");
                        downloadInfo.currentPos = current;
                        notifyDownloadProgressChanged(downloadInfo);
                    }
                }

                @Override
                public void onSuccess(File result) {
                    Log.e("file", result.getPath());
                    Log.e("file", result.length() + "..............");
                    downloadInfo.currentState = STATE_SUCCESS;
                    notifyDownloadStateChanged(downloadInfo);
                }

                @Override
                public void onError(Throwable ex, boolean isOnCallback) {
                    ex.printStackTrace();
                    Log.e("download error","....................................");
                    downloadInfo.currentState = STATE_ERROR;
                    notifyDownloadStateChanged(downloadInfo);
                }

                @Override
                public void onCancelled(CancelledException cex) {

                }

                @Override
                public void onFinished() {
                    taskHashMap.remove(downloadInfo.id);
                }
            });
        }

    }
}
