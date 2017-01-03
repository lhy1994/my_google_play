package com.example.liuhaoyuan.mygoogleplay;

import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.os.Process;
import android.text.AndroidCharacter;

import org.xutils.x;

/**
 * Created by liuhaoyuan on 2016/12/27.
 */

public class MyApplication extends Application {

    private static Context context;
    private static Handler handler;
    private static int mainThreadId;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        handler =new Handler();
        mainThreadId = Process.myTid();

        x.Ext.init(this);
    }


    public static Context getContext() {
        return context;
    }

    public static Handler getHandler() {
        return handler;
    }

    public static int getMainThreadId() {
        return mainThreadId;
    }
}
