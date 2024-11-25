package com.example.redrestaurantapp.Views;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
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
import com.example.redrestaurantapp.Utils.ThreadPoolManager;
import com.facebook.shimmer.ShimmerFrameLayout;

import java.util.List;

public class NotificationsActivity extends BaseActivity {
    private final String TAG = "NotificationActivity";

    private ImageButton mBtnBack;
    private ImageButton mBtnNotification;
    private ImageButton mBtnCart;

    private TextView mTxtTitle;
    private TextView mTxtCartCount;

    private RecyclerView mNotificationsRecycler;

    private ConstraintLayout mNotificationListContainer;
    private ShimmerFrameLayout mShimmerFrameLayout;

    private final Notifications mNotifications;
    private final UserManager mUserManager;
    private final Cart mCart;
    private final ThreadPoolManager mThreadPoolManager;

    public NotificationsActivity() {
        mCart = Cart.getInstance();
        mNotifications = Notifications.getInstance();
        mUserManager = new UserManager();
        mThreadPoolManager = ThreadPoolManager.getInstance();
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

        mNotificationListContainer = findViewById(R.id.notificationListContainer);
        mShimmerFrameLayout = findViewById(R.id.notificationsShimmerContainer);

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

    private void markAllAsSeen() {
        mNotifications.markAllAsSeen();
    }

    private void loadNotifications() {
        setShimmer(true);
        mThreadPoolManager.submitTask(new Runnable() {
            @Override
            public void run() {
                mNotifications.fetch();

                while(mNotifications.isLoading());

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        populateNotificationsRecycler();
                        setShimmer(false);
                        markAllAsSeen();
                    }
                });
            }
        });
    }

    private void populateNotificationsRecycler() {
        mNotificationsRecycler = findViewById(R.id.notificationsRecycler);
        NotificationAdapter notificationAdapter = new NotificationAdapter(NotificationsActivity.this, mNotifications.getList(), NotificationsActivity.this::onNotificationClick);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(NotificationsActivity.this, LinearLayoutManager.VERTICAL, false);
        mNotificationsRecycler.setLayoutManager(layoutManager);
        mNotificationsRecycler.setAdapter(notificationAdapter);
    }

    private void onNotificationClick(int position) {

    }

    private void setShimmer(boolean state){
        if(state){
            mNotificationListContainer.setVisibility(View.GONE);
            mShimmerFrameLayout.setVisibility(View.VISIBLE);
            mShimmerFrameLayout.startShimmer();

            return;
        }
        mNotificationListContainer.setVisibility(View.VISIBLE);
        mShimmerFrameLayout.setVisibility(View.GONE);
        mShimmerFrameLayout.stopShimmer();
    }
}