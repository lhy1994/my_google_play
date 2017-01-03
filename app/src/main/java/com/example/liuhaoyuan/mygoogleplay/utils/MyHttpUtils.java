package com.example.liuhaoyuan.mygoogleplay.utils;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;


import org.xutils.common.Callback;
import org.xutils.common.util.KeyValue;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;


public class MyHttpUtils<T> {

    private String mUrl;
    private Context mContext;
    private T mResult;
    private resultCallBack<T> mResultCallBack;
    private String mIndex="";

    public interface resultCallBack<T> {
        T parseData(String result);
    }

    public MyHttpUtils(Context context) {
        this.mContext = context;
    }

    public T getData(RequestParams params, resultCallBack<T> resultCallBack ,boolean useCache) {
        mResult = null;
        mUrl = params.getUri();
        List<KeyValue> list = params.getQueryStringParams();
        if (list!=null && list.size()>0){
            KeyValue keyValue = list.get(0);
            mIndex = keyValue.getValueStr();
        }
        mResultCallBack = resultCallBack;

        if (useCache){
            String cache = readCache();
            if (TextUtils.isEmpty(cache)) {
                getDataFromServer(params);
                return mResult;
            } else {
                return mResultCallBack.parseData(cache);
            }
        }else {
            getDataFromServer(params);
            return mResult;
        }
    }

    private void getDataFromServer(RequestParams params) {
        MyCallBack myCallBack = new MyCallBack();
        x.http().get(params, myCallBack);
    }

    private String readCache() {
        String encode = MD5Utils.encode(mUrl + mIndex);
        File cacheFile = new File(mContext.getCacheDir(), encode);
        if (cacheFile.exists()) {
            BufferedReader reader = null;
            try {
                reader = new BufferedReader(new FileReader(cacheFile));
                String expire = reader.readLine();
                if (System.currentTimeMillis() < Long.parseLong(expire)) {
                    StringBuilder result = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }
                    Log.e("test", "read data .......................");
                    return result.toString();
                } else {
                    return null;
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return null;
    }

    private void writeCache(String result) {
        String encode = MD5Utils.encode(mUrl + mIndex);
        File file = new File(mContext.getCacheDir(), encode);
        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter(file);
            String expire = System.currentTimeMillis() + 30 * 60 * 1000 + "\n";
            fileWriter.write(expire + result);
            fileWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fileWriter != null) {
                try {
                    fileWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private class MyCallBack implements Callback.CommonCallback<String> {

        @Override
        public void onSuccess(String result) {
            mResult=mResultCallBack.parseData(result);
            writeCache(result);
            Log.e("result", result);
        }

        @Override
        public void onError(Throwable ex, boolean isOnCallback) {
            Toast.makeText(mContext, "get server data error", Toast.LENGTH_LONG).show();
            mResult=null;
        }

        @Override
        public void onCancelled(CancelledException cex) {
            Log.e("test", "get data cancel");
        }

        @Override
        public void onFinished() {
            Log.e("test", "get data finish");
        }
    }
}
