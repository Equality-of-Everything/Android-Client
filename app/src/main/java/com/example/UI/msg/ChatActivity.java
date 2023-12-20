package com.example.UI.msg;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.adapter.ChatAdapter;
import com.example.android_client.R;
import com.google.android.material.appbar.MaterialToolbar;
import com.hyphenate.EMCallBack;
import com.hyphenate.EMMessageListener;
import com.hyphenate.EMValueCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMCursorResult;
import com.hyphenate.chat.EMMessage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ChatActivity extends AppCompatActivity {
    private MaterialToolbar toolbar;
    private ListView msgListView;
    private EditText msgInput;
    private Button btnSend;
    private ChatAdapter msgAdapter;
    private String conversationId;
    List<EMMessage> messagesList = new ArrayList<>();// 获取消息数据
    EMMessageListener msgListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        setSupportActionBar(toolbar);

        init();

        //获取本地所有会话
        List<EMConversation> conversations = EMClient.getInstance().chatManager().getAllConversationsBySort();

        // msgId：要获取消息的消息 ID。
        EMMessage msg = EMClient.getInstance().chatManager().getMessage(conversationId);

        //从上一个界面获取对方用户的ID
        Intent intent = getIntent();
        conversationId = intent.getStringExtra("conversationId");

        toolbarListener(conversationId);//顶部导航栏的监听器


        // 设置消息适配器
        msgAdapter = new ChatAdapter(this, messagesList);
        msgListView.setAdapter(msgAdapter);

        // 异步获取与特定好友的所有聊天记录
        EMClient.getInstance().chatManager().asyncFetchHistoryMessage(
                conversationId,
                EMConversation.EMConversationType.Chat,
                20, // 一次获取的消息数量
                null, // 起始消息ID，可为null
                EMConversation.EMSearchDirection.UP, // 查询方向
                new EMValueCallBack<EMCursorResult<EMMessage>>() {
                    @Override
                    public void onSuccess(EMCursorResult<EMMessage> value) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                List<EMMessage> allMessages = value.getData();
                                messagesList.addAll(allMessages);
                                msgAdapter.notifyDataSetChanged();
                                msgListView.smoothScrollToPosition(messagesList.size() - 1);

                                // 批量导入消息到本地数据库
                                EMClient.getInstance().chatManager().importMessages(allMessages);
                            }
                        });

                    }

                    @Override
                    public void onError(int error, String errorMsg) {
                        // 处理异常情况
                        Toast.makeText(ChatActivity.this, "获取聊天记录失败", Toast.LENGTH_SHORT).show();
                    }
                }
        );


        receiveMsg();//接收消息
        setListener();//发送消息

    }

    private void toolbarListener(String name) {
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();//返回上一个界面
            }
        });

        // 设置标题文字
        toolbar.setTitle(name);
    }


    private void receiveMsg() {
        msgListener = new EMMessageListener() {

            // 收到消息，遍历消息队列，解析和显示。
            @Override
            public void onMessageReceived(List<EMMessage> messages) {
                //更新UI
                runOnUiThread(() -> {
                    // 清空原有的消息列表
//                    messagesList.clear();
                    // 将收到的所有消息添加到消息列表中
                    messagesList.addAll(messages);

                    // 刷新适配器
                    msgAdapter.notifyDataSetChanged();
                    msgListView.smoothScrollToPosition(messagesList.size() - 1);
                });
            }
        };
        // 注册消息监听
        EMClient.getInstance().chatManager().addMessageListener(msgListener);
    }

    private void setListener() {
        btnSend.setOnClickListener(v -> {
            String editContent = msgInput.getText().toString();
            if (!editContent.isEmpty()) {
                EMMessage message = EMMessage.createTxtSendMessage(editContent, conversationId);
                message.setChatType(EMMessage.ChatType.Chat);
                message.setMessageStatusCallback(new EMCallBack() {
                    @Override
                    public void onSuccess() {
                        runOnUiThread(() -> {
                            Toast.makeText(ChatActivity.this, "发送成功", Toast.LENGTH_SHORT).show();
                            messagesList.add(message);
                            msgAdapter.notifyDataSetChanged();
                            msgListView.smoothScrollToPosition(messagesList.size() - 1);
                        });
                    }

                    @Override
                    public void onError(int code, String error) {
                        runOnUiThread(() -> Toast.makeText(ChatActivity.this, "发送失败", Toast.LENGTH_SHORT).show());
                    }

                    @Override
                    public void onProgress(int progress, String status) {

                    }
                });
                EMClient.getInstance().chatManager().sendMessage(message);

                msgInput.setText("");
            }
        });
    }

    @Override
    protected void onDestroy() {
        // 解注册消息监听
        EMClient.getInstance().chatManager().removeMessageListener(msgListener);
        super.onDestroy();
        // 取消所有的图片加载操作
//        Glide.with(this).pauseRequests();
    }

    @Override
    protected void onStop() {
        super.onStop();
        // 恢复所有的图片加载操作
        Glide.with(this).resumeRequests();
    }

    public void init() {
        msgListView = findViewById(R.id.msg_list);
        msgInput = findViewById(R.id.input_msg);
        btnSend = findViewById(R.id.btn_send);
        toolbar = findViewById(R.id.topAppBar_name);
    }


}