package com.example.redrestaurantapp.Views;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.redrestaurantapp.Adapters.CartAdapter;
import com.example.redrestaurantapp.Controllers.OrderController;
import com.example.redrestaurantapp.Interfaces.OnCompleteListener;
import com.example.redrestaurantapp.Models.CartRecord;
import com.example.redrestaurantapp.Models.Order;
import com.example.redrestaurantapp.Models.Product;
import com.example.redrestaurantapp.R;
import com.example.redrestaurantapp.ServiceLayer.UserManager;
import com.example.redrestaurantapp.Utils.AlertBox;
import com.example.redrestaurantapp.Utils.Cart;
import com.example.redrestaurantapp.Utils.ThreadPoolManager;
import com.google.firebase.Timestamp;

import java.util.stream.Collectors;

public class CartActivity extends AppCompatActivity {
    private final String TAG = "CartActivity";

    private ImageButton mBtnBack;
    private ImageButton mBtnCart;

    private ConstraintLayout mProgressOverlay;

    private ConstraintLayout mCartEmptyViewContainer;

    private LinearLayout mCartViewContainer;

    private RecyclerView mCartListRecycler;

    private CheckBox mCheckBoxUtensils;

    private TextView mTxtTitle;
    private TextView mTxtSubtotal;

    private Button mBtnPlaceOrder;

    private final OrderController mOrderController;
    private final com.example.redrestaurantapp.ServiceLayer.UserManager mUserManager;
    private final Cart mCart;
    private CartAdapter mCartAdapter;

    public CartActivity() {
        mCart = Cart.getInstance();
        mOrderController = new OrderController();
        mUserManager = new UserManager();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(mTxtSubtotal != null)
            updateSubtotal();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_cart);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mBtnBack = findViewById(R.id.btnBack);
        mBtnBack.setOnClickListener(this::onBackClick);

        mBtnCart = findViewById(R.id.btnCart);
        mBtnCart.setVisibility(View.GONE);

        mProgressOverlay = findViewById(R.id.progressOverlay);

        mCartViewContainer = findViewById(R.id.cartViewContainer);
        mCartEmptyViewContainer = findViewById(R.id.cartEmptyViewContainer);

        mCartListRecycler = findViewById(R.id.cartListRecycler);
        mCheckBoxUtensils = findViewById(R.id.chkUtensils);
        mTxtTitle = findViewById(R.id.txtAppbarTitle);
        mTxtSubtotal = findViewById(R.id.txtSubtotal);

        mBtnPlaceOrder = findViewById(R.id.btnPlaceOrder);
        mBtnPlaceOrder.setOnClickListener(this::onPlaceOrderClick);

        mTxtTitle.setText(R.string.cart_title);

        if(mCart.getCartCount() == 0)
            setEmptyState(true);
        else
            setEmptyState(false);

        populateCart();
        updateSubtotal();
    }

    private void onBackClick(View v){
        finish();
    }

    private void onPlaceOrderClick(View v){
        setLoading(true);

        Order newOrder = new Order(
                (int)(Math.random()*100000),
                mUserManager.getCurrentUser().getUid(),
                mCart.getList().get(0).getProduct().getImageUrl(),
                mCheckBoxUtensils.isChecked(),
                "",
                mCart.getList(),
                getSubtotal(),
                Timestamp.now(),
                "Pending");

        mOrderController.addOrder(newOrder, new OnCompleteListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                mCart.clear();
                setEmptyState(true);
                setLoading(false);

                AlertBox alertBox = new AlertBox(String.format("Order #%s has been placed successfully.", newOrder.getId()), AlertBox.Type.INFO);
                alertBox.show(getSupportFragmentManager(), TAG, new AlertBox.Action() {
                    @Override
                    public void onClick() {
                        alertBox.dismiss();
                    }
                }, null);
            }

            @Override
            public void onFailure(@NonNull Exception e) {
                setLoading(false);

                AlertBox alertBox = new AlertBox("Failed placing order! Something went wrong!", AlertBox.Type.ERROR);
                alertBox.show(getSupportFragmentManager(), TAG, new AlertBox.Action() {
                    @Override
                    public void onClick() {
                        alertBox.dismiss();
                    }
                }, null);
            }
        });
    }

    private void setLoading(boolean state){
        if(state){
            mProgressOverlay.setVisibility(View.VISIBLE);
            return;
        }
        mProgressOverlay.setVisibility(View.GONE);
    }

    private void setEmptyState(boolean state){
        if(state) {
            mCartViewContainer.setVisibility(View.GONE);
            mBtnPlaceOrder.setVisibility(View.GONE);
            mCartEmptyViewContainer.setVisibility(View.VISIBLE);

            return;
        }

        mCartViewContainer.setVisibility(View.VISIBLE);
        mBtnPlaceOrder.setVisibility(View.VISIBLE);
        mCartEmptyViewContainer.setVisibility(View.GONE);
    }

    private void populateCart() {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mCartAdapter = new CartAdapter(this, mCart.getList(), OnQuantityChanged());
        mCartListRecycler.setLayoutManager(layoutManager);
        mCartListRecycler.setAdapter(mCartAdapter);
    }

    private CartAdapter.onQuantityChangeListener OnQuantityChanged() {
        return new CartAdapter.onQuantityChangeListener() {
            @Override
            public void onAddClick(int position) {
                long newQty = mCartAdapter.getItem(position).getQuantity() + 1;

                mCart.setQty(mCartAdapter.getItem(position).getId(), newQty);
                mCartAdapter.notifyItemChanged(position);
                updateSubtotal();
            }

            @Override
            public void onRemoveClick(int position) {
                long newQty = mCartAdapter.getItem(position).getQuantity() - 1;

                if(newQty == 0){
                    mCart.remove(mCartAdapter.getItem(position));
                    mCartAdapter.notifyItemRemoved(position);

                    if(mCart.getCartCount() == 0)
                        setEmptyState(true);

                    updateSubtotal();
                    return;
                }

                mCart.setQty(mCartAdapter.getItem(position).getId(), newQty);
                mCartAdapter.notifyItemChanged(position);
                updateSubtotal();
            }
        };
    }

    private double getSubtotal() {
        double subtotal = 0.d;
        for(CartRecord cr : mCart.getList())
            subtotal += cr.getTotal();

        return subtotal;
    }

    private void updateSubtotal() {
        mTxtSubtotal.setText(String.format("LKR %s", getSubtotal()));
    }
}