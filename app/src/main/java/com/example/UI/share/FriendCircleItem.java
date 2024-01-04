package com.example.UI.share;

import java.util.List;

/**
 * @Author : 你的名字
 * @Date : Created in 2023/12/20 16:59
 * @Decription :
 */

public class FriendCircleItem {
    // 发布者的用户名
    private String userName;
    // 发布者的头像
    private String avatarUrl;
    // 发布者的文字内容
    private String textContent;
    // 包含图片URL列表
    private List<String> mediaUrls;
    // 发布时间
    private String publishTime;
    //视频url
    private String videoUrl;

    public FriendCircleItem(String userName, String avatarUrl, String textContent, List<String> mediaUrls,String videoUrl,String publishTime) {
        this.userName = userName;
        this.avatarUrl = avatarUrl;
        this.textContent = textContent;
        this.mediaUrls = mediaUrls;
        this.publishTime = publishTime;
        this.videoUrl = videoUrl;
    }

    public FriendCircleItem() {

    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getPublishTime() {
        return publishTime;
    }
    public void setPublishTime(String publishTime) {
        this.publishTime = publishTime;
    }
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getTextContent() {
        return textContent;
    }

    public void setTextContent(String textContent) {
        this.textContent = textContent;
    }

    public List<String> getMediaUrls() {
        return mediaUrls;
    }

    public void setMediaUrls(List<String> mediaUrls) {
        this.mediaUrls = mediaUrls;
    }

    @Override
    public String toString() {
        return "FriendCircleItem{" +
                "userName='" + userName + '\'' +
                ", avatarUrl='" + avatarUrl + '\'' +
                ", textContent='" + textContent + '\'' +
                ", mediaUrls=" + mediaUrls +
                ", publishTime='" + publishTime + '\'' +
                ", videoUrl='" + videoUrl + '\'' +
                '}';
    }
}
