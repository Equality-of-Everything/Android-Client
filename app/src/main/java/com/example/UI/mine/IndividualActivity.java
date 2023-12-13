package com.example.UI.mine;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.UI.msg.ChatActivity;
import com.example.android_client.R;

public class IndividualActivity extends AppCompatActivity {
    private ImageView ivAvatar;
    private TextView tvUserName;
    private Button btnSendMsg;
    private String conversationId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_individual);

        init();
        setListener();

        Intent intent = getIntent();
        conversationId = intent.getStringExtra("friendId");
        if (conversationId!= null) {
            tvUserName.setText(conversationId);
        }
    }

    private void setListener() {
        btnSendMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(IndividualActivity.this, ChatActivity.class);
                intent.putExtra("conversationId", conversationId);
                startActivity(intent);
            }
        });
    }

    public void init() {
        ivAvatar = findViewById(R.id.iv_avatar);
        tvUserName = findViewById(R.id.tv_username);
        btnSendMsg = findViewById(R.id.btn_send_message);
    }
}