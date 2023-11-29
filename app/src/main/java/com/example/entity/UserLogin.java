package com.example.entity;

import java.io.Serializable;
import java.sql.Date;

/**
 * @Author : Lee
 * @Date : Created in 2023/11/29 13:40
 * @Decription :
 */

public class UserLogin implements Serializable {
    // 用户ID
    private Integer userId;
    // 用户名
    private String username;
    // 密码
    private String password;
    // 邮箱
    private String email;
    // 手机号
    private String phoneNumber;
    // 昵称
    private String nickname;
    // 头像
    private String avatar;
    // 注册时间
    private Date registrationTime;
    // 最后登录日期
    private Date lastLoginDate;
    // 登录错误次数
    private Integer loginErrorCount;

    public UserLogin(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
    }

    public UserLogin() {}
    public UserLogin(Integer userId, String username, String password,
                     String email, String phoneNumber, String nickname,
                     String avatar, Date registrationTime,
                     Date lastLoginDate, Integer loginErrorCount) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.nickname = nickname;
        this.avatar = avatar;
        this.registrationTime = registrationTime;
        this.lastLoginDate = lastLoginDate;
        this.loginErrorCount = loginErrorCount;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
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

    public Date getRegistrationTime() {
        return registrationTime;
    }

    public void setRegistrationTime(Date registrationTime) {
        this.registrationTime = registrationTime;
    }

    public Date getLastLoginDate() {
        return lastLoginDate;
    }

    public void setLastLoginDate(Date lastLoginDate) {
        this.lastLoginDate = lastLoginDate;
    }

    public Integer getLoginErrorCount() {
        return loginErrorCount;
    }

    public void setLoginErrorCount(Integer loginErrorCount) {
        this.loginErrorCount = loginErrorCount;
    }

}




























