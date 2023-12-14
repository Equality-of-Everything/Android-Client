package com.example.UI.camera;
import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.DisplayMetrics;

import android.util.Rational;
import android.util.Size;
import android.view.LayoutInflater;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.camera.core.AspectRatio;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraControl;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.Preview;
import androidx.camera.core.ZoomState;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;

import com.example.android_client.R;
import com.google.common.util.concurrent.ListenableFuture;

import java.util.concurrent.ExecutionException;

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
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View CameraView = inflater.inflate(R.layout.fragment_camera, container, false);
        cameraView = CameraView.findViewById(R.id.camera_view);
        //获取 CameraProvider
        cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext());
        //手势操作
        scaleGestureDetector = new ScaleGestureDetector(requireContext(), new ScaleListener());
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

        Preview preview = previewBuilder
                .build();

        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build();
        preview.setSurfaceProvider(cameraView.getSurfaceProvider());
            //获取相机的控制器
        Camera camera = cameraProvider.bindToLifecycle((LifecycleOwner)this, cameraSelector, preview);
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