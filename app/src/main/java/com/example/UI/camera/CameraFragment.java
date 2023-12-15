package com.example.UI.camera;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.icu.text.SimpleDateFormat;
import android.location.Location;
import android.location.LocationManager;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
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
import androidx.camera.core.ExperimentalGetImage;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
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
import java.io.OutputStream;

import java.nio.ByteBuffer;
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
    private PreviewView cameraView;
    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    private Camera camera;
    private CameraControl cameraControl;
    private ScaleGestureDetector scaleGestureDetector;
    private float maxZoomRatio;
    private ImageButton camera_shot;
    private ExecutorService cameraExecutor;
    private ImageCapture imageCapture;

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
                    if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        // 如果相机权限未被授予，则请求权限
                        ActivityCompat.requestPermissions((Activity) getContext(),
                                new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
                    } else {
                        initializeCamera(); // 如果已经有权限，直接初始化相机
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }, ContextCompat.getMainExecutor(getContext()));
        camera_shot.setOnClickListener(v -> {
            // 调用拍照功能
            takePhoto();
        });

        return CameraView;
        }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 相机权限已被授予，执行相应操作
                initializeCamera();
            } else {
                // 相机权限被拒绝，给出相应的提示
                Toast.makeText(getContext(), "相机权限被拒绝", Toast.LENGTH_SHORT).show();
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
            // 实例化 ImageCapture 用例
            imageCapture = new ImageCapture.Builder()
                    .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                    .setTargetRotation(cameraView.getDisplay().getRotation())
                    .build();
            // 绑定相机生命周期
            camera = cameraProvider.bindToLifecycle((LifecycleOwner)this, cameraSelector, preview,imageCapture);
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
                Log.e(TAG, "没有能用的摄像头，大傻春");Log.e(TAG, "没有能用的摄像头，大傻春");
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
     * @description  缩放
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
    private void takePhoto() {

        if (imageCapture != null) {
            Executor mainExecutor = ContextCompat.getMainExecutor(getContext());
            imageCapture.takePicture(mainExecutor, new ImageCapture.OnImageCapturedCallback() {
                @Override
                public void  onCaptureSuccess(@NonNull ImageProxy image) {
                    super.onCaptureSuccess(image);

                    Toast.makeText(getContext(), "拍照成功", Toast.LENGTH_SHORT).show();
                    // 拍照成功后的处理
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
                    String currentTime = sdf.format(new Date());
                    String locationInfo = getLocationInfo();
                    // 在拍照成功后执行保存到媒体库的操作
                    ContentValues values = new ContentValues();
                    values.put(MediaStore.Images.Media.TITLE, "Image_" + currentTime + "_" + locationInfo);
                    values.put(MediaStore.Images.Media.DESCRIPTION, "My Image Description");
                    values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
                    Uri uri = requireActivity().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                    saveImageToMediaStore(uri, image);
                }

                @Override
                public void onError(@NonNull ImageCaptureException exception) {
                    super.onError(exception);
                    // 拍照出错时的处理
                    Toast.makeText(getContext(), "拍照失败", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void saveImageToMediaStore(Uri uri, ImageProxy image) {
        try {
            OutputStream outputStream = requireActivity().getContentResolver().openOutputStream(uri);
            @ExperimentalGetImage
            Image myImage = image.getImage();
            ByteBuffer buffer = image.getPlanes()[0].getBuffer();
            byte[] bytes = new byte[buffer.remaining()];
            buffer.get(bytes);
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            // 这里假设你有一个名为bitmap的Bitmap对象，用于保存拍摄的图像
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Toast.makeText(getContext(), "保存成功", Toast.LENGTH_SHORT).show();
        image.close(); // 释放Image资源
    }

    private String getLocationInfo() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            LocationManager locationManager = (LocationManager) requireContext().getSystemService(Context.LOCATION_SERVICE);
            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (location!= null) {
                return "Latitude: " + location.getLatitude() + " Longitude: " + location.getLongitude();
            }
            return "Latitude: " + 0 + " Longitude: " + 0;}
        return null;
    }

    // 处理相机资源的生命周期方法
    @Override
    public void onPause() {
        super.onPause();
        releaseCamera(); // 当片段暂停时释放相机
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


}