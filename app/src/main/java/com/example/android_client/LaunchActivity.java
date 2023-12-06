package com.example.android_client;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.vectordrawable.graphics.drawable.Animatable2Compat;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

public class LaunchActivity extends AppCompatActivity {
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);

        imageView = findViewById(R.id.iv_img);

        init();
    }

    /**
     * @param :
     * @return void
     * @author Lee
     * @description 加载开屏动画
     * @date 2023/12/6 14:38
     */
    public void init() {
        //显示本地图片
        Glide.with(this)
                .asGif()
                .load(R.drawable.ic_launch)//显示raw目录中的图片
                .listener(new RequestListener<GifDrawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<GifDrawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GifDrawable resource, Object model, Target<GifDrawable> target, DataSource dataSource, boolean isFirstResource) {
                        resource.setLoopCount(1); // 设置gif循环播放次数，这里设置为1，播放一次后停止
                        resource.registerAnimationCallback(new Animatable2Compat.AnimationCallback() {
                            @Override
                            public void onAnimationEnd(Drawable drawable) {
                                // 在播放完毕后执行操作
                                Intent intent = new Intent(LaunchActivity.this, LoginActivity.class);
                                startActivity(intent);
                            }
                        });
                        return false;
                    }
                })
                .centerCrop()
                .into(imageView);
    }
}