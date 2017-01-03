package com.example.liuhaoyuan.mygoogleplay.activity;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.liuhaoyuan.mygoogleplay.ConstansValue;
import com.example.liuhaoyuan.mygoogleplay.R;
import com.example.liuhaoyuan.mygoogleplay.domain.AppInfo;
import com.example.liuhaoyuan.mygoogleplay.domain.DownloadInfo;
import com.example.liuhaoyuan.mygoogleplay.utils.DownloadManager;
import com.example.liuhaoyuan.mygoogleplay.utils.UIUtils;
import com.example.liuhaoyuan.mygoogleplay.view.ProgressHorizontal;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;

public class AppDetailActivity extends AppCompatActivity implements DownloadManager.DownloadObserver, View.OnClickListener {

    private ProgressBar loadingBar;
    private LinearLayout errorLayout;
    private Button retryButton;
    private LinearLayout successLayout;
    private ImageView appIconImageView;
    private TextView appNameTextView;
    private RatingBar ratingBar;
    private TextView downloadTextView;
    private TextView versionTextView;
    private TextView dateTextView;
    private TextView sizeTextView;
    private ImageView[] safeImages;
    private ImageView arrowImageView;
    private LinearLayout[] safeLayouts;
    private ImageView[] safeDesImageViews;
    private TextView[] safeDesTextViews;
    private LinearLayout safeDesLayout;
    private RelativeLayout safeRelativeLayout;
    private int mDesLayoutHeight;
    private ViewGroup.LayoutParams mLayoutParams;
    private ImageView[] screenImageViews;
    private TextView desTextView;
    private float mDownloadProgress;
    private int mDownloadState;
    private Button downloadButton;
    private FrameLayout downloadLayout;
    private ProgressHorizontal progressBar;
    private AppInfo appInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_detail);
        initView();
        initData();
    }

    private void initView() {
        loadingBar = (ProgressBar) findViewById(R.id.pb_detail);
        errorLayout = (LinearLayout) findViewById(R.id.ll_error);
        retryButton = (Button) findViewById(R.id.btn_retry_detail);
        successLayout = (LinearLayout) findViewById(R.id.ll_success);

        appIconImageView = (ImageView) findViewById(R.id.iv_app_icon_detail);
        appNameTextView = (TextView) findViewById(R.id.tv_app_name_detail);
        ratingBar = (RatingBar) findViewById(R.id.rb_detail);
        downloadTextView = (TextView) findViewById(R.id.tv_download);
        versionTextView = (TextView) findViewById(R.id.tv_version);
        dateTextView = (TextView) findViewById(R.id.tv_date);
        sizeTextView = (TextView) findViewById(R.id.tv_app_size_detail);

        safeDesLayout = (LinearLayout) findViewById(R.id.ll_des_root);
        safeRelativeLayout = (RelativeLayout) findViewById(R.id.rl_des_root);

        safeImages = new ImageView[4];
        safeImages[0]= (ImageView) findViewById(R.id.iv_safe1);
        safeImages[1]= (ImageView) findViewById(R.id.iv_safe2);
        safeImages[2]= (ImageView) findViewById(R.id.iv_safe3);
        safeImages[3]= (ImageView) findViewById(R.id.iv_safe4);
        arrowImageView = (ImageView) findViewById(R.id.iv_arrow);

        safeLayouts = new LinearLayout[4];
        safeLayouts[0]= (LinearLayout) findViewById(R.id.ll_des1);
        safeLayouts[1]= (LinearLayout) findViewById(R.id.ll_des2);
        safeLayouts[2]= (LinearLayout) findViewById(R.id.ll_des3);
        safeLayouts[3]= (LinearLayout) findViewById(R.id.ll_des4);

        safeDesImageViews = new ImageView[4];
        safeDesImageViews[0]= (ImageView) findViewById(R.id.iv_des1);
        safeDesImageViews[1]= (ImageView) findViewById(R.id.iv_des2);
        safeDesImageViews[2]= (ImageView) findViewById(R.id.iv_des3);
        safeDesImageViews[3]= (ImageView) findViewById(R.id.iv_des4);

        safeDesTextViews = new TextView[4];
        safeDesTextViews[0]= (TextView) findViewById(R.id.tv_des1);
        safeDesTextViews[1]= (TextView) findViewById(R.id.tv_des2);
        safeDesTextViews[2]= (TextView) findViewById(R.id.tv_des3);
        safeDesTextViews[3]= (TextView) findViewById(R.id.tv_des4);

        screenImageViews = new ImageView[5];
        screenImageViews[0]= (ImageView) findViewById(R.id.iv_pic1);
        screenImageViews[1]= (ImageView) findViewById(R.id.iv_pic2);
        screenImageViews[2]= (ImageView) findViewById(R.id.iv_pic3);
        screenImageViews[3]= (ImageView) findViewById(R.id.iv_pic4);
        screenImageViews[4]= (ImageView) findViewById(R.id.iv_pic5);

        desTextView = (TextView) findViewById(R.id.tv_des_detail);

        downloadButton = (Button) findViewById(R.id.btn_download_detail);
        downloadLayout = (FrameLayout) findViewById(R.id.fl_progress);
        progressBar = new ProgressHorizontal(getApplicationContext());
        progressBar.setProgressBackgroundResource(R.drawable.progress_bg);
        progressBar.setProgressResource(R.drawable.progress_normal);
        progressBar.setProgressTextColor(Color.WHITE);
        FrameLayout.LayoutParams layoutParams=new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        downloadLayout.addView(progressBar,layoutParams);
        DownloadManager.getInstance().registerObserver(this);

        loadingBar.setVisibility(View.VISIBLE);
        successLayout.setVisibility(View.INVISIBLE);
        errorLayout.setVisibility(View.INVISIBLE);

        retryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initData();
            }
        });
     }

    private void initData() {
        getDataFromServer();
    }

    private void showErrorPage(){
        loadingBar.setVisibility(View.INVISIBLE);
        successLayout.setVisibility(View.INVISIBLE);
        errorLayout.setVisibility(View.VISIBLE);
    }

    private void getDataFromServer() {
        String url= ConstansValue.SERVER_URL+"detail";
        String packageName = getIntent().getStringExtra("packageName");
        RequestParams params=new RequestParams(url);
        params.addParameter("packageName",packageName);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                parseData(result);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                showErrorPage();
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    private void parseData(String result){
        try {
            JSONObject jsonObject=new JSONObject(result);
            appInfo = new AppInfo();
            appInfo.des = jsonObject.getString("des");
            appInfo.downloadUrl = jsonObject.getString("downloadUrl");
            appInfo.iconUrl = jsonObject.getString("iconUrl");
            appInfo.id = jsonObject.getString("id");
            appInfo.name = jsonObject.getString("name");
            appInfo.packageName = jsonObject.getString("packageName");
            appInfo.size = jsonObject.getLong("size");
            appInfo.starts = (float) jsonObject.getDouble("stars");
            appInfo.author=jsonObject.getString("author");
            appInfo.date=jsonObject.getString("date");
            appInfo.downloadNum=jsonObject.getString("downloadNum");
            appInfo.version=jsonObject.getString("version");

            ArrayList<AppInfo.SafeInfo> list=new ArrayList<>();
            JSONArray safeArray = jsonObject.getJSONArray("safe");
            for (int i=0;i<safeArray.length();i++){
                JSONObject object = safeArray.getJSONObject(i);

                AppInfo.SafeInfo safeInfo=new AppInfo.SafeInfo();
                safeInfo.safeDes=object.getString("safeDes");
                safeInfo.safeDesUrl=object.getString("safeDesUrl");
                safeInfo.safeUrl=object.getString("safeUrl");
                list.add(safeInfo);
            }
            appInfo.safe=list;

            ArrayList<String> list1=new ArrayList<>();
            JSONArray screenArray=jsonObject.getJSONArray("screen");
            for (int i=0;i<screenArray.length();i++){
                list1.add(screenArray.getString(i));
            }
            appInfo.screen=list1;

            initSuccessView(appInfo);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void initSuccessView(AppInfo appInfo){
        loadingBar.setVisibility(View.INVISIBLE);
        errorLayout.setVisibility(View.INVISIBLE);
        successLayout.setVisibility(View.VISIBLE);

        x.image().bind(appIconImageView,ConstansValue.SERVER_URL + "image?name="+appInfo.iconUrl);
        appNameTextView.setText(appInfo.name);
        ratingBar.setRating(appInfo.starts);
        downloadTextView.setText(appInfo.downloadNum);
        dateTextView.setText(appInfo.version);
        versionTextView.setText(appInfo.version);
        sizeTextView.setText(appInfo.size+"");

        ArrayList<AppInfo.SafeInfo> safeInfos = appInfo.safe;
        for (int i=0;i<4;i++){
            if (i<safeInfos.size()){
                safeImages[i].setVisibility(View.VISIBLE);
                x.image().bind(safeImages[i],ConstansValue.SERVER_URL + "image?name="+safeInfos.get(i).safeUrl);
                x.image().bind(safeDesImageViews[i],ConstansValue.SERVER_URL + "image?name="+safeInfos.get(i).safeDesUrl);
                safeDesTextViews[i].setText(safeInfos.get(i).safeDes);
            }else {
                safeImages[i].setVisibility(View.GONE);
                safeDesTextViews[i].setVisibility(View.GONE);
                safeDesImageViews[i].setVisibility(View.GONE);
            }
        }

        safeDesLayout.measure(0,0);
        mDesLayoutHeight = safeDesLayout.getMeasuredHeight();

        mLayoutParams = safeDesLayout.getLayoutParams();
        mLayoutParams.height=0;
        safeDesLayout.setLayoutParams(mLayoutParams);

        safeRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggle();
            }
        });

        for (int i=0;i<5;i++){
            if (i<appInfo.screen.size()){
                x.image().bind(screenImageViews[i],ConstansValue.SERVER_URL + "image?name="+appInfo.screen.get(i));
            }else {
                screenImageViews[i].setVisibility(View.GONE);
            }
        }

        desTextView.setText(appInfo.des);

        DownloadInfo downloadInfo = DownloadManager.getInstance().getDownloadInfo(appInfo);
        if (downloadInfo!=null){
            mDownloadProgress = downloadInfo.getProgress();
            mDownloadState = downloadInfo.currentState;
        }else {
            mDownloadProgress =0;
            mDownloadState =DownloadManager.STATE_UNDO;
        }
        downloadLayout.setOnClickListener(this);
        downloadButton.setOnClickListener(this);
        refreshDownloadView(mDownloadState, mDownloadProgress);
    }

    private void refreshDownloadView(int downloadState, float downloadProgress) {
        Log.e("test","refresh");
        mDownloadState=downloadState;
        mDownloadProgress=downloadProgress;
        switch (downloadState){
            case DownloadManager.STATE_UNDO:
                downloadButton.setVisibility(View.VISIBLE);
                downloadLayout.setVisibility(View.GONE);
                downloadButton.setText("下载");
                break;
            case DownloadManager.STATE_WAITING:
                downloadButton.setVisibility(View.VISIBLE);
                downloadLayout.setVisibility(View.GONE);
                downloadButton.setText("等待中");
                break;
            case DownloadManager.STATE_DOWNLOADING:
                downloadButton.setVisibility(View.GONE);
                downloadLayout.setVisibility(View.VISIBLE);
                progressBar.setProgress(downloadProgress);
                progressBar.setCenterText(""+downloadProgress);
                break;
            case DownloadManager.STATE_PAUSE:
                downloadButton.setVisibility(View.GONE);
                downloadLayout.setVisibility(View.VISIBLE);
                progressBar.setProgress(downloadProgress);
                progressBar.setCenterText("暂停");
                break;
            case DownloadManager.STATE_ERROR:
                downloadLayout.setVisibility(View.GONE);
                downloadButton.setVisibility(View.VISIBLE);
                downloadButton.setText("失败");
                break;
            case DownloadManager.STATE_SUCCESS:
                downloadLayout.setVisibility(View.GONE);
                downloadButton.setVisibility(View.VISIBLE);
                downloadButton.setText("安装");
                break;
        }
    }

    private void runOnMainThread(final DownloadInfo downloadInfo){
        UIUtils.runOnUIThread(new Runnable() {
            @Override
            public void run() {
                refreshDownloadView(downloadInfo.currentState,downloadInfo.getProgress());
            }
        });
    }

    private boolean isOpen=false;
    private void toggle(){
        ValueAnimator animator;
        if (isOpen){
            isOpen=false;
            animator=ValueAnimator.ofInt(mDesLayoutHeight,0);
        }else {
            isOpen=true;
            animator=ValueAnimator.ofInt(0,mDesLayoutHeight);
        }
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                Integer value = (Integer) animation.getAnimatedValue();
                mLayoutParams.height=value;
                safeDesLayout.setLayoutParams(mLayoutParams);
            }
        });
        animator.setDuration(500);
        animator.start();
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (isOpen){
                    arrowImageView.setImageResource(R.drawable.arrow_up);
                }else {
                    arrowImageView.setImageResource(R.drawable.arrow_down);
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    @Override
    public void onDownloadStateChanged(DownloadInfo info) {
        if (appInfo!=null){
            if (info!=null && info.id.equals(appInfo.id)){
                runOnMainThread(info);
            }
        }
    }

    @Override
    public void onDownloadProgressChanged(DownloadInfo info) {
        if (appInfo!=null){
            if (info!=null && info.id.equals(appInfo.id)){
                runOnMainThread(info);
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_download_detail:
            case R.id.fl_progress:
                Log.e("test","onclick "+mDownloadState);
                if (mDownloadState==DownloadManager.STATE_UNDO ||
                        mDownloadState==DownloadManager.STATE_ERROR ||
                        mDownloadState==DownloadManager.STATE_PAUSE){
                    DownloadManager.getInstance().download(appInfo);
                }else if (mDownloadState==DownloadManager.STATE_DOWNLOADING || mDownloadState==DownloadManager.STATE_WAITING){
                    DownloadManager.getInstance().pause(appInfo);
                }else if (mDownloadState==DownloadManager.STATE_SUCCESS){
                    DownloadManager.getInstance().install(appInfo);
                }
                break;
        }
    }
}
