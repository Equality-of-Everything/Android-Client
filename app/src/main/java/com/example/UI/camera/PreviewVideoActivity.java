package com.example.UI.camera;


import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;
import android.widget.VideoView;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.example.android_client.R;
import com.example.util.TokenManager;
import com.example.video.VideoUploader;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class PreviewVideoActivity extends AppCompatActivity {
    private VideoView videoPreview;
    private Button saveLocal;
    private Button videoPublish;
    private Button backCamera;
    private String videoPath;
    private static final String TAG = "YourActivity";
    private Uri videoUri;

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;

    @SuppressLint({"MissingInflatedId", "WrongViewCast"})
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
                applyLocationPermission();

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

    /**
     * @param :
     * @return void
     * @author zhang
     * @description 申请位置权限
     * @date 2023/12/20 15:25
     */
    private void applyLocationPermission() {
        // 请求位置权限
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // 如果已经有位置权限，直接获取位置
            getLocation();
        } else {
            // 请求位置权限
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @SuppressLint("MissingPermission")
    private void getLocation() {
        // 获取 LocationManager
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        // 检查是否启用了位置服务
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            // 注册位置监听器
            locationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER,
                    0,
                    0,
                    new LocationListener() {
                        @Override
                        public void onLocationChanged(Location location) {
                            // 获取位置信息
                            double latitude = location.getLatitude();
                            double longitude = location.getLongitude();

                            // 在这里使用获取到的经纬度信息，可以保存到视频的元数据中，或者上传到服务器等
                            Toast.makeText(PreviewVideoActivity.this, "Latitude: " + latitude + "\nLongitude: " + longitude, Toast.LENGTH_SHORT).show();
                            Log.e("Latitude", latitude+"");
                            Log.e("Longtitude", longitude+"");
                            getLocationFromLatLng(latitude, longitude);
                            // 停止位置更新，因为我们只需要一次位置信息
                            locationManager.removeUpdates(this);
                        }

                        @Override
                        public void onStatusChanged(String provider, int status, Bundle extras) {
                        }

                        @Override
                        public void onProviderEnabled(String provider) {
                        }

                        @Override
                        public void onProviderDisabled(String provider) {
                        }
                    }
            );
        } else {
            // 如果位置服务未启用，提示用户打开位置服务
            Toast.makeText(this, "请启用位置服务", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * @param latitude:
     * @param longitude:
     * @return void
     * @author Lee
     * @description 逆编码方法，经纬度转地理位置，并把经纬度，地理位置一起传到backLocation方法处理
     * @date 2023/12/1 16:18
     */
    public void getLocationFromLatLng(double latitude, double longitude) {
        // 创建一个GeoCoder实例
        GeoCoder geoCoder = GeoCoder.newInstance();

        // 设置逆地理编码查询结果的监听器
        geoCoder.setOnGetGeoCodeResultListener(new OnGetGeoCoderResultListener() {
            public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
                String address;
                if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
                    // 没有找到检索结果
                    address = null;
                }
                // 获取到逆地理编码结果
                address = result.getAddress();  // 获取地理位置的地址信息
                String city = null;
                if(address != null) {
                    city = address.substring(0, address.indexOf("市")+1);  // 获取地理位置所属的城市
                }
                // 在这里可以处理获取到的地理位置信息
                System.out.println("Address: " + address);
                System.out.println("City: " + city);
                // 执行发布视频操作
                publishVideo(videoPath,city);
            }

            public void onGetGeoCodeResult(GeoCodeResult result) {
                // 不需要处理正地理编码的结果，可以忽略
            }
        });

        // 创建一个经纬度对象
        LatLng latLng = new LatLng(latitude, longitude);
        // 发起逆地理编码请求
        geoCoder.reverseGeoCode(new ReverseGeoCodeOption().location(latLng));
        // 释放资源
        geoCoder.destroy();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 用户授予了位置权限，获取位置
                getLocation();
            } else {
                // 用户拒绝了位置权限，可以根据需要处理
                Toast.makeText(this, "未授予位置权限，无法获取位置信息", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void publishVideo(String videoPath,String city) {
        File file = new File(videoPath);
        VideoUploader videoUploader = new VideoUploader();
        videoUploader.uploadVideo(file,city,PreviewVideoActivity.this, TokenManager.getUserName(PreviewVideoActivity.this));
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