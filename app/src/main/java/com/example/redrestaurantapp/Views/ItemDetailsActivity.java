package com.example.redrestaurantapp.Views;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.redrestaurantapp.Models.CartRecord;
import com.example.redrestaurantapp.Models.Product;
import com.example.redrestaurantapp.R;
import com.example.redrestaurantapp.Utils.AlertBox;
import com.example.redrestaurantapp.Utils.Cart;
import com.example.redrestaurantapp.Utils.ImageLoader;

public class ItemDetailsActivity extends AppCompatActivity {
    private final String TAG = "ItemDetailsActivity";

    private TextView mTxtAppBarTitle;
    private TextView mTxtCartCount;
    private TextView mTxtProductName;
    private TextView mTxtProductPrice;
    private TextView mTxtProductDescription;
    private TextView mTxtQty;

    private ImageView mImgProduct;
    private ImageView mImgVegState;
    private ImageView mImgFeaturedState;

    private ImageButton mBtnBack;
    private ImageButton mBtnCart;
    private ImageButton mBtnAdd;
    private ImageButton mBtnRemove;

    private Button mBtnAddToCart;

    private Product mProduct;

    private int mQty;
    private double mTotal;

    private final Cart mCart = Cart.getInstance();
    private final ImageLoader mImageLoader = ImageLoader.getInstance(this);

    @Override
    protected void onResume() {
        super.onResume();

        if(mTxtCartCount != null)
            mCart.updateCartLabel(mTxtCartCount);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_item_details);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Intent intent = getIntent();
        mProduct = (Product) intent.getSerializableExtra("product");

        mTxtAppBarTitle = findViewById(R.id.txtAppbarTitle);
        mTxtCartCount = findViewById(R.id.txtCartCount);
        mTxtProductName = findViewById(R.id.txtProductName);
        mTxtProductPrice = findViewById(R.id.txtProductPrice);
        mTxtProductDescription = findViewById(R.id.txtProductDescription);
        mTxtQty = findViewById(R.id.txtQty);

        mImgProduct = findViewById(R.id.imgProduct);
        mImgVegState = findViewById(R.id.imgVegState);
        mImgFeaturedState = findViewById(R.id.imgFeaturedState);

        mBtnBack = findViewById(R.id.btnBack);
        mBtnCart = findViewById(R.id.btnCart);
        mBtnAdd = findViewById(R.id.btnAdd);
        mBtnRemove = findViewById(R.id.btnRemove);
        mBtnAddToCart = findViewById(R.id.btnAddToCart);

        mBtnBack.setOnClickListener(this::onBackClick);
        mBtnCart.setOnClickListener(this::onCartClick);
        mBtnAdd.setOnClickListener(this::onAddClick);
        mBtnRemove.setOnClickListener(this::onRemoveClick);
        mBtnAddToCart.setOnClickListener(this::onAddToCartClick);

        mCart.updateCartLabel(mTxtCartCount);

        mQty = 1;
        mTotal = mProduct.getPrice();

        mTxtQty.setText(String.valueOf(mQty));

        populateScreen();
    }

    private void populateScreen() {
        if(mProduct == null) return;

        mTxtAppBarTitle.setText(mProduct.getName());
        mTxtAppBarTitle.setSelected(true);

        mImageLoader.loadImage(mImgProduct, mProduct.getImageUrl());

        mTxtProductName.setText(mProduct.getName());
        mTxtProductPrice.setText(String.format("LKR %s/=", String.valueOf(mProduct.getPrice())));
        mTxtProductDescription.setText(mProduct.getDescription());

        if(!mProduct.isVegetarian())
            mImgVegState.setImageResource(R.drawable.non_veg);

        if(!mProduct.isFeatured())
            mImgFeaturedState.setVisibility(View.GONE);

        mBtnAddToCart.setText(String.format("%s/= Add to cart", String.valueOf(mTotal)));
    }

    private void onBackClick(View v){
        finish();
    }

    private void onCartClick(View v){
        Intent cartActivity = new Intent(this, CartActivity.class);
        startActivity(cartActivity);
        finish();
    }

    private void onAddClick(View v){
        ++mQty;

        updateUI();
    }

    private void onRemoveClick(View v){
        if(mQty <= 0) return;
        --mQty;

        updateUI();
    }

    private void updateUI() {
        mTotal = mProduct.getPrice() * mQty;

        mTxtQty.setText(String.valueOf(mQty));
        mBtnAddToCart.setText(String.format("%s/= Add to cart", String.valueOf(mTotal)));
    }

    private void onAddToCartClick(View v){
        mCart.add(new CartRecord(mProduct, mQty, mTotal));
        mCart.updateCartLabel(mTxtCartCount);

        AlertBox alertBox = new AlertBox("Item added to the cart!", AlertBox.Type.INFO);
        alertBox.show(getSupportFragmentManager(), TAG, new AlertBox.Action() {
            @Override
            public void onClick() {
                alertBox.dismiss();
            }
        }, null);
    }
}