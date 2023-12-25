package com.example.entity;

import java.time.LocalDateTime;

/**
 * @Author : zhang
 * @Date : Created in 2023/12/25 14:53
 * @Decription : 评论类
 */


public class Comment {

    private int id; // ID

    private int shareInfoId; // 与图文与视频分享信息表关联ID

    private String type; // 类型（区别评论来自动态还是视频）

    private int friendShareId; // 与好友动态分享关联ID

    private int userId; // 评论用户ID

    private LocalDateTime commentTime; // 评论时间

    private int videoId; // 视频ID

    private String commentText; // 评论内容

    private LocalDateTime commentDate; // 评论时间

    public Comment(int id, int shareInfoId, String type, int friendShareId, int userId, LocalDateTime commentTime, int videoId, String commentText, LocalDateTime commentDate) {
        this.id = id;
        this.shareInfoId = shareInfoId;
        this.type = type;
        this.friendShareId = friendShareId;
        this.userId = userId;
        this.commentTime = commentTime;
        this.videoId = videoId;
        this.commentText = commentText;
        this.commentDate = commentDate;
    }

    public Comment() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getShareInfoId() {
        return shareInfoId;
    }

    public void setShareInfoId(int shareInfoId) {
        this.shareInfoId = shareInfoId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getFriendShareId() {
        return friendShareId;
    }

    public void setFriendShareId(int friendShareId) {
        this.friendShareId = friendShareId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public LocalDateTime getCommentTime() {
        return commentTime;
    }

    public void setCommentTime(LocalDateTime commentTime) {
        this.commentTime = commentTime;
    }

    public int getVideoId() {
        return videoId;
    }

    public void setVideoId(int videoId) {
        this.videoId = videoId;
    }

    public String getCommentText() {
        return commentText;
    }

    public void setCommentText(String commentText) {
        this.commentText = commentText;
    }

    public LocalDateTime getCommentDate() {
        return commentDate;
    }

    public void setCommentDate(LocalDateTime commentDate) {
        this.commentDate = commentDate;
    }
}


