package com.example.UI.share;
import static com.example.UI.mine.MineFragment.MEDIA_TYPE_JPG;
import static com.example.android_client.LoginActivity.ip;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Canvas;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import com.example.adapter.selectedImagesAdapter;
import com.example.android_client.MainActivity;
import com.example.android_client.R;
import com.example.util.GlideEngine;
import com.example.util.Result;
import com.example.util.TokenManager;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.gson.Gson;
import com.luck.picture.lib.basic.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.SelectMimeType;
import com.luck.picture.lib.entity.LocalMedia;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ShareEditActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private selectedImagesAdapter selectedImagesAdapter;
    private List<Uri> selectedImages = new ArrayList<>();//选中的图片
    private ImageButton selectedButton;
    private static final String PREFS_NAME = "MyPrefsFile";
    private TextView deleteArea;
    List<LocalMedia> selectedMedia;
    private int lastitemViewBottom = 0; // 用于存储上一次的 itemView 的顶部位置
    private boolean isOverDeleteArea = false;
    private int lastDraggedPosition = RecyclerView.NO_POSITION;
    private List<LocalMedia> newSelectedMedia;
    private MaterialToolbar backShare;
    private LocalMedia removeMedia;
    private EditText shareContent;//分享的文本
    private Button btnShareLaunch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_edit);
        // 绑定selectedImages数组
        recyclerView = findViewById(R.id.selectedImages);
        // 绑定添加图片的控件
        selectedButton = findViewById(R.id.selectedButton);
        // 绑定删除区域
        deleteArea = findViewById(R.id.deleteArea);
        //绑定返回按钮
        backShare = findViewById(R.id.back_to_Share);
        btnShareLaunch = findViewById(R.id.btn_share_launch);
        shareContent = findViewById(R.id.share_text);

        setListener();

        // 绑定触摸事件
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelperCallback());
        itemTouchHelper.attachToRecyclerView(recyclerView);
        // 设置布局管理器
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        selectedImagesAdapter = new selectedImagesAdapter(selectedImages,this);
        recyclerView.setAdapter(selectedImagesAdapter);
        // 点击按钮触发图片选择
        selectedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImages(selectedMedia);
            }
        });
        // 点击返回按钮触发返回事件
        backShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent resultIntent = new Intent();
                resultIntent.putExtra("returnToFragment", "ShareEditTAG");
                setResult(Activity.RESULT_OK, resultIntent);
                finish();
            }
        });
    }

    //发布按钮，将数据传到后端数据库
    private void setListener() {
        btnShareLaunch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               new Thread(new Runnable() {
                   @Override
                   public void run() {
                       Gson json = new Gson();

                       String content = shareContent.getText().toString();
                       OkHttpClient client = new OkHttpClient.Builder()
                               .connectTimeout(15, TimeUnit.SECONDS)
                               .readTimeout(15, TimeUnit.SECONDS)
                               .writeTimeout(15, TimeUnit.SECONDS)
                               .build();;

                       MultipartBody.Builder builder = new MultipartBody.Builder()
                               .setType(MultipartBody.FORM);

                       for(Uri imageUri : selectedImages) {
                           System.out.println(imageUri);
                           String filePath = getRealPathFromURI(imageUri);
                           File file = new File(filePath);
                           builder.addFormDataPart("files",file.getName(),
                                   RequestBody.create(MEDIA_TYPE_JPG, file));

                       }

                       builder.addFormDataPart("username",TokenManager.getUserName(ShareEditActivity.this))
                               .addFormDataPart("textContent",content);

                       MultipartBody body = builder.build();
                       Request request = new Request.Builder()
                               .url("http://"+ip+":8080/friendShare/upload")
                               .post(body)
                               .build();

                       client.newCall(request).enqueue(new Callback() {
                           @Override
                           public void onFailure(@NonNull Call call, @NonNull IOException e) {
                               e.printStackTrace();
                               runOnUiThread(new Runnable() {
                                   @Override
                                   public void run() {
                                       Toast.makeText(ShareEditActivity.this,"服务器连接失败，请稍后重试",Toast.LENGTH_SHORT).show();
                                   }
                               });
                           }

                           @Override
                           public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                               Log.e("ShareEditActivity", "请求后端数据成功");
                               String responseData = response.body().string();
                               Result result = json.fromJson(responseData, Result.class);
                               runOnUiThread(new Runnable() {
                                   @Override
                                   public void run() {
                                       if(!result.getFlag()) {
                                           Toast.makeText(ShareEditActivity.this, "发布失败，请稍后重试", Toast.LENGTH_SHORT).show();
                                           return;
                                       }
                                       Toast.makeText(ShareEditActivity.this, "发布成功", Toast.LENGTH_SHORT).show();
                                       Intent intent = new Intent(ShareEditActivity.this, MainActivity.class); // 替换为您的MainActivity
                                       intent.putExtra("FRAGMENT_TO_LOAD", "ShareFragment"); // 传递标识符
                                       startActivity(intent);
                                       finish(); // 结束当前 Activity
                                   }
                               });

                           }
                       });
                   }
               }).start();
            }
        });
    }


    // 获取图片真实路径
    public String getRealPathFromURI(Uri contentUri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(contentUri, projection, null, null, null);

        if (cursor != null) {
            int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            String filePath = cursor.getString(columnIndex);
            cursor.close();
            return filePath;
        }

        return contentUri.getPath(); // Fallback to the original URI if cursor is null
    }


    // ItemTouchHelper.Callback 实现
    private class ItemTouchHelperCallback extends ItemTouchHelper.Callback {
        @Override
        public boolean isLongPressDragEnabled() {
            return true; // 启用长按拖动
        }
        @Override
        public boolean isItemViewSwipeEnabled() {
            return false; // 禁用滑动删除
        }
        @Override
        public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            // 启用上下左右拖动
            int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
            // 启用上下左右滑动删除
            return makeMovementFlags(dragFlags, 0);
        }

        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
            // 获取拖动的位置
            int fromPos = viewHolder.getAdapterPosition();
            int toPos = target.getAdapterPosition();
            // 交换数据集中的图片位置
            Uri temp = selectedImages.remove(fromPos);
            selectedImages.add(toPos, temp);
            // 交换selectedMedia列表中对应元素的位置
            LocalMedia tempMedia = selectedMedia.remove(fromPos);
            selectedMedia.add(toPos, tempMedia);
            // 通知适配器更新数据
            selectedImagesAdapter.notifyItemMoved(fromPos, toPos);
            // 记录最后拖拽的位置
            lastDraggedPosition = toPos;
            return true;
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
            // 滑动删除未启用，此方法留空
        }
        @Override
        public boolean canDropOver(RecyclerView recyclerView, RecyclerView.ViewHolder current, RecyclerView.ViewHolder target) {
            // 允许在任何位置放下
            return true;
        }
        @Override
        public void onChildDrawOver(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            super.onChildDrawOver(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);

            // 获取 itemView 的实时位置
            int[] itemViewLocation = new int[2];
            viewHolder.itemView.getLocationOnScreen(itemViewLocation);
            int itemViewTop = itemViewLocation[1];
            int itemViewBottom = itemViewTop + viewHolder.itemView.getHeight();
            lastitemViewBottom = itemViewBottom;
            // 检测是否进入删除区域
            isOverDeleteArea = isInViewBounds(deleteArea, lastitemViewBottom);
            if (isOverDeleteArea) {
                deleteArea.setBackgroundColor(getResources().getColor(R.color.red));
            }else {
                deleteArea.setBackgroundColor(getResources().getColor(R.color.red1));
            }
        }
        // 新的 isInViewBounds 方法，接受实时的 itemView 位置信息
        private boolean isInViewBounds(View deleteView, int itemViewBottom) {
            // 获取 deleteView 的位置信息
            int[] deleteViewLocation = new int[2];
            deleteView.getLocationOnScreen(deleteViewLocation);
            int deleteViewTop = deleteViewLocation[1];

            // 确保 itemView 至少部分进入了 deleteView 的区域
            return itemViewBottom > deleteViewTop ;
        }
        @Override
        public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            super.clearView(recyclerView, viewHolder);
        }
        @Override
        public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
            super.onSelectedChanged(viewHolder, actionState);
            if (actionState == ItemTouchHelper.ACTION_STATE_DRAG) {
                // 拖拽开始，显示删除区域
                if (viewHolder != null) {
                    lastDraggedPosition = viewHolder.getAdapterPosition();
                    deleteArea.setVisibility(View.VISIBLE);
                }
            }
            if (actionState == ItemTouchHelper.ACTION_STATE_IDLE) {
                deleteArea.setVisibility(View.GONE);
                if (isOverDeleteArea) {
                    // 在拖拽结束时，使用记录的位置信息进行相应的操作
                    selectedImages.remove(lastDraggedPosition);
                    selectedImagesAdapter.notifyItemRemoved(lastDraggedPosition);
                    // 以便下次拖拽时能正确记录最后拖拽的位置
                    selectedImagesAdapter.notifyDataSetChanged();
                    removeMedia = selectedMedia.remove(lastDraggedPosition);
                    System.out.println("removeMedia" + removeMedia);
                    System.out.println("selectedMedia" + selectedMedia);
                    System.out.println("lastDraggedPosition"+lastDraggedPosition);
                    lastDraggedPosition = RecyclerView.NO_POSITION;

                }
            }

        }
    }

    /**
     * @param preSelectedMedia:
     * @return void
     * @author xcc
     * @description 点击按钮触发图片选择
     * @date 2023/12/25 14:37
     */

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

    /**
     * @param requestCode:
     * @param resultCode:
     * @param data:
     * @return void
     * @author xcc
     * @description 图片选择回调
     * @date 2023/12/25 14:37
     */

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        newSelectedMedia = PictureSelector.obtainSelectorList(data);
        if (requestCode == PictureConfig.CHOOSE_REQUEST && resultCode == RESULT_OK && data != null) {
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

                // 更新 selectedMedia
                selectedMedia=newSelectedMedia;

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

                // 通知适配器更新 RecyclerView
                selectedImagesAdapter.notifyDataSetChanged();
            } else {
                // 处理 selectedImages 或 newSelectedMedia 为 null 的情况
                Log.e("ShareEditActivity", "selectedImages or newSelectedMedia is null");
            }
        }
    }
}