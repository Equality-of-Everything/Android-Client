package com.example.UI.camera;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;
import android.widget.VideoView;

import com.example.android_client.MainActivity;
import com.example.android_client.R;
import com.example.video.VideoUploader;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class PreviewVideoActivity extends AppCompatActivity {
    private VideoView videoPreview;
    private ImageButton saveLocal;
    private ImageButton videoPublish;
    private ImageButton backCamera;
    private String videoPath;
    private static final String TAG = "YourActivity";
    private Uri videoUri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview_video);
        videoPreview = findViewById(R.id.video_preview);
        saveLocal = findViewById(R.id.save_to_local);
        videoPublish = findViewById(R.id.video_publish);
        backCamera = findViewById(R.id.back_to_camera);
        // 获取从上一个活动传递过来的视频路径
        videoPath = getIntent().getStringExtra("videoPath");
        // 设置VideoView显示视频
        videoPreview.setVideoPath(videoPath);
        videoPreview.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                // 设置循环播放
                mp.setLooping(true);
            }
        });
        //开始播放
        videoPreview.start();
        saveLocal.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // 保存视频到本地
                saveVideoToMediaStore(videoPath);
                Toast.makeText(PreviewVideoActivity.this, "视频保存成功", Toast.LENGTH_SHORT).show();
            }
        });
        videoPublish.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // 执行发布视频操作
//                publishVideo(videoPath);
                Toast.makeText(PreviewVideoActivity.this, "视频发布成功", Toast.LENGTH_SHORT).show();
            }
        });
        backCamera.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // 返回到CameraActivity
                Intent resultIntent = new Intent();
                resultIntent.putExtra("returnToFragment", "CameraFragmentTAG");
                setResult(Activity.RESULT_OK, resultIntent);
                finish();
            }
        });
    }

    private void publishVideo(String videoPath) {
        File file = new File(videoPath);
        VideoUploader videoUploader = new VideoUploader();
        videoUploader.uploadVideo(file);
    }

    private void saveVideoToMediaStore(String videoPath) {
        File moviesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES);
        File videoFile = new File(videoPath);
        File destFile = new File(moviesDir, videoFile.getName());
        try {
            Files.copy(new File(videoPath).toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            // 视频保存成功后，发送广播通知媒体库更新
            MediaScannerConnection.scanFile(this, new String[] { destFile.getAbsolutePath() }, null, null);
            Log.d(TAG, "视频保存成功：" + destFile.getAbsolutePath());
        } catch (IOException e) {
            Log.e(TAG, "视频保存失败：" + e.getMessage(), e);
        }
    }
}