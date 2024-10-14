package com.example.redrestaurantapp.Adapters;

import android.content.Context;
import android.media.Image;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.redrestaurantapp.Models.Product;
import com.example.redrestaurantapp.R;
import com.example.redrestaurantapp.Utils.ImageLoader;

import java.util.List;

public class ProductsAdapter extends RecyclerView.Adapter<ProductsAdapter.ProductsViewHolder> {
    private final Context mCtx;
    private final List<Product> mProductList;
    private final LayoutType mLayoutType;
    private final ImageLoader mImageLoader;
    private final onItemClickListener mOnItemClickListener;

    public ProductsAdapter(Context ctx, List<Product> mProductList, LayoutType layoutType, onItemClickListener mOnItemClickListener) {
        this.mCtx = ctx;
        this.mProductList = mProductList;
        mLayoutType = layoutType;
        mImageLoader = ImageLoader.getInstance(ctx);
        this.mOnItemClickListener = mOnItemClickListener;
    }

    @NonNull
    @Override
    public ProductsAdapter.ProductsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layoutId = mLayoutType == LayoutType.WIDE ? R.layout.product_item_wide : R.layout.product_item;
        View view = LayoutInflater.from(mCtx).inflate(layoutId, parent, false);

        return new ProductsViewHolder(view, mOnItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductsAdapter.ProductsViewHolder holder, int position) {
        mImageLoader.loadImage(holder.mImg, mProductList.get(position).getImageUrl());

        holder.mProductName.setText(mProductList.get(position).getName());
        holder.mDescription.setText(mProductList.get(position).getDescription());
        holder.mPrice.setText("LKR " + mProductList.get(position).getPrice() + "/=");
    }

    @Override
    public int getItemCount() {
        return mProductList.size();
    }

    public static class ProductsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView mImg;
        TextView mProductName;
        TextView mDescription;
        TextView mPrice;
        onItemClickListener onItemClickListener;

        public ProductsViewHolder(@NonNull View itemView, ProductsAdapter.onItemClickListener onItemClickListener) {
            super(itemView);

            mImg = itemView.findViewById(R.id.productImg);
            mProductName = itemView.findViewById(R.id.productName);
            mDescription = itemView.findViewById(R.id.productDescription);
            mPrice = itemView.findViewById(R.id.productPrice);
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

    public enum LayoutType {
        WIDE,
        NARROW
    }
}
