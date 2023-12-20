package com.example.UI.mine;

import static com.example.android_client.LoginActivity.ip;
import static com.example.util.Code.LOGIN_ERROR_NOUSER;
import static com.example.util.Code.LOGIN_ERROR_PASSWORD;
import static com.example.util.TokenManager.saveAvatar;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.android_client.EmailActivity;
import com.example.android_client.LoginActivity;
import com.example.android_client.R;
import com.example.util.Result;
import com.example.util.TokenManager;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.hyphenate.EMValueCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMDeviceInfo;
import com.hyphenate.chat.EMUserInfo;
import com.hyphenate.exceptions.HyphenateException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;


public class MineFragment extends Fragment {
    private ImageView btnCamera;
    private TextView tvMineName;
    private TextView tvUid;
    private Button btnFriends;
    private Button btnEdit;
    private Button btnLogout;
    private String userName ;
    private String avatarPath;
    public static final int REQUEST_IMAGE_OPEN = 2;
    public static final MediaType MEDIA_TYPE_JPG = MediaType.parse("image/jpeg");

    private View contextView;


    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mine, container, false);

        userName = TokenManager.getUserName(getActivity());
        avatarPath = TokenManager.getAvatar(getActivity());


        btnCamera = view.findViewById(R.id.image_avatar);
        tvMineName = view.findViewById(R.id.tv_mine_name);
        btnFriends = view.findViewById(R.id.btn_friends);
        btnEdit = view.findViewById(R.id.btn_edit);
        btnLogout = view.findViewById(R.id.btn_logout);
        tvUid = view.findViewById(R.id.tv_mine_uid);
        contextView = view.findViewById(R.id.context_view);

        String uid = String.valueOf((int) ((Math.random() * 9 + 1) * 100000));
        tvUid.setText("uid: "+uid);

        setListenerForLogout();

        setListeners();

        tvMineName.setText(userName);
        if(avatarPath!= null) {
            btnCamera.setImageURI(Uri.parse(avatarPath));
        }

        return view;
    }

    /**
     * @param :
     * @return void
     * @author Lee
     * @description 跳转发信息页面
     * @date 2023/12/14 10:42
     */
    private void setListeners() {
        btnFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //点击跳转所有好友页面
                Intent intent = new Intent(getActivity(), FriendsActivity.class);
                startActivity(intent);
            }
        });

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //跳转到个人信息编辑页面
                Intent intent = new Intent(getActivity(), PersonDataEditActivity.class);
                startActivity(intent);
            }
        });

        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getActivity());
                bottomSheetDialog.setContentView(R.layout.dialog_bottom_sheet);
                bottomSheetDialog.show();

                // 查看头像
                Button btnViewAvatar = bottomSheetDialog.findViewById(R.id.btn_view_avatar);
                btnViewAvatar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });

                // 从相册选择一张头像上传作为个人头像
                Button btnSelectAvatar = bottomSheetDialog.findViewById(R.id.btn_select_avatar);
                btnSelectAvatar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //打开相册
                        Intent intent = new  Intent(Intent.ACTION_PICK);
                        //指定获取的是图片
                        intent.setType("image/*");
                        startActivityForResult(intent, REQUEST_IMAGE_OPEN);

                        // 关闭底部面板
                        bottomSheetDialog.dismiss();
                    }
                });

                //拍照以上传个人头像
                Button btnTakePhoto = bottomSheetDialog.findViewById(R.id.btn_take_photo);
                btnTakePhoto.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    // Add your code to handle the "拍照" button click
                    }
                });
            }
        });
    }

    /**
     * @param requestCode:
     * @param resultCode:
     * @param data:
     * @return void
     * @author Lee
     * @description 选择完头像图片后的操作
     * @date 2023/12/19 8:14
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_OPEN && resultCode == Activity.RESULT_OK && data != null) {
            // 获取所选图片的URI
            Uri selectedImageUri = data.getData();
            //将选择的图片保存在本地
            SaveToLocalStorage(String.valueOf(selectedImageUri));
        }
    }

    /**
     * @param selectedImageUri:
     * @return void
     * @author Lee
     * @description 把选择的图片保存在本地
     * @date 2023/12/19 19:28
     */
    private void SaveToLocalStorage(String selectedImageUri) {
        try {
            // 将选择的图片复制到应用的内部存储中
            InputStream inputStream = getActivity().getContentResolver().openInputStream(Uri.parse(selectedImageUri));
            File internalStorageDir = getActivity().getFilesDir();
            File avatarFile = new File(internalStorageDir, "avatar.jpg");
            OutputStream outputStream = new FileOutputStream(avatarFile);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }
            outputStream.close();
            inputStream.close();

            // 将保存的头像图片路径存储起来，以便以后使用
            saveAvatar(getActivity(), avatarFile.getAbsolutePath());

            // 将所选图片设置为头像
            btnCamera.setImageURI(Uri.parse(selectedImageUri));

            uploadToServer(userName, avatarFile);//上传到后端

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param userName:
     * @param imageFile:
     * @return void
     * @author Lee
     * @description
     * @date 2023/12/19 8:35
     */
    private void uploadToServer(String userName, File imageFile) {
        OkHttpClient client = new OkHttpClient();

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("username", userName)
                .addFormDataPart("file", "avatar.jpg",
                        RequestBody.create(MEDIA_TYPE_JPG, imageFile))
                .build();

        Request request = new Request.Builder()
                .url("http://"+ip+":8080/userInfo/setUserAvatar") // 替换成后端服务器的URL
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // 处理上传失败情况
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showSnackBar(getView(), "服务器故障，请稍后重试", "我知道了");
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Gson json = new Gson();
                String responseData = response.body().string();
                Result result = json.fromJson(responseData, Result.class);
                Log.e("minefragment : ", result.toString());
                // 处理上传成功情况
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(result.getData() != null) {
                            //将头像对应的url传到环信
                            String imageUrl = result.getData().toString();
                            EMClient.getInstance().userInfoManager().updateOwnInfoByAttribute(EMUserInfo.EMUserInfoType.AVATAR_URL, imageUrl, new EMValueCallBack<String>() {
                                @Override
                                public void onSuccess(String value) {
                                    Log.i("mine", "头像上传环信成功");
                                }

                                @Override
                                public void onError(int error, String errorMsg) {
                                    Log.e("mine", "头像上传环信失败");
                                }
                            });
                            Log.e("minefragment : " , imageUrl);
                        }
                        showSnackBar(getView(), "头像更新成功", "我知道了");

                    }
                });
            }
        });
    }

    /*
     * @param view:
    	 * @param txt:
    	 * @param btnTxt:
      * @return void
     * @author zhang
     * @description 用于展示SnackBar
     * @date 2023/12/19 9:11
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

    /**
     * @param :
     * @return void
     * @author zhang
     * @description 为登出按钮绑定点击事件
     * @date 2023/12/13 15:41
     */
    private void setListenerForLogout() {
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TokenManager.deleteExpiredTokenFromSharedPreferences(getActivity());
                new Thread(){
                    @Override
                    public void run() {
                        super.run();
                        EMClient.getInstance().logout(true);
                        Log.e("mineFragment : ", "登出成功");
                    }
                }.start();

                //点击跳转登出页面
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
            }
        });
    }


    /**
     * @param :
     * @return void
     * @author Lee
     * @description 查看头像
     * @date 2023/12/18 16:24
     */
//    private void viewProfileImage() {
//        // 跳转到新界面，显示当前头像的大图或详情
//        Intent intent = new Intent(getActivity(), ViewAvatarActivity.class);
//        intent.putExtra("imageUri", getImageUri()); // 传递当前头像的Uri
//        startActivity(intent);
//    }

}
