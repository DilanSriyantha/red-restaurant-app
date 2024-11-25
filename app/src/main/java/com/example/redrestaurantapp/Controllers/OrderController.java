package com.example.redrestaurantapp.Controllers;

import android.os.Build;
import android.os.Handler;
import android.util.Log;
import android.util.Pair;

import androidx.annotation.RequiresApi;

import com.example.redrestaurantapp.Interfaces.GetCollectionCallback;
import com.example.redrestaurantapp.Interfaces.OnCompleteListener;
import com.example.redrestaurantapp.Models.Order;
import com.example.redrestaurantapp.R;
import com.example.redrestaurantapp.ServiceLayer.FireStore;
import com.example.redrestaurantapp.ServiceLayer.UserManager;
import com.example.redrestaurantapp.Utils.ThreadPoolManager;
import com.google.firebase.firestore.auth.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class OrderController {
    private List<Order> mOrderList;
    private final FireStore mFirestore;
    private final UserManager mUserManager;

    private boolean FLAG_LOADING = false;
    private final String TAG = "OrderController";

    public OrderController(){
        mOrderList = new ArrayList<>();
        mFirestore = new FireStore();
        mUserManager = new UserManager();

        loadData();
    }

    public void addOrder(Order order, OnCompleteListener<Void> onCompleteListener){
        mOrderList.add(order);

        Log.d(TAG, order.toString());

        mFirestore.insertOne(
                "order",
                String.format("Order #%s", order.getId()),
                order,
                onCompleteListener
        );
    }

    public Order getOrder(int idx){
        return mOrderList.get(idx);
    }

    public List<Order> getOrders() {
        return mOrderList;
    }

    public boolean isLoading() {
        return FLAG_LOADING;
    }

    private void loadData() {
        FLAG_LOADING = true;
        mFirestore.getCollection(
                "order",
                "timestamp",
                new Pair<>("userId", mUserManager.getCurrentUser().getUid()),
                Order.class,
                new GetCollectionCallback<Order>() {
            @Override
            public void onSuccess(List<Order> resultList) {
                mOrderList = resultList;

                for(Order order : resultList){
                    Log.d(TAG, ""+order.getId());
                }

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        FLAG_LOADING = false;
                    }
                }, 1000);
            }

            @Override
            public void onFailure(Exception ex) {
                ex.printStackTrace();
                FLAG_LOADING = false;
            }
        });
    }

    public void getOrders(int limitToFirst, OnCompleteOrdersLoading callback){
        mFirestore.getCollection(
                "order",
                limitToFirst,
                "timestamp",
                new Pair<>("userId", mUserManager.getCurrentUser().getUid()),
                Order.class,
                new GetCollectionCallback<Order>() {
                    @Override
                    public void onSuccess(List<Order> resultList) {
                        callback.onComplete(resultList);
                    }

                    @Override
                    public void onFailure(Exception ex) {
                        callback.onFailure(ex);
                    }
        });
    }

    public interface OnCompleteOrdersLoading {
        void onComplete(List<Order> orders);
        void onFailure(Exception ex);
    }
}
