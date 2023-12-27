package com.example.UI.map;
import static com.example.android_client.LoginActivity.ip;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.adapter.CommentAdapter;
import com.example.adapter.VideoAdapter;
import com.example.android_client.LoginActivity;
import com.example.android_client.MainActivity;
import com.example.android_client.R;
import com.example.entity.Comment;
import com.example.entity.ShareInfo;
import com.example.util.Result;
import com.example.util.TokenManager;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.zip.Inflater;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Map_VideoActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private VideoAdapter adapter;
    private View imageVr;



    private CommentAdapter commentAdapter;

    private String[] serverVideoUrls;
    private String[] serverImageUrls;
    private ArrayList<Integer> videoIds;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_video);

        String[] videoUrls = {
                "https://sns-video-hw.xhscdn.net/stream/110/259/01e5096bc81243b6010377038aaccf5cf2_259.mp4",
                "https://sns-video-hw.xhscdn.net/stream/110/259/01e52e7fdf10a938010370038b3da44153_259.mp4",
                "https://sns-video-hw.xhscdn.net/stream/110/259/01e5002c950b5279010370038a88aeabe0_259.mp4",
                "https://sns-video-hw.xhscdn.net/stream/110/259/01e46f7034bb07080103710388534f75c5_259.mp4",
                "https://sns-video-hw.xhscdn.net/stream/110/259/01e52d1dd707d8d3010377038b383db786_259.mp4"
        };
        String[] imageUrls={
                "https://img.zcool.cn/community/014d9e5ae19da2a801214a61308a99.JPG@2o.jpg",
                null,
                "https://img.zcool.cn/community/01429859a3e6c2a801211d25e8611e.jpg@2o.jpg",
                "https://img.zcool.cn/community/01241455683b7e0000012b206b751a.jpg@3000w_1l_2o_100sh.jpg",
                null,
                null
        };
//        获取urls
        serverVideoUrls = getIntent().getStringArrayExtra("videoUrls");
        serverImageUrls = getIntent().getStringArrayExtra("vrUrls");
        videoIds = getIntent().getIntegerArrayListExtra("videoIds");





        imageUrls = serverImageUrls;
        videoUrls = serverVideoUrls;

        //初始化 RecyclerView 和 VideoAdapter
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setNestedScrollingEnabled(false);
        imageVr = findViewById(R.id.btn_jump_vr);



        SnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new VideoAdapter(videoUrls,this,imageUrls,imageVr,videoIds.toArray(new Integer[videoIds.size()]), TokenManager.getUserName(this));
        recyclerView.setAdapter(adapter);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    int position = ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPosition();
                    if (adapter != null) {
                        adapter.setCurrentPlayingPosition(position);
                        adapter.setPlayWhenReady(true); // 开始播放
                    }
                }else {
                    // 滚动中暂停播放
                    adapter.setPlayWhenReady(false);
                }
            }
        });



    }


    @Override
    protected void onStart() {
        super.onStart();
        adapter.setPlayWhenReady(false);
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.setPlayWhenReady(false);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // 暂停播放器
        adapter.setPlayWhenReady(false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 获取当前可见的ViewHolder
        int firstVisiblePosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPosition();
        VideoAdapter.VideoViewHolder viewHolder = (VideoAdapter.VideoViewHolder) recyclerView.findViewHolderForAdapterPosition(firstVisiblePosition);

        // 隐藏按钮
        if (viewHolder != null) {
            viewHolder.setPlayIconVisibility(View.GONE);
        }
        // 恢复播放器
        adapter.setPlayWhenReady(true);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        adapter.releasePlayer();
    }
    public void VROnclick(View view) {
        Intent intent = new Intent(Map_VideoActivity.this, Map_VRActivity.class);
        startActivity(intent);
    }
}