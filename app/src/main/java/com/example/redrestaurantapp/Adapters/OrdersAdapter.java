package com.example.redrestaurantapp.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.redrestaurantapp.Models.Order;
import com.example.redrestaurantapp.R;
import com.example.redrestaurantapp.Utils.ImageLoader;
import com.google.firebase.Timestamp;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

public class OrdersAdapter extends RecyclerView.Adapter<OrdersAdapter.OrdersViewHolder> {
    private final Context mCtx;
    private final List<Order> mOrderList;
    private final ImageLoader mImageLoader;
    private final onItemClickListener mOnItemClickListener;

    public OrdersAdapter(Context ctx, List<Order> orderList, onItemClickListener onItemClickListener) {
        mCtx = ctx;
        mOrderList = orderList;
        mOnItemClickListener = onItemClickListener;
        mImageLoader = ImageLoader.getInstance(ctx);
    }

    @NonNull
    @Override
    public OrdersAdapter.OrdersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mCtx).inflate(R.layout.order_item_wide, parent, false);

        return new OrdersViewHolder(view, mOnItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull OrdersAdapter.OrdersViewHolder holder, int position) {
        mImageLoader.loadImage(holder.mImgOrderThumbnail, mOrderList.get(position).getImageUrl());

        holder.mTxtOrderCaption.setText(String.format("Order #%s", mOrderList.get(position).getId()));
        holder.mTxtOrderItemsCount.setText(String.format("LKR %s/=", mOrderList.get(position).getSubtotal()));
        holder.mTxtDateTime.setText(formatTimestamp(mOrderList.get(position).getTimestamp()));

        if(position == mOrderList.size() - 1)
            holder.mSeparator.setVisibility(View.GONE);
    }

    private String formatTimestamp(Timestamp timestamp){
        return new SimpleDateFormat("yyyy-MM-dd hh:mm:ss a").format(timestamp.toDate());
    }

    public Order getOrder(int position){
        return mOrderList.get(position);
    }

    @Override
    public int getItemCount() {
        return mOrderList.size();
    }

    public static class OrdersViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        ImageView mImgOrderThumbnail;
        TextView mTxtOrderCaption;
        TextView mTxtOrderItemsCount;
        TextView mTxtDateTime;
        FrameLayout mBtnViewOrder;
        View mSeparator;

        onItemClickListener mOnItemClickListener;

        public OrdersViewHolder(@NonNull View itemView, OrdersAdapter.onItemClickListener onItemClickListener) {
            super(itemView);

            mImgOrderThumbnail = itemView.findViewById(R.id.imgOrderThumbnail);
            mTxtOrderCaption = itemView.findViewById(R.id.txtOrderCaption);
            mTxtOrderItemsCount = itemView.findViewById(R.id.txtOrderItemsCount);
            mTxtDateTime = itemView.findViewById(R.id.txtDateTime);
            mBtnViewOrder = itemView.findViewById(R.id.btnViewOrder);
            mSeparator = itemView.findViewById(R.id.separator);
            mOnItemClickListener = onItemClickListener;

            mBtnViewOrder.setOnClickListener(this);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mOnItemClickListener.onItemClick(getAdapterPosition());
        }
    }

    public interface onItemClickListener {
        void onItemClick(int position);
    }
}
