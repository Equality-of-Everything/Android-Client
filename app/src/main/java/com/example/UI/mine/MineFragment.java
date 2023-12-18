package com.example.UI.mine;

import static android.app.Activity.RESULT_OK;
import static com.baidu.mapapi.BMapManager.init;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.android_client.LoginActivity;
import com.example.android_client.R;
import com.example.util.TokenManager;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;


public class MineFragment extends Fragment {
    private ImageView btnCamera;
    private TextView tvMineName;
    private Button btnFriends;
    private Button btnLogout;
    int REQUEST_IMAGE_OPEN = 2;

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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_OPEN && resultCode == Activity.RESULT_OK && data != null) {
            // 获取所选图片的URI
            Uri selectedImageUri = data.getData();

            // 将所选图片设置为头像
            btnCamera.setImageURI(selectedImageUri);
        }
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
