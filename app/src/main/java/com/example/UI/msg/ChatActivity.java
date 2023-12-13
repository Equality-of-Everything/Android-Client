package com.example.UI.msg;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.adapter.MsgAdapter;
import com.example.android_client.R;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {
    private ListView msgListView;
    private EditText msgInput;
    private Button btnSend;
    private MsgAdapter msgAdapter;
    private String conversationId;
    List<EMMessage> messages = new ArrayList<>();// 获取消息数据

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        init();

        msgAdapter = new MsgAdapter(this, messages);
        msgListView.setAdapter(msgAdapter);

        setListener();

        //从上一个界面获取对方用户的ID
        Intent intent = getIntent();
        conversationId = intent.getStringExtra("conversationId");
    }

    private void setListener() {
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String editContent = msgInput.getText().toString();
                if(!editContent.isEmpty()) {
                    // 创建一条文本消息，`content` 为消息文字内容。
                    // `conversationId` 为消息接收方，单聊时为对端用户 ID、群聊时为群组 ID，聊天室时为聊天室 ID。
                    EMMessage message = EMMessage.createTextSendMessage(editContent, conversationId);
                    // 会话类型：单聊为 EMMessage.ChatType.Chat，群聊为 EMMessage.ChatType.GroupChat, 聊天室为EMMessage.ChatType.ChatRoom，默认为单聊。
                    message.setChatType(EMMessage.ChatType.Chat);

                    // 发送消息时可以设置 `EMCallBack` 的实例，获得消息发送的状态。可以在该回调中更新消息的显示状态。例如消息发送失败后的提示等等。
                    message.setMessageStatusCallback(new EMCallBack() {
                        @Override
                        public void onSuccess() {
                            // 发送消息成功
                            Toast.makeText(ChatActivity.this, "发送消息成功", Toast.LENGTH_SHORT).show();
                        }
                        @Override
                        public void onError(int code, String error) {
                            // 发送消息失败
                            Toast.makeText(ChatActivity.this, "发送消息失败", Toast.LENGTH_SHORT).show();
                        }
                        @Override
                        public void onProgress(int progress, String status) {

                        }

                    });
                    // 发送消息。
                    EMClient.getInstance().chatManager().sendMessage(message);

                    // 将发送的消息添加到消息列表中
                    messages.add(message);

                    msgAdapter.notifyDataSetChanged();

                    // 清空输入框
                    msgInput.setText("");
                }
            }
        });
    }

    public void init() {
        msgListView = findViewById(R.id.msg_list);
        msgInput = findViewById(R.id.input_msg);
        btnSend = findViewById(R.id.btn_send);
    }


}