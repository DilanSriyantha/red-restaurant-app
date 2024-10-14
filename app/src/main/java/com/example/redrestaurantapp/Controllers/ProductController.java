package com.example.redrestaurantapp.Controllers;

import android.os.Handler;
import android.util.Log;
import android.util.Pair;

import com.example.redrestaurantapp.Interfaces.GetCollectionCallback;
import com.example.redrestaurantapp.Models.Product;
import com.example.redrestaurantapp.ServiceLayer.FireStore;
import com.example.redrestaurantapp.Utils.ThreadPoolManager;

import java.util.ArrayList;
import java.util.List;

public class ProductController {
    private List<Product> mProductList;
    private final FireStore mFirestore;
    private final ThreadPoolManager mThreadPoolManager;

    private boolean FLAG_LOADING = false;
    private final String TAG = "ProductController";
    private boolean mIsManualDataLoader = false;

    public ProductController() {
        mProductList = new ArrayList<>();
        mFirestore = new FireStore();
        mThreadPoolManager = ThreadPoolManager.getInstance();

        loadData();
    }

    public ProductController(boolean isManualDataLoader){
        mIsManualDataLoader = true;

        mProductList = new ArrayList<>();
        mFirestore = new FireStore();
        mThreadPoolManager = ThreadPoolManager.getInstance();
    }

    public void addProduct(Product product){
        mProductList.add(product);
    }

    public Product getProduct(int idx){
        return mProductList.get(idx);
    }

    public List<Product> getProducts() {
        return mProductList;
    }

    public boolean isLoading() {
        return FLAG_LOADING;
    }

    public void loadData(Pair<String, Object> whereEqualsTo) {
        FLAG_LOADING = true;

        if(whereEqualsTo == null) {
            loadData();
            return;
        }

        mThreadPoolManager.submitTask(new Runnable() {
            @Override
            public void run() {
                mFirestore.getCollection("product", whereEqualsTo, Product.class, new GetCollectionCallback<Product>() {
                    @Override
                    public void onSuccess(List<Product> resultList) {
                        mProductList = resultList;

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
                        Log.d(TAG, ex.getLocalizedMessage());
                        FLAG_LOADING = false;
                    }
                });
            }
        });
    }

    private void loadData() {
        FLAG_LOADING = true;
        mThreadPoolManager.submitTask(new Runnable() {
            @Override
            public void run() {
                mFirestore.getCollection("product", Product.class, new GetCollectionCallback<Product>() {
                    @Override
                    public void onSuccess(List<Product> resultList) {
                        mProductList = resultList;

                        for(Product p : mProductList){
                            Log.d(TAG, p.toString());
                        }

                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                FLAG_LOADING = false;
                            }
                        }, 2000);
                    }

                    @Override
                    public void onFailure(Exception ex) {
                        ex.printStackTrace();
                        Log.d(TAG, ex.getLocalizedMessage());
                        FLAG_LOADING = false;
                    }
                });
            }
        });
    }
}
