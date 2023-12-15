package com.example.UI.camera;
import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.DisplayMetrics;

import android.util.Rational;
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
import androidx.camera.core.CameraProvider;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.core.VideoCapture;
import androidx.camera.core.ZoomState;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;

import com.example.android_client.R;
import com.google.common.util.concurrent.ListenableFuture;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

import java.util.Date;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @Auther : xcc
 * @Date : Create in 2023/12/13 14:18
 * @Decription:
 */
public class CameraFragment extends Fragment {
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 100;
    private PreviewView cameraView;
    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    private Camera camera;
    private CameraControl cameraControl;
    private ScaleGestureDetector scaleGestureDetector;
    private float maxZoomRatio;
    private ImageCapture imageCapture;
    private VideoCapture videoCapture;
    private ExecutorService cameraExecutor;
    private ImageButton camera_shot;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View CameraView = inflater.inflate(R.layout.fragment_camera, container, false);
        cameraView = CameraView.findViewById(R.id.camera_view);
        camera_shot = CameraView.findViewById(R.id.camera_shot_btn);
        camera_shot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePhoto();
                Toast.makeText(requireContext(), "拍照", Toast.LENGTH_SHORT).show();

            }
        });
        //获取 CameraProvider
        cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext());
        //手势操作
        scaleGestureDetector = new ScaleGestureDetector(requireContext(), new ScaleListener());
        cameraExecutor = Executors.newSingleThreadExecutor();
        //检查 CameraProvider 可用性
        cameraProviderFuture.addListener(() -> {
            try {
                if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
                } else {
                    initializeCamera(); // 如果已经有权限，直接初始化相机
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, ContextCompat.getMainExecutor(getContext()));

        return CameraView;
        }


    /**
     * @param requestCode:
     * @param permissions:
     * @param grantResults:
     * @return void
     * @author xcc
     * @description 处理权限请求结果
     * @date 2023/12/13 19:15
     */

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initializeCamera(); // 用户授予了相机权限，初始化相机
            } else {
                Toast.makeText(requireContext(), "我们需要相机权限来拍摄照片以便上传到您的社交媒体账户", Toast.LENGTH_LONG).show();
            }
        }
    }

    /**
     * @param :
     * @return void
     * @author xcc
     * @description 初始化相机
     * @date 2023/12/13 19:15
     */
    private void initializeCamera() {
        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                bindCameraView(cameraProvider);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, ContextCompat.getMainExecutor(requireContext()));
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
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getRealMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels;
        Size screenSize = new Size(width, height);

        Preview.Builder previewBuilder = new Preview.Builder()
                .setTargetResolution(new Size(height, height))
                .setTargetRotation(cameraView.getDisplay().getRotation());

        Preview preview = previewBuilder.build();

        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build();
        preview.setSurfaceProvider(cameraView.getSurfaceProvider());
        // 实例化 ImageCapture 用例
        imageCapture = new ImageCapture.Builder()
                .setTargetRotation(cameraView.getDisplay().getRotation())
                .build();
        cameraProvider.unbindAll();
        //获取相机的控制器
        Camera camera = cameraProvider.bindToLifecycle((LifecycleOwner) this, cameraSelector, preview);
        cameraControl = camera.getCameraControl();
        // 获取最大缩放比例
        maxZoomRatio = camera.getCameraInfo().getZoomState().getValue().getMaxZoomRatio();
        cameraView.setOnTouchListener((view, event) -> {
            scaleGestureDetector.onTouchEvent(event);
            return true;
        });
    }
    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        /**
         * @param detector:
         * @return boolean
         * @author xcc
         * @description 相机缩放
         * @date 2023/12/15 14:09
         */

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
    // 拍照方法
    private void takePhoto() {
        // 创建唯一的文件名
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";

        // 在应用的外部存储目录中创建一个唯一的文件
        File storageDir =  requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File photoFile = new File(storageDir, imageFileName + ".jpg");
        // 设置输出选项
        ImageCapture.OutputFileOptions outputFileOptions =
                new ImageCapture.OutputFileOptions.Builder(photoFile).build();

        // 拍照
        imageCapture.takePicture(outputFileOptions, cameraExecutor,
                new ImageCapture.OnImageSavedCallback() {
                    @Override
                    public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                        // 图片保存成功后，将照片保存到系统相册
                        savePhotoToGallery(photoFile);
                    }
                    @Override
                    public void onError(@NonNull ImageCaptureException exception) {
                        // 图片保存出错
                        exception.printStackTrace();
                    }
                });
    }
//保存到本地相册
    private void savePhotoToGallery(File photoFile) {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "Photo");
        values.put(MediaStore.Images.Media.DESCRIPTION, "From Camera");
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        values.put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis() / 1000);
        values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());

        ContentResolver contentResolver = requireContext().getContentResolver();
        Uri galleryUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        Uri imageUri = contentResolver.insert(galleryUri, values);

        try {
            OutputStream outputStream = contentResolver.openOutputStream(Objects.requireNonNull(imageUri));
            if (outputStream != null) {
                FileInputStream fileInputStream = new FileInputStream(photoFile);
                byte[] buffer = new byte[1024];
                int length;
                while ((length = fileInputStream.read(buffer)) > 0) {
                    outputStream.write(buffer, 0, length);
                }
                fileInputStream.close();
                outputStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 发送广播通知系统相册有新照片
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        mediaScanIntent.setData(imageUri);
        requireContext().sendBroadcast(mediaScanIntent);
    }

//    // 获取文件输出目录
//    private File getOutputDirectory() {
//        File mediaDir = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
//        File outputDir = new File(mediaDir, "camera");
//        if (!outputDir.exists()) {
//            outputDir.mkdirs();
//        }
//        return outputDir;
//    }

    public void onPause() {
        super.onPause();
        cameraProviderFuture.addListener(() -> {
            try {
                cameraProviderFuture.get().unbindAll();
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }, ContextCompat.getMainExecutor(requireContext()));
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        cameraProviderFuture.addListener(() -> {
            try {
                cameraProviderFuture.get().unbindAll();
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }, ContextCompat.getMainExecutor(requireContext()));
    }
}