package com.example.UI.mine;

import static android.app.Activity.RESULT_OK;
import static com.baidu.mapapi.BMapManager.init;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.android_client.LoginActivity;
import com.example.android_client.R;
import com.example.util.TokenManager;
import com.google.android.material.bottomsheet.BottomSheetBehavior;


public class MineFragment extends Fragment {
    private ImageButton btnCamera;
    private TextView tvMineName;
    private Button btnFriends;
    private Button btnLogout;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mine, container, false);

        btnCamera = view.findViewById(R.id.image_avatar);
        tvMineName = view.findViewById(R.id.tv_mine_name);
        btnFriends = view.findViewById(R.id.btn_friends);
        btnLogout = view.findViewById(R.id.btn_logout);

        setListenerForLogout();

        setListeners();

        tvMineName.setText(TokenManager.getUserName(getActivity()));
        return view;
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

        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

//    private void openGallery() {
//        // 使用Intent调用系统相册应用
//        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//        startActivityForResult(intent, PICK_IMAGE_REQUEST);
//    }
//
//    // 处理相册应用返回的结果
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
//            Uri uri = data.getData();
//            // 根据Uri加载图片到ImageView中
//            profileImage.setImageURI(uri);
//        }
//    }
//
//    // 查看头像
//    private void viewProfileImage() {
//        // 跳转到新界面，显示当前头像的大图或详情
//        Intent intent = new Intent(this, ViewProfileImageActivity.class);
//        intent.putExtra("imageUri", getImageUri()); // 传递当前头像的Uri
//        startActivity(intent);
//    }

}
