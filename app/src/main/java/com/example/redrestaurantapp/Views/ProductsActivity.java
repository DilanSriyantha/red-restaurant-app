package com.example.redrestaurantapp.Views;

import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.redrestaurantapp.Adapters.ProductsAdapter;
import com.example.redrestaurantapp.Controllers.ProductController;
import com.example.redrestaurantapp.Models.Category;
import com.example.redrestaurantapp.Models.Notification;
import com.example.redrestaurantapp.R;
import com.example.redrestaurantapp.Utils.ThreadPoolManager;
import com.facebook.shimmer.ShimmerFrameLayout;

public class ProductsActivity extends BaseActivity {
    public enum Mode {
        ALL_PRODUCTS,
        RECENT_PRODUCTS,
        CATEGORY
    };

    private final String TAG = "ProductActivity";

    private Mode mMode;

    private TextView mTxtTitle;
    private TextView mTxtNotificationCount;
    private TextView mTxtCartCount;

    private RecyclerView mProductsRecycler;

    private ConstraintLayout mProductsContainer;
    private ShimmerFrameLayout mProductsShimmerLayout;
    private ConstraintLayout mEmptyContainer;

    private ImageButton mBtnBack;
    private ImageButton mBtnCart;
    private ImageButton mBtnNotification;

    private final ProductController mProductsController;
    private ProductsAdapter mProductAdapter;
    private final ThreadPoolManager mThreadPoolManager;
    private Category mCategory;

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

        if(mMode == Mode.CATEGORY)
            mCategory = (Category) intent.getSerializableExtra("category");

        mTxtTitle = findViewById(R.id.txtAppbarTitle);
        if(mCategory != null)
            mTxtTitle.setText(mCategory.getName());
        else
            mTxtTitle.setText("All Products");

        mTxtNotificationCount = findViewById(R.id.txtNotificationCount);
        mTxtCartCount = findViewById(R.id.txtCartCount);

        mProductsRecycler = findViewById(R.id.productsRecycler);

        mProductsContainer = findViewById(R.id.productsContainer);
        mProductsShimmerLayout = findViewById(R.id.productsShimmerLayout);
        mEmptyContainer = findViewById(R.id.productsEmptyContainer);

        mBtnBack = findViewById(R.id.btnBack);
        mBtnCart = findViewById(R.id.btnCart);
        mBtnNotification = findViewById(R.id.btnNotifications);

        mBtnBack.setOnClickListener(this::onBackClick);
        mBtnCart.setOnClickListener(this::onCartClick);
        mBtnNotification.setOnClickListener(this::onNotificationClick);

        populateView();
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
                if(mMode == Mode.CATEGORY && mCategory != null)
                    mProductsController.loadData(new Pair<>("categoryId", mCategory.getId()));
                else if(mMode == Mode.RECENT_PRODUCTS)
                    mProductsController.loadData(null);
                else
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
        if(mProductsController.getProducts().isEmpty()){
            setEmpty(true);
            return;
        }
        setEmpty(false);
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

    private void setEmpty(boolean state){
        if(state){
            if(mEmptyContainer.getVisibility() == View.VISIBLE) return;
            mProductsContainer.setVisibility(View.GONE);
            mEmptyContainer.setVisibility(View.VISIBLE);
            return;
        }
        if(mEmptyContainer.getVisibility() == View.GONE) return;
        mProductsContainer.setVisibility(View.VISIBLE);
        mEmptyContainer.setVisibility(View.GONE);
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