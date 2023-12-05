package com.example.util;

import android.os.Handler;
import android.os.Looper;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.Player;

/**
 * @Author : 你的名字
 * @Date : Created in 2023/12/5 18:02
 * @Decription :
 */

public class VideoPlayerEventListener implements Player.Listener {
    private ExoPlayer player;
    public VideoPlayerEventListener(ExoPlayer player) {
        this.player = player;
    }
    /**
     * @param playWhenReady:
     * @param playbackState:
     * @return void
     * @author xcc
     * @description 视频播放结束0.5秒后循环播放
     * @date 2023/12/5 18:14
     */
    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        if (playbackState == Player.STATE_ENDED) {
            // 延迟0.1秒后重新播放视频
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    player.seekTo(0);
                    player.setPlayWhenReady(true);
                }
            }, 500);
        }
    }
}
