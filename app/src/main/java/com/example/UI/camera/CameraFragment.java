package com.example.UI.camera;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;

import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.icu.text.SimpleDateFormat;

import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;

import android.util.Log;
import android.util.Size;
import android.view.LayoutInflater;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraControl;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.Preview;
import androidx.camera.core.VideoCapture;
import androidx.camera.core.ZoomState;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;

import com.example.android_client.R;
import com.google.common.util.concurrent.ListenableFuture;

import java.io.File;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @Auther : xcc
 * @Date : Create in 2023/12/13 14:18
 * @Decription:
 */
public class CameraFragment extends Fragment {
    private static final String TAG = "YourActivity";
    //权限码
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 100;
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 101;
    private PreviewView cameraView;
    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    private Camera camera;
    private CameraControl cameraControl;
    private ScaleGestureDetector scaleGestureDetector;
    private float maxZoomRatio;
    private ImageButton camera_shot;
    private ExecutorService cameraExecutor;
    private VideoCapture videoCapture;
    private String videoOutputPath;
    private boolean isRecording = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View CameraView = inflater.inflate(R.layout.fragment_camera, container, false);
        cameraView = CameraView.findViewById(R.id.camera_view);
        camera_shot = CameraView.findViewById(R.id.camera_shot_btn);
        //获取 CameraProvider
        cameraProviderFuture = ProcessCameraProvider.getInstance(getContext());
        //手势操作
        scaleGestureDetector = new ScaleGestureDetector(getContext(), new ScaleListener());
        cameraExecutor = Executors.newSingleThreadExecutor();
        //检查 CameraProvider 可用性
        cameraProviderFuture.addListener(new Runnable() {
            @Override
            public void run() {
                try {
                    if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                            ContextCompat.checkSelfPermission(getContext(), Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                        // 如果相机或录音权限未被授予，则请求权限
                        ActivityCompat.requestPermissions((Activity) getContext(),
                                new String[]{Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO}, CAMERA_PERMISSION_REQUEST_CODE);
                    } else {
                        initializeCamera(); // 如果已经有权限，直接初始化相机
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, ContextCompat.getMainExecutor(getContext()));
        camera_shot.setOnClickListener(v -> {
            if (isRecording) {
                stopRecordingVideo();
                Toast.makeText(getContext(), "视频录制已结束", Toast.LENGTH_SHORT).show();
                isRecording = false;
            } else {
                startRecordingVideo();
                Toast.makeText(getContext(), "视频录制已开始", Toast.LENGTH_SHORT).show();
                isRecording = true;
            }
        });
        return CameraView;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            boolean cameraPermissionGranted = grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED;
            boolean recordAudioPermissionGranted = grantResults.length > 1 && grantResults[1] == PackageManager.PERMISSION_GRANTED;

            if (cameraPermissionGranted && recordAudioPermissionGranted) {
                // 相机和录音权限已被授予，执行相应操作
                initializeCamera();
            } else {
                // 相机或录音权限被拒绝，给出相应的提示
                Toast.makeText(getContext(), "相机或录音权限被拒绝", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void initializeCamera() {
        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                bindCameraView(cameraProvider);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, ContextCompat.getMainExecutor(getContext()));
    }

    /**
     * @param cameraProvider:
     * @return void
     * @author xcc
     * @description 选择相机并绑定生命周期和用例
     * @date 2023/12/13 19:23
     */
    @SuppressLint("RestrictedApi")
    private void bindCameraView(ProcessCameraProvider cameraProvider) {
        // 在Fragment中获取屏幕尺寸和窗口管理器
        int width = 1920;
        int height = 1080;
        Size screenSize = new Size(width, height);
        Preview preview = new Preview.Builder()
                .setTargetResolution(screenSize)
                .setTargetRotation(cameraView.getDisplay().getRotation())
                .build();
        String firstCameraId = getFirstCameraId();
        if (firstCameraId != null) {
            CameraSelector cameraSelector = new CameraSelector.Builder()
                    .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                    .build();
            // 设置预览界面
            preview.setSurfaceProvider(cameraView.getSurfaceProvider());
            // 实例化 videoCapture 用例
            videoCapture = new VideoCapture.Builder()
                    .setTargetRotation(cameraView.getDisplay().getRotation())
                    .setVideoFrameRate(36)//每秒的帧数
                    .setBitRate(3 * 1024 * 1024)
                    .build();
            ListenableFuture<ProcessCameraProvider> cameraProviderFuture =
                    ProcessCameraProvider.getInstance(getContext());
            try {
                cameraProvider= cameraProviderFuture.get();
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            cameraProvider.unbindAll();
            // 绑定相机生命周期
            camera = cameraProvider.bindToLifecycle((LifecycleOwner) this, cameraSelector, preview, videoCapture);
            // 获取相机的控制器
            cameraControl = camera.getCameraControl();
            // 获取最大缩放比例
            maxZoomRatio = camera.getCameraInfo().getZoomState().getValue().getMaxZoomRatio();
            cameraView.setOnTouchListener((view, event) -> {
                scaleGestureDetector.onTouchEvent(event);
                return true;
            });
        } else {
            Toast.makeText(getContext(), "无法找到可用的摄像头", Toast.LENGTH_SHORT).show();
        }
    }

    private String getFirstCameraId() {
        Context context = requireActivity();
        CameraManager cameraManager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
        try {
            // 获取所有可用摄像头的ID列表
            String[] cameraIds = cameraManager.getCameraIdList();
            if (cameraIds.length > 0) {

                Log.e(TAG, "找到了");
                return cameraIds[0];
            } else {
                Log.e(TAG, "没有能用的摄像头，大傻春");
            }
        } catch (CameraAccessException e) {
            Log.e(TAG, "Failed to access camera manager", e);
        }
        return null;
    }

    /**
     * @param
     * @return null
     * @author xcc
     * @description 缩放
     * @date 2023/12/15 15:07
     */

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            float scaleFactor = detector.getScaleFactor();
            ZoomState zoomState = camera.getCameraInfo().getZoomState().getValue();
            float currentZoomRatio = zoomState.getZoomRatio();
            float maxZoomRatio = zoomState.getMaxZoomRatio();
            float newZoomRatio = currentZoomRatio * scaleFactor;
            // 限制缩放比例在有效范围内
            newZoomRatio = Math.max(1.0f, Math.min(newZoomRatio, maxZoomRatio));
            cameraControl.setZoomRatio(newZoomRatio);
            return true;
        }
    }


    @SuppressLint("RestrictedApi")
    private void startRecordingVideo() {
        if (videoCapture != null && !isRecording&& camera != null) {

            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                // 请求录音权限
                ActivityCompat.requestPermissions((Activity) getContext(),
                        new String[]{Manifest.permission.RECORD_AUDIO}, REQUEST_RECORD_AUDIO_PERMISSION);
                return;
            }
            // 创建视频保存的文件地址
            File mediaDir = requireActivity().getExternalFilesDir(Environment.DIRECTORY_MOVIES);
            if (mediaDir == null) {
                Log.e(TAG, "无法获取到外部存储目录");
                return;
            }else {
                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
                File file = new File(mediaDir, "VID_" + timeStamp + ".mp4");
                VideoCapture.OutputFileOptions outputFileOptions =
                        new VideoCapture.OutputFileOptions.Builder(file).build();
                videoCapture.startRecording(outputFileOptions, Executors.newSingleThreadExecutor(), new VideoCapture.OnVideoSavedCallback() {
                    @Override
                    public void onVideoSaved(@NonNull VideoCapture.OutputFileResults outputFileResults) {
                        videoOutputPath=file.getAbsolutePath();

                        saveVideoToMediaStore(videoOutputPath);
                        Log.d(TAG, "视频录制成功：" + videoOutputPath);
                    }

                    @Override
                    public void onError(int videoCaptureError, @NonNull String message, @Nullable Throwable cause) {
                        Log.e(TAG, "视频录制失败：" + message, cause);
                    }
                });
            }
        }
    }
    @SuppressLint("RestrictedApi")
    private void stopRecordingVideo() {
        if (videoCapture != null && isRecording) {
            videoCapture.stopRecording();
        }else {
            Log.e(TAG, "相机未准备就绪，无法开始录制视频");
        }
    }
//    private String getOutputFilePath() {
//        File mediaDir = requireActivity().getExternalFilesDir(Environment.DIRECTORY_MOVIES);
//        if (mediaDir != null) {
//            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
//            return mediaDir.getAbsolutePath() + File.separator + "VID_" + timeStamp + ".mp4";
//        } else {
//            return null;
//        }
//    }
    private void saveVideoToMediaStore(String videoPath) {
        File moviesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES);

        File videoFile = new File(videoPath);
        File destFile = new File(moviesDir, videoFile.getName());
        try {
            Files.copy(new File(videoPath).toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            // 视频保存成功后，发送广播通知媒体库更新
            MediaScannerConnection.scanFile(getContext(), new String[] { destFile.getAbsolutePath() }, null, null);
            Log.d(TAG, "视频保存成功：" + destFile.getAbsolutePath());
        } catch (IOException e) {
            Log.e(TAG, "视频保存失败：" + e.getMessage(), e);
        }
    }



    @Override
    public void onResume() {
        super.onResume();
        initializeCamera();
    }

    private void releaseCamera() {
        if (cameraProviderFuture != null) {
            cameraProviderFuture.addListener(() -> {
                try {
                    ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                    cameraProvider.unbindAll();
                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }
            }, ContextCompat.getMainExecutor(getContext()));
        }
    }
    public void onDestroy() {
        super.onDestroy();
        releaseCamera();
    }
}