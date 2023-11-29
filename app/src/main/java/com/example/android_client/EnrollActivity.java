package com.example.android_client;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.util.Patterns;
import android.widget.Toast;

public class EnrollActivity extends AppCompatActivity {

    private EditText usernameEditText;
    private EditText emailEditText;
    private EditText passwordEditText;
    private EditText confirmPasswordEditText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enroll);
        usernameEditText = findViewById(R.id.edtTxt_enter_name);
        emailEditText = findViewById(R.id.edTxt_enter_email);
        passwordEditText = findViewById(R.id.edTxt_enter_password);
        confirmPasswordEditText = findViewById(R.id.edTxt_enter_confirm_password);
    }

    /**
     * @author xcc
     * @description 用户名只能由字母、数字和下划线组成，且长度至少为1
     * @date 2023/11/29 16:14
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
 * @author xcc
 * @description
 * 用户点击注册按钮，
 * 判断用户名，邮箱，两次输入密码是否有误
 * 不得为空
 * 正确就插入数据库，返回登录界面
 * @date 2023/11/29 16:16
 */
//TODO 给用户弹出具体错误以及在界面上显示格式要求
    public void enrollOnclick(View view) {
        String username=usernameEditText.getText().toString();
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        String confirmPassword = confirmPasswordEditText.getText().toString();
        if (isValidPassword(password) &&
                isValidPassword(confirmPassword)&&
                password.equals(confirmPassword)&&
                isValidUsername(username)&&
                isValidEmail(email)
        ){
            Toast.makeText(EnrollActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }else if (isValidUsername(username)==false) {
            Toast.makeText(EnrollActivity.this, "用户名只能由字母、数字和下划线组成，且长度至少为1", Toast.LENGTH_SHORT).show();
        }
        else if (isValidEmail(email)==false) {
            Toast.makeText(EnrollActivity.this, "邮箱格式错误", Toast.LENGTH_SHORT).show();
        }
        else if (password.equals(confirmPassword)==false) {
            Toast.makeText(EnrollActivity.this, "两次密码不一致", Toast.LENGTH_SHORT).show();
        }
        else if (isValidPassword(password)==false||isValidPassword(confirmPassword)==false) {
            Toast.makeText(EnrollActivity.this, "密码长度至少8位，且没有特殊字符", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(EnrollActivity.this, "密码验证通过", Toast.LENGTH_SHORT).show();
        }
    }

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