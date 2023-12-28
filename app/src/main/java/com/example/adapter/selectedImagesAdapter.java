package com.example.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.android_client.R;

import java.util.List;

/**
 * @Author : xcc
 * @Date : Created in 2023/12/22 16:46
 * @Decription :
 */

public class selectedImagesAdapter extends RecyclerView.Adapter<selectedImagesAdapter.ImageViewHolder>{
    private List<Uri> selectedImages;
    private Context context;
    public selectedImagesAdapter(List<Uri> selectedImages,Context context) {
        this.selectedImages = selectedImages;
        this.context = context;
    }
    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_selected_image,parent,false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        Uri selectedImageUri = selectedImages.get(position);
        Glide.with(context).load(selectedImageUri).into(holder.selectedImagesView);
    }

    @Override
    public int getItemCount() {
        return selectedImages.size();
    }
    public class ImageViewHolder extends RecyclerView.ViewHolder{
        ImageView selectedImagesView;
        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            selectedImagesView = itemView.findViewById(R.id.selectedImagesView);
        }
    }
}
