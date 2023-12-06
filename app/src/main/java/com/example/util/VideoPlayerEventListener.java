package com.example.util;

import static android.content.ContentValues.TAG;

import android.animation.Animator;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.example.entity.ShareInfo;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.PlaybackException;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.ui.DefaultTimeBar;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * @Author : 你的名字
 * @Date : Created in 2023/12/5 18:02
 * @Decription :
 */

public class VideoPlayerEventListener implements Player.Listener {
    private ExoPlayer player;
    private Context context;

    public VideoPlayerEventListener(ExoPlayer player,Context context) {

        this.player = player;
        this.context=context;
    }

/**
 * @param error:
 * @return void
 * @author xcc
 * @description 视频错误回调
 * @date 2023/12/6 9:46
 */
public void onPlayerError(PlaybackException error) {
        Player.Listener.super.onPlayerError(error);
        Log.e(TAG, "Player error: " + error.getMessage());
        Toast.makeText(context, "Player error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
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
