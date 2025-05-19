package com.example.UI.share;

import static com.example.android_client.LoginActivity.ip;
import static com.luck.picture.lib.thread.PictureThreadUtils.runOnUiThread;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.arch.core.executor.DefaultTaskExecutor;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.example.adapter.DynamicAdapter;
import com.example.android_client.R;
import com.example.util.Result;
import com.example.util.TokenManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.gson.Gson;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;

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



public class ShareFragment extends Fragment {
    private Call mCall;
    private String userName;
    private ImageView shareBackground;
    private RecyclerView recyclerView;
    private FloatingActionButton shareEditButton;
    private List<FriendCircleItem> dataSource = new ArrayList<>();
    // 用于处理线程通信
    private Handler handler = new Handler(Looper.getMainLooper());
    private DynamicAdapter adapter;
    // 是否加载下一页
    private boolean isLoading = false;
    // 当前页数
    private int currentPage = 0;
    // 每页显示的数量
    private int pageSize = 5;
    @Nullable
    @Override
    @SuppressLint({"MissingInflatedId", "LocalSuppress"})
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View ShareView = inflater.inflate(R.layout.fragment_share, container, false);
        shareBackground = ShareView.findViewById(R.id.share_background);
        recyclerView = ShareView.findViewById(R.id.share_recyclerView);
        shareEditButton = ShareView.findViewById(R.id.share_edit_button);

        userName = TokenManager.getUserName(getContext());
        shareEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), ShareEditActivity.class);
                startActivity(intent);
            }
        });
        // 设置RecyclerView的布局管理器和适配器
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        // 初始化数据源
        getFriendCircleItemList();
        adapter = new DynamicAdapter(dataSource,getContext());
        //设置适配器
        recyclerView.setAdapter(adapter);

        // 设置滚动监听器
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int visibleItemCount = recyclerView.getLayoutManager().getChildCount();
                int totalItemCount = recyclerView.getLayoutManager().getItemCount();
                int firstVisibleItemPosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPosition();
                Log.e("currentPaeg", currentPage + "");
                if(!isLoading) {
                    if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                            && firstVisibleItemPosition >= 0
                            && totalItemCount >= pageSize) {
                        currentPage++;
                        loadMoreData();
                        isLoading = true;
                    }
                }
            }
        });

        return ShareView;
    }

    /**
     * @param :
     * @return void
     * @author Lee
     * @description 向服务器端请求背景图
     * @date 2024/1/3 14:29
     */
    private void httpRequest() {
        // 使用测试数据替代网络请求
        if (getActivity() != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    // 设置默认背景图
                    shareBackground.setImageResource(R.drawable.mine_background);
                }
            });
        }
    }



    // 发送分页请求，请求更多数据
    private void loadMoreData() {
        isLoading = true;
        
        // 模拟网络延迟
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // 添加新的测试数据
                FriendCircleItem newItem = new FriendCircleItem(
                    "用户" + (currentPage + 1),
                    "https://img1.baidu.com/it/u=1966616150,2146512490&fm=253&fmt=auto&app=138&f=JPEG?w=751&h=500",
                    "这是加载的第 " + currentPage + " 页新内容，测试下拉加载更多功能",
                    Arrays.asList(
                        "https://img0.baidu.com/it/u=2028084904,3939052004&fm=253&fmt=auto&app=138&f=JPEG?w=500&h=333",
                        "https://img2.baidu.com/it/u=1817953371,2718307292&fm=253&fmt=auto&app=138&f=JPEG?w=500&h=333"
                    ),
                    null,
                    "2024-01-05 " + (8 - currentPage) + ":00:00"
                );
                
                dataSource.add(newItem);
                
                if (getActivity() != null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter.notifyDataSetChanged();
                            isLoading = false;
                        }
                    });
                }
            }
        }, 1000); // 延迟1秒
    }

    //获取朋友圈数据测试数据
    private void getFriendCircleItemList() {
        // 添加更多测试数据
        List<FriendCircleItem> testData = new ArrayList<>();
        
        // 测试数据1：纯文本
        FriendCircleItem item1 = new FriendCircleItem("小明",
                "https://img1.baidu.com/it/u=1966616150,2146512490&fm=253&fmt=auto&app=138&f=JPEG?w=751&h=500",
                "今天天气真好，准备出去玩！",
                null,
                null,
                "2024-01-05 12:00:00");
        
        // 测试数据2：带单张图片
        FriendCircleItem item2 = new FriendCircleItem("李华",
                "https://img2.baidu.com/it/u=2048195462,703560066&fm=253&fmt=auto&app=138&f=JPEG?w=500&h=333",
                "分享一张美食照片",
                Arrays.asList("https://img0.baidu.com/it/u=2028084904,3939052004&fm=253&fmt=auto&app=138&f=JPEG?w=500&h=333"),
                null,
                "2024-01-05 11:30:00");
        
        // 测试数据3：带多张图片
        FriendCircleItem item3 = new FriendCircleItem("张三",
                "https://img0.baidu.com/it/u=3345100707,1365954481&fm=253&fmt=auto&app=138&f=JPEG?w=500&h=333",
                "周末出游记录",
                Arrays.asList(
                    "https://img1.baidu.com/it/u=1966616150,2146512490&fm=253&fmt=auto&app=138&f=JPEG?w=751&h=500",
                    "https://img2.baidu.com/it/u=2048195462,703560066&fm=253&fmt=auto&app=138&f=JPEG?w=500&h=333",
                    "https://img0.baidu.com/it/u=3345100707,1365954481&fm=253&fmt=auto&app=138&f=JPEG?w=500&h=333",
                    "https://img2.baidu.com/it/u=1817953371,2718307292&fm=253&fmt=auto&app=138&f=JPEG?w=500&h=333"
                ),
                null,
                "2024-01-05 10:00:00");
        
        // 测试数据4：带视频
        FriendCircleItem item4 = new FriendCircleItem("李四",
                "https://img2.baidu.com/it/u=1817953371,2718307292&fm=253&fmt=auto&app=138&f=JPEG?w=500&h=333",
                "分享一个有趣的视频",
                null,
                "https://vd3.bdstatic.com/mda-mhkku4ndaka5etk3/480p/h264/1629557146541297988/mda-mhkku4ndaka5etk3.mp4",
                "2024-01-05 09:30:00");
        
        // 测试数据5：长文本
        FriendCircleItem item5 = new FriendCircleItem("王五",
                "https://img1.baidu.com/it/u=1966616150,2146512490&fm=253&fmt=auto&app=138&f=JPEG?w=751&h=500",
                "这是一段很长的文本内容，用来测试长文本的显示效果。今天天气特别好，阳光明媚，让人心情愉悦。周末准备和朋友们一起去郊游，期待这次的户外活动！",
                Arrays.asList(
                    "https://img0.baidu.com/it/u=2028084904,3939052004&fm=253&fmt=auto&app=138&f=JPEG?w=500&h=333"
                ),
                null,
                "2024-01-05 09:00:00");

        dataSource.clear();
        dataSource.addAll(Arrays.asList(item1, item2, item3, item4, item5));
        
        if (adapter != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    adapter.notifyDataSetChanged();
                }
            });
        }

        /* 注释掉原有的网络请求代码
        new Thread(new Runnable() {
            @Override
            public void run() {
                // ... 原有的网络请求代码 ...
            }
        }).start();
        */
    }

    @Override
    public void onResume() {
        super.onResume();
        // 不需要调用网络请求，直接使用测试数据
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        // 清理工作
        if (mCall != null) {
            mCall.cancel();
        }
    }
}