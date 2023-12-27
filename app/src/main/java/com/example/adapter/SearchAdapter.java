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
    private RecyclerView recyclerView;

    public SearchAdapter(List<EMContact> searchList, Context context, RecyclerView recyclerView) {
        this.searchList = searchList;
        this.context = context;
        this.recyclerView = recyclerView;  // 初始化RecyclerView的引用
    }

    public void setContactList(List<EMContact> contactList) {
        this.searchList = contactList;
        notifyDataSetChanged();
        recyclerView.getAdapter().notifyDataSetChanged();  // 调用RecyclerView.Adapter的notifyDataSetChanged
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

        Log.e("SearchAdapter", "onBindViewHolder: Data bound for position " + position); // 添加Log来查看数据绑定情况

        getUserInfoFromServer(contact.getUsername(), new UserInfoFetchCallback() {
            @Override
            public void onUserInfoFetched(EMUserInfo userInfo) {
                if (userInfo != null) {
                    String avatarUrl = userInfo.getAvatarUrl();
                    if (avatarUrl != null && !avatarUrl.isEmpty()) {
                        // 在主线程中加载头像图片
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                Glide.with(context)
                                        .load(avatarUrl)
                                        .error(R.drawable.error)
                                        .into(holder.ivAvatar);
                            }
                        });
                    }
                }
            }
        });
    }

    private void getUserInfoFromServer(String userId, UserInfoFetchCallback callback) {
        EMClient.getInstance().userInfoManager().fetchUserInfoByUserId(new String[]{userId}, new EMValueCallBack<Map<String, EMUserInfo>>() {
            @Override
            public void onSuccess(Map<String, EMUserInfo> value) {
                EMUserInfo userInfo = value.get(userId);
                callback.onUserInfoFetched(userInfo);
            }

            @Override
            public void onError(int error, String errorMsg) {
                Log.e("SearchAdapter", "获取用户信息失败：" + error + ", " + errorMsg);
                callback.onUserInfoFetched(null);
            }
        });
    }

    public interface UserInfoFetchCallback {
        void onUserInfoFetched(EMUserInfo userInfo);
    }

    @Override
    public int getItemCount() {
        Log.e("SearchAdapter", "getItemCount: " + searchList.size());
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
