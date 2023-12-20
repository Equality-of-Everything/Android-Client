package com.example.adapter;

import static com.example.util.TokenManager.getUserName;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.provider.ContactsContract;
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

        // 获取消息发送方或接收方的用户名
        String username = message.direct() == EMMessage.Direct.SEND ? getUserName(getContext()) : message.conversationId();

        View finalView = view;
        EMClient.getInstance().userInfoManager().fetchUserInfoByUserId(new String[]{username}, new EMValueCallBack<Map<String, EMUserInfo>>() {
            @Override
            public void onSuccess(Map<String, EMUserInfo> value) {
                EMUserInfo userInfo = value.get(username);
                if (userInfo != null) {
                    String avatarUrl = userInfo.getAvatarUrl();
                    Log.e("FriendAdapter", "获取用户头像成功：" + avatarUrl);
                    // 更新头像加载逻辑
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            ImageView messageAvatar = (ImageView) finalView.findViewById(message.direct() == EMMessage.Direct.SEND ? R.id.msg_send_avatar : R.id.msg_avatar);
                            if (avatarUrl != null && !((Activity) getContext()).isFinishing()) {
                                // 加载头像图片
                                Glide.with(getContext())
                                        .load(avatarUrl)
                                        .error(R.drawable.friend_item)
                                        .into(messageAvatar);
                            } else {
                                // 处理无头像URL的情况，显示默认头像
                                messageAvatar.setImageResource(R.drawable.friend_item);
                            }
                        }
                    });
                }
            }

            @Override
            public void onError(int error, String errorMsg) {

            }

        });


        String sendUserName = getUserName(getContext());
        EMClient.getInstance().userInfoManager().fetchUserInfoByUserId(new String[]{sendUserName}, new EMValueCallBack<Map<String, EMUserInfo>>() {
            @Override
            public void onSuccess(Map<String, EMUserInfo> value) {
                EMUserInfo userInfo = value.get(username);
                if (userInfo != null) {
                    String avatarUrl = userInfo.getAvatarUrl();
                    Log.e("FriendAdapter", "获取用户头像成功：" + avatarUrl);
                    // 更新头像加载逻辑
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            ImageView messageAvatar = (ImageView) finalView.findViewById(message.direct() == EMMessage.Direct.SEND ? R.id.msg_send_avatar : R.id.msg_avatar);
                            if (avatarUrl != null && !((Activity) getContext()).isFinishing()) {
                                // 加载头像图片
                                Glide.with(getContext())
                                        .load(avatarUrl)
                                        .error(R.drawable.friend_item)
                                        .into(messageAvatar);
                            } else {
                                // 处理无头像URL的情况，显示默认头像
                                messageAvatar.setImageResource(R.drawable.friend_item);
                            }
                        }
                    });
                }
            }

            @Override
            public void onError(int error, String errorMsg) {

            }

        });

//        ImageView messageAvatar = (ImageView) view.findViewById(message.direct() == EMMessage.Direct.SEND ? R.id.msg_send_avatar : R.id.msg_avatar);
//        // 根据消息的发送方或接收方来获取头像URL
//        String username = message.direct() == EMMessage.Direct.SEND ? getUserName(getContext()) : message.conversationId();
//        Log.e("ChatAdapter", "获取用户名称：" + getUserName(getContext()));
//        EMClient.getInstance().userInfoManager().fetchUserInfoByUserId(new String[]{username}, new EMValueCallBack<Map<String, EMUserInfo>>() {
//            @Override
//            public void onSuccess(Map<String, EMUserInfo> value) {
//                String username = message.direct() == EMMessage.Direct.SEND ? getUserName(getContext()) : message.conversationId();
//                EMUserInfo userInfo = value.get(username);
//                if (userInfo != null) {
//                    String avatarUrl = userInfo.getAvatarUrl();
//                    Log.e("FriendAdapter", "获取用户头像成功：" + avatarUrl);
//                    // 更新头像加载逻辑
//                    new Handler(Looper.getMainLooper()).post(new Runnable() {
//                        @Override
//                        public void run() {
////                            ImageView messageAvatar = (ImageView) view.findViewById(message.direct() == EMMessage.Direct.SEND ? R.id.msg_send_avatar : R.id.msg_avatar);
//                            if (avatarUrl != null && !((Activity) getContext()).isFinishing()) {
//                                // 加载头像图片
//                                Glide.with(getContext())
//                                        .load(avatarUrl)
//                                        .error(R.drawable.friend_item)
//                                        .into(messageAvatar);
//                            } else {
//                                // 处理无头像URL的情况，显示默认头像
//                                messageAvatar.setImageResource(R.drawable.friend_item);
//                            }
//                        }
//                    });
//                }
//            }
//
//            @Override
//            public void onError(int error, String errorMsg) {
//                // 处理加载头像失败的情况
//                Log.e("FriendAdapter", "获取用户头像失败：" + error + ", " + errorMsg);
//            }
//        });

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
