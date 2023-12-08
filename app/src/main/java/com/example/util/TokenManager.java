package com.example.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * @Author : Lee
 * @Date : Created in 2023/12/4 16:02
 * @Decription : 用于存取Token的工具类
 */

public class TokenManager {

    private static final String TOKEN_PREFS = "TokenPrefs";
    private static final String TOKEN_KEY = "token";

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
        return System.currentTimeMillis() > expireTime;
    }

}
