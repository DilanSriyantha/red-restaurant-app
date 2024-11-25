package com.example.redrestaurantapp.Views.ui.home;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.redrestaurantapp.Adapters.CategoriesAdapter;
import com.example.redrestaurantapp.Adapters.ProductsAdapter;
import com.example.redrestaurantapp.Controllers.CategoryController;
import com.example.redrestaurantapp.Controllers.ProductController;
import com.example.redrestaurantapp.Models.Product;
import com.example.redrestaurantapp.Views.ProductsActivity;
import com.example.redrestaurantapp.ServiceLayer.RealtimeDataBase;
import com.example.redrestaurantapp.Utils.ThreadPoolManager;
import com.example.redrestaurantapp.Views.ItemDetailsActivity;
import com.example.redrestaurantapp.R;
import com.example.redrestaurantapp.databinding.FragmentHomeBinding;
import com.facebook.shimmer.ShimmerFrameLayout;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class HomeFragment extends Fragment {
    private final String TAG = "HomeFragment";
    private final CategoryController mCategoryController;
    private final ProductController mProductController;
    private final ThreadPoolManager mThreadPoolManager;

    private FragmentHomeBinding binding;

    private CategoriesAdapter mCategoryAdapter;
    private ProductsAdapter mOrderAgainProductsAdapter;
    private ProductsAdapter mAllProductsAdapter;

    private RecyclerView mCategoryRecycler;
    private RecyclerView mProductRecycler;
    private RecyclerView mAllProductsRecycler;

    private ImageButton mBtnOrderAgainForward;
    private ImageButton mBtnAllProductsForward;

    private ProgressBar mProgressCategory;
    private ProgressBar mProgressOrderAgain;
    private ProgressBar mProgressAllProducts;

    private EditText mTxtSearch;

    private ShimmerFrameLayout mCategoryShimmerLayout;
    private ShimmerFrameLayout mOrderAgainShimmerLayout;
    private ShimmerFrameLayout mAllProductsShimmerLayout;

    private LinearLayout mOrderAgainContainer;

    private ScrollView mParentScrollView;

    private TextView mTxtCartCount;

    private RealtimeDataBase rdb;

    public HomeFragment() {
        mCategoryController = new CategoryController();
        mProductController = new ProductController(true);
        mThreadPoolManager = ThreadPoolManager.getInstance();

        rdb = RealtimeDataBase.getInstance("notifications");
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        mBtnOrderAgainForward = root.findViewById(R.id.btnOrderAgainForward);
        mBtnAllProductsForward = root.findViewById(R.id.btnAllProductsForward);

        mBtnOrderAgainForward.setOnClickListener(this::onOrderAgainForwardClick);
        mBtnAllProductsForward.setOnClickListener(this::onAllProductsForwardClick);

        mCategoryRecycler = root.findViewById(R.id.categoryRecycler);
        mProductRecycler = root.findViewById(R.id.orderAgainRecycler);
        mAllProductsRecycler = root.findViewById(R.id.allProductsRecycler);

        mProgressCategory = root.findViewById(R.id.progressCategory);
        mProgressOrderAgain = root.findViewById(R.id.progressOrderAgain);
        mProgressAllProducts = root.findViewById(R.id.progressAllProducts);

        mOrderAgainContainer = root.findViewById(R.id.orderAgainContainer);

        mParentScrollView = root.findViewById(R.id.parentScrollLayout);

        mTxtSearch = root.findViewById(R.id.txtSearch);
        mTxtSearch.setOnEditorActionListener(this::onSearchInitiated);

        populateCategoryList();
        populateOrderAgainList();
        populateAllProductsList();

        setParentScrollListener();

        return root;
    }

    private void populateCategoryList() {
        setCategoryLoading(true);
        mThreadPoolManager.submitTask(new Runnable() {
            @Override
            public void run() {
                while(mCategoryController.isLoading());
                Log.d(TAG, "finished loading categories");

                requireActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mCategoryAdapter = new CategoriesAdapter(getActivity(), mCategoryController.getCategories(), HomeFragment.this::onCategoryClick);
                        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
                        mCategoryRecycler.setLayoutManager(layoutManager);
                        mCategoryRecycler.setAdapter(mCategoryAdapter);

                        setCategoryLoading(false);
                    }
                });
            }
        });
    }

    private void populateOrderAgainList() {
        setOrderAgainLoading(true);
        mThreadPoolManager.submitTask(new Runnable() {
            @Override
            public void run() {
                mProductController.getRecentProducts(new ProductController.OnCompleteProductsLoading() {
                    @Override
                    public void onComplete(List<Product> products) {
                        requireActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(products.isEmpty() || products.size() < 5){
                                    mOrderAgainContainer.setVisibility(View.GONE);
                                    return;
                                }
                                mOrderAgainProductsAdapter = new ProductsAdapter(getActivity(), products, ProductsAdapter.LayoutType.NARROW, HomeFragment.this::onOrderAgainItemClick);
                                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
                                mProductRecycler.setLayoutManager(layoutManager);

                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        setOrderAgainLoading(false);
                                        mProductRecycler.setAdapter(mOrderAgainProductsAdapter);
                                    }
                                }, 2000);
                            }
                        });
                    }

                    @Override
                    public void onFailure(Exception ex) {
                        ex.printStackTrace();
                    }
                });
            }
        });
    }

    private void populateAllProductsList() {
        setAllProductsLoading(true);
        mThreadPoolManager.submitTask(new Runnable() {
            @Override
            public void run() {
                mProductController.getAllProducts(new ProductController.OnCompleteProductsLoading() {
                    @Override
                    public void onComplete(List<Product> products) {
                        requireActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mAllProductsAdapter = new ProductsAdapter(getActivity(), products, ProductsAdapter.LayoutType.WIDE,
                                        HomeFragment.this::onAllProductItemClick);
                                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
                                mAllProductsRecycler.setLayoutManager(layoutManager);

                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        setAllProductsLoading(false);
                                        mAllProductsRecycler.setAdapter(mAllProductsAdapter);
                                    }
                                }, 2500);
                            }
                        });
                    }

                    @Override
                    public void onFailure(Exception ex) {
                        ex.printStackTrace();
                    }
                });
            }
        });
    }

    private void onCategoryClick(int position){
        Intent productsActivity = new Intent(getActivity(), ProductsActivity.class);
        productsActivity.putExtra("mode", ProductsActivity.Mode.CATEGORY);
        productsActivity.putExtra("category", mCategoryController.getCategory(position));
        startActivity(productsActivity);
    }

    private void onOrderAgainItemClick(int position){
        Intent productDetailsActivity = new Intent(getActivity(), ItemDetailsActivity.class);
        productDetailsActivity.putExtra("product", mOrderAgainProductsAdapter.getProduct(position));
        startActivity(productDetailsActivity);
    }

    private void onAllProductItemClick(int position){
        Intent productDetailsActivity = new Intent(getActivity(), ItemDetailsActivity.class);
        productDetailsActivity.putExtra("product", mAllProductsAdapter.getProduct(position));
        startActivity(productDetailsActivity);
    }

    private void onOrderAgainForwardClick(View v){
        onAllProductsForwardClick(v);
    }

    private void onAllProductsForwardClick(View v){
        Intent productsActivity = new Intent(requireActivity(), ProductsActivity.class);
        productsActivity.putExtra("mode", ProductsActivity.Mode.ALL_PRODUCTS);
        startActivity(productsActivity);
    }

    private boolean onSearchInitiated(TextView v, int actionId, KeyEvent event) {
        if(actionId != EditorInfo.IME_ACTION_SEARCH) return false;

        if(v.getText().toString().isEmpty()) return false;

        char[] chars = v.getText().toString().toCharArray();
        chars[0] = Character.toUpperCase(chars[0]);
        String searchText = new String(chars);

        Intent productsActivity = new Intent(getActivity(), ProductsActivity.class);
        productsActivity.putExtra("searchText", searchText);
        productsActivity.putExtra("mode", ProductsActivity.Mode.SEARCH);
        startActivity(productsActivity);
        return true;
    }

    private void setParentScrollListener(){
        mParentScrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                int contentHeight = mParentScrollView.getChildAt(0).getMeasuredHeight();
                int scrollY = (mParentScrollView.getHeight() + mParentScrollView.getScrollY());

                if(scrollY >= contentHeight){
                    if(!mAllProductsRecycler.isNestedScrollingEnabled()) {
                        mAllProductsRecycler.setNestedScrollingEnabled(true);
                    }
                }else{
                    if(mAllProductsRecycler.isNestedScrollingEnabled()) {
                        mAllProductsRecycler.setNestedScrollingEnabled(false);
                    }
                }

                Log.d(TAG, "" + mAllProductsRecycler.isNestedScrollingEnabled() + "\nScrollY: " + scrollY + "\ncontentHeight: " + contentHeight);
            }
        });
    }

    private void onParentScrollChange() {
        int contentHeight = mParentScrollView.getChildAt(0).getMeasuredHeight();
        int scrollY = mParentScrollView.getScrollY();
        int parentHeight = mParentScrollView.getHeight();

        if(scrollY + parentHeight >= contentHeight)
            mAllProductsRecycler.setNestedScrollingEnabled(true);
        else
            mAllProductsRecycler.setNestedScrollingEnabled(false);
    }

    private void setCategoryLoading(boolean state){
        try{
            if(state){
                binding.categoryShimmerLayout.setVisibility(View.VISIBLE);
                binding.categoryShimmerLayout.startShimmer();
                return;
            }
            binding.categoryShimmerLayout.setVisibility(View.GONE);
            binding.categoryShimmerLayout.stopShimmer();
        }catch (Exception ex){
            Log.d(TAG, ex.getLocalizedMessage());
        }
    }

    private void setOrderAgainLoading(boolean state){
        try{
            if(state){
                binding.orderAgainShimmerLayout.setVisibility(View.VISIBLE);
                binding.orderAgainShimmerLayout.startShimmer();
                return;
            }
            binding.orderAgainShimmerLayout.setVisibility(View.GONE);
            binding.orderAgainShimmerLayout.stopShimmer();
        }catch (Exception ex){
            Log.d(TAG, ex.getLocalizedMessage());
        }
    }

    private void setAllProductsLoading(boolean state){
        try{
            if(state){
                binding.allProductsShimmerLayout.setVisibility(View.VISIBLE);
                binding.allProductsShimmerLayout.startShimmer();
                return;
            }
            binding.allProductsShimmerLayout.setVisibility(View.GONE);
            binding.allProductsShimmerLayout.stopShimmer();
        }catch (Exception ex){
            Log.d(TAG, ex.getLocalizedMessage());
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}