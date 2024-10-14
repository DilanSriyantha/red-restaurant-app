package com.example.redrestaurantapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
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

import com.example.redrestaurantapp.Adapters.ProductsAdapter;
import com.example.redrestaurantapp.Controllers.ProductController;
import com.example.redrestaurantapp.Utils.ThreadPoolManager;
import com.example.redrestaurantapp.Views.CartActivity;
import com.example.redrestaurantapp.Views.ItemDetailsActivity;
import com.facebook.shimmer.ShimmerFrameLayout;

import java.io.Serializable;

public class ProductsActivity extends AppCompatActivity {
    public enum Mode {
        ALL_PRODUCTS,
        RECENT_PRODUCTS
    };

    private final String TAG = "ProductActivity";

    private Mode mMode;

    private TextView mTxtTitle;

    private RecyclerView mProductsRecycler;

    private ConstraintLayout mProductsContainer;
    private ShimmerFrameLayout mProductsShimmerLayout;

    private ImageButton mBtnBack;
    private ImageButton mBtnCart;
    private ImageButton mBtnNotification;

    private final ProductController mProductsController;
    private ProductsAdapter mProductAdapter;
    private final ThreadPoolManager mThreadPoolManager;

    public ProductsActivity() {
        mProductsController = new ProductController(true);
        mThreadPoolManager = ThreadPoolManager.getInstance();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_products);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Intent intent = getIntent();
        mMode = (Mode) intent.getSerializableExtra("mode");

        mTxtTitle = findViewById(R.id.txtAppbarTitle);
        mTxtTitle.setText("All Products");

        mProductsRecycler = findViewById(R.id.productsRecycler);

        mProductsContainer = findViewById(R.id.productsContainer);
        mProductsShimmerLayout = findViewById(R.id.productsShimmerLayout);

        mBtnBack = findViewById(R.id.btnBack);
        mBtnCart = findViewById(R.id.btnCart);
        mBtnNotification = findViewById(R.id.btnNotifications);

        mBtnBack.setOnClickListener(this::onBackClick);
        mBtnCart.setOnClickListener(this::onCartClick);
        mBtnNotification.setOnClickListener(this::onNotificationClick);

        populateView();
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

    private void populateView() {
        setShimmer(true);

        mThreadPoolManager.submitTask(new Runnable() {
            @Override
            public void run() {
                mProductsController.loadData(null);

                while(mProductsController.isLoading());

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        populateProductsRecycler();
                        setShimmer(false);
                    }
                });
            }
        });
    }

    private void populateProductsRecycler() {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mProductAdapter = new ProductsAdapter(this, mProductsController.getProducts(), ProductsAdapter.LayoutType.LARGE, this::onItemClick);
        mProductsRecycler.setLayoutManager(layoutManager);
        mProductsRecycler.setAdapter(mProductAdapter);
    }

    private void onItemClick(int pos){
        Intent itemDetailsActivity = new Intent(this, ItemDetailsActivity.class);
        itemDetailsActivity.putExtra("product", mProductAdapter.getProduct(pos));
        startActivity(itemDetailsActivity);
    }

    private void setShimmer(boolean state){
        if(state){
            mProductsContainer.setVisibility(View.GONE);
            mProductsShimmerLayout.setVisibility(View.VISIBLE);
            mProductsShimmerLayout.startShimmer();

            return;
        }
        mProductsContainer.setVisibility(View.VISIBLE);
        mProductsShimmerLayout.setVisibility(View.GONE);
        mProductsShimmerLayout.stopShimmer();
    }
}