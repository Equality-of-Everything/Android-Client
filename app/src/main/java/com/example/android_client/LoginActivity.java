package com.example.android_client;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.entity.UserLogin;
import com.example.util.OkHttpUtils;
import com.google.gson.Gson;

import java.io.IOException;


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
        String json = gson.toJson(userLogin);

        
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















