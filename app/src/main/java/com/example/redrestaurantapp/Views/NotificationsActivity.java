package com.example.redrestaurantapp.Views;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.redrestaurantapp.Adapters.NotificationAdapter;
import com.example.redrestaurantapp.Models.Notification;
import com.example.redrestaurantapp.R;
import com.example.redrestaurantapp.ServiceLayer.RealtimeDataBase;
import com.example.redrestaurantapp.ServiceLayer.UserManager;
import com.example.redrestaurantapp.Utils.Cart;
import com.example.redrestaurantapp.Utils.Notifications;

import java.util.List;

public class NotificationsActivity extends BaseActivity {
    private final String TAG = "NotificationActivity";

    private ImageButton mBtnBack;
    private ImageButton mBtnNotification;
    private ImageButton mBtnCart;

    private TextView mTxtTitle;
    private TextView mTxtCartCount;

    private RecyclerView mNotificationsRecycler;

    private final Notifications mNotifications;
    private final UserManager mUserManager;
    private final Cart mCart;

    public NotificationsActivity() {
        mCart = Cart.getInstance();
        mNotifications = Notifications.getInstance();
        mUserManager = new UserManager();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_notifications);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mBtnBack = findViewById(R.id.btnBack);
        mBtnBack.setOnClickListener(this::onBackClick);

        mBtnNotification = findViewById(R.id.btnNotifications);
        mBtnNotification.setVisibility(View.GONE);

        mBtnCart = findViewById(R.id.btnCart);
        mBtnCart.setOnClickListener(this::onCartClick);

        mTxtTitle = findViewById(R.id.txtAppbarTitle);
        mTxtTitle.setText("Notifications");

        mTxtCartCount = findViewById(R.id.txtCartCount);

        loadNotifications();
    }

    @Override
    protected void updateCartCountLabel(int count) {
        if(mTxtCartCount == null) return;

        if(count == 0){
            mTxtCartCount.setVisibility(View.GONE);
            return;
        }

        if(mTxtCartCount.getVisibility() == View.GONE)
            mTxtCartCount.setVisibility(View.VISIBLE);

        mTxtCartCount.setText(String.valueOf(count));
    }

    @Override
    protected void updateNotificationsLabel(int count, Notification newNotification) {
        // do nothing here
    }

    private void onBackClick(View v){
        finish();
    }

    private void onCartClick(View v){
        Intent cartActivity = new Intent(this, CartActivity.class);
        startActivity(cartActivity);
    }

    private void loadNotifications() {
        mNotifications.fetch(new RealtimeDataBase.OnFetchCompleted<Notification>() {
            @Override
            public void onSuccessful(List<Notification> children) {
                for(Notification n : children){
                    Log.d(TAG, n.toString());
                }
                mNotificationsRecycler = findViewById(R.id.notificationsRecycler);
                NotificationAdapter notificationAdapter = new NotificationAdapter(NotificationsActivity.this, children, NotificationsActivity.this::onNotificationClick);
                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(NotificationsActivity.this, LinearLayoutManager.VERTICAL, false);
                mNotificationsRecycler.setLayoutManager(layoutManager);
                mNotificationsRecycler.setAdapter(notificationAdapter);
            }

            @Override
            public void onFailure(Exception ex) {

            }
        });
    }

    private void onNotificationClick(int position) {

    }
}