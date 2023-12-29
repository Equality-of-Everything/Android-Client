package com.example.entity;

import java.util.Date;

public class FriendShare {
    // ID
    private int id;
    // 个人信息表关联ID
    private int userInfoId;
    // 分享视频地址
    private String videoUrl;
    // 图文地址
    private String imageUrl;
    // 文字内容
    private String textContent;
    // 点赞数
    private int likeCount;
    // 与评论表关联ID
    private int commentId;
    // 与点赞人用户ID表关联ID
    private int likeUserId;
    // 评论时间
    private Date commentTime;

    public FriendShare() {}
    public FriendShare(int id, int userInfoId, String videoUrl,
                       String imageUrl, String textContent, int likeCount,
                       int commentId, int likeUserId, Date commentTime) {
        this.id = id;
        this.userInfoId = userInfoId;
        this.videoUrl = videoUrl;
        this.imageUrl = imageUrl;
        this.textContent = textContent;
        this.likeCount = likeCount;
        this.commentId = commentId;
        this.likeUserId = likeUserId;
        this.commentTime = commentTime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserInfoId() {
        return userInfoId;
    }

    public void setUserInfoId(int userInfoId) {
        this.userInfoId = userInfoId;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getTextContent() {
        return textContent;
    }

    public void setTextContent(String textContent) {
        this.textContent = textContent;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    public int getCommentId() {
        return commentId;
    }

    public void setCommentId(int commentId) {
        this.commentId = commentId;
    }

    public int getLikeUserId() {
        return likeUserId;
    }

    public void setLikeUserId(int likeUserId) {
        this.likeUserId = likeUserId;
    }

    public Date getCommentTime() {
        return commentTime;
    }

    public void setCommentTime(Date commentTime) {
        this.commentTime = commentTime;
    }

    @Override
    public String toString() {
        return "FriendShare{" +
                "id=" + id +
                ", userInfoId=" + userInfoId +
                ", videoUrl='" + videoUrl + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", textContent='" + textContent + '\'' +
                ", likeCount=" + likeCount +
                ", commentId=" + commentId +
                ", likeUserId=" + likeUserId +
                ", commentTime=" + commentTime +
                '}';
    }
}