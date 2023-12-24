package com.example.entity;

import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.request.Request;
import com.bumptech.glide.request.target.SizeReadyCallback;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * @Author : Lee
 * @Date : Created in 2023/12/24 21:32
 * @Decription :
 */

public class ImageLoaderTarget implements Target {
    private CircleImageView imageView;

    public ImageLoaderTarget(CircleImageView imageView) {
        this.imageView = imageView;
    }

    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
        imageView.setImageDrawable(resource);
    }

    @Override
    public void onLoadCleared(@Nullable Drawable placeholder) {
        imageView.setImageDrawable(placeholder);
    }

    @Override
    public void getSize(@NonNull SizeReadyCallback cb) {

    }

    @Override
    public void removeCallback(@NonNull SizeReadyCallback cb) {

    }

    @Override
    public void setRequest(@Nullable Request request) {

    }

    @Nullable
    @Override
    public Request getRequest() {
        return null;
    }

    @Override
    public void onLoadStarted(@Nullable Drawable placeholder) {

    }

    @Override
    public void onLoadFailed(@Nullable Drawable errorDrawable) {
        imageView.setImageDrawable(errorDrawable);
    }

    @Override
    public void onResourceReady(@NonNull Object resource, @Nullable Transition transition) {

    }

    @Override
    public void onStart() {
        // 可选实现
    }

    @Override
    public void onStop() {
        // 可选实现
    }

    @Override
    public void onDestroy() {

    }

}
