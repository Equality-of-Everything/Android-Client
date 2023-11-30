package com.example.android_client;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.entity.UserLogin;
import com.example.util.Reslut;
import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class LoginActivity extends AppCompatActivity {
    private EditText edtLoginUser, edtLoginPwd;
    private Button btnLogin;
    @Override
    protected void onCreate(Bundle savedInstanceState){

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String loginUser = edtLoginUser.getText().toString();
                String loginPwd = edtLoginPwd.getText().toString();

                login(loginUser, loginPwd);
            }
        });

    }


    /**
     * @param :
     * @return void
     * @author Lee
     * @description 登录功能
     * @date 2023/11/29 10:08
     */
    private void login(String loginUser, String loginPwd) {
        String url = "";
        UserLogin userLogin = new UserLogin();
        userLogin.setUsername(loginUser);
        userLogin.setPassword(loginPwd);

        Gson gson = new Gson();
        String formBody = gson.toJson(userLogin);
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(JSON, formBody);

        new Thread(new Runnable() {
            Gson json = new Gson();
            @Override
            public void run() {
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url("http://192.168.104.223:8080/user/login")
                        .post(body)
                        .build();

                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                        Log.e("login failed", e.getMessage());
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(LoginActivity.this, "登录失败！", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                        String responseData = response.body().string();
                        Reslut reslut = json.fromJson(responseData, Reslut.class);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(!reslut.getFlag()) {
                                    Toast.makeText(LoginActivity.this, reslut.getMsg()+"", Toast.LENGTH_SHORT).show();
                                }
                                Toast.makeText(LoginActivity.this, reslut.getMsg()+"", Toast.LENGTH_SHORT).show();
                                //跳转主界面
                            }
                        });
                    }
                });

            }
        }).start();
        
    }

    /**
     * @param :
     * @return void
     * @author Lee
     * @description 初始化登录界面控件
     * @date 2023/11/29 10:07
     */
    public void init() {
        edtLoginUser = findViewById(R.id.edt_login_user);
        edtLoginPwd = findViewById(R.id.edt_login_pwd);
        btnLogin = findViewById(R.id.btn_login);
    }

    /**
    * @param :
    * @return void
    * @author xcc
    * @description 点击text跳转到注册页面
    * @date 2023/11/29 11:13
    */
    public void jumpEnroll(View view) {
        Intent intent = new Intent(this, EnrollActivity.class);
        startActivity(intent);
    }

    /**
     * @param :
     * @return Button
     * @author Lee
     * @description TODO
     * @date 2023/11/29 16:14
     */
    public Button getBtnLogin() {
        return btnLogin;
    }


}















