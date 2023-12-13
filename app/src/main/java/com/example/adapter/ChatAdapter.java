package com.example.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.android_client.R;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

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
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            // 根据消息的发送方来决定使用哪种布局
            if (message.direct() == EMMessage.Direct.SEND) {
                view = inflater.inflate(R.layout.item_chat_send, parent, false); // 发送方消息的布局
            } else {
                view = inflater.inflate(R.layout.item_chat_receive, parent, false); // 接收方消息的布局
            }
        }

        // 设置消息内容
        TextView messageText = view.findViewById(R.id.msg_text);
        if (message.getType() == EMMessage.Type.TXT) {
            EMTextMessageBody textBody = (EMTextMessageBody) message.getBody();
            messageText.setText(textBody.getMessage()); // 设置消息内容
        }

        // 设置消息时间
        TextView messageTime = view.findViewById(R.id.msg_time);
        Date time = new Date(message.getMsgTime());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        messageTime.setText(sdf.format(time));


        return view;
    }
}
