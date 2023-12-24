package com.example.adapter;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.android_client.R;
import com.example.entity.ImageLoaderTarget;
import com.hyphenate.EMValueCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMContact;
import com.hyphenate.chat.EMUserInfo;

import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * @Author : Lee
 * @Date : Created in 2023/12/23 19:42
 * @Decription :
 */

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.SearchViewHolder> {
    private List<EMContact> searchList;
    private Context context;

    public SearchAdapter(List<EMContact> friendList, Context context) {
        this.searchList = friendList;
        this.context = context;
    }

    public void setContactList(List<EMContact> contactList) {
        this.searchList = contactList;
    }

    @NonNull
    @Override
    public SearchAdapter.SearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_item_layout, parent, false);
        return new SearchAdapter.SearchViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchAdapter.SearchViewHolder holder, int position) {
        EMContact contact = searchList.get(position);
        holder.tvName.setText(contact.getUsername());

        String[] userId = new String[1];
        userId[0] = contact.getUsername();
        EMClient.getInstance().userInfoManager().fetchUserInfoByUserId(userId, new EMValueCallBack<Map<String, EMUserInfo>>() {
            @Override
            public void onSuccess(Map<String, EMUserInfo> value) {
                // 获取用户头像属性成功后，在主线程中加载用户头像
                EMUserInfo userInfo = value.get(userId[0]);
                if (userInfo != null) {
                    String avatarUrl = userInfo.getAvatarUrl();
                    if (avatarUrl != null && !avatarUrl.isEmpty()) {
                        // 加载头像图片
                        Glide.with(context)
                                .load(avatarUrl)
                                .error(R.drawable.error) // 加载失败显示的图片
                                .into(new ImageLoaderTarget(holder.ivAvatar)); // 这里改为使用.into()的回调函数
                    }
                }
            }

            @Override
            public void onError(int error, String errorMsg) {
                // 处理加载头像失败的情况
                Log.e("SearchAdapter", "获取用户头像失败：" + error + ", " + errorMsg);
            }
        });
    }

    @Override
    public int getItemCount() {
        return searchList.size();
    }

    public static class SearchViewHolder extends RecyclerView.ViewHolder {
        CircleImageView ivAvatar;
        TextView tvName;
        TextView tvAddFriend;
        public SearchViewHolder(@NonNull View itemView) {
            super(itemView);

            ivAvatar = itemView.findViewById(R.id.iv_search_avatar);
            tvName = itemView.findViewById(R.id.tv_search_name);
            tvAddFriend = itemView.findViewById(R.id.tv_add_friend);
        }
    }

}
