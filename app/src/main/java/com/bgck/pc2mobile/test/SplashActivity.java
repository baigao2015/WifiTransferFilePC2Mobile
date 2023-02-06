/*
 * Copyright (c) 2019.
 * 贵州日报当代融媒体集团有限责任公司 2019-2023 版权所有.
 * 本软件为贵州日报当代融媒体集团的保密专有信息。您不得透露此类机密信息,
 * 应仅按照您与贵州日报当代融媒体集团签订的许可协议条款使用.
 */
package com.bgck.pc2mobile.test;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowInsets;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.bgck.pc2mobile.MainActivity;
import com.bgck.pc2mobile.R;
import com.bgck.pc2mobile.util.StatusBarCompat;
import com.bgck.pc2mobile.wegit.AlignTextView;
import com.bgck.pc2mobile.wegit.XRTextView;

/**
 * @author bgck
 * @author (lastest modification by 修改人)
 * @version 版本号_ 2023/2/3 9:24
 * @since 2022302
 */
public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            View decorView = window.getDecorView();
            decorView.setOnApplyWindowInsetsListener(new View.OnApplyWindowInsetsListener() {
                @TargetApi(Build.VERSION_CODES.KITKAT_WATCH)
                @Override
                public WindowInsets onApplyWindowInsets(View v, WindowInsets insets) {
                    WindowInsets defaultInsets = v.onApplyWindowInsets(insets);
                    return defaultInsets.replaceSystemWindowInsets(
                            defaultInsets.getSystemWindowInsetLeft(),
                            0,
                            defaultInsets.getSystemWindowInsetRight(),
                            defaultInsets.getSystemWindowInsetBottom());
                }
            });
            ViewCompat.requestApplyInsets(decorView);
            //将状态栏设成透明，如不想透明可设置其他颜色
            //将状态栏设成透明，如不想透明可设置其他颜色
            window.setStatusBarColor(ContextCompat.getColor(this, android.R.color.transparent));
//            window.setStatusBarColor(getResources().getColor(R.color.colorWifiConnected));
            StatusBarCompat.setStatusBarDarkFont(this, true);

            //After LOLLIPOP not translucent status bar
//            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//            //Then call setStatusBarColor.
//            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//            window.setStatusBarColor(getResources().getColor(R.color.colorWifiConnected));

        } else {
            ViewGroup mDecorView = (ViewGroup) getWindow().getDecorView();


//add fake status bar view
            View mStatusBarView = new View(this);
            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,  80);
            layoutParams.gravity = Gravity.TOP;
            mStatusBarView.setLayoutParams(layoutParams);
            mStatusBarView.setBackgroundColor(getResources().getColor(R.color.colorWifiConnected));
            mDecorView.addView(mStatusBarView, 0);
            mDecorView.setTag(true);
        }
/*

        Window window = getWindow();
        ViewGroup mContentView = (ViewGroup) findViewById(Window.ID_ANDROID_CONTENT);

//First translucent status bar.
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        int statusBarHeight = StatusBarCompat.getStatusBarHeight(this);

        View mChildView = mContentView.getChildAt(0);
        if (mChildView != null) {
            FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) mChildView.getLayoutParams();
            //如果已经为 ChildView 设置过了 marginTop, 再次调用时直接跳过
            if (lp != null && lp.topMargin < statusBarHeight && lp.height != statusBarHeight) {
                //不预留系统空间
                ViewCompat.setFitsSystemWindows(mChildView, false);
                lp.topMargin += statusBarHeight;
                mChildView.setLayoutParams(lp);
            }
        }

        View statusBarView = mContentView.getChildAt(0);
        if (statusBarView != null && statusBarView.getLayoutParams() != null && statusBarView.getLayoutParams().height == statusBarHeight) {
            //避免重复调用时多次添加 View
            statusBarView.setBackgroundColor(getResources().getColor(R.color.colorWifiConnected));
            return;
        }
        statusBarView = new View(this);
        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, statusBarHeight);
        statusBarView.setBackgroundColor(getResources().getColor(R.color.colorWifiConnected));
//向 ContentView 中添加假 View
        mContentView.addView(statusBarView, 0, lp);
*/

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                finish();
            }
        }, 3000);

    }

}
