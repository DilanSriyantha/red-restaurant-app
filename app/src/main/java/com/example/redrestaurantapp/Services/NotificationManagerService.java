package com.example.redrestaurantapp.Services;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ServiceInfo;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.ServiceCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.PackageManagerCompat;

import com.example.redrestaurantapp.R;
import com.example.redrestaurantapp.Utils.Notifications;

import java.util.List;

public class NotificationManagerService extends Service {
    private static final String TAG = "NotificationManagerService";
    private static final String CHANNEL_ID = "NotificationManagerChannel";
    private static final String POST_CHANNEL_ID = "PostNotificationChannel";
    private static Notifications mNotifications;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "service created");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "service started");

        createNotificationChannel();
        startForeground();

        new Thread(() -> {
            while(true){
                try{
                    Thread.sleep(1000);
//                    Log.d(TAG, "service is running");
                }catch (Exception ex){
                    ex.printStackTrace();
                }
            }
        }).start();

        new Thread(() -> {
            mNotifications = Notifications.getInstance();
            mNotifications.startListening();
            mNotifications.setOnChangeListener(new Notifications.OnNotificationChange() {
                @Override
                public void onChange(int count, com.example.redrestaurantapp.Models.Notification newNotification) {
                    if(count == -1) return;

                    Log.d(TAG, "posted notification");
                    postNotification(newNotification);
                }
            });

            Log.d(TAG, "" + mNotifications.getCount());
        }).start();

        return START_STICKY; // service restarts if terminated.
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void startForeground() {
        int specialUsePermission =
                ContextCompat.checkSelfPermission(this, Manifest.permission.FOREGROUND_SERVICE_SPECIAL_USE);
        if(specialUsePermission == PackageManager.PERMISSION_DENIED){
            stopSelf();
            return;
        }

        try{
            Notification notification =
                    new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID).build();

            int type = 0;
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                type = ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE;
            }
            ServiceCompat.startForeground(this, 100, notification, type);
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    private void postNotification(com.example.redrestaurantapp.Models.Notification notification) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle(notification.getTitle())
                .setContentText(notification.getMessage())
                .setStyle(new NotificationCompat.BigTextStyle().bigText(notification.getMessage()))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(NotificationCompat.DEFAULT_ALL);

        int postNotificationPermission =
                ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS);
        if(postNotificationPermission == PackageManager.PERMISSION_DENIED)
            return;

        NotificationManagerCompat
                .from(this)
                .notify(createNotificationID(), builder.build());
    }

    private int createNotificationID() {
        return (int)(Math.random() * 1000000);
    }

    private void createNotificationChannel() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Red Restaurant Notification Channel",
                    NotificationManager.IMPORTANCE_HIGH
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }
}
