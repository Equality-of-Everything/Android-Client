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
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;

import java.util.ArrayList;
import java.util.Collections;
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

        //获取本地所有会话
        List<EMConversation> conversations = EMClient.getInstance().chatManager().getAllConversationsBySort();
//        //在界面上显示所有会话

//        for (EMConversation conversation : conversations) {
//            List<EMMessage> messages = conversation.getAllMessages();
//            messagesList.addAll(messages);
//        }
        // msgId：要获取消息的消息 ID。
        EMMessage msg = EMClient.getInstance().chatManager().getMessage(conversationId);

        msgAdapter = new ChatAdapter(this, messagesList);
        msgListView.setAdapter(msgAdapter);

        //从上一个界面获取对方用户的ID
        Intent intent = getIntent();
        conversationId = intent.getStringExtra("conversationId");

        // 根据会话ID获取特定会话的消息，并设置适配器
        for (EMConversation conversation : conversations) {
            String conversationId = conversation.conversationId();
            if (conversationId.equals(this.conversationId)) {
                List<EMMessage> messages = conversation.getAllMessages();
                messagesList.clear(); // 清空原有的消息列表
                messagesList.addAll(messages); // 将特定会话的消息添加到消息列表中
                msgAdapter = new ChatAdapter(this, messagesList);
                msgListView.setAdapter(msgAdapter);
                break; // 找到特定会话后退出循环
            }
        }


        receiveMsg();//接收消息
        setListener();//发送消息

        // 批量导入消息到本地数据库
        EMClient.getInstance().chatManager().importMessages(messagesList);
        // 正在使用 `EMConversation` 类时：先获取会话，再更新 SDK 本地数据库会话中的消息
        EMConversation conversation = EMClient.getInstance().chatManager().getConversation(conversationId);
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
                    //gundong
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
                            //gundong
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