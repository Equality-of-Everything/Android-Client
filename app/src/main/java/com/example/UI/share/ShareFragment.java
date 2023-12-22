package com.example.UI.share;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.adapter.DynamicAdapter;
import com.example.android_client.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.common.util.concurrent.ListenableFuture;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ShareFragment extends Fragment {
    private RecyclerView recyclerView;
    private FloatingActionButton shareEditButton;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View ShareView = inflater.inflate(R.layout.fragment_share, container, false);
        recyclerView = ShareView.findViewById(R.id.share_recyclerView);
        shareEditButton = ShareView.findViewById(R.id.share_edit_button);
        shareEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), ShareEditActivity.class);
                startActivity(intent);
            }
        });
        // 设置RecyclerView的布局管理器和适配器
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        List<FriendCircleItem> friendCircleItemList = getFriendCircleItemList();
        DynamicAdapter adapter = new DynamicAdapter(friendCircleItemList,getContext());
        //设置适配器
        recyclerView.setAdapter(adapter);
        return ShareView;
    }
    //获取朋友圈数据,
    //TODO: 这里需要从服务器获取数据,还需要评论内容，发布评论的人，点赞的人，点赞数，评论数
    private List<FriendCircleItem> getFriendCircleItemList() {
        FriendCircleItem item1 = new FriendCircleItem("xcc", "https://picx.zhimg.com/80/v2-da2b0a3b96103d87a682409fc5a261a9_720w.webp?source=1def8aca", "今天", Arrays.asList("https://tupian.qqw21.com/article/UploadPic/2021-4/202141120475135553.jpg"),"2021-04-20 12:00:00");
        FriendCircleItem item2 = new FriendCircleItem("lee", "https://picx.zhimg.com/80/v2-da2b0a3b96103d87a682409fc5a261a9_720w.webp?source=1def8aca", "平平无奇的朋友圈", Arrays.asList("https://tupian.qqw21.com/article/UploadPic/2021-4/202141120475135553.jpg","https://tupian.qqw21.com/article/UploadPic/2021-4/202141120475135553.jpg"),"2023-04-20 12:00:00");
        FriendCircleItem item3 = new FriendCircleItem("哇嘎嘎", "https://picx.zhimg.com/80/v2-da2b0a3b96103d87a682409fc5a261a9_720w.webp?source=1def8aca", "看什么看", Arrays.asList("https://tupian.qqw21.com/article/UploadPic/2021-4/202141120475135553.jpg","https://tupian.qqw21.com/article/UploadPic/2021-4/202141120475135553.jpg","https://tupian.qqw21.com/article/UploadPic/2021-4/202141120475135553.jpg","https://tupian.qqw21.com/article/UploadPic/2021-4/202141120475135553.jpg","https://tupian.qqw21.com/article/UploadPic/2021-4/202141120475135553.jpg","https://tupian.qqw21.com/article/UploadPic/2021-4/202141120475135553.jpg"),"2023-04-20 12:00:00");
        FriendCircleItem item4 = new FriendCircleItem("某人", "https://picx.zhimg.com/80/v2-da2b0a3b96103d87a682409fc5a261a9_720w.webp?source=1def8aca", "你好，这是第四条", Arrays.asList("https://tupian.qqw21.com/article/UploadPic/2021-4/202141120475135553.jpg","https://tupian.qqw21.com/article/UploadPic/2021-4/202141120475135553.jpg","https://tupian.qqw21.com/article/UploadPic/2021-4/202141120475135553.jpg","https://tupian.qqw21.com/article/UploadPic/2021-4/202141120475135553.jpg","https://tupian.qqw21.com/article/UploadPic/2021-4/202141120475135553.jpg","https://tupian.qqw21.com/article/UploadPic/2021-4/202141120475135553.jpg","https://tupian.qqw21.com/article/UploadPic/2021-4/202141120475135553.jpg","https://tupian.qqw21.com/article/UploadPic/2021-4/202141120475135553.jpg","https://tupian.qqw21.com/article/UploadPic/2021-4/202141120475135553.jpg","https://tupian.qqw21.com/article/UploadPic/2021-4/202141120475135553.jpg","https://tupian.qqw21.com/article/UploadPic/2021-4/202141120475135553.jpg","https://tupian.qqw21.com/article/UploadPic/2021-4/202141120475135553.jpg"),"2023-04-20 12:00:00");
        FriendCircleItem item5 = new FriendCircleItem("同学", "https://picx.zhimg.com/80/v2-da2b0a3b96103d87a682409fc5a261a9_720w.webp?source=1def8aca", "你好，这是第五条朋友圈", Arrays.asList("https://tupian.qqw21.com/article/UploadPic/2021-4/202141120475135553.jpg","https://tupian.qqw21.com/article/UploadPic/2021-4/202141120475135553.jpg","https://tupian.qqw21.com/article/UploadPic/2021-4/202141120475135553.jpg","https://tupian.qqw21.com/article/UploadPic/2021-4/202141120475135553.jpg","https://tupian.qqw21.com/article/UploadPic/2021-4/202141120475135553.jpg","https://tupian.qqw21.com/article/UploadPic/2021-4/202141120475135553.jpg","https://tupian.qqw21.com/article/UploadPic/2021-4/202141120475135553.jpg","https://tupian.qqw21.com/article/UploadPic/2021-4/202141120475135553.jpg","https://tupian.qqw21.com/article/UploadPic/2021-4/202141120475135553.jpg","https://tupian.qqw21.com/article/UploadPic/2021-4/202141120475135553.jpg","https://tupian.qqw21.com/article/UploadPic/2021-4/202141120475135553.jpg","https://tupian.qqw21.com/article/UploadPic/2021-4/202141120475135553.jpg","https://tupian.qqw21.com/article/UploadPic/2021-4/202141120475135553.jpg","https://tupian.qqw21.com/article/UploadPic/2021-4/202141120475135553.jpg","https://tupian.qqw21.com/article/UploadPic/2021-4/202141120475135553.jpg","https://tupian.qqw21.com/article/UploadPic/2021-4/202141120475135553.jpg","https://tupian.qqw21.com/article/UploadPic/2021-4/202141120475135553.jpg","https://tupian.qqw21.com/article/UploadPic/2021-4/202141120475135553.jpg","https://tupian.qqw21.com/article/UploadPic/2021-4/202141120475135553.jpg","https://tupian.qqw21.com/article/UploadPic/2021-4/202141120475135553.jpg","https://tupian.qqw21.com/article/UploadPic/2021-4/202141120475135553.jpg","https://tupian.qqw21.com/article/UploadPic/2021-4/202141120475135553.jpg"),"2023-04-20 12:00:00");
        FriendCircleItem item6 = new FriendCircleItem("同学", "https://picx.zhimg.com/80/v2-da2b0a3b96103d87a682409fc5a261a9_720w.webp?source=1def8aca", "你好，这是第六条", Arrays.asList("https://tupian.qqw21.com/article/UploadPic/2021-4/202141120475135553.jpg","https://tupian.qqw21.com/article/UploadPic/2021-4/202141120475135553.jpg","https://tupian.qqw21.com/article/UploadPic/2021-4/202141120475135553.jpg","https://tupian.qqw21.com/article/UploadPic/2021-4/202141120475135553.jpg"),"2023-04-20 12:00:00");
        return new ArrayList<FriendCircleItem>() {{
            add(item1);
            add(item2);
            add(item3);
            add(item4);
            add(item5);
            add(item6);
        }};
    }
}