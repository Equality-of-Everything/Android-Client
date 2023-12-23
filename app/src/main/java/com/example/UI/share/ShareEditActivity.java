package com.example.UI.share;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
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
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class ShareEditActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private selectedImagesAdapter selectedImagesAdapter;
    private List<Uri> selectedImages = new ArrayList<>();
    private ImageButton selectedButton;
    private static final String PREFS_NAME = "MyPrefsFile";
    List<LocalMedia> selectedMedia;
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
                selectImages(selectedMedia);
            }
        });
    }
    // 点击按钮触发图片选择
    public void selectImages(List<LocalMedia> preSelectedMedia) {

        PictureSelector.create(this)
                // 打开相册并设置媒体类型（这里设置为图片）
                .openGallery(SelectMimeType.ofAll())
                // 设置最大选择数量
                .setMaxSelectNum(9)
                .isEmptyResultReturn(true)
                //快速滑动选择
                .isFastSlidingSelect(true)
                // 使用 setSelectedData 方法设置已选中的媒体
                .setSelectedData(preSelectedMedia)
                // 设置图片加载引擎，例如 Glide、Picasso
                .setImageEngine(GlideEngine.createGlideEngine())
                // 设置回调请求码
                .forResult(PictureConfig.CHOOSE_REQUEST);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PictureConfig.CHOOSE_REQUEST && resultCode == RESULT_OK && data != null) {
            List<LocalMedia> newSelectedMedia = PictureSelector.obtainSelectorList(data);

            // 空引用检查
            if (selectedImages != null && newSelectedMedia != null) {
                // 处理取消选择的逻辑
                Iterator<Uri> iterator = selectedImages.iterator();
                while (iterator.hasNext()) {
                    Uri selectedImageUri = iterator.next();
                    String selectedImagePath = selectedImageUri.getPath();
                    boolean found = false;

                    for (LocalMedia media : newSelectedMedia) {
                        if (selectedImagePath.equals(media.getPath())) {
                            found = true;
                            break;
                        }
                    }

                    if (!found) {
                        iterator.remove(); // 从 selectedImages 中移除取消选择的图片
                    }
                }

                // 处理新选择的逻辑
                for (LocalMedia media : newSelectedMedia) {
                    Uri selectedImageUri = Uri.parse(media.getPath());
                    if (!selectedImages.contains(selectedImageUri)) {
                        if (selectedImages.size() < 9) {
                            selectedImages.add(selectedImageUri);
                        } else {
                            Toast.makeText(this, "最多只能选择 " + 9 + " 张图片", Toast.LENGTH_SHORT).show();
                            break;
                        }
                    }
                }

                // 更新 selectedMedia
                selectedMedia = newSelectedMedia;

                // 通知适配器更新 RecyclerView
                selectedImagesAdapter.notifyDataSetChanged();
            } else {
                // 处理 selectedImages 或 newSelectedMedia 为 null 的情况
                Log.e("ShareEditActivity", "selectedImages or newSelectedMedia is null");
            }
        }
    }
}