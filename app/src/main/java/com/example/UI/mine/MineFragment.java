package com.example.UI.mine;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.android_client.LoginActivity;
import com.example.android_client.R;
import com.example.util.TokenManager;
import com.google.android.material.bottomsheet.BottomSheetBehavior;


public class MineFragment extends Fragment {
    private Button btnFriends;
    private Button btnLogout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mine, container, false);

        btnFriends = view.findViewById(R.id.btn_friends);
        btnLogout = view.findViewById(R.id.btn_logout);

        setListenerForLogout();

        setListeners();

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

    private void setListeners() {
        btnFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //点击跳转所有好友页面
                Intent intent = new Intent(getActivity(), FriendsActivity.class);
                startActivity(intent);
            }
        });
    }

}
