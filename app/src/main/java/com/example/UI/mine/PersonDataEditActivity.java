package com.example.UI.mine;

import static com.example.android_client.LoginActivity.ip;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android_client.LoginActivity;
import com.example.android_client.R;
import com.example.entity.UserInfo;
import com.example.entity.UserLogin;
import com.example.util.Result;
import com.example.util.TokenManager;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class PersonDataEditActivity extends AppCompatActivity {
    private String userName;
    private Button mPickDateButton;
    private TextView mShowSelectedDateText;
    private TextInputEditText mSignatureEditText ;
    private TextInputEditText mEmailEditText ;
    private Button mSaveButton;
    private RadioGroup mradioGroup;
    private RadioButton radio_button_1;
    private RadioButton radio_button_2;
    private RadioButton radio_button_3;
    private static final String TAG = "PersonDataActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_mine);

        //出生日期选择
        mPickDateButton = findViewById(R.id.pick_date_button);
        mShowSelectedDateText = findViewById(R.id.show_selected_date);
        mradioGroup = findViewById(R.id.radioGroup);
        mSignatureEditText = findViewById(R.id.signature_edit);
        mEmailEditText = findViewById(R.id.email_edit);
        mSaveButton = findViewById(R.id.confirm_change);
        radio_button_1 = findViewById(R.id.radio_button_1);
        radio_button_2 = findViewById(R.id.radio_button_2);
        radio_button_3 = findViewById(R.id.radio_button_3);

        MaterialDatePicker.Builder materialDateBuilder = MaterialDatePicker.Builder.datePicker();
        materialDateBuilder.setTitleText("选择日期");
        final MaterialDatePicker materialDatePicker = materialDateBuilder.build();
        mPickDateButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        materialDatePicker.show(getSupportFragmentManager(), "MATERIAL_DATE_PICKER");
                    }
                });
        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 获取个性签名
                String signature = mSignatureEditText.getText().toString();
                // 获取邮箱地址
                String email = mEmailEditText.getText().toString();
                // 获取性别
                int selectedRadioButtonId = mradioGroup.getCheckedRadioButtonId();
                RadioButton selectedRadioButton = findViewById(selectedRadioButtonId);
                String gender = selectedRadioButton.getText().toString();
                // 获取出生日期
                String birthDate  = mShowSelectedDateText.getText().toString();
                saveUserDataAndSendToServer(signature, email, gender, birthDate);
            }
        });

        // now handle the positive button click from the
        // material design date picker
        materialDatePicker.addOnPositiveButtonClickListener(
                new MaterialPickerOnPositiveButtonClickListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onPositiveButtonClick(Object selection) {

                        // if the user clicks on the positive
                        // button that is ok button update the
                        // selected date
                        mShowSelectedDateText.setText("出生日期: " +materialDatePicker.getHeaderText());
                        // in the above statement, getHeaderText
                        // is the selected date preview from the
                        // dialog
                    }
                });
        loadUserData();
    }

    private void saveUserDataAndSendToServer(String signature, String email, String gender, String birthDate) {
        // 检查信息是否为空
        if (signature.isEmpty() || email.isEmpty() || gender.isEmpty() || birthDate.isEmpty()) {
            Toast.makeText(this, "请填写完整信息", Toast.LENGTH_SHORT).show();
            return;
        } else if (!isValidEmail(email)) {
            // 电子邮件地址无效，显示错误信息给用户
            Toast.makeText(this, "电子邮件无效", Toast.LENGTH_SHORT).show();
            return;
        }else {
            // 保存用户个人信息到SharedPreferences
            saveUserData(signature, email, gender, birthDate);
            Toast.makeText(PersonDataEditActivity.this, "修改成功", Toast.LENGTH_SHORT).show();
            userName = TokenManager.getUserName(this);
            //将信息上传到服务器
            uploadToServer(userName, signature, email, gender, birthDate);
        }
    }

    private void uploadToServer(String userName, String signature, String email, String gender, String birthDate) {
        UserInfo userInfo = new UserInfo();
        userInfo.setUsername(userName);
        userInfo.setSignature(signature);
        userInfo.setEmail(email);
        userInfo.setGender(gender);

        // 去掉"出生日期:"文本
        String formattedBirthDate = birthDate.replace("出生日期: ", "");

        // 定义日期格式
        DateTimeFormatter formatterBirthday = new DateTimeFormatterBuilder()
                .appendPattern("yyyy年M月d日")
                .parseDefaulting(ChronoField.MONTH_OF_YEAR, 1)
                .parseDefaulting(ChronoField.DAY_OF_MONTH, 1)
                .toFormatter(Locale.CHINA);
        LocalDate date = LocalDate.parse(formattedBirthDate, formatterBirthday);

        // 获取当前时刻
        LocalDateTime now = LocalDateTime.now();

        userInfo.setBirthday(date.format(formatterBirthday));
        userInfo.setLastModifiedTime(now.format(DateTimeFormatter.ofPattern("yyyy年MM月dd日")));
        Log.e("now", now.toString());
        Log.e("date", date.toString());

        Gson gson = new Gson();
        String formBody = gson.toJson(userInfo);
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(JSON, formBody);
        Log.d("Body", body.toString());

        new Thread(new Runnable() {
            Gson json = new Gson();
            @Override
            public void run() {
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                       .url("http://"+ip+":8080/userInfo/setUserInformation")
                        .post(body)
                       .build();

                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                        Log.e("PersonDataEditActivity", "上传到服务器端失败 " + e.getMessage());
                    }

                    @Override
                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                        Log.i("PersonDataEditActivity", "上传到服务器端成功");
                        String responseData = response.body().string();
                        Result result = json.fromJson(responseData, Result.class);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (result.getFlag()) {
                                    Log.e("PersonDataEditActivity", "服务器响应修改成功 " + result.getMsg());

                                    onBackPressed();//返回上一个页面
                                } else {
                                    Log.e("PersonDataEditActivity", "服务器响应修改失败 " + result.getMsg());
                                }
                            }
                        });
                    }
                });

            }
        }).start();
    }


    private boolean isValidEmail(String email) {
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        Pattern pattern = Pattern.compile(emailPattern);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    // 保存用户个人信息
    private void saveUserData(String signature, String email, String gender, String birthDate) {
        SharedPreferences sharedPreferences = getSharedPreferences("user_data", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("signature", signature);
        editor.putString("email", email);
        editor.putString("gender", gender);
        editor.putString("birthDate", birthDate);
        editor.apply();
    }
    // 加载用户个人信息
    private void loadUserData() {
        SharedPreferences sharedPreferences = getSharedPreferences("user_data", Context.MODE_PRIVATE);
        String signature = sharedPreferences.getString("signature", "");
        String email = sharedPreferences.getString("email", "");
        String gender = sharedPreferences.getString("gender", "");
        String birthDate = sharedPreferences.getString("birthDate", "");
        // 将加载的个人信息设置到界面上
        mSignatureEditText.setText(signature);
        mEmailEditText.setText(email);
        // 设置性别的选择状态
        if (gender.equals("男")) {
            radio_button_1.setChecked(true);
        } else if (gender.equals("女")) {
            radio_button_2.setChecked(true);
        } else {
            radio_button_3.setChecked(true);
        }
        mShowSelectedDateText.setText(birthDate);
    }

}





