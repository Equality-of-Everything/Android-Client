package com.example.util;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * @Author : Lee
 * @Date : Created in 2023/11/29 13:49
 * @Decription :
 */

public class OkHttpUtils {
    private static final String TAG = "okHttp";
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private static OkHttpUtils instance;
    private OkHttpClient client;
    private Handler handler;

    private OkHttpUtils() {
        client = new OkHttpClient();
        handler = new Handler(Looper.getMainLooper());
    }

    /**
     * @param :
     * @return OkHttpUtils
     * @author Lee
     * @description 单例模式
     * @date 2023/11/29 19:09
     */
    public static OkHttpUtils getInstance() {
        if(instance == null) {
            instance = new OkHttpUtils();
        }
        return instance;
    }

    //sendJsonPostRequest
    //sendJsonGetRequest

    /**
     * @param url:
     * @param jsonBody:
     * @return String
     * @author Lee
     * @description 封装okHttp, Post同步请求
     * @date 2023/11/29 20:33
     */
    public String syncPostRequest(String url, String jsonBody) {
        RequestBody requestBody = RequestBody.create(jsonBody, JSON);
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        try{
            Response response = client.newCall(request).execute();
            return response.body().string();
        } catch (IOException e) {
            Log.e(TAG, "Server Error");
            return null;
        }

    }

    /**
     * @param url:
     * @param jsonBody:
     * @param callback:
     * @return void
     * @author Lee
     * @description 封装okHttp, Post异步请求
     * @date 2023/11/29 20:34
     */
    public void asyncPostRequest(String url, String jsonBody, final Callback callback) {
        RequestBody requestBody = RequestBody.create(jsonBody, JSON);
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            //失败
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e(TAG, "Server Error");
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                final String responseBody = response.body().string();
                handler.post(new Runnable() {
                    @Override
                    public void run() {

                    }
                });
            }
        });
    }

    /**
     * @param url:
     * @param callback:
     * @return void
     * @author Lee
     * @description 封装okHttp, Get异步请求
     * @date 2023/11/29 20:34
     */
    public void asyncGetRequest(String url, final Callback callback) {
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e(TAG, "Server Error");

            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                final String responseBody = response.body().string();
                handler.post(new Runnable() {
                    @Override
                    public void run() {

                    }
                });
            }
        });
    }



}




































