package com.example.UI.msg;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.UI.camera.CameraFragment;
import com.example.adapter.MsgAdapter;
import com.example.android_client.R;
import com.google.android.material.search.SearchBar;
import com.google.common.util.concurrent.ListenableFuture;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;

import java.util.List;
import java.util.concurrent.ExecutionException;


public class MessageFragment extends Fragment {

    private RecyclerView recyclerView;
    private MsgAdapter adapter;
    private SearchBar searchBar;
    private Button btnFriendRequest;
    public MessageFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_message, container, false);

        recyclerView = view.findViewById(R.id.rv_conversation);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        searchBar = view.findViewById(R.id.search_bar);
        btnFriendRequest = view.findViewById(R.id.btn_friend_request);

//         获取所有会话
        List<EMConversation> conversations = EMClient.getInstance().chatManager().getAllConversationsBySort();

//         初始化Adapter并设置给RecyclerView
        adapter = new MsgAdapter(conversations);
        recyclerView.setAdapter(adapter);

        setListener();

        return view;
    }

    /**
     * @param :
     * @return void
     * @author Lee
     * @description 为搜索框和添加按钮设置监听器
     * @note
     * @date 2023/12/23 18:58
     */
    private void setListener() {
        searchBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //跳转搜索页面
                Intent intent = new Intent(getActivity(), SearchActivity.class);
                startActivity(intent);
            }
        });
        btnFriendRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //跳转查看请求加好友列表页面
                Intent intent = new Intent(getActivity(), FriendRequestActivity.class);
                startActivity(intent);
            }
        });
    }

    private void refreshConversations() {
        List<EMConversation> conversations = EMClient.getInstance().chatManager().getAllConversationsBySort();
        adapter.updateConversations(conversations);
    }

    @Override
    public void onResume() {
        super.onResume();
    }


}
