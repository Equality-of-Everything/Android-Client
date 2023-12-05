package com.example.UI.map;
import androidx.appcompat.app.AppCompatActivity;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.example.android_client.R;
import com.example.util.VideoPlayerEventListener;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.PlayerView;

public class Map_VideoActivity extends AppCompatActivity {

    private PlayerView playerView;
    private ExoPlayer player;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_video);
        //引用播放图标
        ImageView playIcon=findViewById(R.id.video_play);
        //将播放器附加到视图
        playerView = findViewById(R.id.map_video);
        initializePlayer();
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


    private void initializePlayer() {
        player = new SimpleExoPlayer.Builder(this).build();
        //添加VideoPlayerEventListener实例实现循环播放
        VideoPlayerEventListener videoPlayerEventListener=new VideoPlayerEventListener(player);
        player.addListener(videoPlayerEventListener);

        playerView.setPlayer(player);
        Uri videoUri = Uri.parse("https://fd.aigei.com/src/vdo/mp4/ec/ec9ec17489c6444188c65f3204f09630.mp4?e=1701804180&token=P7S2Xpzfz11vAkASLTkfHN7Fw-oOZBecqeJaxypL:0XjlD4E3HNDaDmH86Y9NMhk3bVU=");
        MediaItem mediaItem = MediaItem.fromUri(videoUri);
        player.setMediaItem(mediaItem);
        player.prepare();
        player.play();
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
}