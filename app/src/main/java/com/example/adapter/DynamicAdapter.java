package com.example.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.media.MediaPlayer;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.UI.share.FriendCircleItem;
import com.example.UI.share.NineGridLayout;
import com.example.android_client.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * @Author : xcc
 * @Date : Created in 2023/12/20 15:13
 * @Decription :
 */

public class DynamicAdapter extends RecyclerView.Adapter<DynamicAdapter.DynamicViewHolder> {
    private List<FriendCircleItem> friendCircleItemList;
    private Context context;
    public DynamicAdapter(List<FriendCircleItem> friendCircleItemList,Context context) {
        this.friendCircleItemList = friendCircleItemList;
        this.context = context;
    }
    @NonNull
    @Override
    public DynamicViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.dynamic_item, parent, false);
        return new DynamicViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DynamicAdapter.DynamicViewHolder holder, int position) {
        FriendCircleItem friendCircleItem = friendCircleItemList.get(position);
        //设置头像
        Glide.with(holder.itemView.getContext())
                .load(friendCircleItem.getAvatarUrl())
                .into(holder.share_user_avatar);
        // 设置用户名和文本内容
        holder.share_user_name.setText(friendCircleItem.getUserName());
        holder.textDynamic.setText(friendCircleItem.getTextContent());
        holder.gridLayout.setUrls(friendCircleItem.getMediaUrls());
        holder.shareTime.setText(friendCircleItem.getPublishTime());
    }


    @Override
    public int getItemCount() {
        return friendCircleItemList.size();
    }

    public class DynamicViewHolder extends RecyclerView.ViewHolder {
        CircleImageView share_user_avatar;
        TextView share_user_name;
        TextView textDynamic;
        NineGridLayout gridLayout;
        TextView shareTime;

        public DynamicViewHolder(@NonNull View itemView) {
            super(itemView);
            share_user_avatar = itemView.findViewById(R.id.share_user_avatar);
            share_user_name = itemView.findViewById(R.id.share_user_name);
            textDynamic = itemView.findViewById(R.id.textDynamic);
            gridLayout = itemView.findViewById(R.id.gridLayout);
            shareTime = itemView.findViewById(R.id.share_time);
        }
    }
}
