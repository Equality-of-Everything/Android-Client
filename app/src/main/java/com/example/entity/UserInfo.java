package com.example.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @Author : Lee
 * @Date : Created in 2023/12/13 14:31
 * @Decription :
 */

public class UserInfo {
    // 用户ID
    private int userId;
    // 用户名
    private String username;
    // 昵称
    private String nickname;
    // 头像
    private String avatar;
    // 性别
    private String gender;
    // 状态
    private String status;
    // 个性签名
    private String signature;
    // 生日
    private LocalDate birthday;
    //邮箱
    private String email;
    // 最近修改时间
    private LocalDateTime lastModifiedTime;
    // 地址
    private String address;
    // 与图文与视频分享信息表关联的ID
    private int shareInfoId;
    // 与轨迹表关联的ID
    private int trajectoryId;
    // 与好友动态分享关联的ID
    private int friendShareId;

    public UserInfo() {}

    public UserInfo(int userId, String username, String nickname,
                    String avatar, String gender, String status,
                    String signature, LocalDate birthday, String email,
                    LocalDateTime lastModifiedTime, String address,
                    int shareInfoId, int trajectoryId, int friendShareId) {
        this.userId = userId;
        this.username = username;
        this.nickname = nickname;
        this.avatar = avatar;
        this.gender = gender;
        this.status = status;
        this.signature = signature;
        this.birthday = birthday;
        this.email = email;
        this.lastModifiedTime = lastModifiedTime;
        this.address = address;
        this.shareInfoId = shareInfoId;
        this.trajectoryId = trajectoryId;
        this.friendShareId = friendShareId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public LocalDate getBirthday() {
        return birthday;
    }

    public void setBirthday(LocalDate birthday) {
        this.birthday = birthday;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDateTime getLastModifiedTime() {
        return lastModifiedTime;
    }

    public void setLastModifiedTime(LocalDateTime lastModifiedTime) {
        this.lastModifiedTime = lastModifiedTime;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getShareInfoId() {
        return shareInfoId;
    }

    public void setShareInfoId(int shareInfoId) {
        this.shareInfoId = shareInfoId;
    }

    public int getTrajectoryId() {
        return trajectoryId;
    }

    public void setTrajectoryId(int trajectoryId) {
        this.trajectoryId = trajectoryId;
    }

    public int getFriendShareId() {
        return friendShareId;
    }

    public void setFriendShareId(int friendShareId) {
        this.friendShareId = friendShareId;
    }

    @Override
    public String toString() {
        return "UserInfo{" +
                "userId=" + userId +
                ", username='" + username + '\'' +
                ", nickname='" + nickname + '\'' +
                ", avatar='" + avatar + '\'' +
                ", gender='" + gender + '\'' +
                ", status='" + status + '\'' +
                ", signature='" + signature + '\'' +
                ", birthday=" + birthday +
                ", email='" + email + '\'' +
                ", lastModifiedTime=" + lastModifiedTime +
                ", address='" + address + '\'' +
                ", shareInfoId=" + shareInfoId +
                ", trajectoryId=" + trajectoryId +
                ", friendShareId=" + friendShareId +
                '}';
    }
}
