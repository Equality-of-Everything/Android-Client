package com.example.UI.camera;
import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.DisplayMetrics;

import android.util.Size;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;

import com.example.android_client.R;
import com.google.common.util.concurrent.ListenableFuture;

/**
 * @Auther : xcc
 * @Date : Create in 2023/12/13 14:18
 * @Decription:
 */
public class CameraFragment extends Fragment {
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 100;
    private PreviewView cameraView;
    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View CameraView = inflater.inflate(R.layout.fragment_camera, container, false);
        cameraView = CameraView.findViewById(R.id.camera_view);
        //获取 CameraProvider
        cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext());
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
        Size screenSize = new Size(displayMetrics.widthPixels, displayMetrics.heightPixels);
//        //自定义
//        int screenWidth = displayMetrics.widthPixels;
//        int screenHeight = displayMetrics.heightPixels;
//        // 设置自定义预览宽高比（例如：16:9）
//        float aspectRatio = 4.0f / 3.0f;
//        // 计算预览大小以匹配指定的宽高比
//        int previewWidth = (int) Math.floor(screenWidth * aspectRatio);
//        int previewHeight = (int) Math.floor(screenHeight * aspectRatio);
// 设置预览的宽高比和分辨率
        Preview.Builder previewBuilder = new Preview.Builder()
                .setTargetResolution(screenSize)
                .setTargetRotation(cameraView.getDisplay().getRotation());

        Preview preview = previewBuilder.build();

        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build();

        preview.setSurfaceProvider(cameraView.getSurfaceProvider());

        Camera camera = cameraProvider.bindToLifecycle((LifecycleOwner)this, cameraSelector, preview);
    }
}