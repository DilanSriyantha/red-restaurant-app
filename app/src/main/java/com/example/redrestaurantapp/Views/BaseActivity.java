package com.example.redrestaurantapp.Views;

import android.os.Bundle;
import android.os.PersistableBundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.redrestaurantapp.Models.Notification;
import com.example.redrestaurantapp.Utils.Cart;
import com.example.redrestaurantapp.Utils.Notifications;

public abstract class BaseActivity extends AppCompatActivity {
    private final Notifications.OnNotificationChange notificationChangeListener = new Notifications.OnNotificationChange() {
        @Override
        public void onChange(int count, Notification newNotification) {
            updateNotificationsLabel(count, newNotification);
        }
    };

    private final Cart.OnCartCountChange cartCountChangeListener = new Cart.OnCartCountChange() {
        @Override
        public void onChange(int count) {
            updateCartCountLabel(count);
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
    }

    @Override
    protected void onResume() {
        super.onResume();

        Notifications.getInstance().setOnChangeListener(notificationChangeListener);
        updateNotificationsLabel(Notifications.getInstance().getCount(), null);

        Cart.getInstance().setOnCountChangeListener(cartCountChangeListener);
        updateCartCountLabel(Cart.getInstance().getCartCount());
    }

    protected abstract void updateCartCountLabel(int count);
    protected abstract void updateNotificationsLabel(int count, Notification newNotification);
}
