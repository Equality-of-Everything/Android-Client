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

        httpRequest();



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
        Gson json = new Gson();

        FormBody body = new FormBody.Builder()
                .add("username", userName)
                .build();

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("http://"+ip+":8080/userInfo/getBackgroundImage")
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e("MineFragment", "请求后端数据失败");
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                Log.e("MineFragment", "请求后端数据成功");

                String responseData = response.body().string();
                Result result = json.fromJson(responseData, Result.class);
                if(result.getFlag()) {
                    Log.e("shareFragment", "后端响应请求成功");
                    if (result.getData() != null) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Glide.with(getActivity()).load(result.getData()).into(shareBackground);
                                Log.e("shareFragment", "获取背景图成功");
                            }
                        });
                    }
                } else {
                    Log.e("shareFragment", "后端响应请求失败");
                }
            }
        });
    }



    // 发送分页请求，请求更多数据
    private void loadMoreData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpClient client = new OkHttpClient();
                int cu = currentPage * pageSize;
                Request request = new Request.Builder()
                        .get()
                        .url("http://"+ip+":8080/friendShare/getFriendShare?page=" + cu + "&size=" + pageSize)
                        .build();
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                        e.printStackTrace();
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getContext(), "服务器连接失败，请稍后重试", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                        Gson gson = new GsonBuilder()
                                .registerTypeAdapter(FriendCircleItem.class, new FriendCircleItemDeserializer())
                                .create();
                        String res = response.body().string();
                        Result result = gson.fromJson(res, Result.class);
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (!result.getFlag()) {
                                    Toast.makeText(getContext(), result.getMsg(), Toast.LENGTH_SHORT).show();
                                    return;
                                }

                                JsonElement dataElement = gson.toJsonTree(result.getData());
                                List<FriendCircleItem> friendCircleItemList = gson.fromJson(dataElement, new TypeToken<List<FriendCircleItem>>() {}.getType());
                                // 在添加新数据之前检查是否已包含相同的数据项
                                for (FriendCircleItem newItem : friendCircleItemList) {
                                    if (!dataSource.contains(newItem)) {
                                        dataSource.add(newItem);
                                    }
                                }
                                Log.e("datasource", dataSource.toString());
                                adapter.notifyDataSetChanged();
