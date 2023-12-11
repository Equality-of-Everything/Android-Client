package com.example.android_client;

import static com.example.util.Code.LOGIN_ERROR_NOUSER;
import static com.example.util.Code.LOGIN_ERROR_PASSWORD;
import static com.example.util.TokenManager.getToken;
import static com.example.util.TokenManager.isTokenExpired;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.entity.UserLogin;
import com.example.util.Result;
import com.example.util.TokenManager;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMOptions;

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
    private Button btnLogin;//点击登录
    private Button btnJumpEnroll;//跳转注册（没有账号，注册一个）
    private Button btnJumpEmail;//跳转邮箱（忘记密码，验证邮箱以重置）
    private View contextView;
    @Override
    protected void onCreate(Bundle savedInstanceState){

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        EMOptions options = new EMOptions();
        options.setAppKey("1133231211160621#android-client");
        // 其他 EMOptions 配置。
        EMClient.getInstance().init(this, options);

        init();

        Log.e("isTokenExpired", TokenManager.isTokenExpired(this)+"");

        if (isLogin()&&!TokenManager.isTokenExpired(this)) {
            jumpToMainPage();
        }

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String loginUser = edtLoginUser.getText().toString();
                String loginPwd = edtLoginPwd.getText().toString();

                if (TextUtils.isEmpty(loginUser) || TextUtils.isEmpty(loginPwd)) {
                    showSnackBar(contextView, "用户名或密码为空！", "我知道了");
                    return;
                }

                login(loginUser, loginPwd);
            }
        });

        setListener();
    }

    /*
     * @param :
      * @return boolean
     * @author zhang
     * @description 用于判断用户是否已经登录
     * @date 2023/12/8 11:00
     */
    private boolean isLogin() {
        String token = TokenManager.getToken(this);
        return token != null;
    }

    /**
     * @param :
     * @return void
     * @author Lee
     * @description 登录功能
     * @date 2023/11/29 10:08
     */
    private void login(String loginUser, String loginPwd) {
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
                        .url("http://10.7.88.235:8080/user/login")
                        .post(body)
                        .build();

                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                        Log.e("login failed", e.getMessage());
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                showSnackBar(contextView,"登录失败！","我知道了");
//                                Toast.makeText(LoginActivity.this, "登录失败！", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                        String responseData = response.body().string();
                        Result result = json.fromJson(responseData, Result.class);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //失败
                                if(!result.getFlag()) {
                                   switch (result.getCode()) {
                                       case LOGIN_ERROR_NOUSER:
                                           isJumpEnroll();
                                           break;
                                       case LOGIN_ERROR_PASSWORD:
                                           showSnackBar(contextView,"密码错误！","我知道了");
//                                           Toast.makeText(LoginActivity.this, "密码错误！", Toast.LENGTH_SHORT).show();
                                           break;
                                   }
                                    return;
                                }
                                //成功
                                //存Token
                                TokenManager.saveToken(LoginActivity.this, result.getData().toString());
                                showSnackBar(contextView,result.getMsg()+"","我知道了");
//                                Toast.makeText(LoginActivity.this, result.getMsg()+"", Toast.LENGTH_SHORT).show();

                                //登录环信账号
                                loginUser(loginUser, loginPwd);

                                //跳转主界面
                                jumpToMainPage();
                            }
                        });
                    }
                });

            }
        }).start();
        
    }

    // 用户登录
    public void loginUser(String username, String password) {
        // 调用环信 SDK 提供的登录方法
        EMClient.getInstance().login(username, password, new EMCallBack() {
            @Override
            public void onSuccess() {
                // 登录成功
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // 在登录成功后，可以在界面上显示登录成功的提示信息
                        Toast.makeText(LoginActivity.this, "成功", Toast.LENGTH_SHORT).show();
                        // 初始化环信 SDK
                        EMClient.getInstance().groupManager().loadAllGroups();
                        EMClient.getInstance().chatManager().loadAllConversations();
                    }
                });
            }

            @Override
            public void onError(int code, String error) {
                // 登录失败
                Log.e("LoginActivity", "登录失败，错误码：" + code + "，错误信息：" + error);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // 在界面上显示登录失败的提示信息
                        Toast.makeText(LoginActivity.this, "失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onProgress(int progress, String status) {
                // 登录过程中的进度回调，可以不处理
            }
        });
    }

    /**
     * @param :
     * @return void
     * @author zhang
     * @description 登录成功后跳转到主页面
     * @date 2023/12/3 11:20
     */
    private void jumpToMainPage() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
    }

    /**
    * @param :
    * @return void
    * @author xcc
    * @description 点击text跳转到注册页面
    * @date 2023/11/29 11:13
    */
    public void isJumpEnroll() {
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
        builder.setTitle("跳转提醒")
                .setMessage("用户名不存在，是否跳转注册页面？")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(LoginActivity.this, EnrollActivity.class);
                        startActivity(intent);
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        builder.create().show();
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
        btnJumpEnroll = findViewById(R.id.btn_jump_enrol);
        btnJumpEmail = findViewById(R.id.btn_jump_email);
        contextView = findViewById(R.id.context_view);
    }

    /**
     * @param :
     * @return void
     * @author Lee
     * @description 跳转注册/跳转找回密码
     * @date 2023/12/6 9:29
     */
    private void setListener() {
        btnJumpEnroll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, EnrollActivity.class);
                startActivity(intent);
            }
        });

        btnJumpEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, EmailActivity.class);
                startActivity(intent);
            }
        });
    }

    /**
     * @param :
     * @return void
     * @author tcy
     * @description 点击text跳转到注册页面
     * @date 2023/12/7
     */
    public void showSnackBar(View view,String txt,String btnTxt){
        Snackbar snackbar = Snackbar.make(view, txt, Snackbar.LENGTH_LONG);
        snackbar.setAction(btnTxt, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 处理撤销逻辑
            }
        });
        snackbar.show();

    }

}















