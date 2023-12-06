package com.example.base;

import android.net.Uri;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.android_client.R;

/**
 * @Author : 你的名字
 * @Date : Created in 2023/12/6 11:58
 * @Decription :
 */

public class videoItem {
    private Uri uri;
    private String title;
    private String description;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textView;

        public ViewHolder(View itemView) {
            super(itemView);

        }
    }

}
