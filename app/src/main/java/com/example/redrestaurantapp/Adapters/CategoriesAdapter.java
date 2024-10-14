package com.example.redrestaurantapp.Adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.redrestaurantapp.Models.Category;
import com.example.redrestaurantapp.R;
import com.example.redrestaurantapp.Utils.ImageLoader;

import java.io.Console;
import java.util.List;

public class CategoriesAdapter extends RecyclerView.Adapter<CategoriesAdapter.CategoryViewHolder> {
    private final Context mCtx;
    private final List<Category> mCategoryList;
    private final onItemClickListener mOnItemClickListener;
    private final ImageLoader mImageLoader;
    private final String TAG = "CategoryAdapter";

    public CategoriesAdapter(Context ctx, List<Category> categoryList, CategoriesAdapter.onItemClickListener onItemClickListener) {
        mCtx = ctx;
        mCategoryList = categoryList;
        mImageLoader = ImageLoader.getInstance(ctx);
        mOnItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mCtx).inflate(R.layout.category_item, parent, false);

        return new CategoryViewHolder(view, mOnItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        holder.mTxt.setText(mCategoryList.get(position).getName());

        mImageLoader.loadImage(holder.mImg, mCategoryList.get(position).getImageUrl());
    }

    @Override
    public int getItemCount() {
        return mCategoryList.size();
    }

    public static class CategoryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView mImg;
        TextView mTxt;
        onItemClickListener onItemClickListener;

        public CategoryViewHolder(@NonNull View itemView, CategoriesAdapter.onItemClickListener onItemClickListener) {
            super(itemView);

            mImg = itemView.findViewById(R.id.categoryImg);
            mTxt = itemView.findViewById(R.id.categoryName);
            mTxt.setSelected(true);
            this.onItemClickListener = onItemClickListener;

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onItemClickListener.onItemClick(getAdapterPosition());
        }
    }

    public interface onItemClickListener {
        void onItemClick(int position);
    }
}
