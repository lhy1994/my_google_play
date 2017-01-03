package com.example.liuhaoyuan.mygoogleplay.utils;

import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Process;

import com.example.liuhaoyuan.mygoogleplay.MyApplication;


public class UIUtils {
    public static boolean isRunOnUIThread(){
        return Process.myTid() == MyApplication.getMainThreadId();
    }

    public static void runOnUIThread(Runnable runnable){
        if (isRunOnUIThread()){
            runnable.run();
        }else {
            MyApplication.getHandler().post(runnable);
        }
    }

    public static GradientDrawable getGradientDrawable(int color,int radius){
        GradientDrawable shape=new GradientDrawable();
        shape.setShape(GradientDrawable.RECTANGLE);
        shape.setCornerRadius(radius);
        shape.setColor(color);
        return shape;
    }

    public static StateListDrawable getSelecter(Drawable normal, Drawable press){
        StateListDrawable selecter=new StateListDrawable();
        selecter.addState(new int[]{android.R.attr.state_pressed},press);
        selecter.addState(new int[]{},normal);
        return selecter;
    }
}
