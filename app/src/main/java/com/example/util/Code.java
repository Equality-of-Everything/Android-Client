package com.example.util;

/**
 * @Author : Lee
 * @Date : Created in 2023/11/30 10:19
 * @Decription :
 */

public class Code {

    public static final int SUCCESS = 200;
    //用户名不存在
    public static final int LOGIN_ERROR_NOUSER = 410;

    //密码错误
    public static final int LOGIN_ERROR_PASSWORD = 411;

    // 获取视频信息成功
    public static final int GET_VIDEO_SUCCESS = 200;

    // 当地视频内容为空
    public static final int GET_VIDEO_EMPTY = 404;

    // Token不存在
    public static final int TOKEN_NOT_EXIST = 401;

    // Token不合法或已过期
    public static final int TOKEN_INVALID = 402;

    // 用户异地登录
    public static final int TOKEN_OTHER_LOGIN = 403;


    // 用户已经点赞
    public static final int VIDEO_HAS_LIKED = 200;

    // 用户未点赞
    public static final int VIDEO_NOT_LIKED = 408;
}
