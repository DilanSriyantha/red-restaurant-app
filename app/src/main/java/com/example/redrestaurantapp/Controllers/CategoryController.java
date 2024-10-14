package com.example.redrestaurantapp.Controllers;

import android.net.Uri;
import android.os.Handler;
import android.util.Log;

import com.example.redrestaurantapp.Interfaces.GetCollectionCallback;
import com.example.redrestaurantapp.Models.Category;
import com.example.redrestaurantapp.R;
import com.example.redrestaurantapp.ServiceLayer.FireStore;
import com.example.redrestaurantapp.Utils.ThreadPoolManager;

import java.util.ArrayList;
import java.util.List;

public class CategoryController {
    private List<Category> mCategoriesList;
    private final FireStore mFirestore;
    private final ThreadPoolManager mThreadPoolManager;

    private boolean FLAG_LOADING = false;
    private final String TAG = "CategoryController";

    public CategoryController(){
        mCategoriesList = new ArrayList<>();
        mFirestore = new FireStore();
        mThreadPoolManager = ThreadPoolManager.getInstance();

        loadData();
    }

    public Category getCategory(int idx){
        return mCategoriesList.get(idx);
    }

    public void addCategory(Category category){
        mCategoriesList.add(category);
    }

    public List<Category> getCategories() {
        return mCategoriesList;
    }

    public boolean isLoading() {
        return FLAG_LOADING;
    }

    private void loadData() {
        FLAG_LOADING = true;
        mThreadPoolManager.submitTask(new Runnable() {
            @Override
            public void run() {
                mFirestore.getCollection("category", Category.class, new GetCollectionCallback<Category>() {
                    @Override
                    public void onSuccess(List<Category> resultList) {
                        mCategoriesList = resultList;
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
                        Log.d(TAG, ex.getLocalizedMessage());
                        FLAG_LOADING = false;
                    }
                });
            }
        });
    }
}
