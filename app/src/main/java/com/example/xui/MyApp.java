package com.example.xui;

import android.app.Application;

import com.xuexiang.xui.XUI;

/**
 * @Author : Lee
 * @Date : Created in 2023/11/23 9:17
 * @Decription :
 */

public class MyApp extends Application {

    /*
    * XUI全局配置
    * */
    @Override
    public void onCreate() {
        super.onCreate();

        XUI.init(this);
        XUI.debug(true);
    }
}
