package com.example.UI.map;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.android_client.MainActivity;
import com.example.android_client.R;
//import com.google.vr.sdk.widgets.pano.VrPanoramaEventListener;
//import com.google.vr.sdk.widgets.pano.VrPanoramaView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public class Map_VRActivity extends AppCompatActivity {
    //vr控件
//    private VrPanoramaView vrpview;
    //byte格式
    private Bitmap bitmap = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_vractivity);
//        vrpview =(VrPanoramaView) findViewById(R.id.my_vr_view);
//        VrPanoramaView.Options options = new VrPanoramaView.Options();
//        options.inputType = VrPanoramaView.Options.TYPE_MONO;
//        //隐藏全屏模式
//        vrpview.setFullscreenButtonEnabled(false);
//        //隐藏信息按钮
//        vrpview.setInfoButtonEnabled(false);
        //
        String imageUrl =  getIntent().getStringExtra("imageUrl");

        Glide.with(this)
                .asBitmap()
                .load(imageUrl)
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        resource.compress(Bitmap.CompressFormat.PNG, 100, stream);
                        byte[] byteArray = stream.toByteArray();
//                        vrpview.loadImageFromByteArray(byteArray, options);
                    }
                });

//        vrpview.setEventListener(new VrPanoramaEventListener(){
//            @SuppressLint("ShowToast")
//            @Override
//            public void onLoadSuccess() {
//                //加载成功
//                super.onLoadSuccess();
////                Toast.makeText(Map_VRActivity.this, "加载完成", Toast.LENGTH_SHORT).show();
//                showToast(Map_VRActivity.this, "加载完成");
//            }
//
//            @SuppressLint("ShowToast")
//            @Override
//            public void onLoadError(String errorMessage) {
//                //加载失败
//                super.onLoadError(errorMessage);
////                Toast.makeText(Map_VRActivity.this, "加载失败", Toast.LENGTH_SHORT).show();
//                showToast(Map_VRActivity.this, "加载失败");
//
//            }
//
//            @SuppressLint("ShowToast")
//            @Override
//            public void onClick() {
//                //点击事件
//                super.onClick();
//
//            }
//
//            @SuppressLint("ShowToast")
//            @Override
//            public void onDisplayModeChanged(int newDisplayMode) {
//                //切换模式
//                super.onDisplayModeChanged(newDisplayMode);
////                Toast.makeText(Map_VRActivity.this, "改变显示模式", Toast.LENGTH_SHORT).show();
//                showToast(Map_VRActivity.this, "改变显示模式");
//            }
//        });
    }

//    private byte[] makeimageToByte(String path) {
//        byte[] data = null;
//        InputStream input = null;
//        ByteArrayOutputStream output = null;
//        try {
//            input = getAssets().open(path);
//            output = new ByteArrayOutputStream();
//            byte[] buf = new byte[1024];
//            int numBytesRead;
//            while ((numBytesRead = input.read(buf)) != -1) {
//                output.write(buf, 0, numBytesRead);
//            }
//            data = output.toByteArray();
//        } catch (IOException ex1) {
//            ex1.printStackTrace();
//        } finally {
//            try {
//                if (input != null) {
//                    input.close();
//                }
//                if (output != null) {
//                    output.close();
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//        return data;
//    }
    /**
     * @param :
     * @return void
     * @author xcc
     * @description 渲染3D
     * @date 2023/12/11 14:37
     */

//    @Override
//    protected void onResume() {
//        super.onResume();
//        if (vrpview != null){
//            vrpview.resumeRendering();
//        }
//    }
    /**
     * @param :
     * @return void
     * @author xcc
     * @description 暂停渲染
     * @date 2023/12/11 14:38
     */

//    @Override
//    protected void onPause() {
//        super.onPause();
//        if (vrpview != null){
//            vrpview.pauseRendering();
//        }
//    }
    /**
     * @param :
     * @return void
     * @author xcc
     * @description  释放资源
     * @date 2023/12/11 14:38
     */

//    @Override
//    protected void onDestroy() {
//
//        vrpview.shutdown();
//        if (bitmap != null && !bitmap.isRecycled()){
//            bitmap.recycle();
//            System.gc();
//        }
//        super.onDestroy();
//    }

    /**
     * @param context:
     * @param string:
     * @return void
     * @author zhang
     * @description 用于自定义Toast
     * @date 2023/12/13 15:13
     */
//    public void showToast(Context context, String string){
//        Toast toast = new Toast(context);
//        Display display = getWindowManager().getDefaultDisplay();
//        int height = display.getHeight();
//        //设置Toast显示位置，居中，向X,Y轴偏移量均为0
//        toast.setGravity(Gravity.CENTER,0,height/3);
//        //获取自定义视图
//        View view = LayoutInflater.from(context).inflate(R.layout.layout_toast,null);
//        TextView tvMessage = (TextView) view.findViewById(R.id.ErrorTips);
//        //设置文本
//        tvMessage.setText(string);
//        //设置视图
//        toast.setView(view);
//        //设置显示时长
//        toast.setDuration(Toast.LENGTH_SHORT);
//        //显示
//        toast.show();
//
//    }
}