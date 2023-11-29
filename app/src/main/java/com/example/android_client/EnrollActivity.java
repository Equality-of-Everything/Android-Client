package com.example.android_client;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.EditText;


public class EnrollActivity extends AppCompatActivity {

    private EditText usernameEditText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enroll);
        usernameEditText = findViewById(R.id.edtTxt_enter_name);
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