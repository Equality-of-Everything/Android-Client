package com.example.android_client;

import static com.example.android_client.LoginActivity.ip;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.util.Result;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ResetActivity extends AppCompatActivity {
    private TextInputEditText password;
    private TextInputEditText confPassword;
    private Button submit;
    private View contextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset);

        initViews();

        String mail = getIntent().getStringExtra("mail");

        submit.setOnClickListener(v -> {
            // 重置用户密码
             resetPasswordByMail(mail);
            }
        );

    }

    /**
     * @param :
     * @return void
     * @author zhang
     * @description 获取控件
     * @date 2023/12/6 15:31
     */
    private void initViews() {
        password = findViewById(R.id.edTxt_reset_password);
        confPassword = findViewById(R.id.edTxt_reset_confirm_password);
        submit = findViewById(R.id.btn_reset);
        contextView = findViewById(R.id.context_view);
    }

    /*
     * @param mail:
    	 * @param code:
      * @return void
     * @author zhang
     * @description 重置用户密码
     * @date 2023/12/6 15:28
     */
    private void resetPasswordByMail(String mail) {
        String passwordStr = password.getText().toString();
        String confPasswordStr = confPassword.getText().toString();
        if (TextUtils.isEmpty(passwordStr) || TextUtils.isEmpty(confPasswordStr)) {
            showSnackBar(contextView,"请输入完整信息后确认","我知道了");
//            Toast.makeText(this, "请输入完整信息后再确认", Toast.LENGTH_SHORT).show();
            return;
        } else if (!passwordStr.equals(confPasswordStr)) {
            showSnackBar(contextView,"两次输入的密码不一致","我知道了");
//            Toast.makeText(this, "两次输入的密码不一致", Toast.LENGTH_SHORT).show();
            return;
        }

        sendRequest(mail, passwordStr);
    }

    /*
     * @param mail:
     * @param passwordStr:
     * @return void
     * @author zhang
     * @description 向服务器端发送请求，进行重置密码
     * @date 2023/12/6 15:37
     */
    private void sendRequest(String mail, String passwordStr) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Gson json = new Gson();
                FormBody body = new FormBody.Builder()
                        .add("mail", mail)
                        .add("password", passwordStr)
                        .build();
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url("http://"+ip+":8080/user/resetPassword")
                        .post(body)
                        .build();
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                showSnackBar(contextView,"服务器错误，请稍后重试","我知道了");
//                                Toast.makeText(ResetActivity.this, "服务器错误，请稍后重试", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                        String responseStr = response.body().string();
                        Result result = json.fromJson(responseStr, Result.class);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (!result.getFlag()) {
                                    showSnackBar(contextView,result.getMsg(),"我知道了");
//                                    Toast.makeText(ResetActivity.this, result.getMsg(), Toast.LENGTH_SHORT).show();
                                    return;
                                }
//                                showSnackBar(contextView,result.getMsg(),"我知道了");
//                                Toast.makeText(ResetActivity.this, result.getMsg(), Toast.LENGTH_SHORT).show();
                                showToast(ResetActivity.this,result.getMsg());
                                Intent intent = new Intent(ResetActivity.this, LoginActivity.class);
                                startActivity(intent);
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
     * @author tcy
     * @description showSnackBar方法
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
    public void showToast(Context context, String string){
        Toast toast = new Toast(context);
        Display display = getWindowManager().getDefaultDisplay();
        int height = display.getHeight();
        //设置Toast显示位置，居中，向X,Y轴偏移量均为0
        toast.setGravity(Gravity.CENTER,0,height/3);
        //获取自定义视图
        View view = LayoutInflater.from(context).inflate(R.layout.layout_toast,null);
        TextView tvMessage = (TextView) view.findViewById(R.id.ErrorTips);
        //设置文本
        tvMessage.setText(string);
        //设置视图
        toast.setView(view);
        //设置显示时长
        toast.setDuration(Toast.LENGTH_SHORT);
        //显示
        toast.show();

    }
}