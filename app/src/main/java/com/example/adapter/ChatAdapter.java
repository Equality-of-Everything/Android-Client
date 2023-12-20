package com.example.adapter;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.android_client.R;
import com.hyphenate.EMValueCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.chat.EMUserInfo;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * @Author : Lee
 * @Date : Created in 2023/12/13 13:59
 * @Decription : 聊天界面的Adapter
 */

public class ChatAdapter extends ArrayAdapter<EMMessage> {
    private Context mContext;
    private List<EMMessage> mMsg;

    public ChatAdapter(Context context, List<EMMessage> messages) {
        super(context, 0, messages);
        mContext = context;
        mMsg = messages;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        EMMessage message = mMsg.get(position);
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // 根据消息的发送方来决定使用哪种布局
        if (message.direct() == EMMessage.Direct.SEND) {
            view = inflater.inflate(R.layout.item_chat_send, parent, false); // 发送方消息的布局
        } else {
            view = inflater.inflate(R.layout.item_chat_receive, parent, false); // 接收方消息的布局
        }

        ImageView messageAvatar = view.findViewById(R.id.msg_avatar);
        // 使用好友的 ID 来获取头像属性
        String[] userId = new String[1];
        userId[0] = message.conversationId();
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
                            Glide.with(getContext())
                                    .load(avatarUrl)
                                    .placeholder(R.drawable.loading)
                                    .error(R.drawable.friend_item)
                                    .into(messageAvatar);
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

        // 设置消息内容
        TextView messageText = view.findViewById(R.id.msg_text);
        if (message.getType() == EMMessage.Type.TXT) {
            EMTextMessageBody textBody = (EMTextMessageBody) message.getBody();
            messageText.setText(textBody.getMessage()); // 设置消息内容
        }

        // 设置消息时间
        TextView messageTime = view.findViewById(R.id.msg_time);
        if (shouldDisplayTime(position)) {
            Date time = new Date(message.getMsgTime());
            SimpleDateFormat sdf;
            if (isToday(time)) {
                sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
            } else {
                sdf = new SimpleDateFormat("MM-dd HH:mm", Locale.getDefault());
            }
            messageTime.setText(sdf.format(time));
            messageTime.setVisibility(View.VISIBLE);
        } else {
            messageTime.setVisibility(View.GONE);
        }

        return view;
    }

    /**
     * @param time:
     * @return boolean
     * @author Lee
     * @description 判断时间是否是当天
     * @date 2023/12/18 15:34
     */
    private boolean isToday(Date time) {
        Calendar messageTime = Calendar.getInstance();
        messageTime.setTime(time);
        Calendar now = Calendar.getInstance();
        return messageTime.get(Calendar.YEAR) == now.get(Calendar.YEAR) &&
                messageTime.get(Calendar.DAY_OF_YEAR) == now.get(Calendar.DAY_OF_YEAR);
    }

    /**
     * @param position:
     * @return boolean
     * @author Lee
     * @description 间隔3分钟不互相发消息显示时间
     * @date 2023/12/18 15:34
     */
    private boolean shouldDisplayTime(int position) {
        if (position == 0) {
            return true; // 第一条消息总是显示时间
        } else {
            EMMessage currentMsg = mMsg.get(position);
            EMMessage lastMsg = mMsg.get(position - 1);
            long interval = currentMsg.getMsgTime() - lastMsg.getMsgTime();
            if (interval > 3 * 60 * 1000) {
                return true; // 间隔大于3分钟，显示时间
            }
        }
        return false; // 其他情况不显示时间
    }

    @Override
    public boolean isEnabled(int position){
        return false;
    }

}
