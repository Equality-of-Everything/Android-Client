package com.example.UI.map;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.motion.widget.OnSwipe;

import android.net.Uri;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.android_client.R;
import com.example.util.VideoPlayerEventListener;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

public class Map_VideoActivity extends AppCompatActivity {
    private GestureDetector gestureDetector;
    private PlayerView playerView;
    private ExoPlayer player;
    private int currentVideoIndex;
    private String[] videoUrls = {
            "https://fd.aigei.com/src/vdo/mp4/eb/eb0e11470b624784ae463adc31729023.mp4?e=1701882300&token=P7S2Xpzfz11vAkASLTkfHN7Fw-oOZBecqeJaxypL:1c8ywdNjffq4-lpTqU6rgvqxh1E=",
            "https://fd.aigei.com/src/vdo/mp4/47/47de612bc3d841bf923db4d5ffb2b1e0.mp4?e=1701882300&token=P7S2Xpzfz11vAkASLTkfHN7Fw-oOZBecqeJaxypL:45czvwAA4pQch9Q287yL7rP4_08=",
            "https://fd.aigei.com/src/vdo/mp4/df/df61d584e74f4e5fa73bc6d1cc67f11b.mp4?e=1701882300&token=P7S2Xpzfz11vAkASLTkfHN7Fw-oOZBecqeJaxypL:7Qfbm0dN0uwIJ048hVmZC6MZ0lA="
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_video);
        //引用播放图标
        ImageView playIcon=findViewById(R.id.video_play);
        //将播放器附加到视图
        playerView = findViewById(R.id.map_video);
        // 使用LayoutInflater加载custom_media_controller.xml文件
        LayoutInflater inflater = LayoutInflater.from(this);
        View controllerLayout=inflater.inflate(R.layout.custom_media_controller,null);
        gestureDetector = new GestureDetector(this, new GestureListener());
        player = new SimpleExoPlayer.Builder(this).build();
        VideoPlayerEventListener videoPlayerEventListener=new VideoPlayerEventListener(player,this);
        player.addListener(videoPlayerEventListener);
        playerView.setPlayer(player);
        currentVideoIndex = 0;
        playCurrentVideo();

//        initializePlayer();
        playerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 检查当前播放器的状态
                if (player.getPlayWhenReady()) {
                    // 当前正在播放，暂停播放
                    player.setPlayWhenReady(false);
                    playIcon.setVisibility(View.VISIBLE);
                } else {
                    // 当前暂停中，开始播放
                    player.setPlayWhenReady(true);
                    playIcon.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releasePlayer();
    }

    private void releasePlayer() {
        if (player != null) {
            player.release();
            player = null;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return gestureDetector.onTouchEvent(event) || super.onTouchEvent(event);
    }

    private class GestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            if (Math.abs(velocityX) > Math.abs(velocityY)) {
                // 横向滑动，忽略
                return false;
            } else {
                // 纵向滑动
                if (velocityY < 0) {
                    // 上滑，播放下一个视频
                    currentVideoIndex++;
                    if (currentVideoIndex >= videoUrls.length) {
                        currentVideoIndex = 0;
                    }
                    playCurrentVideo();
                } else {
                    // 下滑，播放上一个视频
                    currentVideoIndex--;
                    if (currentVideoIndex < 0) {
                        currentVideoIndex = videoUrls.length - 1;
                    }
                    playCurrentVideo();
                }
                return true;
            }
        }
    }

    private void playCurrentVideo() {
        Uri uri = Uri.parse(videoUrls[currentVideoIndex]);

        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(this,
                Util.getUserAgent(this, "YourApp"));

        MediaSource mediaSource = new ProgressiveMediaSource.Factory(dataSourceFactory)
                .createMediaSource(MediaItem.fromUri(uri));

        player.setMediaSource(mediaSource);
        player.prepare();
        player.play();
    }
}