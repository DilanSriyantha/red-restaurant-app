package com.example.redrestaurantapp.Views;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.redrestaurantapp.Models.Notification;
import com.example.redrestaurantapp.R;
import com.example.redrestaurantapp.Services.NotificationManagerService;
import com.example.redrestaurantapp.Utils.Cart;
import com.example.redrestaurantapp.Utils.Notifications;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.redrestaurantapp.databinding.ActivityMainBinding;

public class MainActivity extends BaseActivity {
    private final String TAG = "MainActivity";
    private final Cart mCart;
    private final Notifications mNotification;

    private ActivityMainBinding binding;

    private ImageButton mBtnCart;
    private ImageButton mBtnNotifications;
    private TextView mTxtCartCount;
    private TextView mTxtNotificationCount;

    public MainActivity() {
        mCart = Cart.getInstance();
        mNotification = Notifications.getInstance();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_orders, R.id.navigation_account)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        // NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);

        mBtnCart = findViewById(R.id.btnCart);
        mBtnCart.setOnClickListener(this::onCartClick);

        mBtnNotifications = findViewById(R.id.btnNotifications);
        mBtnNotifications.setOnClickListener(this::onNotificationsClick);

        mTxtNotificationCount = findViewById(R.id.txtNotificationCount);

        Intent serviceIntent = new Intent(this, NotificationManagerService.class);
        startService(serviceIntent);
    }

    @Override
    protected void onResume() {
        super.onResume();

        mTxtCartCount = findViewById(R.id.txtCartCount);
        mCart.updateCartLabel(mTxtCartCount);
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
        if(mTxtNotificationCount == null) return;

        if(count == 0){
            mTxtNotificationCount.setVisibility(View.GONE);
            return;
        }

        if(mTxtNotificationCount.getVisibility() == View.GONE)
            mTxtNotificationCount.setVisibility(View.VISIBLE);

        mTxtNotificationCount.setText(String.valueOf(count));
    }



    private void onCartClick(View v){
        Intent cartActivity = new Intent(this, CartActivity.class);
        startActivity(cartActivity);
    }

    private void onNotificationsClick(View v){
        Intent notificationsActivity = new Intent(this, NotificationsActivity.class);
        startActivity(notificationsActivity);
    }
}