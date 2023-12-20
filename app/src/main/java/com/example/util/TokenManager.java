package com.example.util;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Date;

/**
 * @Author : Lee
 * @Date : Created in 2023/12/4 16:02
 * @Decription : 用于存取Token的工具类
 */

public class TokenManager {

    private static final String TOKEN_PREFS = "TokenPrefs";
    private static final String TOKEN_KEY = "token";
    private static final String USER_NAME = "username";
    private static final String AVATAR_KEY = "avatar";

    /**
     * @param context:
     * @param token:
     * @return void
     * @author Lee
     * @description 存Token
     * @date 2023/12/4 16:08
     */
    public static void saveToken(Context context, String token) {
        long expireTime = System.currentTimeMillis() + 60*1000;
        SharedPreferences sharedPreferences = context.getSharedPreferences(TOKEN_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(TOKEN_KEY, token);
        editor.putLong("expireTime", expireTime);
        editor.apply();
    }

    /**
     * @param context:
     * @return String
     * @author Lee
     * @description 取Token
     * @date 2023/12/4 16:08
     */
    public static String getToken(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(TOKEN_PREFS, Context.MODE_PRIVATE);
        return sharedPreferences.getString(TOKEN_KEY, null);
    }

    /*
     * @param context:
      * @return boolean
     * @author zhang
     * @description 用于判断Token是否过期
     * @date 2023/12/8 11:06
     */
    public static boolean isTokenExpired(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(TOKEN_PREFS, Context.MODE_PRIVATE);
        long expireTime = sharedPreferences.getLong("expireTime", 0);
        System.out.println("expireTime:"+new Date(expireTime));
        return System.currentTimeMillis() > expireTime;
    }

    /*
     * @param context:
      * @return void
     * @author zhang
     * @description 用于删除Token
     * @date 2023/12/11 22:00
     */
    public static void deleteExpiredTokenFromSharedPreferences(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(TOKEN_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("token");
        editor.apply();
    }

    /**
     * @param context:
     * @param username:
     * @return void
     * @author Lee
     * @description 登录成功，存一下用户名
     * @date 2023/12/14 8:45
     */
    public static void saveUserName(Context context, String username) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(TOKEN_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(USER_NAME, username);
        editor.apply();
    }

    /**
     * @param context:
     * @return String
     * @author Lee
     * @description 取用户名
     * @date 2023/12/14 8:49
     */
    public static String getUserName(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(TOKEN_PREFS, Context.MODE_PRIVATE);
        return sharedPreferences.getString(USER_NAME, null);
    }

}
