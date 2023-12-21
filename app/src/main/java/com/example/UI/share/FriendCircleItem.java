package com.example.UI.share;

import java.util.List;

/**
 * @Author : 你的名字
 * @Date : Created in 2023/12/20 16:59
 * @Decription :
 */

public class FriendCircleItem {
    private String userName;
    private String avatarUrl;
    private String textContent;
    // 包含图片和视频的URL列表
    private List<String> mediaUrls;
    private String publishTime;

    public FriendCircleItem(String userName, String avatarUrl, String textContent, List<String> mediaUrls,String publishTime) {
        this.userName = userName;
        this.avatarUrl = avatarUrl;
        this.textContent = textContent;
        this.mediaUrls = mediaUrls;
        this.publishTime = publishTime;
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
}
