package com.example.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.UI.mine.IndividualActivity;
import com.example.android_client.R;
import com.hyphenate.chat.EMContact;

import java.util.List;

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
