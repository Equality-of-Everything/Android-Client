package com.example.UI.msg;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.adapter.ChatAdapter;
import com.example.android_client.R;
import com.hyphenate.EMCallBack;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {
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

        init();

        msgAdapter = new ChatAdapter(this, messagesList);
        msgListView.setAdapter(msgAdapter);

        //从上一个界面获取对方用户的ID
        Intent intent = getIntent();
        conversationId = intent.getStringExtra("conversationId");

        receiveMsg();//接收消息
        setListener();//发送消息
    }


    private void receiveMsg() {
        msgListener = new EMMessageListener() {

            // 收到消息，遍历消息队列，解析和显示。
            @Override
            public void onMessageReceived(List<EMMessage> messages) {
                //更新UI
                runOnUiThread(() -> {
                    // 收到新消息时，直接将其添加到消息列表中
                    messagesList.addAll(messages);
                    msgAdapter.notifyDataSetChanged();
                    // 滚动到最后一条消息
                    msgListView.smoothScrollToPosition(msgAdapter.getCount() - 1);
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
    }

    public void init() {
        msgListView = findViewById(R.id.msg_list);
        msgInput = findViewById(R.id.input_msg);
        btnSend = findViewById(R.id.btn_send);
    }


}