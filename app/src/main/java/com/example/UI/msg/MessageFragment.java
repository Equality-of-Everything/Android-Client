package com.example.UI.msg;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.adapter.MsgAdapter;
import com.example.android_client.R;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;

import java.util.List;


public class MessageFragment extends Fragment {

    private RecyclerView recyclerView;
    private MsgAdapter adapter;

    public MessageFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_message, container, false);

        recyclerView = view.findViewById(R.id.rv_conversation);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

//         获取所有会话
        List<EMConversation> conversations = EMClient.getInstance().chatManager().getAllConversationsBySort();

//         初始化Adapter并设置给RecyclerView
        adapter = new MsgAdapter(conversations);
        recyclerView.setAdapter(adapter);

        return view;
    }

    private void refreshConversations() {
        List<EMConversation> conversations = EMClient.getInstance().chatManager().getAllConversationsBySort();
        adapter.updateConversations(conversations);
    }

}
