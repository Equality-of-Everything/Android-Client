package com.example.android_client;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.util.Patterns;
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

public class EmailActivity extends AppCompatActivity {
    private TextInputEditText email;
    private TextInputEditText code;
    private Button txtNum;
    private Button btnVerify;
    private View contextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email);

        initViews();

        // 为发送验证码按钮绑定点击事件
        setOnClickListenerForTxtNum(email,code);

        // 为验证按钮绑定点击事件
        setOnClickListenerForBtnVerify(email,code);

    }

    /**
     * @param :
     * @param email
     * @param code
     * @return void
     * @author zhang
     * @description 为验证按钮绑定点击事件
     * @date 2023/12/6 10:19
     */
    private void setOnClickListenerForBtnVerify(TextInputEditText email, TextInputEditText code) {
       btnVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifyMailPatternAndEmpty(email, code, R.id.btn_verify);
            }
        });
    }

    /**
     * @param :
     * @param email
     * @param code
     * @return void
     * @author zhang
     * @description 为发送验证码按钮绑定点击事件
     * @date 2023/12/6 10:19
     */
    private void setOnClickListenerForTxtNum(TextInputEditText email, TextInputEditText code) {
        txtNum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifyMailPatternAndEmpty(email, code, R.id.Txt_verify_number);
            }
        });
    }

    /*
     * @param :
      * @return CountDownTimer
     * @author zhang
     * @description 发送验证码倒计时功能
     * @date 2023/12/6 14:49
     */
    private CountDownTimer getCountDownTimerButton() {
        // 倒计时1分钟
        CountDownTimer countDownTimer = new CountDownTimer(60*1000,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                txtNum.setEnabled(false);
                txtNum.setText("已发送(" + millisUntilFinished / 1000 + ")");
            }

            @Override
            public void onFinish() {
                txtNum.setEnabled(true);
                txtNum.setText("获取验证码");
            }
        };
        return countDownTimer;
    }

    /**
     * @param mailStr:
     * @return void
     * @author zhang
     * @description 获取验证码
     * @date 2023/12/6 10:30
     */
    private void sendMailCode(String mailStr) {
        new Thread(new Runnable() {
            Gson json = new Gson();
            @Override
            public void run() {
                OkHttpClient client = new OkHttpClient();//创建OkHttpClient对象
                String url = "http://10.7.88.235:8080/mail/sendCode?mail=" + mailStr;
                Request request = new Request.Builder()
                        .url(url)
                        .get()
                        .build();
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                showSnackBar(contextView,"服务器错误，请稍后再试","我知道了");
//                                Toast.makeText(EmailActivity.this, "服务器错误，请稍后重试", Toast.LENGTH_SHORT).show();
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
                                if (!result.getFlag()) {
                                    showSnackBar(contextView,result.getMsg(),"我知道了");
//                                    Toast.makeText(EmailActivity.this, result.getMsg(), Toast.LENGTH_SHORT).show();
                                }
                                showSnackBar(contextView,result.getMsg(),"我知道了");
//                                Toast.makeText(EmailActivity.this, result.getMsg(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            }
        }).start();
    }

    /**
     * @param email:
     * @param code:
     * @param btnId: 传入按钮Id
     * @return void
     * @author zhang
     * @description 用于验证邮箱格式和非空判断
     * @date 2023/12/6 10:15
     */
    private void verifyMailPatternAndEmpty(TextInputEditText email, TextInputEditText code,int btnId) {
        switch (btnId) {
            case R.id.btn_verify:
                if (TextUtils.isEmpty(email.getText()+"") || TextUtils.isEmpty(code.getText()+"")) {
                    showSnackBar(contextView,"请输入完整信息后再要求验证","我知道了");
//                    Toast.makeText(this, "请输入完整信息后再要求验证", Toast.LENGTH_SHORT).show();
                    return;
                } else if (!isValidEmail(email.getText()+"")) {
                    showSnackBar(contextView,"邮箱格式不正确，请输入正确邮箱","我知道了");
//                    Toast.makeText(this, "邮箱格式不正确，请输入正确邮箱", Toast.LENGTH_SHORT).show();
                    return;
                }
                verifyMailCode(email.getText().toString().trim(), code.getText().toString().trim());
                break;
            case R.id.Txt_verify_number:
                if ( TextUtils.isEmpty(email.getText()+"") ) {
                    showSnackBar(contextView,"请输入邮箱","我知道了");
//                    Toast.makeText(this, "请输入邮箱", Toast.LENGTH_SHORT).show();
                    return;
                } else if (!isValidEmail(email.getText()+"")) {
                    showSnackBar(contextView,"邮箱格式不正确，请输入正确邮箱","我知道了");
//                    Toast.makeText(this, "邮箱格式不正确，请输入正确邮箱", Toast.LENGTH_SHORT).show();
                    return;
                }
                CountDownTimer countDownTimerButton = getCountDownTimerButton();
                countDownTimerButton.start();
                String mailStr = email.getText().toString().trim();
                sendMailCode(mailStr);
                break;
            }
    }

    /*
     * @param mail:
    	 * @param code:
      * @return void
     * @author zhang
     * @description 进行邮箱验证码验证
     * @date 2023/12/6 15:01
     */
    private void verifyMailCode(String mail, String code) {
        Gson json = new Gson();
        OkHttpClient client = new OkHttpClient();//创建OkHttpClient对象
        FormBody formBody = new FormBody.Builder()
               .add("mail", mail)
               .add("code", code)
               .build();
        Request request = new Request.Builder()
               .url("http://10.7.88.235:8080//mail/checkCode")
               .post(formBody)
               .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showSnackBar(contextView,"服务器错误，请稍后重试","我知道了");
//                        Toast.makeText(EmailActivity.this, "服务器错误，请稍后重试", Toast.LENGTH_SHORT).show();
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
                        if (!result.getFlag()) {
                            showSnackBar(contextView,result.getMsg(),"我知道了");
//                            Toast.makeText(EmailActivity.this, result.getMsg(), Toast.LENGTH_SHORT).show();
                            return;
                        }
//                        showSnackBar(contextView,result.getMsg(),"我知道了");
                        Toast.makeText(EmailActivity.this, result.getMsg(), Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(EmailActivity.this, ResetActivity.class);
                        intent.putExtra("mail", mail + "");
                        startActivity(intent);
                    }
                });
            }
        });
    }

    private void initViews() {
        email = findViewById(R.id.edTxt_verify_email);
        code = findViewById(R.id.edtTxt_verify_number);
        btnVerify = findViewById(R.id.btn_verify);
        txtNum = findViewById(R.id.Txt_verify_number);
        contextView = findViewById(R.id.context_view);
    }

    /**
     * @author xcc
     * @description 验证用户输入的邮箱格式是否合法
     * @date 2023/11/29 15:39
     */
    public boolean isValidEmail(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
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

}