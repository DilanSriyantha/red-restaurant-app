package com.example.redrestaurantapp.Utils;

import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.TextView;

import com.example.redrestaurantapp.Models.Notification;
import com.example.redrestaurantapp.R;
import com.example.redrestaurantapp.ServiceLayer.RealtimeDataBase;
import com.example.redrestaurantapp.ServiceLayer.UserManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Notifications {
    private static final String TAG = "Notifications";
    private static Notifications mInstance;
    private static final List<Notification> mNotificationList = new ArrayList<>();
    private static RealtimeDataBase mRdb;
    private static final List<OnNotificationChange> mOnChangeListeners = new ArrayList<>();
    private static UserManager mUserManager;
    private static boolean isLoading = false;

    public Notifications() {
        mRdb = RealtimeDataBase.getInstance("notifications");
        mUserManager = new UserManager();
    }

    public static Notifications getInstance() {
        if(mInstance == null)
            mInstance = new Notifications();

        return mInstance;
    }

    public void startListening() {
        mRdb.startListeningToChildEvents("to", mUserManager.getCurrentUser().getUid(), Notification.class, new RealtimeDataBase.OnRealTimeDataChangedCallback<Notification>() {
            @Override
            public void onChange(Notification newNotification) {
                if(newNotification.isSeen()) return;

                mNotificationList.add(newNotification);
                printNotifications();
                notifyListeners(newNotification);
            }

            @Override
            public void onError(Exception ex) {
                ex.printStackTrace();
            }
        });
    }

    public void fetch(RealtimeDataBase.OnFetchCompleted<Notification> callback) {
        mRdb.fetch(mUserManager.getCurrentUser().getUid(), Notification.class, callback);
    }

    public void fetch(){
        isLoading = true;
        mRdb.fetch(mUserManager.getCurrentUser().getUid(), Notification.class, new RealtimeDataBase.OnFetchCompleted<Notification>() {
            @Override
            public void onSuccessful(List<Notification> children) {
                mNotificationList.clear();
                mNotificationList.addAll(children);
                printNotifications();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        isLoading = false;
                    }
                }, 1000);
            }

            @Override
            public void onFailure(Exception ex) {
                ex.printStackTrace();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        isLoading = false;
                    }
                }, 1000);
            }
        });
    }

    public void markAllAsSeen() {
        ThreadPoolManager.getInstance().submitTask(new Runnable() {
            @Override
            public void run() {
                Map<String, Object> updates = new HashMap<>();
                for(Notification notification : mNotificationList){
                    if(notification.isSeen()) continue;

                    updates.put("notifications/"+notification.getKey()+"/seen", true);
                }
                if(updates.isEmpty()) {
                    Log.d(TAG, "no updates to notifications");
                    return;
                }
                mRdb.updateChildren(updates, new RealtimeDataBase.OnUpdateCompleted() {
                    @Override
                    public void onCompleted() {
                        Log.d(TAG, "notifications are marked as seen successfully.");
                        for(Notification notification : mNotificationList){
                            notification.setSeen(true);
                        }
                    }

                    @Override
                    public void onError(Exception ex) {
                        ex.printStackTrace();
                    }
                });
            }
        });
    }

    public boolean isLoading() {
        return isLoading;
    }

    public void add(Notification notification){
        int id = (int)(Math.random()*10000000);
        notification.setId(id);
        mNotificationList.add(notification);
    }

    public void remove(int id){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
            mNotificationList.removeIf((notification -> notification.getId() == id));
        }
    }

    public void remove(Notification notification){
        mNotificationList.remove(notification);
    }

    public int getCount() {
        int count = 0;
        for(Notification notification : mNotificationList){
            if(notification.isSeen()) continue;
            count++;
        }

        return count;
    }

    public List<Notification> getList() {
        return mNotificationList;
    }

    public void setSeen(int id){
        for(Notification notification : mNotificationList){
            if(notification.getId() == id){
                notification.setSeen(true);

                break;
            }
        }
    }

    public void updateNotificationLabel(TextView notificationLabel){
        if(notificationLabel == null) return;

        if(getCount() == 0){
            notificationLabel.setVisibility(View.GONE);
            return;
        }

        if(notificationLabel.getVisibility() == View.GONE)
            notificationLabel.setVisibility(View.VISIBLE);

        notificationLabel.setText(String.valueOf(getCount()));
    }

    public void setOnChangeListener(OnNotificationChange listener){
        mOnChangeListeners.add(listener);
    }

    public void clear() {
        mNotificationList.clear();
    }

    private void printNotifications() {
        for(Notification notification : mNotificationList){
            Log.d(TAG, notification.toString());
        }
    }

    private void notifyListeners(Notification newNotification) {
        Log.d(TAG, "called");
        for(OnNotificationChange listener : mOnChangeListeners){
            listener.onChange(mInstance.getCount(), newNotification);
        }
    }

    public interface OnNotificationChange {
        void onChange(int count, Notification newNotification);
    }
}
