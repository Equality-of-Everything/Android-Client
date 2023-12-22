package com.example.UI.share;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.example.adapter.selectedImagesAdapter;
import com.example.android_client.R;
import com.example.util.GlideEngine;
import com.luck.picture.lib.basic.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.config.SelectMimeType;
import com.luck.picture.lib.entity.LocalMedia;

import java.util.ArrayList;
import java.util.List;

public class ShareEditActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private selectedImagesAdapter selectedImagesAdapter;
    private List<Uri> selectedImages = new ArrayList<>();
    private ImageButton selectedButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_edit);
        recyclerView = findViewById(R.id.selectedImages);
        selectedButton = findViewById(R.id.selectedButton);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        selectedImagesAdapter = new selectedImagesAdapter(selectedImages,this);
        recyclerView.setAdapter(selectedImagesAdapter);
        selectedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ShareEditActivity.this,"点击",Toast.LENGTH_SHORT).show();
                selectImages();
            }
        });
    }
    // 点击按钮触发图片选择
    public void selectImages() {
        Toast.makeText(this, "选择图片", Toast.LENGTH_SHORT).show();
        PictureSelector.create(this)
                // 打开相册并设置媒体类型（这里设置为图片）
                .openGallery(SelectMimeType.ofImage())
                // 设置最大选择数量
                .setMaxSelectNum(9)
                // 设置图片加载引擎，例如 Glide、Picasso
                .setImageEngine(GlideEngine.createGlideEngine())
                // 设置回调请求码
                .forResult(PictureConfig.CHOOSE_REQUEST);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PictureConfig.CHOOSE_REQUEST && resultCode == RESULT_OK && data != null) {
            List<LocalMedia> images = PictureSelector.obtainSelectorList(data);
            if (images != null && !images.isEmpty()) {
                for (LocalMedia media : images) {
                    selectedImages.add(Uri.parse(media.getPath())); // 将选中的图片转换成 Uri 添加到列表中
                }
                selectedImagesAdapter.notifyDataSetChanged(); // 更新 RecyclerView
            }
        }
    }
}