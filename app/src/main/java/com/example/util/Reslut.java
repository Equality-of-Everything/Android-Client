package com.example.util;

import java.io.Serializable;

/**
 * @Author : zhang
 * @Date : Created in 2023/11/29 16:58
 * @Decription : 用于规范前后端交互的返回值格式
 */


public class Reslut<T> implements Serializable {
    private boolean flag; //编码：true成功，false为失败
    private String msg; //错误信息
    private T data; //数据

    public Reslut() {
    }

    @Override
    public String toString() {
        return "Reslut{" +
                "flag=" + flag +
                ", msg='" + msg + '\'' +
                ", data=" + data +
                '}';
    }

    public Reslut(boolean flag, String msg, T data) {
        this.flag = flag;
        this.msg = msg;
        this.data = data;
    }

    public boolean getFlag() {
        return flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
