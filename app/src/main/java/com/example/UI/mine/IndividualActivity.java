package com.example.UI.mine;

import static com.example.android_client.LoginActivity.ip;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.UI.msg.ChatActivity;
import com.example.android_client.R;
import com.example.entity.UserInfo;
import com.example.util.Result;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class IndividualActivity extends AppCompatActivity {
    private ImageView ivAvatar;//头像
    private TextView tvUserName;//用户名
    private TextView tvGender;//性别
    private TextView tvBrithday;//生日
    private TextView tvEmail;//邮箱

    private TextView tvSignature;//签名
    private TextView tvLastPublish;
    private Button btnSendMsg;//发信息按钮
    private Button btnBack;//返回按钮
    private String conversationId;//用户名

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

        httpRequest();//向后端数据库请求数据，显示在页面上
    }

    // 使用OkHttp发送HTTP请求并获取数据
    private void httpRequest() {
        //前置准备
        Gson json = new Gson();

        FormBody body = new FormBody.Builder()
                .add("username", conversationId)
                .build();

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("http://"+ip+":8080/userInfo/getUserInfo")
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e("IndividualActivity", "请求后端数据失败");
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                Log.e("IndividualActivity", "请求后端数据成功");

                String responseData = response.body().string();
                Result result = json.fromJson(responseData, Result.class);
                if(result.getFlag()) {
                    Log.e("IndividualActivity", "后端响应请求成功");
                    if (result.getData() != null) {
                        String responseD = json.toJson(result.getData());
                        UserInfo userInfo = json.fromJson(responseD, UserInfo.class); // 修正此处的变量名
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                tvGender.setText(userInfo.getGender());
                                tvBrithday.setText(userInfo.getBirthday());
                                tvEmail.setText(userInfo.getEmail());
                                tvSignature.setText(userInfo.getSignature());
                                Log.e("IndividualActivity", userInfo.getGender()+" "+userInfo.getBirthday()+" "+userInfo.getEmail()+" "+userInfo.getSignature());
                            }
                        });
                    }
                } else {
                    Log.e("IndividualActivity", "后端响应请求失败");
                }
            }
        });
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
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();//返回上一个页面
            }
        });
    }

    public void init() {
        btnBack = findViewById(R.id.btn_back);
        ivAvatar = findViewById(R.id.iv_individual_avatar);
        tvUserName = findViewById(R.id.tv_individual_username);
        tvGender = findViewById(R.id.tv_individual_gender);
        tvBrithday = findViewById(R.id.tv_individual_brithday);
        tvEmail = findViewById(R.id.tv_individual_email);
        tvSignature = findViewById(R.id.tv_individual_signature);
        tvLastPublish = findViewById(R.id.tv_individual_last_publish);
        btnSendMsg = findViewById(R.id.btn_send_message);
    }
}