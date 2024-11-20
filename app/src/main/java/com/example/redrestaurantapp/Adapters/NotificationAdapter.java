package com.example.redrestaurantapp.Adapters;

import android.content.Context;
import android.icu.text.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.redrestaurantapp.Models.Notification;
import com.example.redrestaurantapp.R;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder> {
    private final Context mCtx;
    private final String TAG = "NotificationAdapter";
    private final List<Notification> mNotificationsList;
    private final onItemClickListener mOnItemClickListener;
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss a");

    public NotificationAdapter(Context ctx, List<Notification> notificationsList, onItemClickListener onItemClickListener){
        mCtx = ctx;
        mNotificationsList = notificationsList;
        mOnItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public NotificationAdapter.NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mCtx).inflate(R.layout.notification_strip, parent, false);

        return new NotificationViewHolder(view, mOnItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationAdapter.NotificationViewHolder holder, int position) {
//        if(!mNotificationsList.get(position).isSeen())
//            holder.mNotificationBackground.setBackgroundColor(mCtx.getResources().getColor(R.color.notification_unread));
        holder.mTxtTitle.setText(mNotificationsList.get(position).getTitle());
        holder.mTxtMessage.setText(mNotificationsList.get(position).getMessage());
        holder.mTxtDatetime.setText(timestampToDate(mNotificationsList.get(position).getTimestamp()));
    }

    @Override
    public int getItemCount() {
        return mNotificationsList.size();
    }

    public static class NotificationViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        LinearLayout mNotificationBackground;
        TextView mTxtTitle;
        TextView mTxtMessage;
        TextView mTxtDatetime;
        onItemClickListener mOnItemClickListener;

        public NotificationViewHolder(@NonNull View itemView, onItemClickListener onItemClickListener) {
            super(itemView);

            mNotificationBackground = itemView.findViewById(R.id.notificationBackground);
            mTxtTitle = itemView.findViewById(R.id.txtNotificationTitle);
            mTxtMessage = itemView.findViewById(R.id.txtNotificationMessage);
            mTxtDatetime = itemView.findViewById(R.id.txtNotificationDateTime);
            mOnItemClickListener = onItemClickListener;

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mOnItemClickListener.onItemClick(getAdapterPosition());
        }
    }

    private String timestampToDate(long timestamp){
        Date date = new Timestamp(timestamp);

        Log.d(TAG, String.valueOf(timestamp));

        return dateFormat.format(date);
    }

    public interface onItemClickListener {
        void onItemClick(int position);
    }
}
