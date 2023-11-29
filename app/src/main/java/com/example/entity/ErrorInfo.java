package com.example.entity;

/**
 * @Author : zhang
 * @Date : Created in 2023/11/29 20:04
 * @Decription : 用于做登录或注册编辑框输入信息非法时的验证，给多条if-else做优化
 */


public class ErrorInfo {
    String title;
    String msg;

    public ErrorInfo(String title, String msg) {
        this.title = title;
        this.msg = msg;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
