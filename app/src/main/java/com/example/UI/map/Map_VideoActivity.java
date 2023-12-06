package com.example.UI.map;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.motion.widget.OnSwipe;

import android.net.Uri;
import android.os.Bundle;
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
        // 使用LayoutInflater加载custom_media_controller.xml文件
        LayoutInflater inflater = LayoutInflater.from(this);
        View controllerLayout=inflater.inflate(R.layout.custom_media_controller,null);
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
/**
 * @param :
 * @return void
 * @author xcc
 * @description 初始化视频播放器
 * @date 2023/12/5 18:22
 */
private void initializePlayer() {
        player = new SimpleExoPlayer.Builder(this).build();
        // 创建用于监听滑动手势的View
        ImageView gestureView = new ImageView(this);
        // 记录当前视频的索引
        int currentVideoIndex = 0;
        // 监听滑动手势

        Uri videoUri1 = Uri.parse("https://fd.aigei.com/src/vdo/mp4/66/666d68d9196940819b05e3f1af1e7435.mp4?e=1701860820&token=P7S2Xpzfz11vAkASLTkfHN7Fw-oOZBecqeJaxypL:LAxwO2dM1FhNiEPEUHvvQwMSt7E=");
        Uri videoUri2 = Uri.parse("https://fd.aigei.com/src/vdo/mp4/95/95c234e95afb4524bd35d06bbc7f7f60.mp4?e=1701861420&token=P7S2Xpzfz11vAkASLTkfHN7Fw-oOZBecqeJaxypL:fv-Ai-Ow8fsyUKAuaiMOOK7bK0c=");
        VideoPlayerEventListener videoPlayerEventListener=new VideoPlayerEventListener(player,this);
        player.addListener(videoPlayerEventListener);
        playerView.setPlayer(player);
        MediaItem mediaItem1 = MediaItem.fromUri(videoUri1);
        MediaItem mediaItem2 = MediaItem.fromUri(videoUri2);
        player.setMediaItem(mediaItem1);
        player.setMediaItem(mediaItem2);
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