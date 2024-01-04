package com.example.UI.share;

import static com.baidu.mapapi.BMapManager.init;

import android.app.Dialog;
import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageView;

import android.widget.VideoView;
import com.bumptech.glide.Glide;
import com.example.android_client.R;
import com.github.chrisbanes.photoview.PhotoView;


import java.util.List;

/**
 * @Author : xcc
 * @Date : Created in 2023/12/21 13:19
 * @Decription :
 */

public class NineGridLayout extends GridLayout {
    private static final int MAX_CHILD_COUNT = 9;
    private List<String> urls;

    public NineGridLayout(Context context) {
        super(context);
        init();
    }

    public NineGridLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public void setUrls(List<String> urls) {
        this.urls = urls;
        if (urls == null || urls.size() == 0) {
            setVisibility(GONE);
            return;
        }
        setVisibility(VISIBLE);

        removeAllViews();
        int size = urls.size();
        int rows, columns;

        if (size == 1) {
            rows = columns = 1;
        } else if (size == 3 || size == 4 || size == 2) {
            rows = columns = 2;
        } else {
            rows = columns = 3;
            if (size > MAX_CHILD_COUNT) {
                size = MAX_CHILD_COUNT;
            }
        }
        setRowCount(rows);
        setColumnCount(columns);
        for (int i = 0; i < size; i++) {
            final int position = i;
            ImageView imageView = new ImageView(getContext());
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = 0;
            params.height = LayoutParams.WRAP_CONTENT;
            params.setMargins(dpToPx(2), dpToPx(2), dpToPx(2), dpToPx(2));
            params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
            params.rowSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
            imageView.setLayoutParams(params);

            // 加载图片，你可以使用 Glide 或其他图片加载库
            Glide.with(imageView)
                    .load(urls.get(i))
                    .into(imageView);
            imageView.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    openImagePreview(urls, position);
                }
            });
            addView(imageView);
//            // 检查图片数量，如果为零，则隐藏 NineGridLayout
//            if (getChildCount() == 0) {
//                setVisibility(View.GONE);
//            } else {
//                setVisibility(View.VISIBLE);
//            }
        }
    }
    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }

    /**
     * @param urls:
     * @param position:
     * @return void
     * @author xcc
     * @description 打开图片预览
     * @date 2023/12/27 8:41
     */
    private void openImagePreview(List<String> urls, int position) {
        String url = urls.get(position);

//        // 判断是否是视频
//        boolean isVideo = isVideoUrl(url);
//        Dialog videoDialog;
//        if (isVideo) {
//            // 使用 Dialog 进行视频预览
//            videoDialog = new Dialog(getContext(), android.R.style.Theme_Black_NoTitleBar_Fullscreen);
//            videoDialog.setContentView(R.layout.dialog_full_screen_video);
//            // 在 Dialog 中找到 VideoView，并设置视频路径
//            VideoView videoView = videoDialog.findViewById(R.id.fullScreenVideoView);
//            videoView.setVideoURI(Uri.parse(url));
//            // 设置点击监听器以关闭 Dialog 并停止视频播放
//            videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//                @Override
//                public void onCompletion(MediaPlayer mp) {
//                    videoDialog.dismiss();
//                    videoView.stopPlayback();
//                }
//            });
//            // 显示 Dialog
//            videoDialog.show();
//            // 开始播放视频
//            videoView.start();
//        } else {
            // 使用 PhotoView 库进行图片预览
            PhotoView photoView = new PhotoView(getContext());

            // 使用 Glide 加载图片
            Glide.with(getContext())
                    .load(url)
                    .into(photoView);

            // 显示图片
            Dialog dialog = new Dialog(getContext(), android.R.style.Theme_Black_NoTitleBar_Fullscreen);
            dialog.setContentView(R.layout.dialog_full_screen_image);
            // 在 Dialog 中找到 PhotoView，并设置图片
            photoView = dialog.findViewById(R.id.fullScreenImageView);
            Glide.with(getContext())
                    .load(url)
                    .into(photoView);
            // 设置点击监听器以关闭 Dialog
            photoView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            // 显示 Dialog
            dialog.show();
        }
//    }

    private boolean isVideoUrl(String url) {
        // 判断逻辑，根据URL的后缀或其他特征判断是否是视频
        return url.toLowerCase().endsWith(".mp4");
    }

}