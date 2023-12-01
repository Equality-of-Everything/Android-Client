package com.example.UI.map;

import android.app.Application;


import com.baidu.mapapi.SDKInitializer;

/**
 * @Author : xcc
 * @Date : Created in 2023/11/30 19:12
 * @Decription :
 */

public class BaiduMap extends Application {
    @Override
    public void onCreate(){
        super.onCreate();
        //设置隐私
        SDKInitializer.setAgreePrivacy(this,true);
        //初始化地图sdk
        SDKInitializer.initialize(this);
    }


}
