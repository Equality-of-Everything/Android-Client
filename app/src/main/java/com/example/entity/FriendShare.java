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

}