package com.example.android_client;

import static com.example.android_client.LoginActivity.ip;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.entity.ErrorInfo;
import com.example.entity.UserLogin;
import com.example.util.Result;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMOptions;
import com.hyphenate.exceptions.HyphenateException;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import okhttp3.*;

/**
 * 注册页面
 */
public class EnrollActivity extends AppCompatActivity {

    private EditText edt_username;
    private EditText edt_password,edt_password_conf;
    private EditText edt_email;
    private Button btn_register;
    private View contextView;
    private View dialogView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enroll);

        // 获取控件
        getViews();

        // 用于注册信息发送和密码重复验证
        setOnClickListenerForRegister();
    }

    /**
     * @param :
     * @return void
     * @author zhang
     * @description 密码验证与注册信息发送交互
     * @date 2023/11/29 16:21
     */
    private void setOnClickListenerForRegister() {
        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MaterialAlertDialogBuilder adBuilder = new MaterialAlertDialogBuilder(EnrollActivity.this);
                String username = edt_username.getText()+"";
                String password = edt_password.getText()+"";
                String password_conf = edt_password_conf.getText()+"";
                String email = edt_email.getText() + "";

                // 用于存放错误对应处理信息
                LinkedHashMap<Boolean, ErrorInfo> validationRules = new LinkedHashMap<>();

                // 存放各种错误处理对应信息
                getErrorInfoByRegister(username,password,password_conf,email,validationRules);

                for (LinkedHashMap.Entry<Boolean, ErrorInfo> entry : validationRules.entrySet()) {
                    if (entry.getKey()) {
                        ErrorInfo errorInfo = entry.getValue();
                        adBuilder.setTitle(errorInfo.getTitle())
                                .setMessage(errorInfo.getMsg());
                        adBuilder.setPositiveButton("我知道了", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
// Respond to positive button press
                            }
                        });
                        adBuilder.show();
                        return;
                    }
                }

                registerApply(username,password,email);

            }
        });
    }

    /**
     * @param username:
     * @param password:
     * @param password_conf:
     * @param email:
     * @param validationRules:
     * @return void
     * @author zhang
     * @description 用于存放各种错误信息的处理对应方式，如用户名是否存在，密码是否符合规范等
     * @date 2023/11/29 20:09
     */
    private void getErrorInfoByRegister(String username, String password, String password_conf, String email,
                                        Map<Boolean, ErrorInfo> validationRules) {

        validationRules.put(!password.equals(password_conf),
                new ErrorInfo("注册提示", "两次输入的密码不一致,请再次确认"));


        validationRules.put(!isValidPassword(password),
                new ErrorInfo("错误提醒", "密码长度至少8位，请确认您输入的密码符合规范"));

        validationRules.put(!isValidEmail(email),
                new ErrorInfo("错误提醒", "邮箱格式错误，请输入正确的邮箱地址"));

        validationRules.put(!isValidUsername(username),
                new ErrorInfo("错误提醒", "用户名不符合规则，请输入合法用户名"));

        validationRules.put(TextUtils.isEmpty(username) || TextUtils.isEmpty(password)
                        || TextUtils.isEmpty(password_conf) || TextUtils.isEmpty(email),
                new ErrorInfo("错误提醒", "请输入完整信息后再进行注册！"));

    }

    /**
     * @param username:
     * @param password:
     * @param email:
     * @return void
     * @author zhang
     * @description 用于使用OkHttp发送注册申请
     * @date 2023/11/29 16:41
     */
    private void registerApply(String username, String password, String email) {
        Gson json = new Gson();
        UserLogin user = new UserLogin(username,password,email);
        String formBody = json.toJson(user);
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(JSON,formBody);
        new Thread(new Runnable() {
            Gson json = new Gson();
            @Override
            public void run() {
                OkHttpClient client = new OkHttpClient();//创建OkHttpClient对象
                Request request = new Request.Builder()
                       .url("http://"+ip+":8080/user/register")
                       .post(body)
                       .build();
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(@NonNull Call call,@NonNull IOException e) {
                        Log.e("注册失败",e.getMessage());
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                showSnackBar(contextView,"注册失败","我知道了");
//                                Toast.makeText(EnrollActivity.this,"注册失败",Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onResponse(@NonNull Call call,@NonNull Response response) throws IOException {
                        String responseData = response.body().string();
                        Result result = json.fromJson(responseData, Result.class);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (!result.getFlag()) {
                                    showSnackBar(contextView,result.getMsg(),"我知道了");
//                                    Toast.makeText(EnrollActivity.this,result.getMsg()+"",Toast.LENGTH_SHORT).show();
                                }
//                                showSnackBar(contextView,result.getMsg(),"我知道了");
//                                Toast.makeText(EnrollActivity.this, result.getMsg() + "", Toast.LENGTH_SHORT).show();
                                showToast(EnrollActivity.this,result.getMsg());

                                //注册一个环信账号（以支持即时通讯的一系列服务）
                                registerUser(username, password);

                                Intent intent = new Intent(EnrollActivity.this, LoginActivity.class);
                                startActivity(intent);
                            }
                        });
                    }
                });

            }
        }).start();
    }

    /**
     * @param username:
     * @param password:
     * @return void
     * @author Lee
     * @description 注册一个环信账号（以支持即时通讯一系列的服务）
     * @date 2023/12/11 17:16
     */
    public void registerUser(String username, String password) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // 调用环信 SDK 提供的注册方法
                    EMClient.getInstance().createAccount(username, password);
                    // 注册成功
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // 在注册成功后，可以在界面上显示注册成功的提示信息
                        }
                    });
                } catch (HyphenateException e) {
                    // 注册失败
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // 在界面上显示注册失败的提示信息
                        }
                    });
                }
            }
        }).start();
    }

    private void getViews() {
        edt_username = findViewById(R.id.edtTxt_enter_name);
        edt_password_conf = findViewById(R.id.edTxt_enter_confirm_password);
        edt_password = findViewById(R.id.edTxt_enter_password);
        edt_email = findViewById(R.id.edTxt_enter_email);
        btn_register = findViewById(R.id.btn_enroll);
        contextView = findViewById(R.id.context_view);
        dialogView = findViewById(R.id.dialog_view);
    }

    /**
     * @author xcc
     * @description 用户名只能由字母、数字和下划线组成，且长度至少为1
     * @date 2023/11/29 11:49
     * 测试代码
     * //        String userInput = "@";
     * //        if (isValidUsername(userInput)) {
     * //            System.out.println("成功！！！！！！！！！！");
     * //        } else {
     * //            // 用户名不合法，给出错误提示
     * //            System.out.println("错误！！！！！！！！！！");
     * //        }
     */
    public boolean isValidUsername(String username) {
        String pattern = "^[a-zA-Z0-9_]+$";
        return username.matches(pattern);
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
     * @author dell
     * @description 验证用户两次输入的密码一致，要求密码长度至少8位，且没有特殊字符
     * @date 2023/11/29 15:57
     */
    public boolean isValidPassword(String password) {
        // 密码长度至少8位，且没有特殊字符
        String pattern = "^[a-zA-Z0-9]{8,}$";
        return password.matches(pattern);
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

/**
 * @author xcc
 * @description
 * 用户点击注册按钮，
 * 判断用户名，邮箱，两次输入密码是否有误
 * 不得为空
 * 正确就插入数据库，返回登录界面
 * @date 2023/11/29 16:16
 */
//TODO 给用户弹出具体错误以及在界面上显示格式要求
//    public void enrollOnclick(View view) {
//        String username=usernameEditText.getText().toString();
//        String email = emailEditText.getText().toString();
//        String password = passwordEditText.getText().toString();
//        String confirmPassword = confirmPasswordEditText.getText().toString();
//        if (isValidPassword(password) &&
//                isValidPassword(confirmPassword)&&
//                password.equals(confirmPassword)&&
//                isValidUsername(username)&&
//                isValidEmail(email)
//        ){
//            Toast.makeText(EnrollActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
//            Intent intent = new Intent(this, MainActivity.class);
//            startActivity(intent);
//        }else if (isValidUsername(username)==false) {
//            Toast.makeText(EnrollActivity.this, "用户名只能由字母、数字和下划线组成，且长度至少为1", Toast.LENGTH_SHORT).show();
//        }
//        else if (isValidEmail(email)==false) {
//            Toast.makeText(EnrollActivity.this, "邮箱格式错误", Toast.LENGTH_SHORT).show();
//        }
//        else if (password.equals(confirmPassword)==false) {
//            Toast.makeText(EnrollActivity.this, "两次密码不一致", Toast.LENGTH_SHORT).show();
//        }
//        else if (isValidPassword(password)==false||isValidPassword(confirmPassword)==false) {
//            Toast.makeText(EnrollActivity.this, "密码长度至少8位，且没有特殊字符", Toast.LENGTH_SHORT).show();
//        } else {
//            Toast.makeText(EnrollActivity.this, "密码验证通过", Toast.LENGTH_SHORT).show();
//        }
//    }

//    /**
//     * @author dell
//     * @description 测试密码是否规范,测试成功
//     * @date 2023/11/29 16:01
//     */
//    public void test(View view) {
//        String password = passwordEditText.getText().toString();
//        String confirmPassword = confirmPasswordEditText.getText().toString();
//
//        if (isValidPassword(password) && isValidPassword(confirmPassword) && password.equals(confirmPassword)) {
//            System.out.println("成功！！！！！！！！！！");
//        } else {
//            System.out.println("错误！！！！！！！！！！");
//        }
//    }
///**
//* @author dell
//* @description 测试用户输入的email是否合法，测试成功
//* @date 2023/11/29 16:03
//*/
//    public void test(View view) {
//        String userInput = emailEditText.getText().toString();
//        if (isValidEmail(userInput)) {
//        System.out.println("成功！！！！！！！！！！");
//    } else {
//        // 用户名不合法，给出错误提示
//        System.out.println("错误！！！！！！！！！！");}
//    }
///**
//
// * @author xcc
// * @description 测试能否获取到用户输入的内容，测试成功
// * @date 2023/11/29 13:04
// */
//public void test(View view) {
//    String userInput = usernameEditText.getText().toString();
//    if (isValidUsername(userInput)) {
//        System.out.println("成功！！！！！！！！！！");
//    } else {
//        // 用户名不合法，给出错误提示
//        System.out.println("错误！！！！！！！！！！");
//    }
//    }
}