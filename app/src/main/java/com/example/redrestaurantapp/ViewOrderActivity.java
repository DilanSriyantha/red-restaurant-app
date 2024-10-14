package com.example.redrestaurantapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
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

import com.example.redrestaurantapp.Adapters.CartAdapter;
import com.example.redrestaurantapp.Controllers.OrderController;
import com.example.redrestaurantapp.Models.CartRecord;
import com.example.redrestaurantapp.Models.Order;
import com.example.redrestaurantapp.Utils.AlertBox;
import com.example.redrestaurantapp.Utils.Cart;
import com.example.redrestaurantapp.Utils.ThreadPoolManager;
import com.example.redrestaurantapp.Views.CartActivity;
import com.facebook.shimmer.ShimmerFrameLayout;

import java.util.HashMap;
import java.util.Map;

public class ViewOrderActivity extends AppCompatActivity {
    private final String TAG = "ViewOrderActivity";

    private ImageButton mBtnBack;
    private ImageButton mBtnCart;
    private ImageButton mBtnNotification;

    private RecyclerView mCartListRecycler;

    private CheckBox mCheckBoxUtensils;

    private TextView mTxtTitle;
    private TextView mTxtSubtotal;

    private ConstraintLayout mViewOrderContainer;
    private ShimmerFrameLayout mShimmeringLayout;

    private final OrderController mOrderController;
    private final Cart mCart;
    private CartAdapter mCartAdapter;
    private final ThreadPoolManager mThreadPoolManager;
    private int mOrderIdx;
    private Order mOrder;

    public ViewOrderActivity() {
        mOrderController = new OrderController();
        mCart = Cart.getInstance();
        mThreadPoolManager = ThreadPoolManager.getInstance();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_view_order);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Intent intent = getIntent();
        mOrderIdx = intent.getIntExtra("orderIdx", -1);

        mBtnBack = findViewById(R.id.btnBack);
        mBtnCart = findViewById(R.id.btnCart);
        mBtnNotification = findViewById(R.id.btnNotifications);

        mCartListRecycler = findViewById(R.id.cartListRecycler);

        mCheckBoxUtensils = findViewById(R.id.chkUtensils);

        mTxtTitle = findViewById(R.id.txtAppbarTitle);
        mTxtTitle.setText("Order");

        mTxtSubtotal = findViewById(R.id.txtSubtotal);

        mBtnBack.setOnClickListener(this::onBackClick);
        mBtnCart.setOnClickListener(this::onCartClick);
        mBtnNotification.setOnClickListener(this::onNotificationClick);

        mViewOrderContainer = findViewById(R.id.viewOrderContainer);
        mShimmeringLayout = findViewById(R.id.viewOrderShimmerLayout);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                getOrder();
            }
        }, 500);
    }

    private void onBackClick(View v){
        finish();
    }

    private void onCartClick(View v){
        Intent cartActivity = new Intent(this, CartActivity.class);
        startActivity(cartActivity);
    }

    private void onNotificationClick(View v){

    }

    private void getOrder() {
        if(mOrderIdx == -1){
            AlertBox alertBox = new AlertBox("Something went wrong!", AlertBox.Type.ERROR);
            alertBox.show(getSupportFragmentManager(), TAG, new AlertBox.Action() {
                @Override
                public void onClick() {
                    alertBox.dismiss();
                }
            }, null);

            return;
        }

        setShimmer(true);
        mThreadPoolManager.submitTask(new Runnable() {
            @Override
            public void run() {
                while(mOrderController.isLoading());
                Log.d(TAG, "finished loading orders");

                mOrder = mOrderController.getOrder(mOrderIdx);

                try{
                    Log.d(TAG, "Order -> " + mOrder.toString());
                    for(CartRecord cr : mOrder.getCartRecords()){
                        Log.d(TAG, "CartRecord -> " + cr.toString());
                    }
                }catch (Exception ex){
                    ex.printStackTrace();
                }


                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setTitle();
                        populateList();
                        setShimmer(false);
                    }
                });
            }
        });
    }

    private void setShimmer(boolean status){
        if(status){
            mShimmeringLayout.setVisibility(View.VISIBLE);
            mViewOrderContainer.setVisibility(View.GONE);

            mShimmeringLayout.startShimmer();
            return;
        }

        mShimmeringLayout.setVisibility(View.GONE);
        mViewOrderContainer.setVisibility(View.VISIBLE);

        mShimmeringLayout.stopShimmer();
    }

    private void setTitle(){
        if(mOrder == null) return;

        mTxtTitle.setText(String.format("Order #%s", mOrder.getId()));
    }

    private void populateList() {
        if(mOrder == null) return;

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mCartAdapter = new CartAdapter(this, mOrder.getCartRecords(), null);
        mCartListRecycler.setLayoutManager(layoutManager);
        mCartListRecycler.setAdapter(mCartAdapter);

        mCheckBoxUtensils.setChecked(mOrder.isUtensilsAllowed());
        mTxtSubtotal.setText(String.format("LKR %s/=", mOrder.getSubtotal()));
    }
}