package com.example.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.android_client.R;
import com.example.entity.Comment;

import org.w3c.dom.Text;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

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
    public void onBindViewHolder(@NonNull CommentAdapter.CommentViewHolder holder, int position) {
        Comment comment = commentList.get(position);

        Glide.with(context).load(comment.getAvatar()).into(holder.profileImage);
        holder.username.setText(comment.getUsername());
        holder.content.setText(comment.getCommentText());
        holder.commentTime.setText(comment.getCommentDate());
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
