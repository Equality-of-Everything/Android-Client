package com.example.adapter;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android_client.R;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.ui.PlayerView;

import java.util.List;

/**
 * @Author : xcc
 * @Date : Created in 2023/12/6 12:06
 * @Decription : 自定义RecyclerView.Adapter类
 */

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.VideoViewHolder> {

    private List<String> videoPaths;
    private ExoPlayer player;
    private OnVideoChangedListener onVideoChangedListener;

    public VideoAdapter(List<String> videoPaths, ExoPlayer player) {
        this.videoPaths = videoPaths;
        this.player = player;
    }

    public void setOnVideoChangedListener(OnVideoChangedListener listener) {
        this.onVideoChangedListener = listener;
    }

    public String getVideoPath(int position) {
        return videoPaths.get(position);
    }


    @NonNull
    @Override
    public VideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_map_video, parent, false);
        return new VideoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VideoViewHolder holder, int position) {
        String videoPath = videoPaths.get(position);
        MediaItem mediaItem = MediaItem.fromUri(Uri.parse(videoPath));

        holder.playerView.setPlayer(player);
        holder.playerView.setUseController(true);
        holder.playerView.requestFocus();

        player.setMediaItem(mediaItem);

        player.addListener(new Player.Listener() {
            @Override
            public void onPlaybackStateChanged(int state) {
                if (state == Player.STATE_ENDED) {
                    if (onVideoChangedListener != null) {
                        onVideoChangedListener.onVideoChanged(position);
                    }
                }
            }
        });

        player.prepare();
    }

    @Override
    public void onViewRecycled(@NonNull VideoViewHolder holder) {
        super.onViewRecycled(holder);
        player.stop();
        player.clearMediaItems();
    }

    @Override
    public int getItemCount() {
        return videoPaths.size();
    }

    public interface OnVideoChangedListener {
        void onVideoChanged(int position);
    }

    public static class VideoViewHolder extends RecyclerView.ViewHolder {

        PlayerView playerView;

        public VideoViewHolder(@NonNull View itemView) {
            super(itemView);
            playerView = itemView.findViewById(R.id.map_video);
        }
    }
}
