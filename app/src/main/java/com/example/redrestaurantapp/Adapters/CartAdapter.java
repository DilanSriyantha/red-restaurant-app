package com.example.redrestaurantapp.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.redrestaurantapp.Models.CartRecord;
import com.example.redrestaurantapp.Models.Product;
import com.example.redrestaurantapp.R;
import com.example.redrestaurantapp.Utils.Cart;
import com.example.redrestaurantapp.Utils.ImageLoader;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {
    private final Context mCtx;
    private final List<CartRecord> mCartList;
    private final onQuantityChangeListener mOnQuantityChangeListener;
    private final ImageLoader mImageLoader;
    private final String TAG = "CartAdapter";

    public CartAdapter(Context ctx, List<CartRecord> cartList, onQuantityChangeListener onQuantityChangeListener){
        mCtx = ctx;
        mCartList = cartList;
        mImageLoader = ImageLoader.getInstance(ctx);
        mOnQuantityChangeListener = onQuantityChangeListener;
    }

    @NonNull
    @Override
    public CartAdapter.CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mCtx).inflate(R.layout.cart_record, parent, false);

        return new CartViewHolder(view, mOnQuantityChangeListener);
    }

    @Override
    public void onBindViewHolder(@NonNull CartAdapter.CartViewHolder holder, int position) {
        mImageLoader.loadImage(holder.mImgProductThumbnail, mCartList.get(position).getProduct().getImageUrl());

        holder.mTxtProductName.setText(mCartList.get(position).getProduct().getName());
        holder.mTxtPrice.setText(String.format("LKR %s", mCartList.get(position).getTotal()));
        holder.mTxtQty.setText(String.valueOf(mCartList.get(position).getQuantity()));

        if(position == mCartList.size() - 1)
            holder.mSeparator.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        return mCartList.size();
    }

    public CartRecord getItem(int position) {
        return mCartList.get(position);
    }

    public static class CartViewHolder extends RecyclerView.ViewHolder {
        ImageView mImgProductThumbnail;

        TextView mTxtProductName;
        TextView mTxtPrice;
        TextView mTxtQty;

        ImageButton mBtnAddQty;
        ImageButton mBtnRemoveQty;

        View mSeparator;

        onQuantityChangeListener mOnQuantityChangeListener;

        public CartViewHolder(@NonNull View itemView, onQuantityChangeListener onQuantityChangeListener) {
            super(itemView);

            mImgProductThumbnail = itemView.findViewById(R.id.imgProductThumbnail);

            mTxtProductName = itemView.findViewById(R.id.txtProductName);
            mTxtPrice = itemView.findViewById(R.id.txtPrice);
            mTxtQty = itemView.findViewById(R.id.txtQty);

            mBtnAddQty = itemView.findViewById(R.id.btnAdd);
            mBtnRemoveQty = itemView.findViewById(R.id.btnRemove);

            mSeparator = itemView.findViewById(R.id.separator);

            mOnQuantityChangeListener = onQuantityChangeListener;

            mBtnAddQty.setOnClickListener(this::onAddClick);
            mBtnRemoveQty.setOnClickListener(this::onRemoveClick);

            if(mOnQuantityChangeListener == null){
                mBtnAddQty.setVisibility(View.GONE);
                mBtnRemoveQty.setVisibility(View.GONE);
            }
        }

        public void onAddClick(View v) {
            if(mOnQuantityChangeListener == null) return;
            mOnQuantityChangeListener.onAddClick(getAdapterPosition());
        }

        public void onRemoveClick(View v){
            if(mOnQuantityChangeListener == null) return;
            mOnQuantityChangeListener.onRemoveClick(getAdapterPosition());
        }
    }

    public interface onQuantityChangeListener {
        void onAddClick(int position);
        void onRemoveClick(int position);
    }
}
