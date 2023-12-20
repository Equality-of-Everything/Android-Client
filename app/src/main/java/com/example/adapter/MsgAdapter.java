package com.example.adapter;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.UI.msg.ChatActivity;
import com.example.android_client.R;
import com.hyphenate.EMValueCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.chat.EMUserInfo;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * @Author : Lee
 * @Date : Created in 2023/12/11 19:58
 * @Decription : 聊天会话管理的Adapter
 */

public class MsgAdapter extends RecyclerView.Adapter<MsgAdapter.ConversationViewHolder>{

    private static List<EMConversation> conversationList;

    public MsgAdapter(List<EMConversation> conversationList) {
        this.conversationList = conversationList;
    }

    public void updateConversations(List<EMConversation> conversations) {
        conversationList.clear();
        conversationList.addAll(conversations);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MsgAdapter.ConversationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.frag_msg_item_layout, parent, false);
        return new ConversationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MsgAdapter.ConversationViewHolder holder, int position) {
        EMConversation conversation = conversationList.get(position);
        holder.bind(conversation);

        // 使用好友的 ID 来获取头像属性
        String[] userId = new String[1];
        userId[0] = conversation.conversationId();
        EMClient.getInstance().userInfoManager().fetchUserInfoByUserId(userId, new EMValueCallBack<Map<String, EMUserInfo>>() {
            @Override
            public void onSuccess(Map<String, EMUserInfo> value) {
                // 获取用户头像属性成功后，在主线程中加载用户头像
                EMUserInfo userInfo = value.get(userId[0]);
                if (userInfo != null) {
                    String avatarUrl = userInfo.getAvatarUrl();
                    Log.e("MsgAdapter", "获取用户头像成功：" + "avatarUrl" + avatarUrl);
                    // 在主线程中加载头像图片
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            // 加载头像图片
                            Glide.with(holder.itemView.getContext())
                                    .load(avatarUrl)
                                    .placeholder(R.drawable.loading)
                                    .error(R.drawable.friend_item)
                                    .into(holder.ivAvatar);
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

        // 添加点击事件监听器
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 点击事件处理逻辑
                // 获取所点击的会话信息
                String conversationId = conversation.conversationId();

                // 启动 ChatActivity，并传递会话信息
                Intent intent = new Intent(view.getContext(), ChatActivity.class);
                intent.putExtra("conversationId", conversationId);
                view.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return conversationList.size();
    }

    public class ConversationViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivAvatar;
        private TextView tvMsgName;
        private TextView tvLastMsg;
        private TextView tvTime;

        public ConversationViewHolder(@NonNull View itemView) {
            super(itemView);
            ivAvatar = itemView.findViewById(R.id.iv_avatar);
            tvMsgName = itemView.findViewById(R.id.tv_msg_name);
            tvLastMsg = itemView.findViewById(R.id.tv_last_msg);
            tvTime = itemView.findViewById(R.id.tv_msg_time);
        }

        public void bind(EMConversation conversation) {
            // 设置列表项的显示内容
            //发信息来的用户名
            String msgUsername = conversation.conversationId();
            tvMsgName.setText(msgUsername);

            //获取最近一条消息
            EMMessage lastMsg = conversation.getLastMessage();
            if(lastMsg != null) {
                // 获取消息发送时间
                long timestamp = lastMsg.getMsgTime();
                String time = formatTimestamp(timestamp);
                tvTime.setText(time);

                // 获取消息内容
                String messageContent = getMessageContent(lastMsg);
                tvLastMsg.setText(messageContent);
            }
        }


        /**
         * @param timestamp:
         * @return String
         * @author Lee
         * @description 时间数据的简单处理
         * @date 2023/12/11 20:47
         */
        private String formatTimestamp(long timestamp) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(timestamp);
            Calendar currentCalendar = Calendar.getInstance();
            SimpleDateFormat dateFormat;

            if (calendar.get(Calendar.YEAR) == currentCalendar.get(Calendar.YEAR) &&
                    calendar.get(Calendar.DAY_OF_YEAR) == currentCalendar.get(Calendar.DAY_OF_YEAR)) {
                // 当天的消息
                dateFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
            } else {
                // 非当天的消息
                dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
            }

            return dateFormat.format(calendar.getTime());
        }

        /**
         * @param msg:
         * @return String
         * @author Lee
         * @description 简单最近一次消息格式处理
         * @date 2023/12/11 20:51
         */
        private String getMessageContent(EMMessage msg) {
            String msgContent = "";
            switch (msg.getType()) {
                case TXT:
                    // 文本消息
                    EMTextMessageBody textBody = (EMTextMessageBody) msg.getBody();
                    String originalMsg = textBody.getMessage();
                    if (originalMsg.length() > 15) {
                        // 如果消息长度超过15，截取前15个字符并添加省略号
                        msgContent = originalMsg.substring(0, 15) + "...";
                    } else {
                        msgContent = originalMsg;
                    }
                    break;
                case IMAGE:
                    // 图片消息
                    msgContent = "[图片]";
                    break;
                case VOICE:
                    // 语音消息
                    msgContent = "[语音]";
                    break;
                default:
                    // 其他类型的消息处理
                    msgContent = "其它消息类型";
                    break;
            }
            return msgContent;
        }
    }
}
