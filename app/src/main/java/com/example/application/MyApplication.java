package com.example.application;

import android.app.Application;

import com.baidu.mapapi.SDKInitializer;
import com.google.android.material.color.DynamicColors;

/**
 * @Author : zhang
 * @Date : Created in 2023/12/2 19:31
 * @Decription : 合并百度地图和动态颜色Application
 */


public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // 应用动态颜色
        DynamicColors.applyToActivitiesIfAvailable(this);

        //设置隐私
        SDKInitializer.setAgreePrivacy(this,true);
        //初始化地图sdk
        SDKInitializer.initialize(this);
    }
}
