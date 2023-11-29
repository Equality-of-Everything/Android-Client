package com.example.util;

import java.io.IOException;

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

    private static final OkHttpClient client = new OkHttpClient();

    /**
     * @param url:
     * @param json:
     * @return String
     * @author Lee
     * @description 封装OkHttp的网络请求功能
     * @date 2023/11/29 14:08
     */
    public static String sendJsonPostRequest(String url, String json) throws IOException {
        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        }
    }

}




































