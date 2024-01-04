package com.example.adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.media.MediaPlayer;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.UI.share.FriendCircleItem;
import com.example.UI.share.NineGridLayout;
import com.example.android_client.R;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * @Author : xcc
 * @Date : Created in 2023/12/20 15:13
 * @Decription :
 */

public class DynamicAdapter extends RecyclerView.Adapter<DynamicAdapter.DynamicViewHolder> {
    private List<FriendCircleItem> friendCircleItemList;
    private Context context;
    public DynamicAdapter(List<FriendCircleItem> friendCircleItemList,Context context) {
        this.friendCircleItemList = friendCircleItemList;
        this.context = context;
    }
    @NonNull
    @Override
    public DynamicViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.dynamic_item, parent, false);
        return new DynamicViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DynamicAdapter.DynamicViewHolder holder, int position) {
        FriendCircleItem friendCircleItem = friendCircleItemList.get(position);
        //设置头像
        Glide.with(holder.itemView.getContext())
                .load(friendCircleItem.getAvatarUrl())
                .into(holder.share_user_avatar);
        // 设置用户名和文本内容
        holder.share_user_name.setText(friendCircleItem.getUserName());
        holder.textDynamic.setText(friendCircleItem.getTextContent());
        //设置图片
        holder.gridLayout.setUrls(friendCircleItem.getMediaUrls());
        //设置视频
        if(friendCircleItem.getVideoUrl()!=null){
            holder.shareVideo.setVisibility(View.VISIBLE);
            holder.shareVideo.setVideoURI(Uri.parse(friendCircleItem.getVideoUrl()));
            holder.shareVideo.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                public void onPrepared(MediaPlayer mp) {
                    mp.setVolume(0f, 0f);
                    mp.start();
                }
            });
        }else {
            holder.shareVideo.setVisibility(View.GONE);
        }
        //设置发布时间
        Timestamp.valueOf(friendCircleItem.getPublishTime());
        holder.shareTime.setText(formatTimestamp(Timestamp.valueOf(friendCircleItem.getPublishTime()).getTime()));
    }

    /**
     * @param timestamp:
     * @return String
     * @author Lee
     * @description 时间数据的简单处理
     * @date 2023/12/11 20:47
     */
    private String formatTimestamp(long timestamp) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timestamp);
        Calendar currentCalendar = Calendar.getInstance();
        SimpleDateFormat dateFormat;

        if (calendar.get(Calendar.YEAR) == currentCalendar.get(Calendar.YEAR) &&
                calendar.get(Calendar.DAY_OF_YEAR) == currentCalendar.get(Calendar.DAY_OF_YEAR)) {
            // 当天的消息
            dateFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        } else {
            // 非当天的消息
            dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        }

        return dateFormat.format(calendar.getTime());
    }


    @Override
    public int getItemCount() {
        return friendCircleItemList.size();
    }

    public class DynamicViewHolder extends RecyclerView.ViewHolder {
        CircleImageView share_user_avatar;
        TextView share_user_name;
        TextView textDynamic;
        NineGridLayout gridLayout;
        TextView shareTime;
        VideoView shareVideo;
        Dialog videoDialog;
        public DynamicViewHolder(@NonNull View itemView) {
            super(itemView);
            share_user_avatar = itemView.findViewById(R.id.share_user_avatar);
            share_user_name = itemView.findViewById(R.id.share_user_name);
            textDynamic = itemView.findViewById(R.id.textDynamic);
            gridLayout = itemView.findViewById(R.id.gridLayout);
            shareTime = itemView.findViewById(R.id.share_time);
            shareVideo = itemView.findViewById(R.id.share_video);

            shareVideo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openVideoPreview(friendCircleItemList.get(getAdapterPosition()).getVideoUrl());
                }
            });
        }
        // 打开视频放大预览的方法
        private void openVideoPreview(String videoUrl) {
            // 如果视频正在播放，则停止播放
            if (shareVideo.isPlaying()) {
                shareVideo.stopPlayback();
            }
            // 使用 Dialog 进行视频预览
            videoDialog = new Dialog(itemView.getContext(), android.R.style.Theme_Black_NoTitleBar_Fullscreen);
            videoDialog.setContentView(R.layout.dialog_full_screen_video);

            // 在 Dialog 中找到 VideoView，并设置视频路径
            VideoView videoView = videoDialog.findViewById(R.id.fullScreenVideoView);
            videoView.setVideoURI(Uri.parse(videoUrl));

            // 设置点击监听器以关闭 Dialog 并停止视频播放
            videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    videoDialog.dismiss();
                    videoView.stopPlayback();
                }
            });

            // 显示 Dialog
            videoDialog.show();
            // 开始播放视频
            videoView.start();
        }
    }
}
