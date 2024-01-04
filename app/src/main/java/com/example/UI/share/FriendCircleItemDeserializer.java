package com.example.UI.share;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @Author : zhang
 * @Date : Created in 2024/1/3 14:25
 * @Decription : 用于解析Friend的Json数据
 */


public class FriendCircleItemDeserializer implements JsonDeserializer<FriendCircleItem> {
    @Override
    public FriendCircleItem deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        // 解析基本字段
        String userName = json.getAsJsonObject().get("userName").getAsString();
        String avatarUrl = json.getAsJsonObject().get("avatarUrl").getAsString();
        String textContent = json.getAsJsonObject().get("textContent").getAsString();
        String publishTime = json.getAsJsonObject().get("publishTime").getAsString();
        String videoUrl = null;
        if(json.getAsJsonObject().get("videoUrl")!=null) {
            videoUrl = json.getAsJsonObject().get("videoUrl").isJsonNull()
                    ? null
                    : json.getAsJsonObject().get("videoUrl").getAsString();
        }
        // 解析媒体URL列表
        List<String> mediaUrls = new ArrayList<>();
        JsonArray mediaUrlsArray = json.getAsJsonObject().get("mediaUrls").getAsJsonArray();
        for (JsonElement mediaUrlElement : mediaUrlsArray) {
            mediaUrls.add(mediaUrlElement.getAsString());
        }

        return new FriendCircleItem(userName, avatarUrl, textContent, mediaUrls, videoUrl, publishTime);
    }


}
