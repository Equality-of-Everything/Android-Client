package com.example.video;


import static com.example.android_client.LoginActivity.ip;
import static com.google.vr.cardboard.ThreadUtils.runOnUiThread;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.example.UI.camera.PreviewVideoActivity;
import com.example.android_client.MainActivity;
import com.example.util.Result;
import com.google.gson.Gson;

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
    public void uploadVideo(File videoFile, String city, Context context,String username,double latitude,double longitude){
        OkHttpClient client = new OkHttpClient();

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
               .addFormDataPart("video", videoFile.getName(),
                        RequestBody.create(MEDIA_TYPE_MP4, videoFile))
                .addFormDataPart("city",city)
                .addFormDataPart("username",username)
                .addFormDataPart("latitude",String.valueOf(latitude))
                .addFormDataPart("longitude",String.valueOf(longitude))
                .build();

        Request request = new Request.Builder()
               .url("http://"+ip+":8080/map/uploadVideo")
               .post(requestBody)
               .build();

        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context, "连接服务器失败！", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
                Gson json = new Gson();
                String res = response.body().string().trim();
                Log.e("uploadRed-Res", res);
                Result result = json.fromJson(res, Result.class);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (result.getFlag()) {
                            Toast.makeText(context, result.getMsg(), Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(context, MainActivity.class);
                            context.startActivity(intent);
                        }

                    }
                });


            }
        });
    }
}
