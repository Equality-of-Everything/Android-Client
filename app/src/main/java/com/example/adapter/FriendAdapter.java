package com.example.adapter;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.UI.mine.IndividualActivity;
import com.example.android_client.R;
import com.hyphenate.EMValueCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMContact;
import com.hyphenate.chat.EMUserInfo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @Author : Lee
 * @Date : Created in 2023/12/11 21:51
 * @Decription : 好友列表的Adapter
 */

public class FriendAdapter extends RecyclerView.Adapter<FriendAdapter.FriendViewHolder>{

    private List<EMContact> friendList;
    private Context context;

    public FriendAdapter(List<EMContact> friendList, Context context) {
        this.friendList = friendList;
        this.context = context;
    }

    @NonNull
    @Override
    public FriendAdapter.FriendViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.friend_item_layout, parent, false);
        return new FriendViewHolder(view);
    }

    // 绑定数据到ViewHolder
    @Override
    public void onBindViewHolder(@NonNull FriendAdapter.FriendViewHolder holder, int position) {
        EMContact friend = friendList.get(position);
        holder.tvFriendName.setText(friend.getUsername());


        // 使用好友的 ID 来获取头像属性
        // 获取指定用户的头像
        String[] userId = new String[1];
        userId[0] = friend.getUsername();
        EMClient.getInstance().userInfoManager().fetchUserInfoByUserId(userId, new EMValueCallBack<Map<String, EMUserInfo>>() {
            @Override
            public void onSuccess(Map<String, EMUserInfo> value) {
                // 获取用户头像属性成功后，在主线程中加载用户头像
                EMUserInfo userInfo = value.get(userId[0]);
                if (userInfo != null) {
                    String avatarUrl = userInfo.getAvatarUrl();
                    Log.e("FriendAdapter", "获取用户头像成功：" + "avatarUrl" + avatarUrl);
                    // 在主线程中加载头像图片
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            // 加载头像图片
                            Glide.with(holder.itemView.getContext())
                                    .load(avatarUrl)
                                    .placeholder(R.drawable.loading)
                                    .error(R.drawable.error)
                                    .into(holder.ivFriendAvatar);
                        }
                    });
                }
            }

            @Override
            public void onError(int error, String errorMsg) {
                // 处理加载头像失败的情况
                Log.e("FriendAdapter", "获取用户头像失败：" + error + ", " + errorMsg);
            }
        });

        //为item设置点击事件
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, IndividualActivity.class);
                intent.putExtra("friendId", friend.getUsername());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return friendList.size();
    }

    // ViewHolder类
    public static class FriendViewHolder extends RecyclerView.ViewHolder {
        ImageView ivFriendAvatar;
        TextView tvFriendName;

        public FriendViewHolder(@NonNull View itemView) {
            super(itemView);
            ivFriendAvatar = itemView.findViewById(R.id.iv_friend_avatar);
            tvFriendName = itemView.findViewById(R.id.tv_friend_name);
        }

    }

}
