package com.example.util;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
        if (context == null) {
            Log.e("TokenManager", "Context is null");
            return null;
        }
        
        try {
            SharedPreferences sharedPreferences = context.getSharedPreferences("authStatus", Context.MODE_PRIVATE);
            return sharedPreferences.getString("username", null);
        } catch (Exception e) {
            Log.e("TokenManager", "Error accessing SharedPreferences: " + e.getMessage());
            return null;
        }
    }

    /**
     * @param context:
     * @param username:
     * @return void
     * @author Lee
     * @description 存好友用户名
     * @date 2024/1/2 10:53
     */
    public static void saveFriendUsername(Context context, String username) {
        Set<String> friendUsernames = new HashSet<>(getAllFriendUsernames(context));
        friendUsernames.add(username);
        SharedPreferences prefs = context.getSharedPreferences("friends", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putStringSet("FriendUsernames", friendUsernames);
        editor.apply();
    }

    /**
     * @param context:
     * @return List<String>
     * @author Lee
     * @description 取好友用户名
     * @date 2024/1/2 10:54
     */
    public static Set<String> getAllFriendUsernames(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("friends", Context.MODE_PRIVATE);
        return prefs.getStringSet("FriendUsernames", new HashSet<>());
    }

    /**
     * @param context:
     * @param friendNumber:
     * @return void
     * @author Lee
     * @description 删除单个好友用户名
     * @date 2024/1/2 10:54
     */
    public static void deleteFriendUsername(Context context, int friendNumber) {
        SharedPreferences prefs = context.getSharedPreferences("friends", Context.MODE_PRIVATE);
        int numberOfFriends = prefs.getInt("friendNumber", 0); // 获取好友的数量
        if (friendNumber < numberOfFriends) {
            SharedPreferences.Editor editor = prefs.edit();
            editor.remove("friend" + friendNumber); // 删除指定好友的用户名
            // 更新好友编号
            for (int i = friendNumber; i < numberOfFriends - 1; i++) {
                String username = prefs.getString("friend" + (i + 1), "");
                editor.putString("friend" + i, username);
            }
            editor.putInt("friendNumber", numberOfFriends - 1); // 更新好友编号
            editor.apply();
        }
    }

    /**
     * @param context:
     * @param username:
     * @return void
     * @author Lee
     * @description 存所有注册用户名
     * @date 2024/1/2 11:02
     */
    public static void saveEMUserName(Context context, String username) {
        Set<String> emUsernames = new HashSet<>(getEMUserName(context));
        emUsernames.add(username);
        SharedPreferences preferences = context.getSharedPreferences("EMUsers", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putStringSet("EMUsernames", emUsernames);
        editor.apply();
    }

    /**
     * @param context:
     * @return List<String>
     * @author Lee
     * @description 获取所有注册用户名
     * @date 2024/1/2 11:05
     */
    public static Set<String> getEMUserName(Context context) {
        SharedPreferences preferences = context.getSharedPreferences("EMUsers", Context.MODE_PRIVATE);
        return preferences.getStringSet("EMUsernames", new HashSet<>());
    }

}












































