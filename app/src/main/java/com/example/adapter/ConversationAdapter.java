package com.example.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android_client.R;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * @Author : Lee
 * @Date : Created in 2023/12/11 19:58
 * @Decription : 聊天会话管理的Adapter
 */

public class ConversationAdapter extends RecyclerView.Adapter<ConversationAdapter.ConversationViewHolder>{

    private List<EMConversation> conversationList;

    public ConversationAdapter(List<EMConversation> conversationList) {
        this.conversationList = conversationList;
    }

    @NonNull
    @Override
    public ConversationAdapter.ConversationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.frag_msg_item_layout, parent, false);
        return new ConversationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ConversationAdapter.ConversationViewHolder holder, int position) {
        EMConversation conversation = conversationList.get(position);
        holder.bind(conversation);
    }

    @Override
    public int getItemCount() {
        return conversationList.size();
    }

    public static class ConversationViewHolder extends RecyclerView.ViewHolder {
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
                dateFormat = new SimpleDateFormat("yy-MM-dd HH:mm", Locale.getDefault());
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
                    msgContent = textBody.getMessage();
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
