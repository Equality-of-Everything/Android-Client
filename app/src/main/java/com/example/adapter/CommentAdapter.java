package com.example.adapter;

import static com.example.android_client.LoginActivity.ip;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.android_client.R;
import com.example.entity.Comment;
import com.example.util.Result;
import com.google.gson.Gson;

import org.w3c.dom.Text;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * @Author : zhang
 * @Date : Created in 2023/12/25 21:15
 * @Decription : 视频评论Adapter
 */


public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {
    private List<Comment> commentList;
    private Context context;

    public CommentAdapter(List<Comment> commentList, Context context) {
        this.commentList = commentList;
        this.context = context;
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_vido_comment, parent, false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentAdapter.CommentViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Comment comment = commentList.get(position);


        Glide.with(context).load(comment.getAvatar()).into(holder.profileImage);
        holder.username.setText(comment.getUsername());
        holder.content.setText(comment.getCommentText());
        Timestamp timestamp = Timestamp.valueOf(comment.getCommentDate());
        String date = formatTimestamp(timestamp.getTime());
        holder.commentTime.setText(date);

        // 删除评论
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showDeleteCommentDialog(comment,position);
                return true;
            }
        });
    }

    // 用于删除对应评论
    private void showDeleteCommentDialog(Comment comment,int currentVideoPosition) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("删除评论");
        builder.setMessage("确认删除以下评论吗？\n\n" + comment.getCommentText());
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // 用户确认删除，执行删除评论的操作
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Gson gson = new Gson();
                        OkHttpClient client = new OkHttpClient();
                        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                        String commentJson = gson.toJson(comment);
                        RequestBody body = RequestBody.create(JSON, commentJson);
                        Request request = new Request.Builder()
                                .url("http://" + ip + ":8080/map/videos/delComment")
                                .delete(body)
                                .build();
                        client.newCall(request).enqueue(new Callback() {
                            @Override
                            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                                ((Activity)context).runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(context,"服务器连接失败，请稍后重试",Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }

                            @Override
                            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                                String res = response.body().string();
                                Result result = gson.fromJson(res, Result.class);
                                ((Activity)context).runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (!result.getFlag()) {
                                            Toast.makeText(context, result.getMsg(), Toast.LENGTH_SHORT).show();
                                            return;
                                        }
                                        commentList.remove(currentVideoPosition);
                                        notifyDataSetChanged();
                                    }
                                });
                            }
                        });
                    }
                }).start();
            }
        });
        builder.setNegativeButton("取消", null);
        builder.show();
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

    @Override
    public int getItemCount() {
        return commentList.size();
    }

    public class CommentViewHolder extends RecyclerView.ViewHolder {

        // 头像
        CircleImageView profileImage;
        TextView username;
        TextView content;
        TextView commentTime;

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            profileImage = itemView.findViewById(R.id.iv_avatar);
            username = itemView.findViewById(R.id.tv_msg_name);
            content = itemView.findViewById(R.id.tv_last_msg);
            commentTime = itemView.findViewById(R.id.tv_msg_time);
        }
    }

    public void updateData(List<Comment> comments) {
        this.commentList = comments;
        notifyDataSetChanged();
    }
}
