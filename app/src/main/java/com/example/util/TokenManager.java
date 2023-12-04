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
        SharedPreferences sharedPreferences = context.getSharedPreferences(TOKEN_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(TOKEN_KEY, token);
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

}
