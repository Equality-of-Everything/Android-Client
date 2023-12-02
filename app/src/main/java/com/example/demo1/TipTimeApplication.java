package com.example.demo1;

import android.app.Application;

import com.google.android.material.color.DynamicColors;

/**
 * @Author : zhang
 * @Date : Created in 2023/12/2 19:31
 * @Decription :
 */


public class TipTimeApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // 应用动态颜色
        DynamicColors.applyToActivitiesIfAvailable(this);
    }
}