//                                isLoading = false;
                        }
                        });

                    }
                });

            }
        }).start();
    }

    //获取朋友圈数据,
    //TODO: 这里需要从服务器获取数据,还需要评论内容，发布评论的人，点赞的人，点赞数，评论数
    private void getFriendCircleItemList() {
//        FriendCircleItem item1 = new FriendCircleItem("xcc",
//                "https://picx.zhimg.com/80/v2-da2b0a3b96103d87a682409fc5a261a9_720w.webp?source=1def8aca",
//                "今天",
//                null,
//                null,
//                "2021-04-20 12:00:00");
//        FriendCircleItem item2 = new FriendCircleItem("lee",
//                "https://picx.zhimg.com/80/v2-da2b0a3b96103d87a682409fc5a261a9_720w.webp?source=1def8aca",
//                "平平无奇的朋友圈",
//                Arrays.asList("https://tupian.qqw21.com/article/UploadPic/2021-4/202141120475135553.jpg",
//                "https://tupian.qqw21.com/article/UploadPic/2021-4/202141120475135553.jpg"),
//                null,
//                "2023-04-20 12:00:00");
//        FriendCircleItem item3 = new FriendCircleItem("哇嘎嘎",
//                "https://picx.zhimg.com/80/v2-da2b0a3b96103d87a682409fc5a261a9_720w.webp?source=1def8aca",
//                "看什么看",
//                Arrays.asList("https://tupian.qqw21.com/article/UploadPic/2021-4/202141120475135553.jpg",
//                "https://tupian.qqw21.com/article/UploadPic/2021-4/202141120475135553.jpg",
//                "https://tupian.qqw21.com/article/UploadPic/2021-4/202141120475135553.jpg",
//                "https://tupian.qqw21.com/article/UploadPic/2021-4/202141120475135553.jpg",
//                "https://tupian.qqw21.com/article/UploadPic/2021-4/202141120475135553.jpg",
//                "https://tupian.qqw21.com/article/UploadPic/2021-4/202141120475135553.jpg"),
//                null,
//                "2023-04-20 12:00:00");
//        FriendCircleItem item4 = new FriendCircleItem("某人",
//                "https://picx.zhimg.com/80/v2-da2b0a3b96103d87a682409fc5a261a9_720w.webp?source=1def8aca",
//                "你好，这是第四条",
//                Arrays.asList("https://tupian.qqw21.com/article/UploadPic/2021-4/202141120475135553.jpg",
//                "https://tupian.qqw21.com/article/UploadPic/2021-4/202141120475135553.jpg",
//                "https://tupian.qqw21.com/article/UploadPic/2021-4/202141120475135553.jpg",
//                "https://tupian.qqw21.com/article/UploadPic/2021-4/202141120475135553.jpg",
//                "https://tupian.qqw21.com/article/UploadPic/2021-4/202141120475135553.jpg",
//                "https://tupian.qqw21.com/article/UploadPic/2021-4/202141120475135553.jpg",
//                "https://tupian.qqw21.com/article/UploadPic/2021-4/202141120475135553.jpg",
//                "https://tupian.qqw21.com/article/UploadPic/2021-4/202141120475135553.jpg"),
//                null,
//                "2023-04-20 12:00:00");
//        FriendCircleItem item5 = new FriendCircleItem("同学",
//                "https://picx.zhimg.com/80/v2-da2b0a3b96103d87a682409fc5a261a9_720w.webp?source=1def8aca",
//                "你好，这是第五条朋友圈",
//                Arrays.asList("https://tupian.qqw21.com/article/UploadPic/2021-4/202141120475135553.jpg",
//                "https://tupian.qqw21.com/article/UploadPic/2021-4/202141120475135553.jpg",
//                "https://tupian.qqw21.com/article/UploadPic/2021-4/202141120475135553.jpg",
//                "https://tupian.qqw21.com/article/UploadPic/2021-4/202141120475135553.jpg",
//                "https://tupian.qqw21.com/article/UploadPic/2021-4/202141120475135553.jpg",
//                "https://tupian.qqw21.com/article/UploadPic/2021-4/202141120475135553.jpg",
//                "https://tupian.qqw21.com/article/UploadPic/2021-4/202141120475135553.jpg"),
//                null,
//                "2023-04-20 12:00:00");
//        FriendCircleItem item6 = new FriendCircleItem("同学",
//                "https://picx.zhimg.com/80/v2-da2b0a3b96103d87a682409fc5a261a9_720w.webp?source=1def8aca",
//                "你好，这是第六条",
//                Arrays.asList("https://tupian.qqw21.com/article/UploadPic/2021-4/202141120475135553.jpg",
//                "https://tupian.qqw21.com/article/UploadPic/2021-4/202141120475135553.jpg",
//                "https://tupian.qqw21.com/article/UploadPic/2021-4/202141120475135553.jpg",
//                "https://tupian.qqw21.com/article/UploadPic/2021-4/202141120475135553.jpg"),
//                null,
//                "2023-04-20 12:00:00");
//        FriendCircleItem item7 = new FriendCircleItem("同学",
//                "https://picx.zhimg.com/80/v2-da2b0a3b96103d87a682409fc5a261a9_720w.webp?source=1def8aca",
//                "你好，这是第七条",
//                null,
//                "https://sns-video-hw.xhscdn.net/stream/110/259/01e5096bc81243b6010377038aaccf5cf2_259.mp4",
//                "2021-04-20 12:00:00");
//        return new ArrayList<FriendCircleItem>() {{
//            add(item1);
//            add(item2);
//            add(item3);
//            add(item4);
//            add(item5);
//            add(item6);
//            add(item7);
//        }};
        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .get()
                        .url("http://"+ip+":8080/friendShare/getFriendShare?page=" + currentPage + "&size=" + pageSize)
                        .build();
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                        e.printStackTrace();
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getContext(), "服务器连接失败，请稍后重试", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                        Gson gson = new GsonBuilder()
                                .registerTypeAdapter(FriendCircleItem.class, new FriendCircleItemDeserializer())
                                .create();
                        String res = response.body().string();
                        Result result = gson.fromJson(res, Result.class);
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (!result.getFlag()) {
                                    Toast.makeText(getContext(), result.getMsg(), Toast.LENGTH_SHORT).show();
                                    return;
                                }

                                JsonElement dataElement = gson.toJsonTree(result.getData());
                                List<FriendCircleItem> friendCircleItemList = gson.fromJson(dataElement, new TypeToken<List<FriendCircleItem>>() {}.getType());
                                Log.e("ShareFradsad", friendCircleItemList.get(0).toString());
                                if (friendCircleItemList != null) {

                                    // 在添加新数据之前检查是否已包含相同的数据项
                                    for (FriendCircleItem newItem : friendCircleItemList) {
                                        if (!dataSource.contains(newItem)) {
                                            dataSource.add(newItem);
                                        }
                                    }
                                    adapter.notifyDataSetChanged();

                                }
//                                handler.post(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        dataSource = friendCircleItemList;
//                                    }
//                                });
                            }
                        });

                    }
                });

            }
        }).start();

    }
}