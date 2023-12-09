package com.example.UI.map;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import android.os.Build;
import android.os.Bundle;
import com.example.adapter.VideoAdapter;
import com.example.android_client.R;
public class Map_VideoActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private VideoAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_video);

        String[] videoUrls = {
                "https://sns-video-al.xhscdn.com/stream/110/259/01e56ee29bdc6bee010370038c392f5e82_259.mp4",
                "https://sns-video-al.xhscdn.com/stream/110/259/01e55482b5c53132010370038bd21ed732_259.mp4",
                "https://sns-video-bd.xhscdn.com/stream/110/259/01e550946db51284010377038bc2c498a5_259.mp4",
                "https://sns-video-bd.xhscdn.com/stream/110/259/01e53b3440ab6ad9010370038b6f4458c0_259.mp4",
                "https://sns-video-bd.xhscdn.com/stream/110/259/01e54b8c7fc5288c010370038baf2109be_259.mp4"
        };
        // 获取urls
        //String[] urls = getIntent().getStringArrayExtra("urls");
        //videoUrls = urls;
        //System.out.println("videoUrls:"+videoUrls);


        //初始化 RecyclerView 和 VideoAdapter
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setNestedScrollingEnabled(false);

        SnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new VideoAdapter(videoUrls,this);
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
        // 恢复播放器
        adapter.setPlayWhenReady(true);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        adapter.releasePlayer();
    }
}