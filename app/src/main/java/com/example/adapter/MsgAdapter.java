package com.example.adapter;

import android.content.Context;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.android_client.R;
import com.example.entity.UserInfo;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;

import java.util.List;

/**
 * @Author : Lee
 * @Date : Created in 2023/12/13 13:59
 * @Decription :
 */

public class MsgAdapter  extends ArrayAdapter<EMMessage> {
    private Context mContext;
    private List<EMMessage> mMsg;

    public MsgAdapter(Context context, List<EMMessage> messages) {
        super(context, 0, messages);
        mContext = context;
        mMsg = messages;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.item_chat, parent, false);
        }

        EMMessage message = mMsg.get(position);
        UserInfo userInfo = new UserInfo();

        // 设置消息发送者的头像
        ImageView senderAvatar = view.findViewById(R.id.msg_sender_avatar);
//        senderAvatar.setImageResource(message.getSenderAvatar());
//
//        // 设置消息内容
        TextView messageText = view.findViewById(R.id.msg_text);
        if (message.getType() == EMMessage.Type.TXT) {
            EMTextMessageBody textBody = (EMTextMessageBody) message.getBody();
            messageText.setText(textBody.getMessage()); // 设置消息内容
        }
//        messageText.setText(message.getBody().toString());
//
//        // 设置消息时间
        TextView messageTime = view.findViewById(R.id.msg_time);
//        messageTime.setText(message.getTime());

        return view;
    }
}
