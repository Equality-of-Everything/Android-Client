package com.example.entity;

import java.util.Date;

/**
 * @Author : Lee
 * @Date : Created in 2023/12/1 16:55
 * @Decription : 点击地图跳转视频 相关功能 （方便与后端交互）
 */

public class ShareInfo {

    // ID - 唯一标识符
    private int id;
    // Personal Info Id - 与个人信息表关联的ID
    private int userInfoId;
    // Map Info Id - 地图背后信息存储ID
    private int mapInfoId;
    // IP - IP地址
    private String ip;
    // Video URL - 视频地址
    private String videoUrl;
    // Image Library Id - 与图片库-图文与视频分享信息表关联的ID
    private int imageLibraryId;
    // Text - 文字内容
    private String text;
    // Type - 类型（区分视频和图文）
    private String type;
    // Like Count - 点赞数
    private int likeCount;
    // Comment Id - 与评论表关联ID
    private int commentId;
    // Tag Id - 与标签表关联的ID
    private int tagId;
    // Like User Id - 与点赞人用户ID表关联ID
    private int likeUserId;
    // Upload Time - 上传时间
    private Date uploadTime;
    private String vrImageUrl;

    public String getVrImageUrl() {
        return vrImageUrl;
    }

    public void setVrImageUrl(String vrImageUrl) {
        this.vrImageUrl = vrImageUrl;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ShareInfo(int id, int userInfoId, int mapInfoId, String ip, String videoUrl, int imageLibraryId, String text, String type, int likeCount, int commentId, int tagId, int likeUserId, Date uploadTime, String vrImageUrl) {
        this.id = id;
        this.userInfoId = userInfoId;
        this.mapInfoId = mapInfoId;
        this.ip = ip;
        this.videoUrl = videoUrl;
        this.imageLibraryId = imageLibraryId;
        this.text = text;
        this.type = type;
        this.likeCount = likeCount;
        this.commentId = commentId;
        this.tagId = tagId;
        this.likeUserId = likeUserId;
        this.uploadTime = uploadTime;
        this.vrImageUrl = vrImageUrl;
    }

    public ShareInfo() {}
    public int getUserInfoId() {
        return userInfoId;
    }

    public void setUserInfoId(int userInfoId) {
        this.userInfoId = userInfoId;
    }

    public int getMapInfoId() {
        return mapInfoId;
    }

    public void setMapInfoId(int mapInfoId) {
        this.mapInfoId = mapInfoId;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public int getImageLibraryId() {
        return imageLibraryId;
    }

    public void setImageLibraryId(int imageLibraryId) {
        this.imageLibraryId = imageLibraryId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    public int getTagId() {
        return tagId;
    }

    public void setTagId(int tagId) {
        this.tagId = tagId;
    }

    public int getLikeUserId() {
        return likeUserId;
    }

    public void setLikeUserId(int likeUserId) {
        this.likeUserId = likeUserId;
    }

    public Date getUploadTime() {
        return uploadTime;
    }

    public void setUploadTime(Date uploadTime) {
        this.uploadTime = uploadTime;
    }

    @Override
    public String toString() {
        return "ShareInfo{" +
                "id=" + id +
                ", userInfoId=" + userInfoId +
                ", mapInfoId=" + mapInfoId +
                ", ip='" + ip + '\'' +
                ", videoUrl='" + videoUrl + '\'' +
                ", imageLibraryId=" + imageLibraryId +
                ", text='" + text + '\'' +
                ", type='" + type + '\'' +
                ", likeCount=" + likeCount +
                ", commentId=" + commentId +
                ", tagId=" + tagId +
                ", likeUserId=" + likeUserId +
                ", uploadTime=" + uploadTime +
                '}';
    }
}
