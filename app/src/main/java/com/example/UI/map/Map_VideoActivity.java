package com.example.UI.map;
import androidx.appcompat.app.AppCompatActivity;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.example.android_client.R;
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
        playerView.setPlayer(player);
        Uri videoUri = Uri.parse("https://video.699pic.com/videos/05/12/19/a_qWPD4CwqVDej1600051219_10s.mp4");
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