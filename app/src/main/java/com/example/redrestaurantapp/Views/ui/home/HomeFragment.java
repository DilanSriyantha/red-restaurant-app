package com.example.redrestaurantapp.Views.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.redrestaurantapp.Adapters.CategoriesAdapter;
import com.example.redrestaurantapp.Adapters.ProductsAdapter;
import com.example.redrestaurantapp.Controllers.CategoryController;
import com.example.redrestaurantapp.Controllers.ProductController;
import com.example.redrestaurantapp.Views.ProductsActivity;
import com.example.redrestaurantapp.ServiceLayer.RealtimeDataBase;
import com.example.redrestaurantapp.Utils.ThreadPoolManager;
import com.example.redrestaurantapp.Views.ItemDetailsActivity;
import com.example.redrestaurantapp.R;
import com.example.redrestaurantapp.databinding.FragmentHomeBinding;
import com.facebook.shimmer.ShimmerFrameLayout;

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

    private ShimmerFrameLayout mCategoryShimmerLayout;
    private ShimmerFrameLayout mOrderAgainShimmerLayout;
    private ShimmerFrameLayout mAllProductsShimmerLayout;

    private ScrollView mParentScrollView;

    private TextView mTxtCartCount;

    private RealtimeDataBase rdb;

    public HomeFragment() {
        mCategoryController = new CategoryController();
        mProductController = new ProductController();
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

        mParentScrollView = root.findViewById(R.id.parentScrollLayout);

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
                while(mProductController.isLoading());
                Log.d(TAG, "finished loading products");

                requireActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mOrderAgainProductsAdapter = new ProductsAdapter(getActivity(), mProductController.getProducts(), ProductsAdapter.LayoutType.NARROW, HomeFragment.this::onOrderAgainItemClick);
                        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
                        mProductRecycler.setLayoutManager(layoutManager);
                        mProductRecycler.setAdapter(mOrderAgainProductsAdapter);

                        setOrderAgainLoading(false);
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
                while (mProductController.isLoading());
                Log.d(TAG, "finished loading all products");

                requireActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mAllProductsAdapter = new ProductsAdapter(getActivity(), mProductController.getProducts(), ProductsAdapter.LayoutType.WIDE,
                                HomeFragment.this::onAllProductItemClick);
                        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
                        mAllProductsRecycler.setLayoutManager(layoutManager);
                        mAllProductsRecycler.setAdapter(mAllProductsAdapter);

                        setAllProductsLoading(false);
                    }
                });
            }
        });
    }

    private void onCategoryClick(int position){
        Log.d(TAG, "" + position);
    }

    private void onOrderAgainItemClick(int position){
        Intent productDetailsActivity = new Intent(getActivity(), ItemDetailsActivity.class);
        productDetailsActivity.putExtra("product", mProductController.getProduct(position));
        startActivity(productDetailsActivity);
    }

    private void onAllProductItemClick(int position){
        Intent productDetailsActivity = new Intent(getActivity(), ItemDetailsActivity.class);
        productDetailsActivity.putExtra("product", mProductController.getProduct(position));
        startActivity(productDetailsActivity);
    }

    private void onOrderAgainForwardClick(View v){
        onAllProductsForwardClick(v);
    }

    private void onAllProductsForwardClick(View v){
        Intent productsActivity = new Intent(requireActivity(), ProductsActivity.class);
        startActivity(productsActivity);
    }

    private void setParentScrollListener() {
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
                    if(mAllProductsRecycler.isNestedScrollingEnabled())
                        mAllProductsRecycler.setNestedScrollingEnabled(false);
                }
            }
        });
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