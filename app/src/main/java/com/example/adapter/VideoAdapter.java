package com.example.adapter;

import static com.example.android_client.LoginActivity.ip;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.SurfaceTexture;
import android.media.AudioDeviceInfo;
import android.net.Uri;
import android.os.Looper;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.CompoundButtonCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.UI.map.Map_VRActivity;
import com.example.android_client.R;
import com.example.util.Code;
import com.example.util.Result;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DeviceInfo;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.MediaMetadata;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.PlayerMessage;
import com.google.android.exoplayer2.Renderer;
import com.google.android.exoplayer2.SeekParameters;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.Tracks;
import com.google.android.exoplayer2.analytics.AnalyticsCollector;
import com.google.android.exoplayer2.analytics.AnalyticsListener;
import com.google.android.exoplayer2.audio.AudioAttributes;
import com.google.android.exoplayer2.audio.AuxEffectInfo;
import com.google.android.exoplayer2.decoder.DecoderCounters;
import com.google.android.exoplayer2.source.DefaultMediaSourceFactory;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.source.ShuffleOrder;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.text.CueGroup;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelectionParameters;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource;
import com.google.android.exoplayer2.upstream.cache.Cache;
import com.google.android.exoplayer2.upstream.cache.CacheDataSource;
import com.google.android.exoplayer2.upstream.cache.LeastRecentlyUsedCacheEvictor;
import com.google.android.exoplayer2.upstream.cache.SimpleCache;
import com.google.android.exoplayer2.util.Clock;
import com.google.android.exoplayer2.util.Effect;
import com.google.android.exoplayer2.util.PriorityTaskManager;
import com.google.android.exoplayer2.util.Size;
import com.google.android.exoplayer2.util.Util;
import com.google.android.exoplayer2.video.VideoFrameMetadataListener;
import com.google.android.exoplayer2.video.VideoSize;
import com.google.android.exoplayer2.video.spherical.CameraMotionListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * @Author : xcc
 * @Date : Created in 2023/12/6 12:06
 * @Decription : 自定义RecyclerView.Adapter类
 */

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.VideoViewHolder> {

    private Context context;
    private String[] videoUrls;
    // 当前正在播放的视频位置
    private int currentPlayingPosition = -1;
    private ExoPlayer player;
    private String[] imageUrls;
    private View imageVr;
    private Integer[] videoIds;
    private String username;

    private static SparseBooleanArray favoriteStates;//记录点赞控件状态
    public int getCurrentPlayingPosition() {
        return currentPlayingPosition;
    }


    public VideoAdapter(String[] videoUrls, Context context,String[] imageUrls,View imageVr,Integer[] videoIds,String username) {
        this.videoUrls = videoUrls;
        this.context = context;
        this.imageUrls = imageUrls;
        this.imageVr = imageVr;
        this.videoIds = videoIds;
        this.username = username;
        favoriteStates = new SparseBooleanArray();

        //设置缓冲区大小
        LoadControl loadControl = new DefaultLoadControl.Builder()
                .setBufferDurationsMs(
                        5000,
                        30000,
                        2000,
                        2000
                )
                .build();

        player = new SimpleExoPlayer.Builder(context)
                .setLoadControl(loadControl)
                .build();


        // 设置循环播放模式
        player.setRepeatMode(Player.REPEAT_MODE_ALL);
        // 添加日志
        player.addAnalyticsListener(new AnalyticsListener() {
            @Override
            public void onRenderedFirstFrame(EventTime eventTime, Object output, long positionMs) {
                Log.d("VideoAdapter", "First video frame rendered at position: " + positionMs);
            }

            public void onPlayerError(EventTime eventTime, ExoPlaybackException error) {
                Log.e("VideoAdapter", "Player error occurred: " + error.getMessage());
            }
            @Override
            public void onPlayerStateChanged(EventTime eventTime, boolean playWhenReady, int playbackState) {
                Log.d("VideoAdapter", "Player state changed. playWhenReady: " + playWhenReady + " state: " + playbackState);
            }
        });

    }

    @NonNull
    @Override
    public VideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //添加item布局，并转为一个view对象
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_video, parent, false);

        return new VideoViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull VideoViewHolder holder, @SuppressLint("RecyclerView") int position) {

        holder.playerView.setPlayer(player);
        MediaItem mediaItem = MediaItem.fromUri(videoUrls[position]);
        player.setMediaItem(mediaItem);
        if (position != currentPlayingPosition) {
            player.stop();
            player.clearMediaItems();
            player.setMediaItem(mediaItem);
            player.prepare();
            currentPlayingPosition = position;
        }
        //点击暂停事件
        holder.playerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (player.isPlaying()) {
                    player.pause();
                    holder.playicon.setVisibility(View.VISIBLE);
                } else {
                    player.play();
                    holder.playicon.setVisibility(View.GONE);
                }
            }
        });
        holder.iconFavorite.setChecked(favoriteStates.get(position));
        // 设置视频播放状态监听器
        player.addListener(new Player.Listener() {
            @Override
            public void onPlaybackStateChanged(int state) {
                if (state == Player.STATE_READY && player.getPlayWhenReady()) {
                    // 视频正在播放，隐藏按钮
                    holder.playicon.setVisibility(View.GONE);
                } else {
                    // 视频暂停或停止，显示按钮
                    holder.playicon.setVisibility(View.VISIBLE);
                }
            }
        });
        //判断正在播放的视频的vr图像是否为空
        if (imageUrls[position] != null) {
            imageVr.setVisibility(View.VISIBLE); // 显示图像组件
            imageVr.setOnClickListener(v -> {
                // 点击事件跳转到vractivity，并传递图像URL
                Intent intent = new Intent(context, Map_VRActivity.class);
                intent.putExtra("imageUrl", imageUrls[position]);
                context.startActivity(intent);
            });
        } else {
            imageVr.setVisibility(View.GONE); // 隐藏图像组件
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                if(currentPlayingPosition == -1) currentPlayingPosition = 0;
                int videoId = videoIds[currentPlayingPosition];

                Log.e("videoId", videoId + "");

                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url("http://"+ip+":8080/map/videos/"+videoId+"/likecount?username="+username)
                        .get()
                        .build();
                client.newCall(request).enqueue(new Callback() {

                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                        ((Activity)context).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(context,"获取点赞数失败，请稍后重试",Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    @Override
                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                        Gson json = new Gson();
                        String responseData = response.body().string();
                        Result result = json.fromJson(responseData, Result.class);
                        ((Activity)context).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(context, result.getMsg(), Toast.LENGTH_SHORT).show();
                                String likes = result.getData().toString().substring(0,
                                        result.getData().toString().indexOf("."));
                                holder.favoriteCount.setText(likes + "");

                                boolean isChecked = false;
                                if(result.getCode()== Code.VIDEO_HAS_LIKED) isChecked = true;
                                else isChecked = false;

                                Log.e("isChecked", isChecked + "");

                                if(isChecked) {
                                    holder.iconFavorite.setButtonDrawable(R.drawable.btn_favorite_icon);
                                    CompoundButtonCompat.setButtonTintList(holder.iconFavorite, ColorStateList.valueOf(Color.RED));
                                } else {
                                    holder.iconFavorite.setButtonDrawable(R.drawable.btn_favorite_icon);
                                    CompoundButtonCompat.setButtonTintList(holder.iconFavorite, ColorStateList.valueOf(Color.WHITE));
                                }
                            }

                        });
                    }
                });
            }
        }).start();


        holder.likeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        int videoId = videoIds[currentPlayingPosition];

                        OkHttpClient client = new OkHttpClient();

                        FormBody body = new FormBody.Builder()
                                .add("username",username)
                                .build();
                        Request request = new Request.Builder()
                                .url("http://"+ip+":8080/map/videos/"+videoId+"/like")
                                .post(body)
                                .build();
                        client.newCall(request).enqueue(new Callback() {
                            @Override
                            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                                ((Activity)context).runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(context,"服务器连接失败，请稍后重试",Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }

                            @Override
                            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                                Gson json = new Gson();
                                String responseData = response.body().string();
                                Result result = json.fromJson(responseData, Result.class);

                                ((Activity) context).runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        double data = (double) result.getData();
                                        data += 0;
                                        String data1 = data + "";
                                        String likes = data1.substring(0,
                                                result.getData().toString().indexOf("."));
                                        if (result.getFlag()) {

                                            Log.e("-------", "--------------");
                                            Log.e("likecount", result.getData().toString());
                                            Log.e("currentPosition", currentPlayingPosition + "");
                                            Log.e("videoId", videoIds[currentPlayingPosition] + "");
                                            Log.e("likes", likes);

                                            Log.e("-------", "--------------");

                                            holder.favoriteCount.setText(likes+"");
                                            Toast.makeText(context, result.getMsg(), Toast.LENGTH_SHORT).show();
                                            holder.iconFavorite.setButtonDrawable(R.drawable.btn_favorite_icon);
                                            CompoundButtonCompat.setButtonTintList(holder.iconFavorite, ColorStateList.valueOf(Color.RED));
                                            return;
                                        }
                                        holder.favoriteCount.setText(likes+"");
                                        Toast.makeText(context, result.getMsg(), Toast.LENGTH_SHORT).show();
                                        holder.iconFavorite.setButtonDrawable(R.drawable.btn_favorite_icon);
                                        CompoundButtonCompat.setButtonTintList(holder.iconFavorite, ColorStateList.valueOf(Color.WHITE));
                                    }
                                });
                            }
                        });
                    }
                }).start();
            }
        });

        // 为评论绑定点击事件
        holder.commentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context);
                bottomSheetDialog.setContentView(R.layout.dialog_bottom_comment);
                bottomSheetDialog.show();

                // 为评论中的发送按钮绑定点击事件
                Button postReview = bottomSheetDialog.findViewById(R.id.btn_send);

            }
        });

    }


    @Override
    public void onViewRecycled(@NonNull VideoViewHolder holder) {
        super.onViewRecycled(holder);
        holder.playerView.setPlayer(null);
    }
    @Override
    public void onViewDetachedFromWindow(@NonNull VideoViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        holder.playerView.setPlayer(null);
        player.setPlayWhenReady(false);
    }
    @Override
    public void onViewAttachedToWindow(@NonNull VideoViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        holder.playerView.setPlayer(player);
        if (holder.getAdapterPosition() == currentPlayingPosition) {
            player.setPlayWhenReady(true);
        } else {
            player.setPlayWhenReady(false);
        }
    }


    @Override
    public int getItemCount() {
        return videoUrls.length;
    }

    public void setCurrentPlayingPosition(int position) {
        currentPlayingPosition = position;
        notifyDataSetChanged();
    }
    public void setPlayWhenReady(boolean playWhenReady) {
        if (player != null) {
            player.setPlayWhenReady(playWhenReady);
        }
    }
    public void releasePlayer() {
        player.release();
    }

    public class VideoViewHolder extends RecyclerView.ViewHolder {
        public PlayerView playerView;
        public CheckBox iconFavorite;
        View playicon;
        Button likeButton;  // 添加这两行
        TextView favoriteCount;
        FloatingActionButton commentButton;

        public VideoViewHolder(@NonNull View itemView) {
            super(itemView);
            playerView = itemView.findViewById(R.id.map_video);
            playicon = itemView.findViewById(R.id.video_play);
            iconFavorite = itemView.findViewById(R.id.favorite_icon);
            likeButton = itemView.findViewById(R.id.favorite_icon);  // 初始化
            favoriteCount = itemView.findViewById(R.id.favorite_count);  // 初始化
            commentButton = itemView.findViewById(R.id.msg_icon);

            iconFavorite.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    int adapterPosition = getAdapterPosition();
                    if(isChecked) {
                        iconFavorite.setButtonDrawable(R.drawable.btn_favorite_icon);
                        CompoundButtonCompat.setButtonTintList(iconFavorite, ColorStateList.valueOf(Color.RED));
                        favoriteStates.put(adapterPosition, true);
                    } else {
                        iconFavorite.setButtonDrawable(R.drawable.btn_favorite_icon);
                        CompoundButtonCompat.setButtonTintList(iconFavorite, ColorStateList.valueOf(Color.WHITE));
                        favoriteStates.put(adapterPosition, false);
                    }
                }
            });

        }
        public void setPlayIconVisibility(int visibility) {
            playicon.setVisibility(visibility);
        }
    }
}
