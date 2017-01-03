package com.example.liuhaoyuan.mygoogleplay.utils;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by liuhaoyuanh on 2017/1/2.
 */

public class ThreadManager {
    private static ThreadPool mThreadPool;

    public static synchronized ThreadPool getInstance(){
        if (mThreadPool==null){
            mThreadPool=new ThreadPool();
        }
        return mThreadPool;
    }

    public static class ThreadPool{
        private int corePoolSize;
        private int maximumPoolSzie;
        private long keepAliveTime;
        private ThreadPoolExecutor executor;

        private ThreadPool(){

        }

        public void execute(Runnable runnable){
            if (executor==null){
                executor = new ThreadPoolExecutor(corePoolSize,maximumPoolSzie,keepAliveTime,
                        TimeUnit.SECONDS,new LinkedBlockingQueue<Runnable>(),
                        Executors.defaultThreadFactory(),new ThreadPoolExecutor.AbortPolicy());
            }
            executor.execute(runnable);
        }

        public void cancel(Runnable runnable){
            if (executor!=null){
                executor.getQueue().remove(runnable);
            }
        }
    }
}
