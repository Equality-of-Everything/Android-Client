package com.example.video;

import static com.example.android_client.LoginActivity.ip;

import java.io.File;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * @Author : xcc
 * @Date : Created in 2023/12/20 9:42
 * @Decription :
 */

public class VideoUploader {
    private static final MediaType MEDIA_TYPE_MP4 = MediaType.parse("video/mp4");
    public void uploadVideo(File videoFile){
        OkHttpClient client = new OkHttpClient();

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
               .addFormDataPart("video", videoFile.getName(),
                        RequestBody.create(MEDIA_TYPE_MP4, videoFile))
               .build();

        Request request = new Request.Builder()
               .url("http://"+ip+":8080/userInfo/setUserAvatar")
               .post(requestBody)
               .build();

        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
                if (response.isSuccessful()) {
                    System.out.println("upload success");
                } else {
                    System.out.println("upload failed");
                }
            }
        });
    }
}
