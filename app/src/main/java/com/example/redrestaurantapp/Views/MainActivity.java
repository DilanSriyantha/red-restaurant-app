package com.example.redrestaurantapp.Views;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.redrestaurantapp.Adapters.CategoriesAdapter;
import com.example.redrestaurantapp.Controllers.CategoryController;
import com.example.redrestaurantapp.Interfaces.GetCollectionCallback;
import com.example.redrestaurantapp.Interfaces.GetDocumentCallback;
import com.example.redrestaurantapp.Models.Category;
import com.example.redrestaurantapp.Models.Product;
import com.example.redrestaurantapp.R;
import com.example.redrestaurantapp.ServiceLayer.FireStore;
import com.example.redrestaurantapp.Utils.AlertBox;
import com.example.redrestaurantapp.Utils.Cart;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.redrestaurantapp.databinding.ActivityMainBinding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private final String TAG = "MainActivity";
    private final Cart mCart;

    private ActivityMainBinding binding;

    private ImageButton mBtnCart;
    private TextView mTxtCartCount;

    public MainActivity() {
        mCart = Cart.getInstance();
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
    }

    @Override
    protected void onResume() {
        super.onResume();

        mTxtCartCount = findViewById(R.id.txtCartCount);
        mCart.updateCartLabel(mTxtCartCount);
    }

    private void onCartClick(View v){
        Intent cartActivity = new Intent(this, CartActivity.class);
        startActivity(cartActivity);
    }
}