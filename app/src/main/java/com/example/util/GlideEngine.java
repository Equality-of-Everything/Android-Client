package com.example.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.android_client.R;
import com.luck.picture.lib.engine.ImageEngine;
import com.luck.picture.lib.utils.ActivityCompatHelper;

/**
 * @Author : 你的名字
 * @Date : Created in 2023/12/22 22:27
 * @Decription :
 */

public class GlideEngine implements ImageEngine {

    /**
     * 加载图片
     *
     * @param context   上下文
     * @param url       资源url
     * @param imageView 图片承载控件
     */
    @Override
    public void loadImage(Context context, String url, ImageView imageView) {
        if (!ActivityCompatHelper.assertValidRequest(context)) {
            return;
        }
        Glide.with(context)
                .load(url)
                .into(imageView);
    }

    @Override
    public void loadImage(Context context, ImageView imageView, String url, int maxWidth, int maxHeight) {
        if (!ActivityCompatHelper.assertValidRequest(context)) {
            return;
        }
        Glide.with(context)
                .load(url)
                .override(maxWidth, maxHeight)
                .into(imageView);
    }

    /**
     * 加载相册目录封面
     *
     * @param context   上下文
     * @param url       图片路径
     * @param imageView 承载图片ImageView
     */
    @Override
    public void loadAlbumCover(Context context, String url, ImageView imageView) {
        if (!ActivityCompatHelper.assertValidRequest(context)) {
            return;
        }
        Glide.with(context)
                .asBitmap()
                .load(url)
                .override(180, 180)
                .sizeMultiplier(0.5f)
                .transform(new CenterCrop(), new RoundedCorners(8))
                .placeholder(R.drawable.error)
                .into(imageView);
    }


    /**
     * 加载图片列表图片
     *
     * @param context   上下文
     * @param url       图片路径
     * @param imageView 承载图片ImageView
     */
    @Override
    public void loadGridImage(Context context, String url, ImageView imageView) {
        if (!ActivityCompatHelper.assertValidRequest(context)) {
            return;
        }
        Glide.with(context)
                .load(url)
                .override(200, 200)
                .centerCrop()
                .placeholder(R.drawable.error)
                .into(imageView);
    }

    @Override
    public void pauseRequests(Context context) {
        if (!ActivityCompatHelper.assertValidRequest(context)) {
            return;
        }
        Glide.with(context).pauseRequests();
    }

    @Override
    public void resumeRequests(Context context) {
        if (!ActivityCompatHelper.assertValidRequest(context)) {
            return;
        }
        Glide.with(context).resumeRequests();
    }

    private GlideEngine() {
    }

    private static final class InstanceHolder {
        static final GlideEngine instance = new GlideEngine();
    }

    public static GlideEngine createGlideEngine() {
        return InstanceHolder.instance;
    }
}
